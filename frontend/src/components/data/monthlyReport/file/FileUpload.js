import React, { useEffect, useState, useCallback, useRef } from "react";
import Swal from "sweetalert2";
import msg from "../../../common/message";

import { AgGridReact } from "ag-grid-react";

import axios from "axios";
//
import ContentSearch from "./ContentSearch.js";
import Modal from "../../../common/Modal";
import ContentModal from "./ContentModal.js";
import { checkEditRight, checkUpdateAndDeleteRight, useCodeTree, isFirstAdmin } from "../../../../CommonHook";

export default function FileUpload() {
    const [render, setRender] = useState(true);
    const [data, setData] = useState([]);
    const [row, setRow] = useState({});
    // const btn_search = document.getElementById("btn_search");

    // 지역 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    // 그리드용 매핑 배열
    const sggMap = sgg.reduce((result, item) => {
        result[item.name] = item.value;
        return result;
    }, {});

    // 편집권 검사
    let hasEdit = checkEditRight(document.location.pathname);

    // column setting
    const columnDefs = [
        {
            field: "seq",
            headerName: "번호",
            valueGetter: "node.rowIndex + 1",
            sortable: true,
            width: "70",
        },
        { field: "year", headerName: "연도", sortable: true, width: "80" },
        { field: "month", headerName: "월", sortable: true, width: "70" },
        {
            field: "sggCd",
            headerName: "구군",
            valueFormatter: (params) => {
                return sggMap[params.value] || params.value; // 매핑된 값이 없을 경우 기존 값 사용
            },
            width: "85",
        },
        { field: "dataNm", headerName: "데이터명", width: "280" },
        {
            field: "collectYn",
            headerName: "데이터 승인",
            valueFormatter: (params) => {
                // return params.value == "Y" ? "완료" : "대기";
                switch (params.value) {
                    case "Y":
                        return "완료";
                    case "N":
                        return "대기";
                    default:
                        return "반려";
                }
            },
            cellStyle: (params) => {
                const colorMap = {
                    Y: "black",
                    N: "red",
                    X: "grey",
                };
                return {
                    color: colorMap[params.value] || "black",
                    // fontWeight: params.value == "N" ? "bold" : "normal",
                };
            },
            width: "130",
        },
        {
            field: "dupType",
            headerName: "중복 유무",
            valueFormatter: (params) => {
                switch (parseInt(params.value)) {
                    case 1:
                        return "부분중복";
                    case 2:
                        return "완전중복";
                    default:
                        return "정상";
                }
            },
            cellStyle: (params) => {
                return {
                    color: params.value == 2 ? "red" : "green",
                    // fontWeight: params.value == "N" ? "bold" : "normal",
                };
            },
            width: "120",
        },
        { field: "createId", headerName: "등록자", width: "100" },
        { field: "createDtm", headerName: "등록일시", sortable: true, width: "205" },
    ];

    const defaultColDef = {
        resizable: true,
        autoHeight: true,
    };

    // search 에서 쓰는 api 를 게시판에도 재사용, 별도 호출 없음.
    const onGridReady = useCallback((params) => {
        params.api.sizeColumnsToFit();
    }, []);

    // cell click event
    const cellClickedListener = useCallback(
        (evt) => {
            if (evt.event.target.closest("button")) return;

            // 데이터 수집 이벤트
            if (evt.colDef.field == "collectYn" && evt.value == "N" && hasEdit) {
                // 완전중복인 경우 수집X, 부분중복의 경우 관리자 권한 검사
                if (evt.data.dupType == 2) return Swal.fire(msg.alertMessage["data_absolute_dup"]);
                if (evt.data.dupType == 1 && !checkUpdateAndDeleteRight(hasEdit))
                    return Swal.fire(msg.alertMessage["data_partial_dup"]);

                Swal.fire(msg.alertMessage["double_check_collect"]).then((res) => {
                    const docId = evt.data.id;

                    if (res.isConfirmed) {
                        axios("/api/data/mr/data/collect/" + docId)
                            .then((result) => {
                                if (result.data.collectYn == "Y") document.querySelector("button").click();
                            })
                            .catch((err) => {
                                alert(err.response.data.message);
                            });
                    } else if (res.isDenied) {
                        // [240416] 반려(데이터 승인 거부)
                        if (!isFirstAdmin()) return alert("시 관리자 권한입니다.");

                        axios("/api/data/mr/data/reject/" + docId)
                            .then((result) => {
                                if (result.data.collectYn == "X") document.querySelector("button").click();
                            })
                            .catch((err) => {
                                alert(err.response.data.message);
                            });
                    } else {
                        return;
                    }
                });
            } else {
                if (evt.data.state == "-1") {
                    setProc({ task: "proc", mode: "insert", retry: true });
                } else {
                    setProc({ task: "proc", mode: "update" });
                }
                setRow(evt.data);
                setModalOpen(true);
            }
        },
        [hasEdit]
    );

    // modal
    const [proc, setProc] = useState({});
    const [modalOpen, setModalOpen] = useState(false);
    const onOpenModal = () => {
        setRow({});
        setProc({ task: "proc", mode: "insert" });
        setModalOpen(true);
    };
    const closeModal = () => {
        setModalOpen(false);
    };

    return (
        <div>
            <div className="pageWrap">
                <ContentSearch setData={setData} render={render} sgg={sgg} />

                <div className="btnWrap flxRit">
                    <button className="btn btn_write" onClick={onOpenModal} hidden={!hasEdit}>
                        등록
                    </button>
                </div>

                <div className="ag-theme-alpine" style={{ width: "100%", height: "530px" }}>
                    <AgGridReact
                        headerHeight={40}
                        rowData={data}
                        columnDefs={columnDefs}
                        defaultColDef={defaultColDef}
                        rowSelection="multiple"
                        onGridReady={onGridReady}
                        onCellClicked={cellClickedListener}
                        pagination={true}
                        paginationPageSize={10}
                    />
                </div>
            </div>

            <Modal open={modalOpen} close={closeModal} header="월간보고 - 파일 업로드">
                <ContentModal
                    mode={proc.mode}
                    retry={proc.retry}
                    data={row}
                    close={closeModal}
                    render={() => setRender(!render)}
                    sgg={sgg}
                    collectYn={row.collectYn}
                    hasEdit={hasEdit}
                />
            </Modal>
        </div>
    );
}
