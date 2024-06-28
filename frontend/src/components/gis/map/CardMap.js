import React, { useState, useEffect, useRef } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faXmark, faRightLeft, faSquareMinus } from "@fortawesome/free-solid-svg-icons";
import Draggable from "react-draggable";
import {
    createObjectLayer,
    removeLayer,
    moveFitLayer,
    findLayerList,
    overlapFilterByFeature,
    gugunParseCodeToName,
} from "../CommonGisFunction";
import ContentCardMenu from "../side/ContentCardMenu";
import ContentMenu from "../side/ContentMenu";
import ContentCurrentMenu from "../side/ContentCurrentMenu";

// 관리 카드 GIS 기능
function CardMap({
    mapObj,
    objectBBOX,
    setRange,
    mapData,
    setMapData,
    drawOn,
    blank_year,
    menuHeight,
    menuWidth,
    gisMenuMode,
}) {
    const [filterGugun, setFilterGugun] = useState("31110");
    const [selectBlock, setSelectBlock] = useState([]); // 관리카드 선택 관리
    const [cardData, setCardData] = useState([]); // 관리카드 데이터
    const [selectLayer, setSelectLayer] = useState([]); // 선택 레이어
    const [selectBlockTab, setSelectBlockTab] = useState("주간"); // 테이블 데이터 타입 (주, 야간)
    const [simpleTableMode, setSimpleTableMode] = useState(false); // 테이블 심플 모드
    const cardInfoRef = useRef(); // 관리 카드 테이블 dom
    const dragRef = useRef(null);

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

    // 맵 클릭 이벤트
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

    // 검색 결과 레이어 생성
    const handleCreateLayer = (map, mapData) => {
        removeLayer(map, "objectLayer");
        let first;

        // geojson 데이터 형식
        Object.keys(mapData)
            .filter((key) => mapData[key].on)
            .map((key) => mapData[key])
            .forEach((ele) => {
                const layer = createObjectLayer(map, ele, "objectLayer");
                map.addLayer(layer);
                first = layer;
            });

        return first;
    };

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

    // 블럭 레이어 변경
    useEffect(() => {
        if (mapObj.map) {
            const first = handleCreateLayer(mapObj.map, mapData);

            if (first) {
                moveFitLayer(mapObj.map, first);
            }
        }
    }, [mapObj, mapData]);

    // 레이어 필터링
    useEffect(() => {
        if (mapObj.map) {
            const layerList = findLayerList(mapObj.map, "objectLayer");

            if (objectBBOX) {
                layerList.forEach((layer) => {
                    const source = layer.getSource();
                    const features = source.getFeatures();
                    const filteredFeatures = overlapFilterByFeature(features, objectBBOX);

                    source.clear();
                    source.addFeatures(filteredFeatures);
                });
            } else {
                handleCreateLayer(mapObj.map, mapData);
            }
        }
    }, [mapObj, mapData, objectBBOX]);

    // 선택된 feature 강조 표시
    useEffect(() => {
        const restoreStyles = [];

        selectLayer.forEach((ele) => {
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
    }, [selectLayer]);

    // 선택된 블럭 데이터 변경 감지
    useEffect(() => {
        if (mapObj.map) {
            const layer = findLayerList(mapObj.map, "objectLayer");

            if (layer.length) {
                let blockLayer = layer[0];
                let source = blockLayer.getSource();

                if (source && source.getState() === "ready") {
                    const filterFeatures = source.getFeatures().filter((feature) => {
                        const find = selectBlock.find((ele2) => ele2.block == feature.getStyle()?.getText()?.getText());

                        return find;
                    });

                    const tmpSelectLayer = filterFeatures.map((ele) => ({ feature: ele, layer: blockLayer }));

                    setSelectLayer(tmpSelectLayer);
                }
            }
        }
    }, [selectBlock]);

    // 클릭 이벤트 관리
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

    // 구군 탭 변경 감지
    useEffect(() => {
        setRange(-1);
    }, [filterGugun]);

    return (
        <>
            <ContentMenu
                SideComponent={
                    <ContentCardMenu
                        setMapData={setMapData}
                        mapData={mapData}
                        cardData={cardData}
                        selectBlock={selectBlock}
                        setSelectBlock={setSelectBlock}
                        menuWidth={menuWidth}
                        menuHeight={menuHeight}
                        blank_year={blank_year}
                        filterGugun={filterGugun}
                        setFilterGugun={setFilterGugun}
                    />
                }
                title={`관리카드_${blank_year}`}
                mode={gisMenuMode}
            />
            <Draggable bounds="parent" nodeRef={cardInfoRef}>
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
        </>
    );
}

export default CardMap;
