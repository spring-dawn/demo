import { React, useEffect, useState, useCallback } from "react";
import { AgGridReact } from "ag-grid-react";
import axios from "axios";
//
import ContentSearch from "./ContentSearch";
import ContentModal from "./ContentModal";
import Modal from "../../../../common/Modal";
import { useCodeTree } from "../../../../../CommonHook";
import { func } from "prop-types";
import { gugunParseCodeToName } from "../../../../../CommonFunction";
export default function PfPrivate() {
    const [data, setData] = useState();

    // 구군 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    // 그리드용 매핑 배열
    const sggMap = sgg.reduce((result, item) => {
        result[item.name] = item.value;
        return result;
    }, {});

    const [proc, setProc] = useState({});
    const [modalOpen, setModalOpen] = useState(false);
    const onOpenModal = () => {
        setProc({ task: "proc", mode: "insert" });
        setModalOpen(true);
        setPbl({});
    };
    const closeModal = () => {
        setModalOpen(false);
    };

    // Example cell Click event
    const [pbl, setPbl] = useState();
    const cellClickedListener = useCallback((evt) => {
        setPbl(evt.data);
        setProc({ task: "proc", mode: "update" });
        setModalOpen(true);
    }, []);

    // column setting
    const onGridReady = useCallback((params) => {
        params.api.sizeColumnsToFit();
    }, []);

    const defaultColDef = {
        resizable: true,
        autoHeight: true,
        // 헤더, 셀 텍스트에 개행 효과
        wrapHeaderText: true,
        // wrapText: true,
    };
    const columnDefs = [
        { field: "seq", headerName: "번호", valueGetter: "node.rowIndex + 1", sortable: true, flex: 0.5 },
        { field: "year", flex: 0.5, headerName: "연도", filter: false },
        { field: "month", flex: 0.5, headerName: "월", filter: false },
        {
            field: "sgg",
            flex: 0.8,
            headerName: "구군",
            filter: false,
            cellRenderer: ({ value }) => {
                return gugunParseCodeToName(value);
            },
        },
        {
            field: "crdnNocs",
            flex: 1,
            headerName: "단속건수",
            sortable: true,
        },
        { field: "vhclNm", flex: 2, headerName: "차량", filter: false },
        // { field: "prchsYmd", flex: 2, headerName: "구입일자", filter: false },
        { field: "crdnPrd", flex: 2, headerName: "단속시간", sortable: true },
        { field: "crdnCtrM", flex: 1, headerName: "단속기준", sortable: true },
    ];

    return (
        <div className="pageWrap">
            <ContentSearch sgg={sgg} setData={setData} />
            {/* <div className="btnWrap flxRit">
                <button className="btn btn_write" onClick={onOpenModal}>
                    등록
                </button>
            </div> */}

            <div className="tableWrap">
                <div className="ag-theme-alpine" style={{ width: "100%", height: "512px" }}>
                    <AgGridReact
                        headerHeight={40}
                        rowData={data}
                        columnDefs={columnDefs}
                        defaultColDef={defaultColDef}
                        rowSelection="multiple"
                        onGridReady={onGridReady}
                        // onCellDoubleClicked={cellClickedListener}
                        pagination={true}
                        paginationPageSize={10}
                    />
                </div>
            </div>

            <Modal open={modalOpen} close={closeModal} header="불법주정차 단속 이동형">
                <ContentModal mode={proc.mode} data={pbl} close={closeModal} sgg={sgg} />
            </Modal>
        </div>
    );
}
