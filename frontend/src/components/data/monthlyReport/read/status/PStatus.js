import { React, useEffect, useState, useCallback } from "react";
import { AgGridReact } from "ag-grid-react";
import axios from "axios";
import ContentSearch from "./ContentSearch";
import { useRecoilState } from "recoil";
import { SearchState } from "../../../../../Context";
import Grid from "./ContentGrid";
import GridSggTotal from "./ContentSggTotGrid";
import { useCodeTree } from "../../../../../CommonHook";

export default function PStatus() {
    const [render, setRender] = useState(false);
    const [data, setData] = useState();
    // 구군 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    // 그리드용 매핑 배열
    const sggMap = sgg.reduce((result, item) => {
        result[item.name] = item.value;
        return result;
    }, {});

    // 데이터 표 선택
    const checkSggTotalGrid = () => {
        if (
            search.sggCd == "" ||
            search.sggCd == undefined ||
            search.month == "" ||
            search.month == undefined ||
            search.year == "" ||
            search.year == undefined
        ) {
            return true;
        } else {
            return false;
        }
    };

    // Example cell Click event
    // 이 경우 등록 버튼 토글.
    const [proc, setProc] = useState({ task: "proc", mode: "off" });
    const writeOnOff = (e) => {
        // 읽기-쓰기 스위치
        proc.mode == "on" ? setProc({ task: "proc", mode: "off" }) : setProc({ task: "proc", mode: "on" });
        //
    };

    const [formData, setFormData] = useState();
    const insert = (e) => {
        e.preventDefault();

        axios.post("/api/data/mr/status", formData.status, {
            withCredentials: true,
            headers: {
                "Content-Type": "application/json",
            },
        });

        setRender((prevState) => !prevState);

        // setProc({ task: "proc", mode: "off" });
    };

    const [search, setSearch] = useRecoilState(SearchState);
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

    // 업로드용 양식 다운로드
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
        <div className="pageWrap p_status">
            {/* 서치: 연도, 월, 구군 */}
            <ContentSearch sgg={sgg} setData={setData} render={render} />
            {/* 버튼 유닛 */}
            <div className="btnWrap flxRit">
                {!checkSggTotalGrid() ? (
                    <div className="input_update">
                        <button className="btn btn_save" onClick={insert}>
                            저장
                        </button>
                    </div>
                ) : (
                    <div></div>
                )}
                <div className="excel_update">
                    <button className="btn btn_excel" onClick={excelDownload}>
                        통합 월간보고 데이터 다운로드
                    </button>
                    <button className="btn btn_excel" onClick={excelFormDownload}>
                        통합 월간보고 양식 다운로드
                    </button>
                </div>
            </div>
            {checkSggTotalGrid() ? (
                <GridSggTotal mode={proc.mode} data={data} />
            ) : (
                <Grid mode={proc.mode} data={data} setFormData={setFormData} />
            )}
        </div>
    );
}
