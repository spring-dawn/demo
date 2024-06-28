import { React, useEffect, useState, useCallback } from "react";
import { AgGridReact } from "ag-grid-react";
import axios from "axios";
//
import ContentSearch from "./ContentSearch";
import ContentModal from "./ContentModal";
import Modal from "../../../../common/Modal";
import { useCodeTree } from "../../../../../CommonHook";
import { func } from "prop-types";
import { useRecoilState } from "recoil";
import { SearchState } from "../../../../../Context";

export default function PPublic() {
    const [data, setData] = useState();

    // 구군 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    const [search, setSearch] = useRecoilState(SearchState);

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
        {
            headerName: "월간보고",
            marryChildren: true,
            children: [
                { field: "year", headerName: "연도", width: 100, sortable: true },
                { field: "month", headerName: "월", width: 100, sortable: true },
                {
                    field: "sggCd",
                    headerName: "구군",
                    width: 100,
                    valueFormatter: (params) => {
                        const sggNm = sggMap[params.value];
                        return sggNm || params.value; // 매핑된 값이 없을 경우 기존 값 사용
                    },
                },
            ],
        },
        {
            headerName: "주차장",
            marryChildren: true,
            children: [
                { field: "name", headerName: "주차장명", width: 200, columnGroupShow: "close" },
                { field: "installDt", headerName: "설치일자", width: 100, columnGroupShow: "open" },
                { field: "location", headerName: "위치", width: 200, columnGroupShow: "open" },
                {
                    field: "roadYn",
                    headerName: "노상/노외",
                    columnGroupShow: "open",
                    width: 100,
                    valueFormatter: (params) => {
                        return params.value == "Y" ? "노상" : "노외";
                    },
                },
                { field: "owner", headerName: "소유", width: 100, columnGroupShow: "open" },
                { field: "agency", headerName: "운영기관", width: 150, columnGroupShow: "open" },
            ],
        },
        {
            headerName: "운영시간",
            marryChildren: true,
            children: [
                { field: "wh", headerName: "평일", width: 120 },
                { field: "whSaturday", headerName: "토요일" },
                { field: "whHoliday", headerName: "공휴일" },
                { field: "dayOff", headerName: "휴무일" },
            ],
        },
        {
            headerName: "요금",
            marryChildren: true,
            children: [
                {
                    field: "payYn",
                    headerName: "유/무료",
                    valueFormatter: (params) => {
                        return params.value == "Y" ? "유료" : "무료";
                    },
                },
                { field: "pay4Hour", headerName: "1시간요금", columnGroupShow: "open" },
                { field: "pay4Day", headerName: "1일요금", columnGroupShow: "open" },
            ],
        },
        { field: "totalSpaces", headerName: "전체주차면수", width: 150 },
        { field: "spaces", headerName: "일반주차면수", width: 150 },
        {
            headerName: "전용주차구획",
            children: [
                {
                    headerName: "소계",
                    width: 150,
                    sortable: true,
                    // columnGroupShow: "closed",
                    valueFormatter: (params) => {
                        const forDisabled = params.data.forDisabled || 0;
                        const forLight = params.data.forLight || 0;
                        const forPregnant = params.data.forPregnant || 0;
                        const forBus = params.data.forBus || 0;
                        const forElectric = params.data.forElectric || 0;
                        return forDisabled + forLight + forPregnant + forBus + forElectric;
                    },
                },
                { field: "forDisabled", headerName: "장애인전용", width: 120, columnGroupShow: "open" },
                { field: "forLight", headerName: "경차전용", width: 120, columnGroupShow: "open" },
                { field: "forPregnant", headerName: "임산부전용", width: 120, columnGroupShow: "open" },
                { field: "forBus", headerName: "버스전용", width: 120, columnGroupShow: "open" },
                { field: "forElectric", headerName: "전기차전용", width: 120, columnGroupShow: "open" },
            ],
        },
    ];

    const excelDownload = () => {
        // 검색 조회 결과가 0이면 리턴.
        if (data.length == 0) return alert("다운로드 할 데이터가 조회되지 않았습니다.");

        const year = search.year == undefined ? "" : search.year;
        const month = search.month == undefined ? "" : search.month;
        const sggCd = search.sggCd == undefined ? "" : search.sggCd;

        if (year == "" || month == "") return alert("연도와 월은 필수 입력 요소입니다.");

        let url = "/api/data/mr/data/excelDownload?";
        url += `year=${year}`;
        url += `&month=${month}`;
        url += `&sggCd=${sggCd}`;
        document.location.href = url;
    };
    const excelFormDownload = () => {
        if (!confirm("현재 접속 담당자의 소속에 따른 월간보고 보고서 양식을 다운로드 합니다.")) return;
        axios("/api/data/mr/data/standard/manager/keyword")
            .then((res) => {
                const keyword = res.data;
                let url = "/api/data/mr/data/excelDownload?";
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
            {/* <div className="btnWrap flxRit">
                <button className="btn btn_write" onClick={onOpenModal}>
                    등록
                </button>
            </div> */}
            {/* 버튼 유닛 */}
            <div className="btnWrap flxRit">
                <button className="btn btn_excel" onClick={excelDownload}>
                    통합 월간보고 데이터 다운로드
                </button>
                <button className="btn btn_excel" onClick={excelFormDownload}>
                    통합 월간보고 양식 다운로드
                </button>
            </div>
            <div className="tableWrap">
                <div className="ag-theme-alpine" style={{ width: "100%", height: "530px" }}>
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

            <Modal open={modalOpen} close={closeModal} header="공영주차장 현황">
                <ContentModal mode={proc.mode} data={pbl} close={closeModal} sgg={sgg} />
            </Modal>
        </div>
    );
}
