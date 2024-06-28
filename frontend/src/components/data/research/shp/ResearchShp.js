import React, { useEffect, useState, useCallback, useRef } from "react";

import { AgGridReact } from "ag-grid-react";

import Modal from "../../../common/Modal";
import ResearchShpModal from "./ResearchShpModal";
import { Button, Space } from "antd";
import Swal from "sweetalert2";
import ContentSearch from "./ContentSearch";
import { useCodeTree, checkEditRight } from "../../../../CommonHook";
import msg from "../../../common/message";
import axios from "axios";

function ResearchShp(props) {
    const [render, setRender] = useState(true);
    const [data, setData] = useState([]);
    const [shp, setShp] = useState({});

    // 지역 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    // 실태조사 타입
    const { tree: type, set: setType } = useCodeTree({ parentNm: "TYPE", deps: [] });

    useEffect(() => {
        if (data.find((ele) => ele.state == 1)) {
            const intervalId = setInterval(() => {
                setRender(!render);
            }, 2000);

            return () => {
                clearInterval(intervalId);
            };
        }
    }, [render, data]);

    // column setting
    const columnDefs = [
        { field: "year", flex: 1, headerName: "연도", filter: false },
        { field: "regName", flex: 1, headerName: "지역", filter: false },
        { field: "name", flex: 2, headerName: "데이터명", filter: false },
        { field: "type", flex: 1, headerName: "구분", filter: false },
        {
            field: "viewYn",
            flex: 1,
            headerName: "시각화",
            filter: false,
            cellRenderer: ({ value }) => {
                if (value == "Y") {
                    return "사용";
                } else {
                    return "미사용";
                }
            },
        },
        {
            field: "state",
            flex: 1,
            headerName: "업로드 상태",
            filter: false,
            cellRenderer: ({ value }) => {
                if (value == 0) {
                    return "대기";
                } else if (value == 1) {
                    return "등록중...";
                } else if (value == 2) {
                    return "완료";
                } else {
                    return "실패";
                }
            },
            cellStyle: (params) => {
                return {
                    color:
                        params.value == 0 ? "red" : params.value == 1 ? "black" : params.value == 2 ? "green" : "red",
                };
            },
        },
        // {
        //     field: "layerRetry",
        //     flex: 1,
        //     headerName: "레이어 재발행",
        //     filter: false,
        //     cellRenderer: (value) => {
        //         if (value.data.state == "2") {
        //             return (
        //                 <div>
        //                     <Button
        //                         type="primary"
        //                         style={{ borderRadius: "9px", backgroundColor: "#72B0B5", borderColor: "#72B0B5" }}
        //                         // style={{ borderRadius: "9px", backgroundColor: "#6AA3AC", borderColor: "#6AA3AC" }}
        //                         onClick={() => {
        //                             fetch("/api/gis/publish-layer?resultNo=" + value.data.resultNo, {
        //                                 method: "POST",
        //                             })
        //                                 .then((res) => {
        //                                     if (res.ok) {
        //                                         Swal.fire({
        //                                             text: "레이어를 성공적으로 재발행했습니다.",
        //                                             content: {
        //                                                 element: "span",
        //                                                 attributes: {
        //                                                     style: "font-size: 12px;",
        //                                                 },
        //                                             },
        //                                         });
        //                                     } else {
        //                                         return res.json().then((json) => {
        //                                             throw new Error(json.message);
        //                                         });
        //                                     }
        //                                 })
        //                                 .catch((error) => {
        //                                     Swal.fire({
        //                                         text: error.message,
        //                                         content: {
        //                                             element: "span",
        //                                             attributes: {
        //                                                 style: "font-size: 12px;",
        //                                             },
        //                                         },
        //                                     });
        //                                 });
        //                         }}
        //                     >
        //                         재발행
        //                     </Button>
        //                 </div>
        //             );
        //         } else {
        //             return "";
        //         }
        //     },
        // },
    ];

    const cellClickedListener = useCallback((evt) => {
        if (evt.event.target.closest("button")) return;

        // 데이터 승인 이벤트
        // if (evt.colDef.field == "state" && evt.value == 0) {
        //     Swal.fire(msg.alertMessage["double_check_collect"]).then((res) => {
        //         if (res.isConfirmed) {
        //             const docId = evt.data.resultNo;
        //             fetch("/api/gis/shp?resultNo=" + docId, { method: "post" });
        //             Swal.fire({
        //                 text: "저장하고 있습니다. 잠시만 기다려주세요",
        //                 content: {
        //                     element: "span",
        //                     attributes: {
        //                         style: "font-size: 12px;",
        //                     },
        //                 },
        //             });
        //             setRender((prevState) => !prevState);
        //         } else {
        //             return;
        //         }
        //     });
        // } else {
        if (evt.data.state == "-1") {
            setProc({ task: "proc", mode: "insert", retry: true });
        } else {
            setProc({ task: "proc", mode: "update" });
        }
        setShp(evt.data);
        setModalOpen(true);
        // }
    }, []);

    // modal
    const [proc, setProc] = useState({});
    const [modalOpen, setModalOpen] = useState(false);
    const onOpenModal = () => {
        setShp({});
        setProc({ task: "proc", mode: "insert" });
        setModalOpen(true);
    };
    const closeModal = () => {
        setModalOpen(false);
    };

    // 편집권 검사
    const hasEdit = checkEditRight(document.location.pathname);
    return (
        <div className="research">
            <div className="pageWrap">
                <ContentSearch setData={setData} render={render} sgg={sgg} />
                <div className="tableWrap">
                    <div className="btnWrap flxRit">
                        <button className="btn btn_write" onClick={onOpenModal} hidden={!hasEdit}>
                            등록
                        </button>
                    </div>
                    <div className="ag-theme-alpine" style={{ width: "100%", height: "512px" }}>
                        <AgGridReact
                            headerHeight={40}
                            rowData={data}
                            columnDefs={columnDefs}
                            rowSelection="multiple"
                            onCellClicked={cellClickedListener}
                            pagination={true}
                            paginationPageSize={10}
                        />
                    </div>
                </div>
            </div>

            <Modal open={modalOpen} close={closeModal} header="SHP">
                <ResearchShpModal
                    mode={proc.mode}
                    retry={proc.retry}
                    data={shp}
                    allData={data}
                    close={closeModal}
                    sgg={sgg}
                    type={type}
                    render={() => setRender(!render)}
                />
            </Modal>
        </div>
    );
}

export default ResearchShp;
