import React, { useEffect, useState, useCallback } from "react";

import { AgGridReact } from "ag-grid-react";

import ContentSearch from "./ContentSearch";
import Modal from "../../common/Modal";
import ContentModal from "./ContentModal";

function Privilege(props) {
    const [data, setData] = useState();
    // 임시
    const [tmpData, setTmpData] = useState();
    // useEffect(() => {
    //     let tmp = [
    //         { id: "3", name: "ROLE_ADMIN", label: "관리자", comment: "" },
    //         { id: "4", name: "ROLE_USER", label: "사용자", comment: "" },
    //     ];
    //     setTmpData(tmp);
    // }, []);

    // column setting
    const columnDefs = [
        // { field: "id", headerName: "id", filter: false },
        { field: "name", headerName: "권한코드", filter: true },
        { field: "encodedNm", headerName: "권한명", filter: true },
        { field: "comment", headerName: "비고", filter: false },
        // { field: "name", headerName: "코드", filter: true },
        // { field: "label", headerName: "코드명", filter: true },
        // { field: "comment", headerName: "비고", filter: false },
    ];

    const defaultColDef = {
        resizable: true,
    };

    const onGridReady = useCallback((params) => {
        params.api.sizeColumnsToFit();
    }, []);

    // modal
    const [proc, setProc] = useState({});
    const [modalOpen, setModalOpen] = useState(false);
    const onOpenModal = () => {
        setProc({ task: "proc", mode: "insert" });
        setModalOpen(true);
        setUser({});
    };
    const closeModal = () => {
        setModalOpen(false);
    };

    // Example cell Click event
    const [user, setUser] = useState();
    const cellClickedListener = useCallback((evt) => {
        setUser(evt.data);
        setProc({ task: "proc", mode: "update" });
        setModalOpen(true);
    }, []);

    return (
        <div>
            <div className="pageWrap">
                <ContentSearch setData={setData} />

                <div className="tableWrap">
                    <div className="ag-theme-alpine" style={{ width: "100%", height: "540px" }}>
                        <AgGridReact
                            // rowData={tmpData}
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

                <div className="btnWrap flxRit">
                    <button className="btn btn_write" onClick={onOpenModal}>
                        등록
                    </button>
                </div>
            </div>

            <Modal open={modalOpen} close={closeModal} header="권한관리">
                <ContentModal mode={proc.mode} data={user} close={closeModal} />
            </Modal>
        </div>
    );
}

export default Privilege;
