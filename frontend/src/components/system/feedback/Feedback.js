import React, { useEffect, useState, useCallback, useRef } from "react";
import Swal from "sweetalert2";
import msg from "../../common/message";

import { AgGridReact } from "ag-grid-react";

import Modal from "../../common/Modal";
import FeedbackModal from "./FeedbackModal";
import ContentSearch from "./ContentSearch";
import { useCodeTree } from "../../../CommonHook";
import axios from "axios";

function Feedback(props) {
    const [render, setRender] = useState(true);
    const [data, setData] = useState([]);
    const [feedback, setFeedback] = useState({});

    /// 지역 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    // 그리드용 매핑 배열
    const sggMap = sgg.reduce((result, item) => {
        result[item.name] = item.value;
        return result;
    }, {});

    // column setting
    const columnDefs = [
        { field: "seq", headerName: "번호", valueGetter: "node.rowIndex + 1", sortable: true, flex: 0.5 },
        {
            field: "sggCd",
            flex: 0.5,
            headerName: "구군",
            valueFormatter: (params) => {
                const sggNm = sggMap[params.value];
                return sggNm || params.value; // 매핑된 값이 없을 경우 기존 값 사용
            },
        },
        { field: "dept", headerName: "부서", flex: 0.5 },
        { field: "title", flex: 2, headerName: "제목", filter: false },
        {
            field: "status",
            flex: 0.5,
            headerName: "처리상태",
            filter: false,
            valueFormatter: (params) => {
                return params.value == "0" ? "요청" : "완료";
            },
        },
        { field: "createId", flex: 0.5, headerName: "등록자", filter: false },
        { field: "createDtm", flex: 1, headerName: "등록일시", sortable: true },
    ];

    const defaultColDef = {
        resizable: true,
        autoHeight: true,
    };

    const cellClickedListener = useCallback((evt) => {
        if (evt.event.target.closest("button")) return;
        // 처리상태 변경 이벤트
        if (evt.colDef.field == "status" && evt.value == "0") {
            // 권한 확인

            const userInfo = JSON.parse(localStorage.getItem("user"));
            if (userInfo.roleNm != "ROLE_ADM") return alert("처리상태를 변경하려면 시 관리자 권한이 필요합니다.");

            Swal.fire(msg.alertMessage["update_status"]).then((res) => {
                if (!res.isConfirmed) return;
                //
                const docId = evt.data.id;
                axios("/api/system/feedback/status/" + docId)
                    .then((result) => {
                        document.querySelector("button").click();
                    })
                    .catch((err) => {
                        alert("오류가 있습니다. 담당자에 문의하세요.");
                    });
            });
        } else {
            if (evt.data.state == "-1") {
                setProc({ task: "proc", mode: "insert", retry: true });
            } else {
                setProc({ task: "proc", mode: "update" });
            }
            setFeedback(evt.data);
            setModalOpen(true);
        }
    }, []);

    // modal
    const [proc, setProc] = useState({});
    const [modalOpen, setModalOpen] = useState(false);
    const onOpenModal = () => {
        setFeedback({});
        setProc({ task: "proc", mode: "insert" });
        setModalOpen(true);
    };
    const closeModal = () => {
        setModalOpen(false);
    };

    return (
        <div className="research">
            <div className="pageWrap">
                <ContentSearch setData={setData} render={render} sgg={sgg} />
                <div className="tableWrap">
                    <div className="btnWrap flxRit">
                        <button className="btn btn_write" onClick={onOpenModal}>
                            등록
                        </button>
                    </div>
                    <div className="ag-theme-alpine" style={{ width: "100%", height: "512px" }}>
                        <AgGridReact
                            headerHeight={40}
                            rowData={data}
                            defaultColDef={defaultColDef}
                            columnDefs={columnDefs}
                            rowSelection="multiple"
                            onCellClicked={cellClickedListener}
                            pagination={true}
                            paginationPageSize={10}
                        />
                    </div>
                </div>
            </div>

            <Modal open={modalOpen} close={closeModal} header="시스템 개선 요청">
                <FeedbackModal
                    mode={proc.mode}
                    retry={proc.retry}
                    data={feedback}
                    allData={data}
                    close={closeModal}
                    sgg={sgg}
                    render={() => setRender(!render)}
                />
            </Modal>
        </div>
    );
}

export default Feedback;
