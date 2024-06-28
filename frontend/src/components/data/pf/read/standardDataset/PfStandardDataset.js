import { React, useEffect, useState, useCallback } from "react";
import { AgGridReact } from "ag-grid-react";
import axios from "axios";
import { useRecoilState } from "recoil";
import { SearchState } from "../../../../../Context";
//
import ContentSearch from "./ContentSearch";
import ContentModal from "./ContentModal";
import Modal from "../../../../common/Modal";
import { useCodeTree } from "../../../../../CommonHook";
import { func } from "prop-types";
export default function PfStandardDataset() {
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
        // { field: "seq", headerName: "번호", valueGetter: "node.rowIndex + 1", sortable: true, flex: 0.5 },
        { field: "mngNo", headerName: "관리번호" },
        {
            field: "sggCd",
            headerName: "구군",
            width: 100,
            valueFormatter: (params) => {
                const sggNm = sggMap[params.value];
                return sggNm || params.value; // 매핑된 값이 없을 경우 기존 값 사용
            },
        },
        { field: "lotNm", headerName: "주차장명", filter: false },
        {
            field: "lotType",
            flex: 0.5,
            headerName: "주차유형",
            valueFormatter: (params) => {
                switch (params.value) {
                    case "1":
                        return "공영노상";
                    case "2":
                        return "공영노외";
                    case "3":
                        return "공영부설";
                    case "4":
                        return "민영노상";
                    case "5":
                        return "민영노외";
                    case "6":
                        return "민영부설";
                    case "7":
                        return "부설";
                    case "8":
                        return "부설개방";
                    case "9":
                        return "사유지개방";
                    default:
                        return "유형없음";
                }
            },
        },
        { field: "address", flex: 1, headerName: "지번주소", filter: false },
        { field: "totalSpcs", flex: 0.7, headerName: "총주차면수", sortable: true },
    ];

    // 사유지 개방 표준화 양식 엑셀 다운로드
    const [search, setSearch] = useRecoilState(SearchState);
    const excelDownload = () => {
        // 검색 조회 결과가 0이면 리턴.
        if (data.length == 0) return alert("다운로드 할 데이터가 조회되지 않았습니다.");

        const year = search.year == undefined ? "" : search.year;
        const month = search.month == undefined ? "" : search.month;
        const sggCd = search.sggCd == undefined ? "" : search.sggCd;
        const lotType = search.lotType == undefined ? "" : search.lotType;

        let url = "/api/data/facility/standard/download?";
        url += `year=${year}`;
        url += `&month=${month}`;
        url += `&sggCd=${sggCd}`;
        url += `&lotType=${lotType}`;
        document.location.href = url;
    };

    return (
        <div className="pageWrap">
            <ContentSearch sgg={sgg} setData={setData} />
            <div className="btnWrap flxRit">
                <button className="btn btn_excel" onClick={excelDownload}>
                    다운로드
                </button>
            </div>

            <div className="tableWrap">
                <div className="ag-theme-alpine" style={{ width: "100%", height: "480px" }}>
                    <AgGridReact
                        headerHeight={40}
                        rowData={data}
                        columnDefs={columnDefs}
                        defaultColDef={defaultColDef}
                        rowSelection="multiple"
                        onGridReady={onGridReady}
                        onCellDoubleClicked={cellClickedListener}
                        pagination={true}
                        paginationPageSize={10}
                    />
                </div>
            </div>

            <Modal open={modalOpen} close={closeModal} header="표준데이터셋">
                <ContentModal mode={proc.mode} data={pbl} close={closeModal} sgg={sgg} />
            </Modal>
        </div>
    );
}
