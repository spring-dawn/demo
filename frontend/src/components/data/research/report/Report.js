import React, { useEffect, useState, useCallback, useRef } from "react";

import { AgGridReact } from "ag-grid-react";

import Modal from "../../../common/Modal";
import ReportModal from "./ReportModal";
import { Button, Space } from "antd";
import Swal from "sweetalert2";
import msg from "../../../common/message";
import ContentSearch from "./ContentSearch";
import { useCodeTree, checkEditRight } from "../../../../CommonHook";

function Report(props) {
    const [render, setRender] = useState(true);
    const [data, setData] = useState([]);
    const [report, setReport] = useState({});

    // 지역 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    // column setting
    const columnDefs = [
        { field: "seq", headerName: "번호", valueGetter: "node.rowIndex + 1", sortable: true, flex: 0.5 },
        { field: "year", flex: 0.5, headerName: "연도", filter: false },
        { field: "regName", flex: 0.5, headerName: "지역", filter: false },
        { field: "name", flex: 2, headerName: "데이터명", filter: false },
        { field: "createId", flex: 0.5, headerName: "등록자", filter: false },
        { field: "createDtm", flex: 1, headerName: "등록일시", sortable: true },
    ];

    const defaultColDef = {
        resizable: true,
        autoHeight: true,
    };

    const cellClickedListener = useCallback((evt) => {
        if (evt.event.target.closest("button")) return;

        if (evt.data.state == "-1") {
            setProc({ task: "proc", mode: "insert", retry: true });
        } else {
            setProc({ task: "proc", mode: "update" });
        }
        setReport(evt.data);
        setModalOpen(true);
    }, []);

    // modal
    const [proc, setProc] = useState({});
    const [modalOpen, setModalOpen] = useState(false);
    const onOpenModal = () => {
        setReport({});
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

            <Modal open={modalOpen} close={closeModal} header="보고서">
                <ReportModal
                    mode={proc.mode}
                    retry={proc.retry}
                    data={report}
                    allData={data}
                    close={closeModal}
                    sgg={sgg}
                    render={() => setRender(!render)}
                />
            </Modal>
        </div>
    );
}

export default Report;
