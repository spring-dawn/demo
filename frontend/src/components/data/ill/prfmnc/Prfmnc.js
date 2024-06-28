import React, { useEffect, useRef, useState } from "react";
import CrdnPrfmncTable from "./CrdnPrfmncTable";
import CrdnNocsTable from "./CrdnNocsTable";
import { useCodeTree } from "../../../../CommonHook";
import * as FileSaver from "file-saver";
import * as XLSX from "xlsx";
import axios from "axios";

function Prfmnc() {
    const [type, setType] = useState({ name: "총괄", type: "prfmnc" });
    const [menu, setMenu] = useState(1);

    // 지역 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    // 그리드용 매핑 배열
    const sggMap = sgg.reduce((result, item) => {
        result[item.name] = item.value;
        return result;
    }, {});

    const crdnPrfmncTabList = [
        { name: "총괄", type: "prfmnc" },
        { name: "고정식", type: "prfmnc" },
        { name: "이동식", type: "prfmnc" },
        { name: "인력단속", type: "prfmnc" },
        { name: "버스탑재형", type: "prfmnc" },
        { name: "안전신문고", type: "prfmnc" },
    ];

    const crdnNocsTabList = [
        { name: "단속건수", type: "nocs" },
        { name: "견인건수", type: "nocs" },
    ];

    useEffect(() => {
        if (menu === 1) {
            setType({ name: "총괄", type: "prfmnc" });
        } else if (menu === 2) {
            setType({ name: "단속건수", type: "nocs" });
        }
    }, [menu]);

    const tmpExcelDownload = () => {
        // 통합 데이터 호출
        let prfmnc;
        let nocs;

        const header1 = {
            year: "연도",
            month: "월",
            sgg: "구군",
            gubun: "유형",
            crdnNocs: "단속건수",
            levyAmt: "부과금액",
            clctnNocs: "징수건수",
            clctnAmt: "징수금액",
            clctnRate: "징수율",
            crdnNope: "인력단속(명)",
        };

        const header2 = {
            year: "연도",
            month: "월",
            sgg: "구군",
            gubun: "유형",
            crdnCar: "승용",
            crdnVan: "승합",
            crdnTruck: "화물",
            sum: "소계",
            crdnEtc: "기타",
            amt: "금액(천원)",
        };

        axios.get("/api/data/illegal/prfmnc").then((res1) => {
            prfmnc = res1.data.sort((a, b) => a.sgg - b.sgg);

            axios.get("/api/data/illegal/nocs").then((res2) => {
                nocs = res2.data.sort((a, b) => a.sgg - b.sgg);

                // 데이터 가공: 구군코드 -> 구군명, 징수율 계산
                const data1 = prfmnc.map((data) => {
                    const sggNm = sggMap[data.sgg];
                    const rate = data.levyAmt != 0 ? ((data.clctnAmt / data.levyAmt) * 100).toFixed(1) + "%" : 0.0;
                    return {
                        ...data,
                        sgg: sggNm,
                        clctnRate: rate,
                    };
                });

                const data2 = nocs.map((data) => {
                    const sggNm = sggMap[data.sgg];
                    return {
                        ...data,
                        sgg: sggNm,
                    };
                });

                // 엑셀 생성: 시트 생성 -> 데이터 복사 -> WB 에 통합 -> 파일 세이버로 출력
                const ws1 = XLSX.utils.aoa_to_sheet([Object.values(header1)]);
                XLSX.utils.sheet_add_json(ws1, data1, { skipHeader: true, origin: -1 });

                const ws2 = XLSX.utils.aoa_to_sheet([Object.values(header2)]);
                XLSX.utils.sheet_add_json(ws2, data2, { skipHeader: true, origin: -1 });
                // 헤더 필터 적용
                ws1["!autofilter"] = { ref: "A1:J1" };
                ws2["!autofilter"] = { ref: "A1:J1" };

                // 통합
                const wb = XLSX.utils.book_new();
                XLSX.utils.book_append_sheet(wb, ws1, "불법주정차 단속실적");
                XLSX.utils.book_append_sheet(wb, ws2, "차종별 단속건수 및 견인건수");

                // 파일 출력
                const excelButter = XLSX.write(wb, { bookType: "xlsx", type: "array" });
                const excelFile = new Blob([excelButter], {
                    type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                });
                FileSaver.saveAs(excelFile, "실적 현황 조회.xlsx");
            });
        });
    };

    return (
        <div id={"prfmnc"} className={"pageWrap"}>
            {/* [240416] 엑셀 다운로드 버튼 위치 심각한데 개별 테이블에 접근할 수 없어 일단 맨위로 올림 */}
            <div className="btnWrap flxRit">
                <button className="btn btn_excel" onClick={tmpExcelDownload}>
                    Excel
                </button>
            </div>
            <div className="tab_wrap">
                <ul className="tab">
                    <li
                        onClick={() => {
                            setMenu(1);
                        }}
                        className={`${menu === 1 ? "on" : ""}`}
                    >
                        불법주정차 단속실적
                    </li>
                    <li
                        onClick={() => {
                            setMenu(2);
                        }}
                        className={`${menu === 2 ? "on" : ""}`}
                    >
                        차종별 단속건수 및 견인건수
                    </li>
                </ul>
                {menu === 1 && (
                    <ul className="tab">
                        {crdnPrfmncTabList.map((ele) => {
                            return (
                                <li
                                    key={ele.name}
                                    className={`${ele.name === type.name ? "on" : ""}`}
                                    onClick={() => setType(ele)}
                                >
                                    {ele.name}
                                </li>
                            );
                        })}
                    </ul>
                )}
                {menu === 2 && (
                    <ul className="tab">
                        {crdnNocsTabList.map((ele) => {
                            return (
                                <li
                                    key={ele.name}
                                    className={`${ele.name === type.name ? "on" : ""}`}
                                    onClick={() => setType(ele)}
                                >
                                    {ele.name}
                                </li>
                            );
                        })}
                    </ul>
                )}
            </div>
            <div className="table_wrap">
                {type.type == "prfmnc" && <CrdnPrfmncTable type={type} />}
                {type.type == "nocs" && <CrdnNocsTable type={type} />}
            </div>
        </div>
    );
}

export default Prfmnc;
