import React, { useEffect, useState, useCallback, useRef } from "react";
import Swal from "sweetalert2";
import msg from "../../../common/message";

import { AgGridReact } from "ag-grid-react";

import Modal from "../../../common/Modal";
import ContentModal from "./ContentModal";
import ContentSearch from "./ContentSearch";
import axios from "axios";
import { useCodeTree, checkEditRight } from "../../../../CommonHook";

function RschData(props) {
    const [render, setRender] = useState(true);
    const [data, setData] = useState([]);
    const [boardData, setBoardData] = useState({});

    // 지역 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    // 그리드용 매핑 배열
    const sggMap = sgg.reduce((result, item) => {
        result[item.name] = item.value;
        return result;
    }, {});

    // 편집권 검사
    const hasEdit = checkEditRight(document.location.pathname);

    // column setting
    const columnDefs = [
        { field: "seq", headerName: "번호", valueGetter: "node.rowIndex + 1", sortable: true, flex: 0.5 },
        { field: "year", flex: 0.5, headerName: "연도", sortable: true },
        {
            field: "sggCd",
            flex: 0.5,
            headerName: "지역",
            valueFormatter: (params) => {
                const sggNm = sggMap[params.value];
                return sggNm || params.value; // 매핑된 값이 없을 경우 기존 값 사용
            },
        },
        {
            field: "rschType",
            flex: 0.5,
            headerName: "유형",
            valueFormatter: (params) => {
                return params.value == "0" ? "관리카드" : "정리서식";
            },
        },
        { field: "dataNm", flex: 2, headerName: "데이터명" },
        {
            field: "collectYn",
            flex: 0.5,
            headerName: "데이터화",
            valueFormatter: (params) => {
                return params.value == "Y" ? "완료" : "대기";
            },
            cellStyle: (params) => {
                return {
                    color: params.value == "N" ? "red" : "black",
                    // fontWeight: params.value == "N" ? "bold" : "normal",
                };
            },
        },
        { field: "createId", flex: 0.5, headerName: "등록자" },
        { field: "createDtm", flex: 1, headerName: "등록일시", sortable: true },
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

            // 데이터 수집 이벤트. *실태조사는 3년에 1번으로 시스템 기능을 사용자에게 남겨두지 않고, 유지보수 차원에서 개발자가 직접 DB화 합니다.
            if (evt.colDef.field == "collectYn" && evt.value == "N" && hasEdit) {
                return alert("사용자의 실태조사 데이터 직접 승인은 제한됩니다. 담당 업체에 문의해주세요.");
                // Swal.fire(msg.alertMessage["double_check_collect"]).then((res) => {
                //     if (res.isConfirmed) {
                //         const docId = evt.data.id;
                //         axios("/api/data/rsch/data/collect/" + docId)
                //             .then((result) => {
                //                 // if (result.data.collectYn == "Y") btn_search.click();
                //                 if (result.data.collectYn == "Y") document.querySelector("button").click();
                //             })
                //             .catch((err) => alert(err.response));
                //     } else {
                //         return;
                //     }
                // });
            } else {
                if (evt.data.state == "-1") {
                    setProc({ task: "proc", mode: "insert", retry: true });
                } else {
                    setProc({ task: "proc", mode: "update" });
                }
                setBoardData(evt.data);
                setModalOpen(true);
            }
        },
        [hasEdit]
    );

    // modal
    const [proc, setProc] = useState({});
    const [modalOpen, setModalOpen] = useState(false);
    const onOpenModal = () => {
        setBoardData({});
        setProc({ task: "proc", mode: "insert" });
        setModalOpen(true);
    };
    const closeModal = () => {
        setModalOpen(false);
    };

    const excelDownload = () => {
        console.log("엑셀 다운로드 미구현");
    };

    return (
        <div>
            <div className="pageWrap">
                <ContentSearch setData={setData} render={render} sgg={sgg} />

                <div className="btnWrap flxRit">
                    <button className="btn btn_write" onClick={onOpenModal} hidden={!hasEdit}>
                        등록
                    </button>
                    {/* <button className="btn btn_excel" onClick={excelDownload}>
                        다운로드
                    </button> */}
                </div>

                <div className="ag-theme-alpine" style={{ width: "100%", height: "512px" }}>
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

            <Modal open={modalOpen} close={closeModal} header="조사자료">
                <ContentModal
                    mode={proc.mode}
                    retry={proc.retry}
                    data={boardData}
                    close={closeModal}
                    render={() => setRender(!render)}
                    sgg={sgg}
                    collectYn={boardData.collectYn}
                    hasEdit={hasEdit}
                />
            </Modal>
        </div>
    );
}
export default RschData;
