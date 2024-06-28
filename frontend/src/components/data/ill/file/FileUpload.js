import React, { useEffect, useState, useCallback, useRef } from "react";
import Swal from "sweetalert2";
import msg from "../../../common/message";

import { AgGridReact } from "ag-grid-react";

import axios from "axios";
import { checkEditRight, checkUpdateAndDeleteRight, useCodeTree, isFirstAdmin } from "../../../../CommonHook";
//
import ContentSearch from "./ContentSearch.js";
import Modal from "../../../common/Modal";
import ContentModal from "./ContentModal.js";

export default function FileUpload() {
    const [render, setRender] = useState(true);
    const [data, setData] = useState([]);
    const [row, setRow] = useState({});
    // const btn_search = document.getElementById("btn_search");데

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
        { field: "seq", headerName: "번호", valueGetter: "node.rowIndex + 1", sortable: true, width: "90" },
        { field: "year", flex: 0.5, headerName: "연도", sortable: true },
        { field: "month", flex: 0.5, headerName: "월", sortable: true },
        {
            field: "sggCd",
            flex: 0.5,
            headerName: "구군",
            valueFormatter: (params) => {
                const sggNm = sggMap[params.value];
                return sggNm || params.value; // 매핑된 값이 없을 경우 기존 값 사용
            },
        },
        { field: "dataNm", flex: 1.5, headerName: "데이터명" },
        {
            field: "collectYn",
            flex: 0.5,
            headerName: "데이터승인",
            valueFormatter: (params) => {
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
        },
        { field: "createId", flex: 0.5, headerName: "등록자" },
        { field: "createDtm", flex: 0.7, headerName: "등록일시", sortable: true },
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
                Swal.fire(msg.alertMessage["double_check_collect"]).then((res) => {
                    const docId = evt.data.id;

                    if (res.isConfirmed) {
                        axios("/api/data/illegal/data/collect/" + docId)
                            .then(() => {
                                // Swal.fire(msg.alertMessage["insert_success"]);
                                document.querySelector("button").click();
                            })
                            .catch((err) => {
                                alert("오류가 있습니다. 파일 상태를 체크하고 이후 담당자에 문의해주세요.");
                                console.log("에러 발생", err.response.data.message);
                            });
                    } else if (res.isDenied) {
                        // [240416] 반려(데이터 승인 거부)
                        if (!isFirstAdmin()) return alert("시 관리자 권한입니다.");

                        axios("/api/data/illegal/data/reject/" + docId)
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

    // [240415] 임시 양식 다운로드 기능
    const tmpExcelDownload = () => {
        const userInfo = JSON.parse(localStorage.getItem("user"));
        if (userInfo.agency == "31000" || !userInfo.roleNm.includes("MNGR")) return alert("구군 담당자 전용입니다.");

        if (!confirm("차세대 세외 행정시스템 연계 전 임시 기능입니다. 202311 기준 임시 양식을 내려받으시겠습니까?"))
            return;
        let url = "/api/data/illegal/data/tmp/download";
        document.location.href = url;
    };

    return (
        <div>
            <div className="pageWrap">
                <ContentSearch setData={setData} render={render} sgg={sgg} />

                <div className="btnWrap flxRit">
                    <button className="btn btn_excel" onClick={tmpExcelDownload}>
                        보고서 양식 다운로드(임시)
                    </button>
                    <button className="btn btn_write" onClick={onOpenModal} hidden={!hasEdit}>
                        등록
                    </button>
                </div>

                <div className="ag-theme-alpine" style={{ width: "100%", height: "450px" }}>
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

            <Modal open={modalOpen} close={closeModal} header="불법주정차 단속 - 파일 업로드">
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
