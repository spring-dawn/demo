import { React, useEffect, useState, useCallback } from "react";
import { AgGridReact } from "ag-grid-react";
import axios from "axios";
import ContentSearch from "./ContentSearch";
import ContentModal from "./ContentModal";
import Modal from "../../../../common/Modal";
import { useCodeTree } from "../../../../../CommonHook";
import { useRecoilState } from "recoil";
import { SearchState } from "../../../../../Context";
export default function PResi() {
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
        setResi({});
    };
    const closeModal = () => {
        setModalOpen(false);
    };

    // Example cell Click event
    const [resi, setResi] = useState();
    const cellClickedListener = useCallback((evt) => {
        setResi(evt.data);
        setProc({ task: "proc", mode: "update" });
        setModalOpen(true);
    }, []);

    // TODO: 소계 등의 합계는 서치 키워드가 들어갔을 때 화면에 바로 보여준다면?
    // column setting
    const onGridReady = useCallback((params) => {
        params.api.sizeColumnsToFit();
    }, []);

    const defaultColDef = {
        resizable: true,
        autoHeight: true,
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
        { field: "prevSpaces", headerName: "전월 주차면수" },
        {
            headerName: "변동 추이",
            marryChildren: true,
            children: [
                { field: "variance", headerName: "증감", columnGroupShow: "close" },
                { field: "newSpaces", headerName: "신규", columnGroupShow: "open" },
                { field: "lostSpaces", headerName: "삭제", columnGroupShow: "open" },
                { field: "reSpaces", headerName: "되살리기", columnGroupShow: "open" },
            ],
        },
        { field: "thisSpaces", headerName: "금월 주차면수" },
        { field: "thisArea", headerName: "금월 면적(㎡)" },
        // { field: "varianceReason", headerName: "증감 사유" },
        { field: "nonUse", headerName: "미사용면수" },
        { field: "inUse", headerName: "사용가능면수" },
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
                <div className="ag-theme-alpine" style={{ width: "100%", height: "512px" }}>
                    <AgGridReact
                        // groupHeaderHeight={75}
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

            <Modal open={modalOpen} close={closeModal} header="거주자우선 현황">
                <ContentModal mode={proc.mode} data={resi} close={closeModal} sgg={sgg} />
            </Modal>
        </div>
    );
}
