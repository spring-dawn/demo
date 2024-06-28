import React, { useState, useEffect, useCallback } from "react";
import { useCodeTree } from "../../CommonHook";
import AnalyChart from "./AnalyChart";
import Table1 from "./table/Table1";
import { blockParseCodeToName, cardDataGroupSum, gugunParseCodeToName } from "../../CommonFunction";
import { InputNumber, Button, Select as AntSelect } from "antd";

function Analy(props) {
    // 원본 데이터
    const [originData, setOriginData] = useState([]);

    // 필터링 및 계산된 데이터
    const [filterData, setFilterData] = useState([]);

    // 선택된 구군
    const [selectGugun, setSelectGugun] = useState("31110");

    const [selectDay, setSelectDay] = useState("주간");

    // 선택된 동
    const [selectDong, setSelectDong] = useState(null);

    // 현재 동 LIST
    const [dongList, setDongList] = useState([]);

    // 차트 종류 선택
    const [selectChart, setSelectChart] = useState("1");

    const [clickTable, setClickTable] = useState([]);

    const [custom, setCustom] = useState({});

    const [sendCustom, setSendCustom] = useState({ xMin: 0, xAvg: 0, xMax: 0, yMin: 0, yAvg: 0, yMax: 0 });

    // 지역 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    const dataFilterFn = (originData, key) => {
        const filterMap = {};

        originData.forEach((item) => {
            const keyData = item[key];

            if (!filterMap[keyData]) {
                filterMap[keyData] = [];
            }

            filterMap[keyData].push(item);
        });

        Object.keys(filterMap).forEach((key) => {
            const dataStore = cardDataGroupSum(filterMap[key]);

            filterMap[key] = dataStore;
            filterMap[key].key = key;
        });

        return filterMap;
    };

    // 최초 데이터 호출
    useEffect(() => {
        fetch(`/api/data/rsch/mngCard/search?year=2021&sggNm=${gugunParseCodeToName(selectGugun)}`)
            .then((response) => response.json())
            .then((apiData) => {
                if (selectGugun == "31140") {
                    apiData = apiData.map((ele) => {
                        ele.block = blockParseCodeToName(ele.block);

                        return ele;
                    });
                }

                console.log(apiData);

                setOriginData(apiData);
            });
    }, [selectGugun]);

    useEffect(() => {
        // 행정동값이 없을경우(동별 차트)
        if (!selectDong) {
            const filterMap = dataFilterFn(
                originData
                    .filter((ele) => ele.sggNm == gugunParseCodeToName(selectGugun))
                    .filter((ele) => ele.dayNight == selectDay),
                "hjDong"
            );

            // 현재 동 list 저장
            setDongList(Object.keys(filterMap));

            // 현재 계산된 데이터 저장
            setFilterData(
                Object.values(filterMap).map((ele, idx) => {
                    ele.seq = idx + 1;

                    return ele;
                })
            );
        } else {
            let filterMap;
            if (selectDong === "전체 (블럭별)") {
                filterMap = dataFilterFn(
                    originData
                        .filter((ele) => ele.sggNm == gugunParseCodeToName(selectGugun))
                        .filter((ele) => ele.dayNight == selectDay),
                    "block"
                );
            } else {
                filterMap = dataFilterFn(
                    originData
                        .filter((ele) => ele.sggNm == gugunParseCodeToName(selectGugun))
                        .filter((ele) => ele.hjDong == selectDong)
                        .filter((ele) => ele.dayNight == selectDay),
                    "block"
                );
            }
            setFilterData(
                Object.values(filterMap).map((ele, idx) => {
                    ele.seq = idx + 1;

                    return ele;
                })
            );
        }
    }, [originData, selectGugun, selectDong, selectDay]);

    useEffect(() => {
        const indiv = {};

        if (selectChart == "2") {
            indiv.xMin = 0;
            indiv.xAvg = 50;
            indiv.xMax = 100;
            indiv.yMin = 0;
            if (!selectDong) {
                indiv.yAvg = 10000;
                indiv.yMax = 20000;
            } else {
                indiv.yAvg = 500;
                indiv.yMax = 1000;
            }
        } else {
            indiv.xMin = 0;
            indiv.xAvg = 150;
            indiv.xMax = 300;
            indiv.yMin = 0;
            indiv.yAvg = 50;
            indiv.yMax = 100;
        }

        setCustom(indiv);
        setSendCustom(indiv);
    }, [selectChart, selectDong]);

    const handleClick = (params) => {
        let tmp = [...clickTable];
        const selectGubun = params.data.key;
        const beforeGubun = tmp.find((ele) => ele == selectGubun);

        if (beforeGubun) {
            tmp = tmp.filter((ele) => ele != selectGubun);
        } else {
            tmp = [...tmp, selectGubun];
        }

        setClickTable(tmp);
    };

    return (
        <main id="analyPage">
            <div className="analy_wrap">
                <div className="tab_wrap">
                    {/* 차트 종류 선택하는 탭 */}
                    <ul className="select_tab">
                        <li
                            className={`${selectChart === "1" ? "on" : ""}`}
                            onClick={() => {
                                setSelectChart("1");
                            }}
                            style={{ fontSize: "17px" }}
                        >
                            주차 부족
                        </li>
                        <li
                            className={`${selectChart === "2" ? "on" : ""}`}
                            onClick={() => {
                                setSelectChart("2");
                            }}
                            style={{ fontSize: "17px" }}
                        >
                            주차 효율
                        </li>
                    </ul>

                    {/* 구군 선택하는 탭 */}
                    <ul className="select_tab">
                        {sgg.map((ele) => {
                            return (
                                <li
                                    className={`${ele.name == selectGugun ? "on" : ""}`}
                                    onClick={() => {
                                        setSelectGugun(ele.name);
                                        setSelectDong(null);
                                    }}
                                    key={ele.name}
                                >
                                    {ele.value}
                                </li>
                            );
                        })}
                    </ul>

                    {/* 주/야 선택하는 탭 */}
                    <ul className="select_tab">
                        <li
                            className={`${selectDay === "주간" ? "on" : ""}`}
                            onClick={() => {
                                setSelectDay("주간");
                            }}
                            style={{ fontSize: "17px" }}
                        >
                            주간
                        </li>
                        <li
                            className={`${selectDay === "야간" ? "on" : ""}`}
                            onClick={() => {
                                setSelectDay("야간");
                            }}
                            style={{ fontSize: "17px" }}
                        >
                            야간
                        </li>
                    </ul>

                    {/* 동 선택하는 탭 */}
                    <ul className="select_tab">
                        <AntSelect
                            style={{ width: "100%", borderRadius: "10px" }}
                            options={[
                                { value: "전체 (동별)", label: "전체 (동별)" },
                                { value: "전체 (블럭별)", label: "전체 (블럭별)" },
                                ...dongList.map((dong) => ({ value: dong, label: dong })),
                            ]}
                            value={selectDong || "전체 (동별)"}
                            onChange={(value) => {
                                setSelectDong(value == "전체 (동별)" ? null : value);
                            }}
                        />
                    </ul>

                    <div className="input_wrap">
                        <div className={"input"}>
                            <label>X-MIN</label>
                            <InputNumber
                                value={custom.xMin}
                                onChange={(value) => setCustom({ ...custom, xMin: value || 0 })}
                            />
                        </div>
                        <div className={"input"}>
                            <label>X-AVG</label>
                            <InputNumber
                                value={custom.xAvg}
                                onChange={(value) => setCustom({ ...custom, xAvg: value || 0 })}
                            />
                        </div>
                        <div className={"input"}>
                            <label>X-MAX</label>
                            <InputNumber
                                value={custom.xMax}
                                onChange={(value) => setCustom({ ...custom, xMax: value || 0 })}
                            />
                        </div>
                        <div className={"input"}>
                            <label>Y-MIN</label>
                            <InputNumber
                                value={custom.yMin}
                                onChange={(value) => setCustom({ ...custom, yMin: value || 0 })}
                            />
                        </div>
                        <div className={"input"}>
                            <label>Y-AVG</label>
                            <InputNumber
                                value={custom.yAvg}
                                onChange={(value) => setCustom({ ...custom, yAvg: value || 0 })}
                            />
                        </div>
                        <div className={"input"}>
                            <label>Y-MAX</label>
                            <InputNumber
                                value={custom.yMax}
                                onChange={(value) => setCustom({ ...custom, yMax: value || 0 })}
                            />
                        </div>
                        <Button type="primary" onClick={() => setSendCustom({ ...custom })}>
                            실 행
                        </Button>
                    </div>
                </div>
                <section className={"section1"}>
                    <div className="chart_wrap">
                        <AnalyChart
                            data={filterData.map((ele) => {
                                if (selectChart == 1) {
                                    return { key: ele.key, xData: ele.PK1, yData: ele.PK3 };
                                } else {
                                    return { key: ele.key, xData: ele.PK7, yData: ele.PK9 };
                                }
                            })}
                            selectGugun={selectGugun}
                            selectDay={selectDay}
                            selectDong={selectDong}
                            setClickTable={setClickTable}
                            xName={selectChart == 1 ? "주차장 확보율" : "불법 주차율"}
                            yName={selectChart == 1 ? "주차장 이용률" : "유휴 주차공간"}
                            selectChart={selectChart}
                            sendCustom={sendCustom}
                            clickTable={clickTable}
                        />
                    </div>
                </section>
            </div>

            <section className={"section2"}>
                <Table1
                    gugun={selectGugun}
                    dong={selectDong}
                    data={filterData}
                    handleClick={handleClick}
                    clickTable={clickTable}
                />
            </section>
        </main>
    );
}

export default Analy;
