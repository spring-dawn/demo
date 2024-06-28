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
export default function PfPvOpen() {
    // privateOpen (사유지개방) 일 텐데 이름이 왜 이렇게 돼있지
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
        {
            field: "sggCd",
            headerName: "구군",
            width: 100,
            valueFormatter: (params) => {
                const sggNm = sggMap[params.value];
                return sggNm || params.value; // 매핑된 값이 없을 경우 기존 값 사용
            },
        },
        { field: "lotNm", flex: 2, headerName: "주차장명", filter: false },
        { field: "address", flex: 2, headerName: "지번주소", filter: false },
        { field: "spcs", headerName: "총주차면수", sortable: true },
    ];

    // 사유지 개방 표준화 양식 엑셀 다운로드
    const [search, setSearch] = useRecoilState(SearchState);
    const excelDownload = () => {
        // 검색 조회 결과가 0이면 리턴.
        if (data.length == 0) return alert("다운로드 할 데이터가 조회되지 않았습니다.");

        const year = search.year == undefined ? "" : search.year;
        const month = search.month == undefined ? "" : search.month;
        const sggCd = search.sggCd == undefined ? "" : search.sggCd;

        // if (year == "" || month == "" || sggCd == "")
        //     return alert("표준 양식이 맞춰지도록 검색어 3개를 전부 선택해주세요.");

        let url = "/api/data/facility/read/open/prv/standard/download?";
        url += `year=${year}`;
        url += `&month=${month}`;
        url += `&sggCd=${sggCd}`;
        document.location.href = url;
    };
    // [240308] 업로드용 양식 다운로드
    const formatDownload = () => {
        if (!confirm("현재 접속 담당자의 소속에 따른 사유지개방주차장 보고서 양식을 다운로드 합니다.")) return;
        axios("/api/data/facility/read/open/prv/standard/manager/keyword")
            .then((res) => {
                const keyword = res.data;
                let url = "/api/data/facility/read/open/prv/standard/download?";
                url += `year=${keyword.year}`;
                url += `&month=${keyword.month}`;
                url += `&sggCd=${keyword.sggCd}`;
                document.location.href = url;
            })
            .catch((err) => alert(err.response.data.message));
    };

    return (
        <div className="pageWrap">
            <ContentSearch sgg={sgg} setData={setData} />
            <div className="btnWrap flxRit">
                <button className="btn btn_excel" onClick={excelDownload}>
                    데이터 다운로드
                </button>
                <button className="btn btn_excel" onClick={formatDownload}>
                    보고서 양식 다운로드
                </button>
            </div>

            <div className="tableWrap">
                <div className="ag-theme-alpine" style={{ width: "100%", height: "512px" }}>
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

            <Modal open={modalOpen} close={closeModal} header="사유지개방주차장 현황">
                <ContentModal mode={proc.mode} data={pbl} close={closeModal} sgg={sgg} />
            </Modal>
        </div>
    );
}
