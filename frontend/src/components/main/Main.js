import React, { useState, useEffect, useContext } from "react";
import moment from "moment";
import UlsanSvg from "../common/UlsanSvg";
import PkStatusTable from "./table/PkStatusTable";
import PkSecurementRate from "./table/PkSecurementRate";
import PkIllegal from "./table/PkIllegal";
import PkChangeStatusTable from "./table/PkChangeStatusTable";
import MainChart1 from "./chart/MainChart1";
import MainChart2 from "./chart/MainChart2";
import { useCodeTree } from "../../CommonHook";
import axios from "axios";
import { Tooltip, Button } from "antd";
import Slider from "react-slick";
import { gugunParseCodeToName } from "../../CommonFunction";

function Main(props) {
    // 선택된 구군
    const [selectGugun, setSelectGugun] = useState("all");

    // 월간보고현황 데이터
    const [monthDataList, setMonthDataList] = useState([]);
    const monthDataYear = monthDataList.length && monthDataList[0].year ? monthDataList[0].year : 0;
    const monthDataMonth = monthDataList.length && monthDataList[0].month ? monthDataList[0].month : 0;
    const baseDateFormat = moment(monthDataYear + monthDataMonth, "YYYYMM").format("YYYY/MM");

    // 차량등록대수
    const [car, setCar] = useState([]);

    // 인구수
    const [pop, setPop] = useState([]);

    // 세대수
    const [saedae, setSaedae] = useState([]);

    // 불법 주정차 단속현황
    const [ilegal, setIlegal] = useState([]);

    // 버스탑재형
    const [bus, setBus] = useState([]);

    // 소화전주변
    const [fire, setFire] = useState([]);

    // 어린이보호구역
    const [protect, setProtect] = useState([]);

    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    // 인구수, 세대수 데이터 가져오는 코드
    const getPop = () => {
        fetch("/api/main/kosis/pop")
            .then((response) => response.json())
            .then((apiData) => {
                const filteredData = apiData.filter((item) => item.type === "총인구");
                setPop(filteredData);

                const filtered2Data = apiData.filter((item) => item.type === "세대수");
                setSaedae(filtered2Data);
            });
    };

    // 버스탑재형, 소화전주변, 어린이보호구역 데이터 가져오는 코드
    const getIllMainData = () => {
        fetch("/api/main/ill")
            .then((response) => response.json())
            .then((apiData) => {
                // 버스탑재형
                const busData = apiData.bus;
                setBus(busData);
                // 소화전주변
                const fireData = apiData.fireplug;
                setFire(fireData);
                // 어린이보호구역
                const protectData = apiData.protectedArea;
                setProtect(protectData);

                // 단속건수 현황
                const nocsData = apiData.nocs;
                setIlegal(nocsData);
            });
    };

    // 차량등록대수 데이터 가져오는 코드
    const getKosisCar = () => {
        fetch("/api/main/kosis/car")
            .then((response) => response.json())
            .then((apiData) => {
                setCar(apiData);
            });
    };

    // 최신 월간데이터 시군구별 가져오기 2
    const getMonthData2 = () => {
        axios.get(`/api/data/mr/status/total-range?range=12`).then((res) => {
            setMonthDataList(res.data);
        });
    };

    const filterLastMonthData = (sgg, type) => {
        if (!monthDataList.length) return {};

        const lastData = monthDataList[0];

        if (sgg == "all") {
            const filter = Object.keys(lastData)
                .filter((key) => lastData[key][type])
                .map((key) => lastData[key][type]);

            const sum = {};

            filter.forEach((obj) => {
                Object.entries(obj).forEach(([key, value]) => {
                    if (key == "sggCd" || key == "year" || key == "month" || key == "localDt" || key == "createDtm")
                        return;

                    if (sum[key]) {
                        sum[key] += value;
                    } else {
                        sum[key] = value;
                    }
                });
            });

            sum.month = lastData.month;
            sum.year = lastData.year;

            return sum;
        } else {
            const selectData = lastData[sgg][type];

            if (selectData) {
                return { month: lastData.month, year: lastData.year, ...selectData };
            } else {
                return {};
            }
        }
    };

    const filterLastCarData = (sgg) => {
        if (!car.length) return [];

        const filterValue = sgg == "all" ? "계" : gugunParseCodeToName(sgg);
        const filter = car.filter((ele) => ele.sgg == filterValue);

        return filter[0];
    };

    const filterStatusChartData = (sgg) => {
        if (!car.length || !monthDataList.length) return [];

        // { month: "23.12", spcsArea: 731849, spcsCount: 599266 },

        const dateList = monthDataList.map((ele) => {
            const baseDate = moment(ele.year + ele.month, "YYYYMM").format("YY.MM");
            let spcsArea = 0;
            let spcsCount = 0;
            let rate = 0;

            // 관련 차량등록대수 데이터 찾기
            const findCarData = car.find((ele) => {
                const carDate = moment(ele.date, "YYYYMM").format("YY.MM");
                const filterKey = sgg == "all" ? "계" : gugunParseCodeToName(sgg);

                return ele.sgg == filterKey && carDate == baseDate;
            });

            if (findCarData) {
                spcsCount = Number(findCarData.cnt);
            }

            // 월간 데이터 찾기
            if (sgg == "all") {
                Object.keys(ele).forEach((key) => {
                    spcsArea += ele[key].thisMonth?.TOTAL_S_SUM || 0;
                });
            } else {
                spcsArea = ele[sgg].thisMonth?.TOTAL_S_SUM || 0;
            }

            // 주차장 확보율
            if (spcsArea && spcsCount) {
                rate = ((spcsArea / spcsCount) * 100).toFixed(1);
            }

            spcsArea = spcsArea == 0 ? null : spcsArea;
            spcsCount = spcsCount == 0 ? null : spcsCount;
            rate = rate == 0 ? null : rate;

            return { baseDate, spcsArea, spcsCount, rate };
        });

        return dateList.reverse();
    };

    // 지도 밑 슬라이더
    const createSvgSlider = () => {
        const settings = {
            dots: false,
            infinite: true,
            speed: 500,
            slidesToShow: 2,
            slidesToScroll: 1,
        };

        const popVal =
            selectGugun === "all"
                ? Number(pop.find((item) => item.sgg === "울산광역시")?.cnt) || 0
                : Number(pop.find((item) => item.sgg === gugunParseCodeToName(selectGugun))?.cnt) || 0;

        const houVal =
            selectGugun === "all"
                ? Number(saedae.find((item) => item.sgg === "울산광역시")?.cnt) || 0
                : Number(saedae.find((item) => item.sgg === gugunParseCodeToName(selectGugun))?.cnt) || 0;

        const carVal = Number(filterLastCarData(selectGugun)?.cnt) || 0;

        const illVal =
            selectGugun === "all"
                ? ilegal.reduce((sum, item) => sum + Number(item.sum), 0)
                : Number(ilegal.find((item) => item.sgg === selectGugun)?.sum) || 0;

        return (
            <Slider {...settings}>
                <div className="slider-box">
                    <Tooltip
                        placement="top"
                        title={
                            <div style={{ color: "black" }}>
                                <b>{"단위 : 명"}</b>
                            </div>
                        }
                        color={"white"}
                    >
                        <p>인구</p>
                    </Tooltip>
                    <p>{popVal.toLocaleString()}</p>
                </div>
                <div className="slider-box">
                    <Tooltip
                        placement="top"
                        title={
                            <div style={{ color: "black" }}>
                                <b>{"단위 : 세대"}</b>
                            </div>
                        }
                        color={"white"}
                    >
                        <p>세대수</p>
                    </Tooltip>
                    <p>{houVal.toLocaleString()}</p>
                </div>
                <div className="slider-box">
                    <Tooltip
                        placement="top"
                        title={
                            <div style={{ color: "black" }}>
                                <b>{"단위 : 대"}</b>
                            </div>
                        }
                        color={"white"}
                    >
                        <p>차량등록대수</p>
                    </Tooltip>
                    <p>{carVal.toLocaleString()}</p>
                </div>
                <div className="slider-box">
                    <Tooltip
                        placement="top"
                        title={
                            <div style={{ color: "black" }}>
                                <b>{"단위 : 건"}</b>
                            </div>
                        }
                        color={"white"}
                    >
                        <p>불법 주·정차 단속 현황</p>
                    </Tooltip>
                    <p>{illVal.toLocaleString()}</p>
                </div>
                <div className="slider-box">
                    <Tooltip
                        placement="top"
                        title={
                            <div style={{ color: "black" }}>
                                <b>{"단위 : 대"}</b>
                            </div>
                        }
                        color={"white"}
                    >
                        <p>인구당 차량등록대수</p>
                    </Tooltip>
                    <p>{(carVal / popVal).toLocaleString()}</p>
                </div>
            </Slider>
        );
    };

    // 메인페이지 init
    useEffect(() => {
        getPop();
        getIllMainData();
        getKosisCar();
        getMonthData2();
    }, []);

    return (
        <main id="mainPage">
            <div className="main_wrap">
                <section className="main_section1">
                    <article className="map_svg">
                        <div className="title_thema">
                            울산광역시 주차 확보 현황{" "}
                            <span style={{ color: "#818181", marginLeft: 8 }}>{`(${baseDateFormat})`}</span>
                        </div>
                        <UlsanSvg
                            gugun={selectGugun}
                            className="ulsan-map"
                            onClick={(evt) => {
                                const gugunTarget = evt.target.closest("g");

                                if (!gugunTarget) {
                                    setSelectGugun("all");
                                } else {
                                    const gugunCode = gugunTarget.dataset.code;

                                    if (selectGugun == gugunCode) {
                                        setSelectGugun("all");
                                    } else {
                                        setSelectGugun(gugunCode);
                                    }
                                }
                            }}
                        />
                        <div className="data_carousel">
                            <div style={{ display: "flex", justifyContent: "space-between" }}>
                                <h5 className="slider-title">
                                    {selectGugun === "all" ? "울산광역시" : gugunParseCodeToName(selectGugun)}
                                </h5>
                                {/* <Button type="primary" className="slider-button">
                                    2023.00월 기준
                                </Button> */}
                            </div>
                            <div className="map_info">
                                <div className="slider-container">{createSvgSlider()}</div>
                            </div>
                        </div>
                    </article>
                </section>
                <section className="main_section2">
                    <article className="pk_statistics2">
                        <h2>주차장 확보 현황</h2>
                        <PkStatusTable gugun={selectGugun} monthData={filterLastMonthData(selectGugun, "thisMonth")} />
                        <h2>주차장 확보율</h2>
                        <PkSecurementRate
                            gugun={selectGugun}
                            monthData={filterLastMonthData(selectGugun, "thisMonth")}
                            carData={filterLastCarData(selectGugun)}
                        />
                        <h2>불법 주·정차 단속 운영 누계</h2>
                        <PkIllegal gugun={selectGugun} bus={bus} fire={fire} protect={protect} />
                        <div className="data_chart">
                            <MainChart1 gugun={selectGugun} data={filterStatusChartData(selectGugun)} />
                        </div>
                    </article>
                    <article className="pk_statistics2">
                        <div className="title_thema">주차장 변경 현황</div>
                        <div className="data_wrap">
                            <PkChangeStatusTable monthData={filterLastMonthData(selectGugun, "status")} />
                            <div className="data_chart2">
                                <MainChart2 monthData={filterLastMonthData(selectGugun, "status")} />
                            </div>
                        </div>
                    </article>
                </section>
            </div>
        </main>
    );
}

export default Main;
