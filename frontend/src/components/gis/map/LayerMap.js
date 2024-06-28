import React, { useState, useEffect, useRef } from "react";
import {
    createObjectLayer,
    createImageLayer,
    removeLayer,
    createEmptyFeatureCollection,
    findLayerList,
    removeOverlay,
    gugunParseCodeToName,
} from "../CommonGisFunction";
import { unByKey } from "ol/Observable";
import ContentLayerMenu from "../side/ContentLayerMenu";
import ContentMenu from "../side/ContentMenu";
import { useInfoOverlay } from "../CommonGisHook";
import LayerTable from "../table/LayerTable";
import ContentCardMenu from "../side/ContentCardMenu";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faRightLeft, faXmark } from "@fortawesome/free-solid-svg-icons";
import Draggable from "react-draggable";

// 실태 조사 GIS 기능
function LayerMap({ mapObj, mapData, setMapData, objectBBOX, setRange, drawOn, blank_year, gisMenuMode, parentSize }) {
    const storedUserInfo = localStorage.getItem("user");
    let userInfo;
    if (storedUserInfo) {
        userInfo = JSON.parse(storedUserInfo);
    }

    /* 공통 */
    const [filterGugun, setFilterGugun] = useState(userInfo && userInfo.agency != "31000" ? userInfo.agency : "31110");
    const [filterDong, setFilterDong] = useState("");

    const legendRef = useRef(); // 실태조사 범례

    /* 관리카드 */
    const [cardData, setCardData] = useState([]); // 관리카드 데이터
    const [selectBlockLayer, setSelectBlockLayer] = useState([]); // 선택된 블럭 레이어
    const [selectBlock, setSelectBlock] = useState([]); // 관리카드 선택 관리
    const [selectBlockTab, setSelectBlockTab] = useState("주간"); // 테이블 데이터 타입 (주, 야간)
    const [simpleTableMode, setSimpleTableMode] = useState(false); // 테이블 심플 모드
    const [viewCardMenu, setViewCardMenu] = useState(false);
    const [cardToggleButton, setCardToggleButton] = useState(true);

    const cardInfoRef = useRef(); // 관리 카드 테이블 dom

    /* 실태조사 */
    const [dataStore, setDataStore] = useState({});
    const [pkData, setPkData] = useState({}); // 필터링 범위의 주차장 면수 데이터 (실태조사, 건축물대장API)
    const { DuplInfoComponent, InfoComponent, setSelectLayer } = useInfoOverlay(mapObj.map, drawOn); // 인포창과 관련된 클릭 이벤트, 데이터, 선택레이어 변경
    const [isNight, setIsNight] = useState(null); // 필터링 범위의 주차장 면수 데이터 (실태조사, 건축물대장API)
    const [layerCheckBox, setLayerCheckBox] = useState([]);
    const [layerToggleButton, setLayerToggleButton] = useState(true);

    // 범례 카드
    const [isLegend, setIsLegend] = useState(true);

    // 주자시설 레이어 생성
    const createPkLayer = (map, isNight, data) => {
        data.forEach((ele) => {
            const subType = ele.shpInfo.subType;
            const featureType = ele.shpInfo.featureType;

            if (subType != "노상") {
                const layer = createObjectLayer(map, ele, "objectLayer", objectBBOX);
                map.addLayer(layer);
            } else {
                const templateMap = {
                    template_resident_p: {
                        template_: createEmptyFeatureCollection(),
                        color: "rgba(240,230,34,1)",
                        featureType: "POINT",
                        type: "주차장",
                        customSubType: "노상 거주자",
                        name: "거주자우선 주차장",
                    },
                    template_resident_m: {
                        template_: createEmptyFeatureCollection(),
                        color: "rgba(240,230,34,1)",
                        featureType: "MULTIPOLYGON",
                        type: "주차장",
                        customSubType: "노상 거주자",
                        name: "거주자우선 주차장",
                    },
                    template_pk_p: {
                        template_: createEmptyFeatureCollection(),
                        color: "rgba(127,196,203,1)",
                        featureType: "POINT",
                        type: "주차장",
                        customSubType: "노상 공영",
                        name: "공영 주차장",
                    },
                    template_pk_m: {
                        template_: createEmptyFeatureCollection(),
                        color: "rgba(127,196,203,1)",
                        featureType: "MULTIPOLYGON",
                        type: "주차장",
                        customSubType: "노상 공영",
                        name: "공영 주차장",
                    },
                };

                ele.layer.features.forEach((feature) => {
                    const name = feature.properties["주차장명"] || "";

                    if (name.includes("거주자")) {
                        if (featureType == "POINT") {
                            templateMap.template_resident_p.template_.features.push(feature);
                        } else {
                            templateMap.template_resident_m.template_.features.push(feature);
                        }
                    } else {
                        if (featureType == "POINT") {
                            templateMap.template_pk_p.template_.features.push(feature);
                        } else {
                            templateMap.template_pk_m.template_.features.push(feature);
                        }
                    }
                });

                Object.values(templateMap).forEach((template) => {
                    const layer = createObjectLayer(
                        map,
                        {
                            layer: template.template_,
                            shpInfo: {
                                featureType: template.featureType,
                                color: template.color,
                                name: template.name,
                                type: template.type,
                                customSubType: template.customSubType,
                                zindex: 100,
                            },
                        },
                        "objectLayer",
                        objectBBOX
                    );
                    map.addLayer(layer);
                });
            }
        });
    };

    // 주자수요 레이어 생성
    const createDmLayer = (map, isNight, data) => {
        const templateMap = {
            template_1: {
                template_: createEmptyFeatureCollection(),
                color: "rgb(77,129,250)",
                canvas: {
                    // 파랑
                    color1: "#1265B0",
                    color2: "#55C2FF",
                },
                name: "주차수요_적법_구획내",
                type: "수요",
                customSubType: "적법 구획내",
                featureType: "POINT",
            },
            template_2: {
                // 초록
                template_: createEmptyFeatureCollection(),
                color: "rgb(96,183,108)",
                canvas: {
                    color1: "#2C7744",
                    color2: "#6EFF62",
                },
                name: "주차수요_적법_구획외",
                type: "수요",
                customSubType: "적법 구획외",
                featureType: "POINT",
            },
            template_3: {
                template_: createEmptyFeatureCollection(),
                color: "rgb(222,84,70)",
                canvas: {
                    color1: "#B02512",
                    color2: "#FF6955",
                },
                name: "주차수요_불법",
                type: "수요",
                customSubType: "불법",
                featureType: "POINT",
            },
        };

        let timeKey = isNight ? "야간" : "주간";

        const timeFilter = (feature) => feature.get("조사시간대") == timeKey;

        data.forEach((ele) => {
            const subType = ele.shpInfo.subType;
            const featureType = ele.shpInfo.featureType;

            if (subType != "노상 수요") {
                const layer = createObjectLayer(map, ele, "objectLayer", objectBBOX, timeFilter);
                map.addLayer(layer);
            } else {
                ele.layer.features.forEach((feature) => {
                    const code = feature.properties["적/불법여부"];

                    if (code == "적법(구획 내)") {
                        templateMap.template_1.template_.features.push(feature);
                    } else if (code == "적법(구획 외)") {
                        templateMap.template_2.template_.features.push(feature);
                    } else if (code == "불법") {
                        templateMap.template_3.template_.features.push(feature);
                    }
                });
            }
        });

        Object.values(templateMap).forEach((template) => {
            if (!template.template_.features.length) return;

            const layer = createObjectLayer(
                map,
                {
                    layer: template.template_,
                    shpInfo: {
                        featureType: template.featureType,
                        color: template.color,
                        name: template.name,
                        type: template.type,
                        customSubType: template.customSubType,
                        canvas: template.canvas,
                        zindex: 100,
                    },
                },
                "objectLayer",
                objectBBOX,
                timeFilter
            );

            map.addLayer(layer);
        });
    };

    // 검색 결과 레이어 생성
    const handleCreateLayer = (map, mapData, objectBBOX, isNight) => {
        removeLayer(map, "objectLayer");
        removeLayer(map, "baseLayer");
        removeOverlay(map, "pk");

        const filterData = Object.keys(mapData)
            .filter((key) => mapData[key].on)
            .map((key) => mapData[key]);

        // 베이스 레이어
        filterData
            .filter(({ shpInfo }) => shpInfo.type == "베이스")
            .forEach((ele) => {
                let layer;

                if (ele.layer) {
                    // 벡터 레이어
                    layer = createObjectLayer(map, ele, "objectLayer", objectBBOX);
                    map.addLayer(layer);
                } else {
                    // 이미지 레이어
                    layer = createImageLayer(map, ele, "baseLayer");
                    map.addLayer(layer);
                }
            });

        // 시설 레이어
        createPkLayer(
            map,
            isNight,
            filterData.filter(({ shpInfo }) => shpInfo.type == "주차장")
        );

        // 수요 레이어
        createDmLayer(
            map,
            isNight,
            filterData.filter(({ shpInfo }) => shpInfo.type == "수요")
        );
    };

    // 테이블 생성
    const createCardTable = (selectBlock) => {
        // PK4 = dataStore.pdRDIn / dataStore.pfRDSum;
        // PK7 = (dataStore.pdRDOut + dataStore.pdRDIll) / dataStore.pdTotal;
        // 주차장
        // "pfRDResi" = 노상 거주자: 노상_거주자 레이어 [주차면수 전체] 합계
        // "pfRDEtc" = 노상 그외: 노상_공영 레이어 [주차면수 전체] 합계
        // "pfRDSum" = 노상 소계: 노상거주자 + 노상 그외
        // "pfOutPub" = 노외 공영: 노외 레이어 [공영/민영 = 공영] [주차면수 전체] 합계
        // "pfOutPri" = 노외 민영: 노외 레이어 [공영/민영 = 민영] [주차면수 전체] 합계
        // "pfOutSum" = 노외 소계: 노외 공영 + 노외 민영
        // "pfSubResi" = 부설 주거: 부설 레이어 [주거/비주거 = 주거] [주차면수 전체] 합계
        // "pfSubNonRegi" = 부설 비주거: 부설 레이어 [주거/비주거 = 비주거] [주차면수 전체] 합계
        // "pfSubSum" = 부설 소계: 부설 주거 + 부설 비주거
        // "pfTotal" = 합계: 노상 소계 + 노외 소계 + 부설 소계
        // 수요
        // "pdRDIn" = 노상 구획내: 적법 구획내 레이어 feature size
        // "pdRDOut" = 노상 구획외: 적법 구획외 레이어 feature size
        // "pdRDIll" = 노상 불법: 불법 레이어 feature size
        // "pdRDSum" = 노상 소계: 노상 구획내 + 노상 구획외 + 노상 불법
        // "pdOutPub = 노외 공영:
        // "pdOutPri = 노외 민영:
        // "pdOutSum" = 노외 소계: 노외 수요 레이어 feature size
        // "pdSubResi = 부설 주거:
        // "pdSubNonRegi = 부설 비주거:
        // "pdSubSum" = 부설 소계: 부설 수요 구획외 레이어 feature size
        // "pdTotal" = 합계: 노상 소계 + 노외 소계 + 부설 소계
        // PK1 = 주차장 확보율
        // PK2 = 주차장 과부족(대)
        // PK3 = 주차장 이용률 전체
        // PK4 = 주차장 이용률 노상
        // PK5 = 주차장 이용률 노외
        // PK6 = 주차장 이용률 부설
        // PK7 = 불법 주차율 전체수요 대비
        // PK8 = 불법 주차율 노상수요 대비
        // PK9 = 유휴 부설주차규모 전체

        const colList = [
            "pop",
            "households",
            "vehicleCnt",
            "emptyLands",
            "emptyArea",
            "pfRDResi",
            "pfOutPub",
            "pfSubResi",
            "pfRDEtc",
            "pfOutPri",
            "pfSubNonRegi",
            "pfRDSum",
            "pfOutSum",
            "pfSubSum",
            "pfTotal",
            "pdRDIn",
            "pdOutPub",
            "pdSubResi",
            "pdRDOut",
            "pdRDIll",
            "pdOutPri",
            "pdSubNonRegi",
            "pdRDSum",
            "pdOutSum",
            "pdSubSum",
            "pdTotal",
        ];

        // 기본 테이블 데이터
        const blockData = selectBlock.filter((cardBlock) => {
            if (
                selectBlock.find(
                    (selectBlock) => cardBlock.block == selectBlock.block && cardBlock.dayNight == selectBlockTab
                )
            ) {
                return true;
            } else {
                return false;
            }
        });
        const dataStore = {};

        colList.forEach((key) => {
            dataStore[key] = blockData.reduce((accumulator, item) => accumulator + parseInt(item[key]), 0);
        });

        // 복수 표현이 필요한 데이터
        const blockName = new Set(blockData.map((ele) => ele.block));
        const hjDongName = new Set(blockData.map((ele) => ele.hjDong));
        const landUsageName = new Set(blockData.map((ele) => ele.landUsage));

        // 확보율, 이룔율, 불법 주차율 등 추가 계산이 필요한 데이터
        let PK1 = 0,
            PK2 = 0,
            PK3 = 0,
            PK4 = 0,
            PK5 = 0,
            PK6 = 0,
            PK7 = 0,
            PK8 = 0,
            PK9 = 0,
            PK10 = 0,
            PK11 = 0;

        blockData.forEach((ele) => {
            let pfTotal = parseInt(ele["pfTotal"]);
            let pdTotal = parseInt(ele["pdTotal"]);
            let pdRDIn = parseInt(ele["pdRDIn"]);
            let pdOutSum = parseInt(ele["pdOutSum"]);
            let pdSubSum = parseInt(ele["pdSubSum"]);
            let pfRDSum = parseInt(ele["pfRDSum"]);
            let pfOutSum = parseInt(ele["pfOutSum"]);
            let pfSubSum = parseInt(ele["pfSubSum"]);
            let pdRDIll = parseInt(ele["pdRDIll"]);
            let pdRDSum = parseInt(ele["pdRDSum"]);
            let pfSubResi = parseInt(ele["pfSubResi"]);
            let pdSubResi = parseInt(ele["pdSubResi"]);
            let pfSubNonRegi = parseInt(ele["pfSubNonRegi"]);
            let pdSubNonRegi = parseInt(ele["pdSubNonRegi"]);

            let tmpPK2 = pfTotal - pdTotal;
            let tmpPK9 = pfSubSum - pdSubSum;
            let tmpPK10 = pfSubResi - pdSubResi;
            let tmpPK11 = pfSubNonRegi - pdSubNonRegi;

            PK9 += isFinite(tmpPK9) ? tmpPK9 : 0;
            PK10 += isFinite(tmpPK10) ? tmpPK10 : 0;
            PK11 += isFinite(tmpPK11) ? tmpPK11 : 0;
            PK2 += isFinite(tmpPK2) ? tmpPK2 : 0;
        });

        PK1 = dataStore.pfTotal / dataStore.pdTotal;
        PK3 = (dataStore.pdRDIn + dataStore.pdOutSum + dataStore.pdSubSum) / dataStore.pfTotal;
        PK4 = dataStore.pdRDIn / dataStore.pfRDSum;
        PK5 = dataStore.pdOutSum / dataStore.pfOutSum;
        PK6 = dataStore.pdSubSum / dataStore.pfSubSum;
        PK7 = (dataStore.pdRDOut + dataStore.pdRDIll) / dataStore.pdTotal;
        PK8 = dataStore.pdRDIll / dataStore.pdRDSum;

        PK1 = isFinite(PK1) ? PK1 : 0;
        PK3 = isFinite(PK3) ? PK3 : 0;
        PK4 = isFinite(PK4) ? PK4 : 0;
        PK5 = isFinite(PK5) ? PK5 : 0;
        PK6 = isFinite(PK6) ? PK6 : 0;
        PK7 = isFinite(PK7) ? PK7 : 0;
        PK8 = isFinite(PK8) ? PK8 : 0;

        PK1 = (PK1 * 100).toFixed(1);
        PK3 = (PK3 * 100).toFixed(1);
        PK4 = (PK4 * 100).toFixed(1);
        PK5 = (PK5 * 100).toFixed(1);
        PK6 = (PK6 * 100).toFixed(1);
        PK7 = (PK7 * 100).toFixed(1);
        PK8 = (PK8 * 100).toFixed(1);

        if (simpleTableMode) {
            return (
                <table className={"data_table simple"}>
                    <tbody>
                        <tr className="col_group1">
                            <td colSpan={2} className="header1">
                                인구
                            </td>
                            <td colSpan={2}>{dataStore["pop"]}</td>
                            <td colSpan={2} className="header1">
                                차량
                            </td>
                            <td colSpan={2}>{dataStore["vehicleCnt"]}</td>
                        </tr>

                        <tr className="col_group2">
                            <td colSpan={2} className="header1">
                                주차(노상)
                            </td>
                            <td colSpan={2} className="header1">
                                주차(노외)
                            </td>
                            <td colSpan={2} className="header1">
                                주차(부설)
                            </td>
                            <td colSpan={2} className="header1">
                                주차 합계
                            </td>
                        </tr>
                        <tr className="col_group2">
                            <td colSpan={1} className="header2">
                                소계
                            </td>
                            <td colSpan={1}>{dataStore["pfRDSum"]}</td>
                            <td colSpan={1} className="header2">
                                소계
                            </td>
                            <td colSpan={1}>{dataStore["pfOutSum"]}</td>
                            <td colSpan={1} className="header2">
                                소계
                            </td>
                            <td colSpan={1}>{dataStore["pfSubSum"]}</td>
                            <td colSpan={2}>{dataStore["pfTotal"]}</td>
                        </tr>

                        <tr className="col_group3">
                            <td colSpan={2} className="header1">
                                수요(노상)
                            </td>
                            <td colSpan={2} className="header1">
                                수요(노외)
                            </td>
                            <td colSpan={2} className="header1">
                                수요(부설)
                            </td>
                            <td colSpan={2} className="header1">
                                수요 합계
                            </td>
                        </tr>
                        <tr className="col_group3">
                            <td colSpan={1} className="header2">
                                소계
                            </td>
                            <td colSpan={1}>{dataStore["pdRDSum"]}</td>
                            <td colSpan={1} className="header2">
                                소계
                            </td>
                            <td colSpan={1}>{dataStore["pdOutSum"]}</td>
                            <td colSpan={1} className="header2">
                                소계
                            </td>
                            <td colSpan={1}>{dataStore["pdSubSum"]}</td>
                            <td colSpan={2}>{dataStore["pdTotal"]}</td>
                        </tr>

                        <tr className="col_group3">
                            <td colSpan={4} className="header1">
                                확보율(%)
                            </td>
                            <td colSpan={4} className="header1">
                                과부족(대)
                            </td>
                        </tr>
                        <tr className="col_group3">
                            <td colSpan={4}>{PK1}%</td>
                            <td colSpan={4}>-{PK2}</td>
                        </tr>
                    </tbody>
                </table>
            );
        } else {
            return (
                <table className={"data_table"}>
                    <tbody>
                        <tr className="col_group1">
                            <td colSpan={4} className="header1">
                                행정동
                            </td>
                            <td colSpan={4} className="header1">
                                블록구분
                            </td>
                        </tr>
                        <tr className="col_group1">
                            <td colSpan={4}>
                                <ul className="block scroll">
                                    {[...hjDongName].map((ele) => {
                                        return <li key={ele}>{ele}</li>;
                                    })}
                                </ul>
                            </td>
                            <td colSpan={4}>
                                <ul className="block scroll">
                                    {[...blockName].map((ele) => {
                                        return <li key={ele}>{ele}</li>;
                                    })}
                                </ul>
                            </td>
                        </tr>
                        <tr className="col_group1">
                            <td colSpan={1} className="header2">
                                인구
                            </td>
                            <td colSpan={1}>{dataStore["pop"]}</td>
                            <td colSpan={1} className="header2">
                                가구수
                            </td>
                            <td colSpan={1}>{dataStore["households"]}</td>
                            <td colSpan={2} className="header2">
                                차량등록대수
                            </td>
                            <td colSpan={2}>{dataStore["vehicleCnt"]}</td>
                        </tr>
                        <tr className="col_group1">
                            <td colSpan={1} className="header2">
                                용도지역
                            </td>
                            <td colSpan={1}>{[...landUsageName].join(",")}</td>
                            <td colSpan={1} className="header2">
                                빈터(공한지) 개소
                            </td>
                            <td colSpan={1}>{dataStore["emptyLands"]}</td>
                            <td colSpan={2} className="header2">
                                빈터(공한지) 면적
                            </td>
                            <td colSpan={2}>{dataStore["emptyArea"]}</td>
                        </tr>
                        <tr className="col_group2">
                            <td colSpan={2} className="header1">
                                주차시설(노상)
                            </td>
                            <td colSpan={2} className="header1">
                                주차시설(노외)
                            </td>
                            <td colSpan={2} className="header1">
                                주차시설(부설)
                            </td>
                            <td colSpan={2} className="header1" rowSpan={3}>
                                주차시설 합계
                            </td>
                        </tr>
                        <tr className="col_group2">
                            <td colSpan={1} className="header2">
                                거주자
                            </td>
                            <td colSpan={1}>{dataStore["pfRDResi"]}</td>
                            <td colSpan={1} className="header2">
                                공영
                            </td>
                            <td colSpan={1}>{dataStore["pfOutPub"]}</td>
                            <td colSpan={1} className="header2">
                                주거
                            </td>
                            <td colSpan={1}>{dataStore["pfSubResi"]}</td>
                        </tr>
                        <tr className="col_group2">
                            <td colSpan={1} className="header2">
                                그외
                            </td>
                            <td colSpan={1}>{dataStore["pfRDEtc"]}</td>
                            <td colSpan={1} className="header2">
                                민영
                            </td>
                            <td colSpan={1}>{dataStore["pfOutPri"]}</td>
                            <td colSpan={1} className="header2">
                                비주거
                            </td>
                            <td colSpan={1}>{dataStore["pfSubNonRegi"]}</td>
                        </tr>
                        <tr className="col_group2">
                            <td colSpan={1} className="header2">
                                소계
                            </td>
                            <td colSpan={1}>{dataStore["pfRDSum"]}</td>
                            <td colSpan={1} className="header2">
                                소계
                            </td>
                            <td colSpan={1}>{dataStore["pfOutSum"]}</td>
                            <td colSpan={1} className="header2">
                                소계
                            </td>
                            <td colSpan={1}>{dataStore["pfSubSum"]}</td>
                            <td colSpan={2}>{dataStore["pfTotal"]}</td>
                        </tr>

                        <tr className="col_group3">
                            <td colSpan={2} className="header1">
                                주사수요(노상)
                            </td>
                            <td colSpan={2} className="header1">
                                주차수요(노외)
                            </td>
                            <td colSpan={2} className="header1">
                                주차수요(부설)
                            </td>
                            <td colSpan={2} rowSpan={4} className="header1">
                                주차수요 합계
                            </td>
                        </tr>
                        <tr className="col_group3">
                            <td colSpan={1} className="header2">
                                구획내
                            </td>
                            <td colSpan={1}>{dataStore["pdRDIn"]}</td>
                            <td colSpan={1} className="header2">
                                공영
                            </td>
                            <td colSpan={1}>{dataStore["pdOutPub"]}</td>
                            <td colSpan={1} className="header2">
                                주거
                            </td>
                            <td colSpan={1}>{dataStore["pdSubResi"]}</td>
                        </tr>
                        <tr className="col_group3">
                            <td colSpan={1} className="header2">
                                구획외
                            </td>
                            <td colSpan={1}>{dataStore["pdRDOut"]}</td>
                            <td colSpan={1} rowSpan={2} className="header2">
                                민영
                            </td>
                            <td colSpan={1} rowSpan={2}>
                                {dataStore["pdOutPri"]}
                            </td>
                            <td colSpan={1} rowSpan={2} className="header2">
                                비주거
                            </td>
                            <td colSpan={1} rowSpan={2}>
                                {dataStore["pdSubNonRegi"]}
                            </td>
                        </tr>
                        <tr className="col_group3">
                            <td className="header2">불법</td>
                            <td>{dataStore["pdRDIll"]}</td>
                        </tr>
                        <tr className="col_group3">
                            <td colSpan={1} className="header2">
                                소계
                            </td>
                            <td colSpan={1}>{dataStore["pdRDSum"]}</td>
                            <td colSpan={1} className="header2">
                                소계
                            </td>
                            <td colSpan={1}>{dataStore["pdOutSum"]}</td>
                            <td colSpan={1} className="header2">
                                소계
                            </td>
                            <td colSpan={1}>{dataStore["pdSubSum"]}</td>
                            <td colSpan={2}>{dataStore["pdTotal"]}</td>
                        </tr>

                        <tr className="col_group3">
                            <td colSpan={4} className="header1">
                                주차장 확보율(%)
                            </td>
                            <td colSpan={4} className="header1">
                                주차장 과부족(대)
                            </td>
                        </tr>
                        <tr className="col_group3">
                            <td colSpan={4}>{PK1}%</td>
                            <td colSpan={4}>-{PK2}</td>
                        </tr>

                        <tr className="col_group3">
                            <td colSpan={2} className="header1">
                                주차장 이용률
                            </td>
                            <td colSpan={2} className="header1">
                                불법 주차율
                            </td>
                            <td colSpan={2} className="header1">
                                유휴 부설주차규모
                            </td>
                            <td colSpan={2} className="header1">
                                개방여력
                            </td>
                        </tr>
                        <tr className="col_group3">
                            <td colSpan={1} className="header2">
                                전체
                            </td>
                            <td colSpan={1}>{PK3}%</td>
                            <td colSpan={1} rowSpan={2} className="header2">
                                전체수요 대비
                            </td>
                            <td colSpan={1} rowSpan={2}>
                                {PK7}%
                            </td>
                            <td colSpan={1} rowSpan={4} className="header2">
                                전체
                            </td>
                            <td colSpan={1} rowSpan={4}>
                                {PK9}
                            </td>
                            <td colSpan={1} rowSpan={2} className="header2">
                                주거
                            </td>
                            <td colSpan={1} rowSpan={2}>
                                {PK10}
                            </td>
                        </tr>
                        <tr className="col_group3">
                            <td colSpan={1} className="header2">
                                노상
                            </td>
                            <td colSpan={1}>{PK4}%</td>
                        </tr>
                        <tr className="col_group3">
                            <td colSpan={1} className="header2">
                                노외
                            </td>
                            <td colSpan={1}>{PK5}%</td>
                            <td colSpan={1} rowSpan={2} className="header2">
                                노상수요 대비
                            </td>
                            <td colSpan={1} rowSpan={2}>
                                {PK8}%
                            </td>
                            <td colSpan={1} rowSpan={2} className="header2">
                                비주거
                            </td>
                            <td colSpan={1} rowSpan={2}>
                                {PK11}
                            </td>
                        </tr>
                        <tr className="col_group3">
                            <td colSpan={1} className="header2">
                                부설
                            </td>
                            <td colSpan={1}>{PK6}%</td>
                        </tr>
                    </tbody>
                </table>
            );
        }
    };

    // 블럭 클릭 이벤트
    const handleCardTabMapClick = (evt, drawOn) => {
        // 기본 초기화
        let resultList = [];

        // 선택된 feature
        mapObj.map.forEachFeatureAtPixel(evt.pixel, function (feature, layer) {
            if (!layer) return;

            if (layer.values_.type == "objectLayer") {
                resultList.push({ feature, layer });
            }
        });

        // 반경 레이어 제외
        resultList = resultList.filter((ele) => ele.layer.values_.type != "rangeLayer");

        if (resultList.length > 1) return;

        // 선택 레이어 => 체크박스 매칭
        let blockList = resultList.map((ele) => {
            return ele.feature.getStyle()?.getText()?.getText();
        });
        let selectBlockNames = Array.from(new Set(selectBlock.map((obj) => obj.block)));
        let removeList = [];

        blockList.forEach((ele) => {
            const find = selectBlockNames.find((ele2) => ele == ele2);

            if (find) {
                removeList.push(ele);
            } else {
                selectBlockNames.push(ele);
            }
        });

        selectBlockNames = selectBlockNames.filter((ele) => {
            const find = removeList.find((ele2) => ele2 == ele);

            return !find;
        });

        const tmpSelectBlock = [];

        selectBlockNames.forEach((ele) => {
            cardData.forEach((ele2) => {
                if (ele == ele2.block) {
                    tmpSelectBlock.push(ele2);
                }
            });
        });

        setSelectBlock(tmpSelectBlock);
    };

    // gis 반응형
    useEffect(() => {
        if (parentSize.width < 1100) {
            setIsLegend(false);
            setLayerToggleButton(false);
            setCardToggleButton(false);
        } else {
            setIsLegend(true);
            setLayerToggleButton(true);
            setCardToggleButton(true);
        }
    }, [parentSize.width]);

    // 레이어 생성
    useEffect(() => {
        if (mapObj.map) {
            handleCreateLayer(mapObj.map, mapData, objectBBOX, isNight);
            setSelectLayer([]);
        }
    }, [mapObj, mapData, objectBBOX, isNight]);

    // 필터링 통계 테이블 생성
    useEffect(() => {
        if (mapObj.map && objectBBOX && isNight != null) {
            async function asyncFn() {
                const objectLayers = findLayerList(mapObj.map, "objectLayer");

                // 주차장
                // "pfRDResi" = 노상 거주자: 노상_거주자 레이어 [주차면수 전체] 합계
                // "pfRDEtc" = 노상 그외: 노상_공영 레이어 [주차면수 전체] 합계
                // "pfRDSum" = 노상 소계: 노상거주자 + 노상 그외
                // "pfOutPub" = 노외 공영: 노외 레이어 [공영/민영 = 공영] [주차면수 전체] 합계
                // "pfOutPri" = 노외 민영: 노외 레이어 [공영/민영 = 민영] [주차면수 전체] 합계
                // "pfOutSum" = 노외 소계: 노외 공영 + 노외 민영
                // "pfSubResi" = 부설 주거: 부설 레이어 [주거/비주거 = 주거] [주차면수 전체] 합계
                // "pfSubNonRegi" = 부설 비주거: 부설 레이어 [주거/비주거 = 비주거] [주차면수 전체] 합계
                // "pfSubSum" = 부설 소계: 부설 주거 + 부설 비주거
                // "pfTotal" = 합계: 노상 소계 + 노외 소계 + 부설 소계

                // 수요
                // "pdRDIn" = 노상 구획내: 적법 구획내 레이어 feature size
                // "pdRDOut" = 노상 구획외: 적법 구획외 레이어 feature size
                // "pdRDIll" = 노상 불법: 불법 레이어 feature size
                // "pdRDSum" = 노상 소계: 노상 구획내 + 노상 구획외 + 노상 불법
                // "pdOutPub = 노외 공영:
                // "pdOutPri = 노외 민영:
                // "pdOutSum" = 노외 소계: 노외 수요 레이어 feature size
                // "pdSubResi = 부설 주거:
                // "pdSubNonRegi = 부설 비주거:
                // "pdSubSum" = 부설 소계: 부설 수요 구획외 레이어 feature size
                // "pdTotal" = 합계: 노상 소계 + 노외 소계 + 부설 소계
                // PK1 = 주차장 확보율
                // PK2 = 주차장 과부족(대)
                // PK3 = 주차장 이용률 전체
                // PK4 = 주차장 이용률 노상
                // PK5 = 주차장 이용률 노외
                // PK6 = 주차장 이용률 부설
                // PK7 = 불법 주차율 전체수요 대비
                // PK8 = 불법 주차율 노상수요 대비
                // PK9 = 유휴 부설주차규모 전체

                const dataStore = {
                    pfRDResi: 0,
                    pfRDEtc: 0,
                    pfRDSum: 0,
                    pfOutPub: 0,
                    pfOutPri: 0,
                    pfOutSum: 0,
                    pfSubResi: 0,
                    pfSubNonRegi: 0,
                    pfSubSum: 0,
                    pfTotal: 0,
                    pdRDIn: 0,
                    pdRDOut: 0,
                    pdRDIll: 0,
                    pdRDSum: 0,
                    pdOutPub: 0,
                    pdOutPri: 0,
                    pdOutSum: 0,
                    pdSubResi: 0,
                    pdSubNonRegi: 0,
                    pdSubSum: 0,
                    pdTotal: 0,
                    PK1: 0,
                    PK2: 0,
                    PK3: 0,
                    PK4: 0,
                    PK5: 0,
                    PK6: 0,
                    PK7: 0,
                    PK8: 0,
                    PK9: 0,
                };

                setDataStore(dataStore);

                const promiseList = objectLayers.map((layer) => {
                    return new Promise((resolve, reject) => {
                        const shpInfo = layer.get("shpInfo");
                        const type = shpInfo.type;
                        const subType = shpInfo.subType;
                        const customSubType = shpInfo.customSubType;
                        const featureType = shpInfo.featureType;
                        const source = layer.getSource();
                        let features = [];

                        features = source.getFeatures();

                        if (featureType == "POINT") {
                            let count = 0;

                            const key = source.on("change", () => {
                                if (count > 0) {
                                    unByKey(key);
                                    return;
                                }

                                const features = source.getFeatures();

                                if (source.getState() === "ready" && features.length) {
                                    count++;

                                    if (customSubType == "노상 거주자") {
                                        features.forEach((feature) => {
                                            feature.get("features").forEach((feature2) => {
                                                const val = feature2.get("주차면수 전체");
                                                dataStore.pfRDResi += parseInt(val);
                                            });
                                        });
                                    } else if (customSubType == "노상 공영") {
                                        features.forEach((feature) => {
                                            feature.get("features").forEach((feature2) => {
                                                const val = feature2.get("주차면수 전체") || 0;
                                                dataStore.pfRDEtc += parseInt(val);
                                            });
                                        });
                                    } else if (subType == "노외") {
                                        features.forEach((feature) => {
                                            feature.get("features").forEach((feature2) => {
                                                const f_type = feature2.get("공영/민영");
                                                const val = feature2.get("주차면수 전체") || 0;
                                                const val2WN = feature2.get("이륜차 대수(야간)") || 0;
                                                const valNon2WN = feature2.get("이륜차 외 주차 대수(야간)") || 0;
                                                const val2WD = feature2.get("이륜차 대수(주간)") || 0;
                                                const valNon2WD = feature2.get("이륜차 외 주차 대수(주간)") || 0;

                                                if (f_type == "공영") {
                                                    dataStore.pfOutPub += parseInt(val);

                                                    if (isNight) {
                                                        dataStore.pdOutPub += parseInt(val2WN) + parseInt(valNon2WN);
                                                    } else {
                                                        dataStore.pdOutPub += parseInt(val2WD) + parseInt(valNon2WD);
                                                    }
                                                } else if (f_type == "민영") {
                                                    dataStore.pfOutPri += parseInt(val);

                                                    if (isNight) {
                                                        dataStore.pdOutPri += parseInt(val2WN) + parseInt(valNon2WN);
                                                    } else {
                                                        dataStore.pdOutPri += parseInt(val2WD) + parseInt(valNon2WD);
                                                    }
                                                }
                                            });
                                        });
                                    } else if (subType == "부설") {
                                        features.forEach((feature) => {
                                            feature.get("features").forEach((feature2) => {
                                                const f_type = feature2.get("주거/비주거");
                                                const val = feature2.get("주차면수 전체") || 0;
                                                const val2WN = feature2.get("이륜차 대수(야간)") || 0;
                                                const valNon2WN = feature2.get("이륜차 외 주차 대수(야간)") || 0;
                                                const val2WD = feature2.get("이륜차 대수(주간)") || 0;
                                                const valNon2WD = feature2.get("이륜차 외 주차 대수(주간)") || 0;

                                                if (f_type == "주거") {
                                                    dataStore.pfSubResi += parseInt(val);

                                                    if (isNight) {
                                                        dataStore.pdSubResi += parseInt(val2WN) + parseInt(valNon2WN);
                                                    } else {
                                                        dataStore.pdSubResi += parseInt(val2WD) + parseInt(valNon2WD);
                                                    }
                                                } else if (f_type == "비주거") {
                                                    dataStore.pfSubNonRegi += parseInt(val);

                                                    if (isNight) {
                                                        dataStore.pdSubNonRegi +=
                                                            parseInt(val2WN) + parseInt(valNon2WN);
                                                    } else {
                                                        dataStore.pdSubNonRegi +=
                                                            parseInt(val2WD) + parseInt(valNon2WD);
                                                    }
                                                }
                                            });
                                        });
                                    } else if (customSubType == "적법 구획내") {
                                        features.forEach((feature) => {
                                            dataStore.pdRDIn += parseInt(feature.get("features").length);
                                        });
                                    } else if (customSubType == "적법 구획외") {
                                        features.forEach((feature) => {
                                            dataStore.pdRDOut += parseInt(feature.get("features").length);
                                        });
                                    } else if (customSubType == "불법") {
                                        features.forEach((feature) => {
                                            dataStore.pdRDIll += parseInt(feature.get("features").length);
                                        });
                                    }
                                }

                                resolve();
                            });
                        } else if (featureType == "MULTIPOLYGON" || featureType == "MULTILINESTRING") {
                            if (type == "주차장") {
                                if (customSubType == "노상 거주자") {
                                    features.forEach((feature) => {
                                        const val = feature.get("주차면수 전체") || 0;
                                        dataStore.pfRDResi += parseInt(val);
                                    });

                                    if (!dataStore.pfRDResi) dataStore.pfRDResi = features.length;
                                } else if (customSubType == "노상 공영") {
                                    features.forEach((feature) => {
                                        const val = feature.get("주차면수 전체") || 0;
                                        dataStore.pfRDEtc += parseInt(val);
                                    });

                                    if (!dataStore.pfRDEtc) dataStore.pfRDEtc = features.length;
                                } else if (subType == "노외") {
                                    features.forEach((feature) => {
                                        const f_type = feature.get("공영/민영");
                                        const val = feature.get("주차면수 전체") || 0;
                                        const val2WN = feature.get("이륜차 대수(야간)") || 0;
                                        const valNon2WN = feature.get("이륜차 외 주차 대수(야간)") || 0;
                                        const val2WD = feature.get("이륜차 대수(주간)") || 0;
                                        const valNon2WD = feature.get("이륜차 외 주차 대수(주간)") || 0;

                                        if (f_type == "공영") {
                                            dataStore.pfOutPub += parseInt(val);

                                            if (isNight) {
                                                dataStore.pdOutPub += parseInt(val2WN) + parseInt(valNon2WN);
                                            } else {
                                                dataStore.pdOutPub += parseInt(val2WD) + parseInt(valNon2WD);
                                            }
                                        } else if (f_type == "민영") {
                                            dataStore.pfOutPri += parseInt(val);

                                            if (isNight) {
                                                dataStore.pdOutPri += parseInt(val2WN) + parseInt(valNon2WN);
                                            } else {
                                                dataStore.pdOutPri += parseInt(val2WD) + parseInt(valNon2WD);
                                            }
                                        }
                                    });
                                } else if (subType == "부설") {
                                    features.forEach((feature) => {
                                        const f_type = feature.get("주거/비주거");
                                        const val = feature.get("주차면수 전체") || 0;
                                        const val2WN = feature.get("이륜차 대수(야간)") || 0;
                                        const valNon2WN = feature.get("이륜차 외 주차 대수(야간)") || 0;
                                        const val2WD = feature.get("이륜차 대수(주간)") || 0;
                                        const valNon2WD = feature.get("이륜차 외 주차 대수(주간)") || 0;

                                        if (f_type == "주거") {
                                            dataStore.pfSubResi += parseInt(val);

                                            if (isNight) {
                                                dataStore.pdSubResi += parseInt(val2WN) + parseInt(valNon2WN);
                                            } else {
                                                dataStore.pdSubResi += parseInt(val2WD) + parseInt(valNon2WD);
                                            }
                                        } else if (f_type == "비주거") {
                                            dataStore.pfSubNonRegi += parseInt(val);

                                            if (isNight) {
                                                dataStore.pdSubNonRegi += parseInt(val2WN) + parseInt(valNon2WN);
                                            } else {
                                                dataStore.pdSubNonRegi += parseInt(val2WD) + parseInt(valNon2WD);
                                            }
                                        }
                                    });
                                }
                            }

                            resolve();
                        }
                    });
                });

                await Promise.all(promiseList);

                dataStore.pfRDSum = dataStore.pfRDResi + dataStore.pfRDEtc;
                dataStore.pfOutSum = dataStore.pfOutPub + dataStore.pfOutPri;
                dataStore.pfSubSum = dataStore.pfSubResi + dataStore.pfSubNonRegi;
                dataStore.pfTotal = dataStore.pfRDSum + dataStore.pfOutSum + dataStore.pfSubSum;
                dataStore.pdRDSum = dataStore.pdRDIn + dataStore.pdRDOut + dataStore.pdRDIll;
                dataStore.pdOutSum = dataStore.pdOutPub + dataStore.pdOutPri;
                dataStore.pdSubSum = dataStore.pdSubResi + dataStore.pdSubNonRegi;
                dataStore.pdTotal = dataStore.pdRDSum + dataStore.pdOutSum + dataStore.pdSubSum;

                dataStore.PK1 = dataStore.pfTotal / dataStore.pdTotal;
                dataStore.PK2 = dataStore.pfTotal - dataStore.pdTotal;
                dataStore.PK3 = (dataStore.pdRDIn + dataStore.pdOutSum + dataStore.pdSubSum) / dataStore.pfTotal;
                dataStore.PK4 = dataStore.pdRDIn / dataStore.pfRDSum;
                dataStore.PK5 = dataStore.pdOutSum / dataStore.pfOutSum;
                dataStore.PK6 = dataStore.pdSubSum / dataStore.pfSubSum;
                dataStore.PK7 = (dataStore.pdRDOut + dataStore.pdRDIll) / dataStore.pdTotal;
                dataStore.PK8 = dataStore.pdRDIll / dataStore.pdRDSum;
                dataStore.PK9 = dataStore.pfSubSum - dataStore.pdSubSum;

                dataStore.PK1 = isFinite(dataStore.PK1) ? dataStore.PK1 : 0;
                dataStore.PK2 = isFinite(dataStore.PK2) ? dataStore.PK2 : 0;
                dataStore.PK3 = isFinite(dataStore.PK3) ? dataStore.PK3 : 0;
                dataStore.PK4 = isFinite(dataStore.PK4) ? dataStore.PK4 : 0;
                dataStore.PK5 = isFinite(dataStore.PK5) ? dataStore.PK5 : 0;
                dataStore.PK6 = isFinite(dataStore.PK6) ? dataStore.PK6 : 0;
                dataStore.PK7 = isFinite(dataStore.PK7) ? dataStore.PK7 : 0;
                dataStore.PK8 = isFinite(dataStore.PK8) ? dataStore.PK8 : 0;
                dataStore.PK9 = isFinite(dataStore.PK9) ? dataStore.PK9 : 0;

                dataStore.PK1 = (dataStore.PK1 * 100).toFixed(1);
                dataStore.PK3 = (dataStore.PK3 * 100).toFixed(1);
                dataStore.PK4 = (dataStore.PK4 * 100).toFixed(1);
                dataStore.PK5 = (dataStore.PK5 * 100).toFixed(1);
                dataStore.PK6 = (dataStore.PK6 * 100).toFixed(1);
                dataStore.PK7 = (dataStore.PK7 * 100).toFixed(1);
                dataStore.PK8 = (dataStore.PK8 * 100).toFixed(1);

                setDataStore(dataStore);
            }

            asyncFn();
        }
    }, [mapObj, mapData, objectBBOX, isNight]);

    // 구군 탭 변경 감지
    useEffect(() => {
        setRange(-1);
    }, [filterGugun, filterDong]);

    // 맵 이동에 따른 이벤트
    useEffect(() => {
        if (mapObj.map) {
            mapObj.map.on("moveend", () => {
                const overlays = [];
                const currentZoom = mapObj.map.getView().getZoom();
                const maxZoom = mapObj.map.getView().getMaxZoom();

                // 수요 라벨 표출
                mapObj.map.getOverlays().forEach((overlay) => {
                    if (overlay.options.type == "pk") {
                        overlays.push(overlay);
                    }
                });

                if (currentZoom > maxZoom - 2) {
                    overlays.forEach((pkOverlay) => {
                        pkOverlay.getElement().classList.add("on");
                    });
                } else {
                    overlays.forEach((pkOverlay) => {
                        pkOverlay.getElement().classList.remove("on");
                    });
                }

                // 블럭경계 텍스트 크기 조정
                const layerList = findLayerList(mapObj.map, "objectLayer");

                const blockLayer = layerList.find((layer) => {
                    const shpInfo = layer.get("shpInfo");

                    return shpInfo.subType == "블럭경계";
                });

                if (blockLayer) {
                    let fontSize = 10;

                    if (currentZoom > 17) {
                        fontSize = 22;
                    } else if (currentZoom > 15) {
                        fontSize = 15;
                    }

                    blockLayer
                        .getSource()
                        .getFeatures()
                        .forEach((feature) => {
                            feature.getStyle()?.getText()?.setFont(`${fontSize}px Calibri,sans-serif`);
                        });
                }
            });
        }
    }, [mapObj]);

    // 관리카드 데이터 가져오기
    useEffect(() => {
        fetch(`/api/data/rsch/mngCard/search?year=${blank_year}&sggNm=${gugunParseCodeToName(filterGugun)}`, {
            method: "GET",
        })
            .then((res) => res.json())
            .then((json) => {
                setCardData(
                    json
                        .map((ele) => {
                            ele.block = ele.block.replace("\n", "");

                            return ele;
                        })
                        .filter((ele) => blank_year == ele.year)
                );
            });
    }, [filterGugun]);

    // 블럭 클릭 이벤트 관리
    useEffect(() => {
        if (mapObj.map && !drawOn) {
            let mapClickHandler;

            mapClickHandler = (e) => handleCardTabMapClick(e);

            mapObj.map.on("click", mapClickHandler);

            return () => {
                if (mapObj.map) {
                    mapObj.map.un("click", mapClickHandler);
                }
            };
        }
    }, [mapObj, selectBlock, drawOn]);

    // 선택된 블럭 데이터 변경 감지
    useEffect(() => {
        if (mapObj.map) {
            const layer = findLayerList(mapObj.map, "objectLayer");
            let blockLayer = layer.find((ele) => {
                return ele.get("shpInfo").subType == "블럭경계";
            });

            if (blockLayer) {
                let source = blockLayer.getSource();

                if (source && source.getState() === "ready") {
                    const filterFeatures = source.getFeatures().filter((feature) => {
                        const find = selectBlock.find((ele2) => ele2.block == feature.getStyle()?.getText()?.getText());

                        return find;
                    });

                    const tmpSelectLayer = filterFeatures.map((ele) => ({ feature: ele, layer: blockLayer }));

                    setSelectBlockLayer(tmpSelectLayer);
                }
            }
        }
    }, [selectBlock]);

    // 선택된 feature 강조 표시
    useEffect(() => {
        const restoreStyles = [];

        selectBlockLayer.forEach((ele) => {
            const layer = ele.layer;
            const feature = ele.feature;
            const originStyle = feature.getStyle();
            let animationIntervalId;

            const duration = 200;
            const initialStrokeWidth = 2;
            const finalStrokeWidth = 2;
            let increasing = true;
            let currentStrokeWidth = initialStrokeWidth;

            animationIntervalId = setInterval(() => {
                let clonedStyle = originStyle.clone();

                if (increasing) {
                    currentStrokeWidth += 0.1;
                    if (currentStrokeWidth >= finalStrokeWidth) {
                        increasing = false;
                    }
                } else {
                    currentStrokeWidth -= 0.1;
                    if (currentStrokeWidth <= initialStrokeWidth) {
                        increasing = true;
                    }
                }

                clonedStyle.getFill().setColor("rgba(255,245,58,0.92)");

                feature.setStyle(clonedStyle);
            }, duration / 10);

            restoreStyles.push({ feature, style: originStyle, animationIntervalId: animationIntervalId });
        });

        return () => {
            restoreStyles.forEach(({ feature, style, animationIntervalId }) => {
                clearInterval(animationIntervalId);
                feature.setStyle(style);
            });
        };
    }, [selectBlockLayer]);

    // 현재 체크 상태
    useEffect(() => {
        if (layerCheckBox.find((ele) => ele.key == "블럭경계")?.on) {
            setViewCardMenu(true);
        } else {
            setViewCardMenu(false);
        }

        if (
            !layerCheckBox.find((ele) => ele.key == "주차수요(주간)")?.on &&
            !layerCheckBox.find((ele) => ele.key == "주차수요(야간)")?.on
        ) {
            setIsNight(null);
        } else if (layerCheckBox.find((ele) => ele.key == "주차수요(주간)")?.on) {
            setIsNight(false);
        } else {
            setIsNight(true);
        }
    }, [layerCheckBox]);

    // 관리카드 사이드메뉴 위치 변경
    useEffect(() => {
        const cardMenu = document.querySelector(".mapContent.card");

        if (cardMenu) {
            if (layerToggleButton) {
                cardMenu.style.top = "300px";
            } else {
                cardMenu.style.top = "60px";
            }
        }
    }, [layerToggleButton]);

    return (
        <>
            {/* 실태조사 */}
            <ContentMenu
                SideComponent={
                    <ContentLayerMenu
                        setMapData={setMapData}
                        mapData={mapData}
                        blank_year={blank_year}
                        filterGugun={filterGugun}
                        setFilterGugun={setFilterGugun}
                        filterDong={filterDong}
                        setFilterDong={setFilterDong}
                        setIsNight={setIsNight}
                        setLayerCheckBox={setLayerCheckBox}
                    />
                }
                toggleButton={layerToggleButton}
                setToggleButton={() => setLayerToggleButton((prevState) => !prevState)}
                title={`실태조사_${blank_year}`}
                mode={gisMenuMode}
                type={"layer"}
            />

            {/* 관리카드 */}
            {viewCardMenu && (
                <ContentMenu
                    SideComponent={
                        <ContentCardMenu
                            setMapData={setMapData}
                            mapData={mapData}
                            cardData={cardData}
                            selectBlock={selectBlock}
                            setSelectBlock={setSelectBlock}
                            blank_year={blank_year}
                            filterGugun={filterGugun}
                        />
                    }
                    toggleButton={cardToggleButton}
                    setToggleButton={() => setCardToggleButton((prevState) => !prevState)}
                    title={`관리카드_${blank_year}`}
                    mode={gisMenuMode}
                    type={"card"}
                />
            )}

            <div id="pkStatistical" className={`${objectBBOX && isNight != null ? "on" : ""}`}>
                <div className="table_wrap">
                    <div className="title_header">실태조사 통계</div>
                    <LayerTable dataStore={dataStore} isNight={isNight} />
                </div>
            </div>

            <ul id="pkData" className={`${pkData.shp != undefined && pkData.api != undefined ? "" : "hide"}`}>
                <li>
                    <div className="col">주차면수</div>
                    <div className="val">{pkData.shp ? pkData.shp.toLocaleString() : 0}</div>
                </li>
                <li>
                    <div className="col">건축물관리대장</div>
                    <div className="val">{pkData.api ? pkData.api.toLocaleString() : 0}</div>
                </li>
            </ul>

            {isLegend && (
                <div id="layerLegend" ref={legendRef}>
                    <div className="layerLegend-header">
                        범례
                        <img
                            className="x_button"
                            src={require("../../../assets/img/x_button.png")}
                            onClick={() => {
                                setIsLegend(false);
                            }}
                        />
                    </div>
                    <img src={require("../../../assets/img/layerLegend.png")} />
                </div>
            )}

            {!isLegend && (
                <button
                    onClick={() => {
                        setIsLegend(true);
                    }}
                    className="legend-button"
                >
                    범례
                </button>
            )}

            <Draggable bounds="parent" nodeRef={cardInfoRef} handle={".header"}>
                <div
                    id="cardInfo"
                    ref={cardInfoRef}
                    className={[!selectBlock.length ? "hide" : "", simpleTableMode ? "simple" : ""].join(" ")}
                >
                    <div className="header">
                        <div className="title">
                            <div>관리카드</div>
                            <ul className="tab">
                                {["주간", "야간"].map((ele) => {
                                    return (
                                        <li
                                            className={`${selectBlockTab == ele ? "select" : ""}`}
                                            key={ele}
                                            onClick={() => {
                                                setSelectBlockTab(ele);
                                            }}
                                        >
                                            {ele}
                                        </li>
                                    );
                                })}
                            </ul>
                        </div>
                        <div className="btn_wrap">
                            <div className="change_mode" onClick={() => setSimpleTableMode(!simpleTableMode)}>
                                <FontAwesomeIcon icon={faRightLeft} />
                            </div>
                            <div className="close" onClick={() => setSelectBlock([])}>
                                <FontAwesomeIcon icon={faXmark} />
                            </div>
                        </div>
                    </div>
                    <div className="main scroll">{createCardTable(selectBlock)}</div>
                </div>
            </Draggable>
            {DuplInfoComponent}
            {InfoComponent}
        </>
    );
}

export default LayerMap;
