import React, { useState, useEffect, useRef } from "react";
import { Link } from "react-router-dom";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFireExtinguisher, faSquareUpRight } from "@fortawesome/free-solid-svg-icons";
import { Map as OlMap, View, Feature, Overlay } from "ol"; //뷰 관리
import { Style, Icon, Fill, Stroke, Circle as CircleStyle, Text } from "ol/style";
import proj4 from "proj4/dist/proj4";
import { Point, Polygon } from "ol/geom";
import { Cluster as ClusterSource, Vector as VectorSource } from "ol/source";
import { Vector as VectorLayer } from "ol/layer";
import {
    removeLayer,
    overlapFilterByFeature,
    selectAnimation,
    formatPhoneNumber,
    gugunParseCodeToName,
    borderAnimation,
    initCustomControl,
    moveFitLayer,
    findLayerList,
} from "../CommonGisFunction";
import ContentCurrentMenu from "../side/ContentCurrentMenu";
import ContentMenu from "../side/ContentMenu";
import Circle from "ol/geom/Circle";
import { useInfoOverlay } from "../CommonGisHook";
import { GeoJSON } from "ol/format";
import layerTable from "../table/LayerTable";

// 현황 GIS 기능
function CurrentMap({ mapObj, objectBBOX, drawOn, gisMenuMode, setRange, subMapData, setSubMapData, parentSize }) {
    const storedUserInfo = localStorage.getItem("user");
    let userInfo;
    if (storedUserInfo) {
        userInfo = JSON.parse(storedUserInfo);
    }

    const [filterGugun, setFilterGugun] = useState(userInfo && userInfo.agency != "31000" ? userInfo.agency : "31110"); // 사이드 선택 구군
    const [filterSearch, setFilterSearch] = useState(""); // 사이드 검색어
    const [baseLayer, setBaseLayer] = useState([]);
    const [commonLayer, setCommonLayer] = useState([]); // 공통 레이어(안전구역, 소방시설 등)
    const [rowData, setRowData] = useState([]); // 사이드 row data
    const [sideOn, setSideOn] = useState(true);

    const { DuplInfoComponent, InfoComponent, selectLayer, setSelectLayer } = useInfoOverlay(mapObj.map, drawOn); // 인포창과 관련된 클릭 이벤트, 데이터, 선택레이어 변경

    const clusterDist = 70;

    // 범례 카드
    const [isLegend, setIsLegend] = useState(true);

    // 공영 주차장 레이어 생성 == API
    // const createCurrentLayer = (map, items, bbox) => {
    //     let features = [];
    //     const fromProjection = "EPSG:4326";
    //     const toProjection = "EPSG:3857";
    //
    //     // 주어진 객체들을 피처로 변환
    //     for (const item of items) {
    //         let lon = item["longitude"];
    //         let lat = item["latitude"];
    //
    //         // 좌표 텍스트를 실제 좌표로 변환
    //         lon = parseFloat(lon);
    //         lat = parseFloat(lat);
    //         const projectedCoordinates = proj4(fromProjection, toProjection, [lon, lat]);
    //
    //         const pointGeometry = new Point(projectedCoordinates);
    //
    //         const feature = new Feature({
    //             data: { ...item, key_: item["prkplceNo"] },
    //             ["주차장관리번호"]: item["prkplceNo"],
    //             ["주차장명"]: item["prkplceNm"],
    //             ["주차장구분"]: item["prkplceSe"],
    //             ["주차장유형"]: item["prkplceType"],
    //             ["소재지도로명주소"]: item["rdnmadr"],
    //             ["소재지지번주소"]: item["lnmadr"],
    //             ["주차장구획수"]: item["prkcmprt"],
    //             ["부제시행"]: item["enforceSe"],
    //             ["운영요일"]: item["operDay"],
    //             ["평일운영시작시각"]: item["weekdayOperOpenHhmm"],
    //             ["평일운영종료시각"]: item["weekdayOperColseHhmm"],
    //             ["토요일운영시작시각"]: item["satOperOperOpenHhmm"],
    //             ["토요일운영종료시각"]: item["holidayCloseOpenHhmm"],
    //             ["공휴일운영시작시각"]: item["holidayOperOpenHhmm"],
    //             ["공휴일운영종료시각"]: item["holidayCloseOpenHhmm"],
    //             ["요금정보"]: item["parkingchrgeInfo"],
    //             ["주차기본시간"]: item["basicTime"],
    //             ["주차기본요금"]: item["basicCharge"],
    //             ["추가단위시간"]: item["addUnitTime"],
    //             ["추가단위요금"]: item["addUnitCharge"],
    //             ["1일주차권요금적용시간"]: item["dayCmmtktAdjTime"],
    //             ["1일주차권요금"]: item["dayCmmtkt"],
    //             ["월정기권요금"]: item["monthCmmtkt"],
    //             ["결제방법"]: item["metpay"],
    //             ["관리기관명"]: item["institutionNm"],
    //             ["전화번호"]: item["phoneNumber"],
    //             geometry: pointGeometry,
    //         });
    //
    //         features.push(feature);
    //     }
    //
    //     // 필터 범위 필터링
    //     if (bbox) {
    //         const filteredFeatures = overlapFilterByFeature(features, bbox);
    //         features = filteredFeatures;
    //     }
    //
    //     // 소스 생성
    //     const vectorSource = new VectorSource({
    //         projection: "EPSG:3857",
    //         features: features,
    //     });
    //
    //     // 클러스팅
    //     const clusterSource = new ClusterSource({
    //         distance: clusterDist,
    //         source: vectorSource,
    //     });
    //
    //     const featureStyleFn = (feature) => {
    //         let size = 0;
    //
    //         feature.get("features").forEach((feature2) => {
    //             const val = feature2.get("주차장구획수") || 0;
    //             size += parseInt(val);
    //         });
    //
    //         const style = new Style({
    //             image: new Icon({
    //                 src: require("../../../assets/img/gis/marker/standard1,2,3_text.png"),
    //                 crossOrigin: "anonymous",
    //                 scale: 0.3,
    //             }),
    //             text: new Text({
    //                 text: size.toString(),
    //                 fill: new Fill({
    //                     color: "#000000",
    //                 }),
    //                 stroke: new Stroke({
    //                     color: "rgba(0,0,0,0.5)",
    //                     width: 2,
    //                 }),
    //                 font: "12px sans-serif",
    //                 padding: [2, 2, 2, 2],
    //                 offsetX: 10,
    //             }),
    //         });
    //
    //         // Set the styles to the feature
    //         feature.setStyle(style);
    //     };
    //
    //     // 레이어 생성
    //     const objectLayer = new VectorLayer({
    //         source: clusterSource,
    //         type: "currentLayer",
    //         sub: "prkplce",
    //         shpInfo: { name: "공영 주차장" },
    //         layerType: "cluster",
    //         zIndex: 100,
    //         selectStyle: {
    //             style: (layer, feature) => {
    //                 featureStyleFn(feature);
    //             },
    //         },
    //         style: function (feature) {
    //             return featureStyleFn(feature);
    //         },
    //     });
    //
    //     map.addLayer(objectLayer);
    //
    //     // 생성된 피쳐 기반으로 사이드 현황창 업데이트
    //     const tmpSelectLayer = [];
    //     features.forEach((feature) => {
    //         const find = items.find((item) => item.key_ == feature.get("data")?.key_);
    //
    //         tmpSelectLayer.push(find);
    //     });
    //
    //     return tmpSelectLayer;
    // };

    const createCurrentLayer = (map, items, bbox) => {
        let features = [];
        const fromProjection = "EPSG:4326";
        const toProjection = "EPSG:3857";

        // 주어진 객체들을 피처로 변환
        for (const item of items) {
            let lon = item["lon"];
            let lat = item["lat"];

            // 좌표 텍스트를 실제 좌표로 변환
            lon = parseFloat(lon);
            lat = parseFloat(lat);
            const projectedCoordinates = proj4(fromProjection, toProjection, [lon, lat]);

            const pointGeometry = new Point(projectedCoordinates);

            const feature = new Feature({
                data: { ...item, key_: `공영_${item["seq"]}` },
                ["전체 주차면수"]: item["totalSpaces"],
                ["일반 주차면수"]: item["spaces"],
                ["장애인 전용 주차구획"]: item["forDisabled"],
                ["경차 전용 주차구획"]: item["forLight"],
                ["임산부 전용 주차구획"]: item["forPregnant"],
                ["버스 전용 주차구획"]: item["forBus"],
                ["전기차 전용 주차구획"]: item["forElectric"],
                ["친환경 전용 주차구획"]: item["forEcho"],
                ["어르신 전용 주차구획"]: item["forElderly"],
                ["주차장명"]: item["name"],
                ["주소"]: item["location"],
                ["운영시간_평일"]: item["wh"],
                ["운영시간_토요일"]: item["whSaturday"],
                ["운영시간_공휴일"]: item["whHoliday"],
                ["휴무일"]: item["dayOff"],
                ["유/무료"]: item["payYn"],
                ["1시간 요금"]: item["pay4Hour"],
                ["1일 요금"]: item["pay4Day"],
                ["운영기관"]: item["agency"],
                geometry: pointGeometry,
            });

            features.push(feature);
        }

        // 필터 범위 필터링
        if (bbox) {
            const filteredFeatures = overlapFilterByFeature(features, bbox);
            features = filteredFeatures;
        }

        // 소스 생성
        const vectorSource = new VectorSource({
            projection: "EPSG:3857",
            features: features,
        });

        // 클러스팅
        const clusterSource = new ClusterSource({
            distance: clusterDist,
            source: vectorSource,
        });

        const featureStyleFn = (feature) => {
            let size = 0;

            feature.get("features").forEach((feature2) => {
                const val = feature2.get("전체 주차면수") || 0;
                size += parseInt(val);
            });

            const style = new Style({
                image: new Icon({
                    src: require("../../../assets/img/gis/marker/standard1,2,3_text.png"),
                    crossOrigin: "anonymous",
                    scale: 0.3,
                }),
                text: new Text({
                    text: size.toString(),
                    fill: new Fill({
                        color: "#000000",
                    }),
                    stroke: new Stroke({
                        color: "rgba(0,0,0,0.5)",
                        width: 2,
                    }),
                    font: "12px sans-serif",
                    padding: [2, 2, 2, 2],
                    offsetX: 10,
                }),
            });

            // Set the styles to the feature
            feature.setStyle(style);
        };

        // 레이어 생성
        const objectLayer = new VectorLayer({
            source: clusterSource,
            type: "currentLayer",
            sub: "prkplce",
            shpInfo: { name: "공영 주차장" },
            layerType: "cluster",
            zIndex: 100,
            selectStyle: {
                style: (layer, feature) => {
                    featureStyleFn(feature);
                },
            },
            style: function (feature) {
                return featureStyleFn(feature);
            },
        });

        map.addLayer(objectLayer);

        // 생성된 피쳐 기반으로 사이드 현황창 업데이트
        const tmpSelectLayer = [];
        features.forEach((feature) => {
            const find = items.find((item) => item.key_ == feature.get("data")?.key_);

            tmpSelectLayer.push(find);
        });

        return tmpSelectLayer;
    };

    // 소방옹수 레이어 생성
    const createFireLayer = (map, items, bbox) => {
        let features = [];
        const fromProjection = "EPSG:4326";
        const toProjection = "EPSG:3857";

        // 주어진 객체들을 피처로 변환
        for (const item of items) {
            let lat = item["소방용수(X좌표)"];
            let lon = item["소방용수(Y좌표)"];

            if (!lat || lat.split(".")[0] == "0" || !lon.split(".")[0] || lon == "0") {
                continue;
            }

            // 좌표 텍스트를 실제 좌표로 변환
            lon = parseFloat(lon);
            lat = parseFloat(lat);
            const projectedCoordinates = proj4(fromProjection, toProjection, [lon, lat]);

            const pointGeometry = new Point(projectedCoordinates);

            const feature = new Feature({
                data: { ...item, key_: item["일련번호"] },
                ["일련번호"]: item["일련번호"],
                ["관할서명"]: item["관할서명"],
                ["시도명"]: item["시도명"],
                ["구군명"]: item["구군명"],
                ["도로명"]: item["도로명"],
                ["동명"]: item["동명"],
                ["서센터명"]: item["서센터명"],
                ["소방용수수리번호"]: item["소방용수수리번호"],
                ["지하여부"]: item["지하여부"],
                ["형식명"]: item["형식명"],
                geometry: pointGeometry,
            });

            features.push(feature);
        }

        // bbox 영역 필터링
        if (bbox) {
            const filteredFeatures = overlapFilterByFeature(features, bbox);
            features = filteredFeatures;
        }

        const vectorSource = new VectorSource({
            projection: "EPSG:3857",
            features: features,
        });

        const objectLayer = new VectorLayer({
            source: vectorSource,
            type: "currentLayer",
            sub: "fire",
            shpInfo: { name: "소방용수" },
            layerType: "vector",
            zIndex: 100,
            selectStyle: {
                style: (layer, feature) => selectAnimation(layer, feature),
                clear: (layer) => {
                    const intervalList = layer.get("intervalList");
                    intervalList.forEach((id) => {
                        clearInterval(id);
                    });
                },
            },

            style: function (feature) {
                const pointGeometry = feature.getGeometry();

                const bufferGeometry = new Circle(pointGeometry.getCoordinates(), 5);

                const areaStyle = new Style({
                    fill: new Fill({
                        color: "rgba(255, 0, 0, 0.1)",
                    }),
                    stroke: new Stroke({
                        color: "red",
                        width: 2,
                        lineDash: [3, 3],
                    }),
                    geometry: bufferGeometry,
                });

                feature.setStyle(areaStyle);

                return areaStyle;
            },
        });

        map.addLayer(objectLayer);

        // 생성된 피쳐 기반으로 필터링 데이터 업데이트
        const tmpFilterData = [];
        features.forEach((feature) => {
            const find = items.find((item) => item.key_ == feature.get("data")?.key_);

            tmpFilterData.push(find);
        });

        return tmpFilterData;
    };

    // 안전구역 레이어 생성
    const createSafeLayer = (map, items, bbox) => {
        let features = [];
        const fromProjection = "EPSG:4326";
        const toProjection = "EPSG:3857";

        // 주어진 객체들을 피처로 변환
        for (const item of items) {
            const polygonText = item.GEOM;

            // 텍스트에서 좌표 추출
            const coordinatesText = polygonText.match(/\d+\.\d+/g);

            // 좌표 텍스트를 실제 좌표로 변환
            const coordinates = [];
            for (let i = 0; i < coordinatesText.length; i += 2) {
                const x = parseFloat(coordinatesText[i]);
                const y = parseFloat(coordinatesText[i + 1]);
                const projectedCoordinates = proj4(fromProjection, toProjection, [x, y]);
                coordinates.push(projectedCoordinates);
            }

            const feature = new Feature({
                data: { ...item, key_: item["SNCT_SEQ"] },
                ["보호구역ID"]: item["SNCT_SEQ"],
                ["데이터기준일자"]: item["REG_DT"],
                ["제한속도(보정속도)"]: item["MAX_SPD"],
                ["지자체입력제한속도"]: item["MAX_SPD_ORG"],
                ["CCTV설치대수"]: item["CCTV_CNT"],
                ["대상시설명"]: item["FCLTY_NM"],
                ["시도명"]: item["SIDO_NM"],
                ["보호구역도로폭"]: item["ROAD_WDT"],
                ["CCTV설치여부"]: item["CCTV_YN"],
                ["소재지도로명주소"]: item["ADDR"],
                ["소재지지번주소"]: item["LADDR"],
                ["관할경찰서명"]: item["POL_NM"],
                ["시군구코드"]: item["SIGUN_CD"],
                ["시군구명"]: item["SIGUN_NM"],
                // ["경도"]: item["X"],
                // ["위도"]: item["Y"],
                ["관리기관명"]: item["GOV_NM"],
                ["관리기관전화번호"]: item["GOV_TEL"],
                // ["시설종류"]: item["FCLTY_TY"],
                // ["데이터구분"]: item["DATA_TYPE"],
                // ["지오메트리정보"]: item["GEOM"],
                geometry: new Polygon([coordinates]),
            });

            features.push(feature);
        }

        // bbox 영역 필터링
        if (bbox) {
            const filteredFeatures = overlapFilterByFeature(features, bbox);
            features = filteredFeatures;
        }

        // 데이터 그룹핑
        const safe_1 = features.filter((ele) => ele.values_.data.FCLTY_TY == 1);
        const safe_2 = features.filter((ele) => ele.values_.data.FCLTY_TY == 2);
        const safe_3 = features.filter((ele) => ele.values_.data.FCLTY_TY == 3);

        [safe_1, safe_2, safe_3].forEach((groupFeatures) => {
            if (!groupFeatures.length) return;

            let name = groupFeatures[0].values_.data.shpName_;
            let color = groupFeatures[0].values_.data.backgroundColor_;

            const vectorSource = new VectorSource({
                projection: "EPSG:3857",
                features: groupFeatures,
            });

            const objectLayer = new VectorLayer({
                source: vectorSource,
                type: "currentLayer",
                sub: "safe",
                shpInfo: { name: name, ["출처"]: "경찰청 도시교통정보센터(UTIC)" },
                layerType: "vector",
                zIndex: 100,
                selectStyle: {
                    style: (layer, feature) => selectAnimation(layer, feature),
                    clear: (layer) => {
                        const intervalList = layer.get("intervalList");
                        intervalList.forEach((id) => {
                            clearInterval(id);
                        });
                    },
                },

                style: function (feature) {
                    const style = new Style({
                        fill: new Fill({
                            color: color,
                        }),
                        stroke: new Stroke({
                            color: "#000000",
                            width: 2,
                        }),
                    });

                    feature.setStyle(style);

                    return style;
                },
            });

            map.addLayer(objectLayer);
        });

        // 생성된 피쳐 기반으로 필터링 데이터 업데이트
        const tmpFilterData = [];
        features.forEach((feature) => {
            const find = items.find((item) => item.key_ == feature.get("data")?.key_);

            tmpFilterData.push(find);
        });

        return tmpFilterData;
    };

    // 거주자우선주차장 레이어 생성
    const createResidentLayer = (map, items, bbox) => {
        let features = [];
        const fromProjection = "EPSG:4326";
        const toProjection = "EPSG:3857";

        // 주어진 객체들을 피처로 변환
        for (const item of items) {
            let lon = item["x"];
            let lat = item["y"];

            // 좌표 텍스트를 실제 좌표로 변환
            lon = parseFloat(lon);
            lat = parseFloat(lat);
            const projectedCoordinates = proj4(fromProjection, toProjection, [lon, lat]);

            const pointGeometry = new Point(projectedCoordinates);

            const feature = new Feature({
                data: { ...item, key_: item["resiXyNo"] },
                ["구획라벨"]: item["groupLabel"],
                ["소속구간"]: item["areaLabel"],
                ["지번주소"]: item["addr"],
                ["소속동"]: item["dong"],
                ["전화번호"]: item["dongCellNum"],
                ["과금구분"]: item["chargingYype"],
                ["대상특성"]: item["characteristicsType"],
                ["용도구분"]: item["useType"],
                ["rmrk"]: item["특이사항"],
                ["사용가능시간"]: item["useTime"],
                ["사용상태"]: item["useState"],
                ["사용 시작일"]: item["useTimeStart"],
                ["사용 종료일"]: item["useTimeEnd"],
                ["전용 시작일"]: item["privateTimeStart"],
                ["공사 시작일"]: item["workStartTime"],
                ["공사 종료일"]: item["workEndTime"],
                geometry: pointGeometry,
            });

            features.push(feature);
        }

        // bbox 영역 필터링
        if (bbox) {
            const filteredFeatures = overlapFilterByFeature(features, bbox);
            features = filteredFeatures;
        }

        const vectorSource = new VectorSource({
            projection: "EPSG:3857",
            features: features,
        });

        // 클러스팅
        const clusterSource = new ClusterSource({
            distance: clusterDist,
            source: vectorSource,
        });

        const featureSrtyleFn = (feature) => {
            const size = feature.get("features").length;
            let imageSource =
                size != 1
                    ? require("../../../assets/img/gis/marker/resident_text.png")
                    : require("../../../assets/img/gis/marker/resident_icon.png");

            const style = new Style({
                image: new Icon({
                    src: imageSource,
                    crossOrigin: "anonymous",
                    scale: 0.3,
                }),
                text: new Text({
                    text: size != 1 ? size.toString() : "",
                    fill: new Fill({
                        color: "#000000",
                    }),
                    stroke: new Stroke({
                        color: "rgba(0,0,0,0.5)",
                        width: 2,
                    }),
                    font: "12px sans-serif",
                    padding: [2, 2, 2, 2],
                    offsetX: 10,
                }),
            });

            feature.setStyle(style);

            return style;
        };

        const objectLayer = new VectorLayer({
            source: clusterSource,
            type: "currentLayer",
            sub: "resident",
            shpInfo: { name: "거주자우선" },
            layerType: "cluster",
            zIndex: 100,
            selectStyle: {
                style: (layer, feature) => {
                    featureSrtyleFn(feature);
                },
            },

            style: function (feature) {
                return featureSrtyleFn(feature);
            },
        });

        map.addLayer(objectLayer);

        // 생성된 피쳐 기반으로 필터링 데이터 업데이트
        const tmpFilterData = [];
        features.forEach((feature) => {
            const find = items.find((item) => item.key_ == feature.get("data")?.key_);

            tmpFilterData.push(find);
        });

        return tmpFilterData;
    };

    // 부설주차장 레이어 생성
    const createSubPkLayer = (map, items, bbox) => {
        let features = [];
        const fromProjection = "EPSG:4326";
        const toProjection = "EPSG:3857";

        // 주어진 객체들을 피처로 변환
        for (const item of items) {
            let lon = item["lon"];
            let lat = item["lat"];

            // 좌표 텍스트를 실제 좌표로 변환
            lon = parseFloat(lon);
            lat = parseFloat(lat);
            const projectedCoordinates = proj4(fromProjection, toProjection, [lon, lat]);

            const pointGeometry = new Point(projectedCoordinates);

            const feature = new Feature({
                data: { ...item, key_: item["bmNo"] },
                ["총 주차면수"]:
                    parseInt(item.indrAutoUtcnt) +
                    parseInt(item.indrMechUtcnt) +
                    parseInt(item.oudrAutoUtcnt) +
                    parseInt(item.oudrMechUtcnt),
                ["옥내자주식면적(㎡)"]: item["indrAutoArea"],
                ["옥내자주식대수(대)"]: item["indrAutoUtcnt"],
                ["옥내기계식면적(㎡)"]: item["indrMechArea"],
                ["옥내기계식대수(대)"]: item["indrMechUtcnt"],
                ["옥외자주식면적(㎡)"]: item["oudrAutoArea"],
                ["옥외자주식대수(대)"]: item["oudrAutoUtcnt"],
                ["옥외기계식면적(㎡)"]: item["oudrMechArea"],
                ["옥외기계식대수(대)"]: item["oudrMechUtcnt"],
                ["주용도코드명"]: item["mainPurpsCdNm"],
                ["관리건축물대장ID"]: item["mgmBldrgstPk"],
                ["도로명대지위치"]: item["newPlatPlc"],
                ["대지위치"]: item["platPlc"],

                geometry: pointGeometry,
            });

            features.push(feature);
        }

        // bbox 영역 필터링
        if (bbox) {
            const filteredFeatures = overlapFilterByFeature(features, bbox);
            features = filteredFeatures;
        }

        const vectorSource = new VectorSource({
            projection: "EPSG:3857",
            features: features,
        });

        // 클러스팅
        const clusterSource = new ClusterSource({
            distance: clusterDist,
            source: vectorSource,
        });

        const featureSrtyleFn = (feature) => {
            let size = 0;

            feature.get("features").forEach((feature2) => {
                const val1 = feature2.get("옥내자주식대수(대)") || 0;
                const val2 = feature2.get("옥내기계식대수(대)") || 0;
                const val3 = feature2.get("옥외자주식대수(대)") || 0;
                const val4 = feature2.get("옥외기계식대수(대)") || 0;
                size += parseInt(val1) + parseInt(val2) + parseInt(val3) + parseInt(val4);
            });

            let imageSource =
                size != 1
                    ? require("../../../assets/img/gis/marker/buseol_text.png")
                    : require("../../../assets/img/gis/marker/buseol_icon.png");

            const style = new Style({
                image: new Icon({
                    src: imageSource,
                    crossOrigin: "anonymous",
                    scale: 0.3,
                }),
                text: new Text({
                    text: size != 1 ? size.toString() : "",
                    fill: new Fill({
                        color: "#000000",
                    }),
                    stroke: new Stroke({
                        color: "rgba(0,0,0,0.5)",
                        width: 2,
                    }),
                    font: "12px sans-serif",
                    padding: [2, 2, 2, 2],
                    offsetX: 10,
                }),
            });

            feature.setStyle(style);

            return style;
        };

        const objectLayer = new VectorLayer({
            source: clusterSource,
            type: "currentLayer",
            sub: "subPk",
            shpInfo: { name: "부설주차장" },
            layerType: "cluster",
            zIndex: 100,
            selectStyle: {
                style: (layer, feature) => {
                    featureSrtyleFn(feature);
                },
            },

            style: function (feature) {
                return featureSrtyleFn(feature);
            },
        });

        map.addLayer(objectLayer);

        // 생성된 피쳐 기반으로 필터링 데이터 업데이트
        const tmpFilterData = [];
        features.forEach((feature) => {
            const find = items.find((item) => item.key_ == feature.get("data")?.key_);

            tmpFilterData.push(find);
        });

        return tmpFilterData;
    };

    // 고정식 CCTV
    const createFixCctvLayer = (map, items, bbox) => {
        let features = [];
        const fromProjection = "EPSG:4326";
        const toProjection = "EPSG:3857";

        // 주어진 객체들을 피처로 변환
        for (const item of items) {
            let lon = item["lon"];
            let lat = item["lat"];
            // 좌표 텍스트를 실제 좌표로 변환
            lon = parseFloat(lon);
            lat = parseFloat(lat);
            const projectedCoordinates = proj4(fromProjection, toProjection, [lon, lat]);

            const pointGeometry = new Point(projectedCoordinates);

            const feature = new Feature({
                data: { ...item, key_: item.year + item.month + item.sgg + item.seq },
                ["단속지점"]: item["crdnBrnch"],
                ["주용도코드명"]: item["clctnAmt"],
                ["징수금액"]: item["clctnNocs"],
                ["단속기준(분)"]: item["crdnCtrM"],
                ["단속건수"]: item["crdnNocs"],
                ["단속기간"]: item["crdnPrd"],
                ["설치일자"]: item["instlYmd"],
                ["부과금액"]: item["levyAmt"],
                ["비고"]: item["rmrk"],
                geometry: pointGeometry,
            });

            features.push(feature);
        }

        // bbox 영역 필터링
        if (bbox) {
            const filteredFeatures = overlapFilterByFeature(features, bbox);
            features = filteredFeatures;
        }

        const vectorSource = new VectorSource({
            projection: "EPSG:3857",
            features: features,
        });

        // 클러스팅
        const clusterSource = new ClusterSource({
            distance: clusterDist,
            source: vectorSource,
        });

        const featureSrtyleFn = (feature) => {
            const size = feature.get("features").length;
            let imageSource =
                size != 1
                    ? require("../../../assets/img/gis/marker/cctv_text.png")
                    : require("../../../assets/img/gis/marker/cctv_icon.png");

            const style = new Style({
                image: new Icon({
                    src: imageSource,
                    crossOrigin: "anonymous",
                    scale: 0.3,
                }),
                text: new Text({
                    text: size != 1 ? size.toString() : "",
                    fill: new Fill({
                        color: "#000000",
                    }),
                    stroke: new Stroke({
                        color: "rgba(0,0,0,0.5)",
                        width: 2,
                    }),
                    font: "12px sans-serif",
                    padding: [2, 2, 2, 2],
                    offsetX: 12,
                    // offsetY: 3,
                }),
            });

            feature.setStyle(style);

            return style;
        };

        const objectLayer = new VectorLayer({
            source: clusterSource,
            type: "currentLayer",
            sub: "subPk",
            shpInfo: { name: "고정식 CCTV" },
            layerType: "cluster",
            zIndex: 100,
            selectStyle: {
                style: (layer, feature) => {
                    featureSrtyleFn(feature);
                },
            },

            style: function (feature) {
                return featureSrtyleFn(feature);
            },
        });

        map.addLayer(objectLayer);

        // 생성된 피쳐 기반으로 필터링 데이터 업데이트
        const tmpFilterData = [];
        features.forEach((feature) => {
            const find = items.find((item) => item.key_ == feature.get("data")?.key_);

            tmpFilterData.push(find);
        });

        return tmpFilterData;
    };

    // 표준 데이터 주차장
    const createStandard = (map, items, bbox, cl) => {
        let features = [];
        const fromProjection = "EPSG:4326";
        const toProjection = "EPSG:3857";

        // 주어진 객체들을 피처로 변환
        for (const item of items) {
            let lon = item["lon"];
            let lat = item["lat"];
            // 좌표 텍스트를 실제 좌표로 변환
            lon = parseFloat(lon);
            lat = parseFloat(lat);
            const projectedCoordinates = proj4(fromProjection, toProjection, [lon, lat]);

            const pointGeometry = new Point(projectedCoordinates);

            const featureProp = {
                data: { ...item, key_: item.mngNo },
                geometry: pointGeometry,
            };

            console.log(item.infoData.sort((a, b) => b.infoSeq - a.infoSeq));

            item.infoData
                .sort((a, b) => a.infoSeq - b.infoSeq)
                .forEach((ele) => {
                    featureProp[ele.col] = ele.val;
                });

            const feature = new Feature(featureProp);

            features.push(feature);
        }

        // bbox 영역 필터링
        if (bbox) {
            const filteredFeatures = overlapFilterByFeature(features, bbox);
            features = filteredFeatures;
        }

        const vectorSource = new VectorSource({
            projection: "EPSG:3857",
            features: features,
        });

        // 클러스팅
        const clusterSource = new ClusterSource({
            distance: clusterDist,
            source: vectorSource,
        });

        const featureSrtyleFn = (feature) => {
            let size = 0;

            size = feature.get("features").length;
            let requireIco1_ = feature.get("features")[0].get("data").requireIco1_;
            let requireIco2_ = feature.get("features")[0].get("data").requireIco2_;
            let imageSource = size != 1 ? requireIco1_ : requireIco2_;

            const style = new Style({
                image: new Icon({
                    src: imageSource,
                    crossOrigin: "anonymous",
                    scale: 0.3,
                }),
                text: new Text({
                    text: size != 1 ? size.toString() : "",
                    fill: new Fill({
                        color: "#000000",
                    }),
                    stroke: new Stroke({
                        color: "rgba(0,0,0,0.5)",
                        width: 2,
                    }),
                    font: "12px sans-serif",
                    padding: [2, 2, 2, 2],
                    offsetX: 12,
                    // offsetY: 3,
                }),
            });

            feature.setStyle(style);

            return style;
        };

        const objectLayer = new VectorLayer({
            source: clusterSource,
            type: "currentLayer",
            sub: "subPk",
            shpInfo: { name: cl.name },
            layerType: "cluster",
            zIndex: 100,
            selectStyle: {
                style: (layer, feature) => {
                    featureSrtyleFn(feature);
                },
            },

            style: function (feature) {
                return featureSrtyleFn(feature);
            },
        });

        map.addLayer(objectLayer);

        // 생성된 피쳐 기반으로 필터링 데이터 업데이트
        const tmpFilterData = [];
        features.forEach((feature) => {
            const find = items.find((item) => item.key_ == feature.get("data")?.key_);

            tmpFilterData.push(find);
        });

        return tmpFilterData;
    };

    // 공통 추가 레이어 생성
    const createCommonObjectLayer = (cl, bbox, filterGugun, filterSearch) => {
        let data = [];

        if (cl.data && cl.on) {
            data = cl.data || [];
        }

        // 구군 필터링
        data = data.filter((ele) => ele.gu_ == gugunParseCodeToName(filterGugun));

        // 검색어 필터링
        data = data.filter(({ title_, con1_, con2_ }) => {
            if (!title_ || !con1_ || !con2_) {
                return false;
            }

            if (title_.val.includes(filterSearch)) {
                return true;
            } else if (con1_.val.includes(filterSearch)) {
                return true;
            } else if (con2_.val.includes(filterSearch)) {
                return true;
            } else {
                return false;
            }
        });

        if (cl.value == "safe") {
            return createSafeLayer(mapObj.map, data, bbox);
        }

        if (cl.value == "fire") {
            return createFireLayer(mapObj.map, data, bbox);
        }

        if (cl.value == "prkplce") {
            return createCurrentLayer(mapObj.map, data, bbox);
        }

        if (cl.value == "resident") {
            return createResidentLayer(mapObj.map, data, bbox);
        }

        if (cl.value == "subPk") {
            return createSubPkLayer(mapObj.map, data, bbox);
        }

        if (cl.value == "fixCctv") {
            return createFixCctvLayer(mapObj.map, data, bbox);
        }
        if (cl.value.includes("standard")) {
            return createStandard(mapObj.map, data, bbox, cl);
        }
    };

    // gis 반응형
    useEffect(() => {
        if (parentSize.width < 1100) {
            setIsLegend(false);
            setSideOn(false);
        } else {
            setIsLegend(true);
            setSideOn(true);
        }
    }, [parentSize.width]);

    // 공통 검색 결과 레이어 가져오기
    useEffect(() => {
        fetch(`/api/gis/common-layer?sgg=${filterGugun}`)
            .then((res) => res.json())
            .then((json) => {
                const commonBaseList = [];
                const commonLayerList = [];
                const commonLayerData = json.forEach((ele) => {
                    console.log(ele);

                    let name = ele.name;
                    let data = ele.data || [];
                    let group = ele.group;

                    if (ele.key == "fixCctv") {
                        // 필터링
                        const filter = data.filter((ele) => ele.lon && ele.lat);
                        const legendIcon = <img src={require("../../../assets/img/gis/marker/cctv_icon.png")} />;

                        data = filter.map((ele) => {
                            ele.type_ = "fixCctv";
                            ele.ico_ = <img src={require("../../../assets/img/gis/marker/cctv_icon.png")} />;
                            ele.backgroundColor_ = "rgba(135,183,254,0.37)";
                            ele.key_ = ele.year + ele.month + ele.sgg + ele.seq;
                            ele.gu_ = gugunParseCodeToName(ele["sgg"]);
                            ele.title_ = {
                                col: "단속지점",
                                val: ele.crdnBrnch,
                            };
                            ele.con1_ = {
                                col: "건수",
                                val: `${ele.crdnNocs || 0} 건`,
                            };
                            ele.con2_ = {
                                col: "기준",
                                val: `${ele.crdnCtrM || 0} 분`,
                            };
                            ele.group = group;

                            return ele;
                        });
                        commonLayerList.push({ name: name, value: ele.key, on: true, data: data, group, legendIcon });
                    } else if (ele.key == "safe") {
                        const legendIcon = <img src={require("../../../assets/img/gis/marker/safeZone.png")} />;

                        data = data.items.map((ele) => {
                            let backgroundColor_ = "none";
                            let shpName_ = "";

                            if (ele.FCLTY_TY == 1) {
                                shpName_ = "어린이 보호구역";
                                backgroundColor_ = "rgba(255,218,0,0.2)";
                            } else if (ele.FCLTY_TY == 2) {
                                shpName_ = "노인 보호구역";
                                backgroundColor_ = "rgba(26,255,0,0.2)";
                            } else if (ele.FCLTY_TY == 3) {
                                shpName_ = "장애인 보호구역";
                                backgroundColor_ = "rgba(0,255,225,0.2)";
                            }

                            ele.type_ = "safe";
                            ele.ico_ = <img src={require("../../../assets/img/gis/marker/safeZone.png")} />;
                            ele.backgroundColor_ = backgroundColor_;
                            ele.shpName_ = shpName_;
                            ele.key_ = ele["SNCT_SEQ"];
                            ele.gu_ = ele["SIGUN_NM"];
                            ele.title_ = {
                                col: "구역명",
                                val: ele["FCLTY_NM"] || "",
                            };
                            ele.con1_ = {
                                col: "주소",
                                val: ele["LADDR"] || "",
                            };
                            ele.con2_ = {
                                col: "번호",
                                val: ele["GOV_TEL"] ? formatPhoneNumber(ele["GOV_TEL"]) : "",
                            };
                            ele.group = group;

                            return ele;
                        });

                        commonLayerList.push({ name: name, value: ele.key, on: true, data: data, group, legendIcon });
                    } else if (ele.key == "fire") {
                        const legendIcon = <FontAwesomeIcon icon={faFireExtinguisher} color={"red"} />;

                        data = data.data.map((ele) => {
                            ele.type_ = "fire";
                            ele.ico_ = <img src={require("../../../assets/img/gis/marker/sohwagi.png")} />;
                            ele.backgroundColor_ = "rgba(243,109,109,0.13)";
                            ele.key_ = ele["일련번호"];
                            ele.gu_ = ele["구군명"];
                            ele.title_ = {
                                col: "관활+서센터+일련번호",
                                val: `${ele["관할서명"]}_${ele["서센터명"]}_${ele["일련번호"]}`,
                            };
                            const con1_val = `${ele["시도명"] || ""} ${ele["구군명"] || ""} ${ele["동명"] || ""}`;
                            ele.con1_ = {
                                col: "주소",
                                val: con1_val.trim(" "),
                            };
                            ele.con2_ = {
                                col: "형식",
                                val: ele["형식명"] || "",
                            };
                            ele.group = group;

                            return ele;
                        });
                        commonLayerList.push({ name: name, value: ele.key, on: true, data: data, group, legendIcon });
                    } else if (ele.key == "prkplce") {
                        // 좌표 있는 것만 필터링
                        let filter = data.filter((ele) => ele["lat"] && ele["lon"]);

                        // 기본 값 설정
                        data = filter.map((ele) => {
                            ele.type_ = "prkplce";
                            ele.ico_ = <img src={require("../../../assets/img/gis/marker/standard1,2,3_icon.png")} />;
                            ele.backgroundColor_ = "rgba(57,174,227,0.13)";
                            ele.key_ = `공영_${ele["seq"]}`;
                            ele.gu_ = gugunParseCodeToName(ele.sggCd);
                            ele.title_ = {
                                col: "주차장",
                                val: ele["name"],
                            };
                            ele.con1_ = {
                                col: "주소",
                                val: ele["location"],
                            };
                            ele.con2_ = {
                                col: "구분",
                                val: ele["roadYn"],
                            };

                            return ele;
                        });

                        commonLayerList.push({
                            name: name,
                            value: ele.key,
                            on: false,
                            data: data,
                            group,
                            legendIcon: <img src={require("../../../assets/img/gis/marker/standard1,2,3_icon.png")} />,
                        });

                        /* API 공영 데이터 로직 */
                        // // 좌표 있는 것만 필터링
                        // let filter = data.data.filter((ele) => ele["latitude"] && ele["longitude"]);
                        //
                        // // 최신데이터만 필터링
                        // const recentMap = {};
                        // filter.forEach((ele) => {
                        //     const nowData = recentMap[ele.prkplceNo];
                        //     if (nowData) {
                        //         if (new Date(nowData.referenceDate) < ele.referenceDate) {
                        //             recentMap[ele.prkplceNo] = ele;
                        //         }
                        //     } else {
                        //         recentMap[ele.prkplceNo] = ele;
                        //     }
                        // });
                        //
                        // // 기본 값 설정
                        // filter = Object.values(recentMap);
                        // filter = filter.map((ele) => {
                        //     const eleGu = ele.lnmadr.split(" ")[1];
                        //     ele.type_ = "prkplce";
                        //     ele.ico_ = <img src={require("../../../assets/img/gis/marker/standard1,2,3_icon.png")} />;
                        //     ele.backgroundColor_ = "rgba(57,174,227,0.13)";
                        //     ele.key_ = ele["prkplceNo"];
                        //     ele.gu_ = eleGu;
                        //     ele.title_ = {
                        //         col: "주차장",
                        //         val: ele["prkplceNm"],
                        //     };
                        //     ele.con1_ = {
                        //         col: "주소",
                        //         val: ele["lnmadr"],
                        //     };
                        //     ele.con2_ = {
                        //         col: "번호",
                        //         val: ele["phoneNumber"],
                        //     };
                        //
                        //     return ele;
                        // });
                        //
                        // data = filter;
                        // commonLayerList.push({
                        //     name: name,
                        //     value: ele.key,
                        //     on: false,
                        //     data: data,
                        //     group,
                        //     legendIcon: <img src={require("../../../assets/img/gis/marker/standard1,2,3_icon.png")} />,
                        // });
                    } else if (ele.key == "resident") {
                        // 최신데이터만 필터링
                        const recentMap = {};
                        data.forEach((ele) => {
                            recentMap[ele.addr] = ele;
                        });

                        const legendIcon = <img src={require("../../../assets/img/gis/marker/resident_icon.png")} />;

                        data = Object.values(recentMap).map((ele) => {
                            const eleGu = gugunParseCodeToName(ele["sgg"]);
                            ele.type_ = "resident";
                            ele.backgroundColor_ = "rgba(239,147,112,0.13)";
                            ele.ico_ = <img src={require("../../../assets/img/gis/marker/resident_icon.png")} />;
                            ele.key_ = ele["resiXyNo"];
                            ele.gu_ = eleGu;
                            ele.title_ = {
                                col: "주소",
                                val: ele.addr,
                            };
                            ele.con1_ = {
                                col: "구분",
                                val: ele.useTime,
                            };
                            ele.con2_ = {
                                col: "번호",
                                val: ele.dongCellNum,
                            };
                            ele.group = group;

                            return ele;
                        });
                        commonLayerList.push({ name: name, value: ele.key, on: false, data: data, group, legendIcon });
                    } else if (ele.key == "subPk") {
                        const filter = data.filter(
                            (ele) =>
                                parseInt(ele.indrAutoUtcnt) +
                                parseInt(ele.indrMechUtcnt) +
                                parseInt(ele.oudrAutoUtcnt) +
                                parseInt(ele.oudrMechUtcnt)
                        );

                        const legendIcon = <img src={require("../../../assets/img/gis/marker/buseol_icon.png")} />;

                        data = filter.map((ele) => {
                            const eleGu = gugunParseCodeToName(ele["sigunguCd"]);
                            ele.type_ = ele.key;
                            ele.backgroundColor_ = "rgba(44,181,115,0.13)";
                            ele.ico_ = <img src={require("../../../assets/img/gis/marker/buseol_icon.png")} />;
                            ele.key_ = ele["bmNo"];
                            ele.gu_ = eleGu;
                            ele.title_ = {
                                col: "주소",
                                val: ele.platPlc,
                            };
                            ele.con1_ = {
                                col: "구분",
                                val: ele.etcPurps,
                            };
                            ele.con2_ = {
                                col: "구분2",
                                val: ele.strctCdNm,
                            };
                            ele.group = group;

                            return ele;
                        });
                        commonLayerList.push({ name: name, value: ele.key, on: false, data: data, group, legendIcon });
                    } else if (ele.key.includes("standard")) {
                        let legendIcon = <img src={require(`../../../assets/img/gis/marker/default_icon.png`)} />;

                        if (ele.key == "standard1,2,3") {
                            legendIcon = <img src={require(`../../../assets/img/gis/marker/standard1,2,3_icon.png`)} />;
                        } else if (ele.key == "standard4,5,6") {
                            legendIcon = <img src={require(`../../../assets/img/gis/marker/standard4,5,6_icon.png`)} />;
                        } else if (ele.key == "standard8") {
                            legendIcon = <img src={require(`../../../assets/img/gis/marker/standard8_icon.png`)} />;
                        } else if (ele.key == "standard9") {
                            legendIcon = <img src={require(`../../../assets/img/gis/marker/standard9_icon.png`)} />;
                        }

                        data = data.map((ele2) => {
                            ele2.type_ = ele.key;

                            let defaultTextImagePath = require(`../../../assets/img/gis/marker/default_text.png`);
                            let defaultIconImagePath = require(`../../../assets/img/gis/marker/default_icon.png`);

                            if (ele.key == "standard1,2,3") {
                                defaultTextImagePath = require(`../../../assets/img/gis/marker/standard1,2,3_text.png`);
                                defaultIconImagePath = require(`../../../assets/img/gis/marker/standard1,2,3_icon.png`);
                                ele2.backgroundColor_ = "rgba(100,139,191,0.4)";
                            } else if (ele.key == "standard4,5,6") {
                                defaultTextImagePath = require(`../../../assets/img/gis/marker/standard4,5,6_text.png`);
                                defaultIconImagePath = require(`../../../assets/img/gis/marker/standard4,5,6_icon.png`);
                                ele2.backgroundColor_ = "rgba(148,99,190,0.28)";
                            } else if (ele.key == "standard8") {
                                defaultTextImagePath = require(`../../../assets/img/gis/marker/standard8_text.png`);
                                defaultIconImagePath = require(`../../../assets/img/gis/marker/standard8_icon.png`);
                                ele2.backgroundColor_ = "rgba(169,82,83,0.34)";
                            } else if (ele.key == "standard9") {
                                defaultTextImagePath = require(`../../../assets/img/gis/marker/standard9_text.png`);
                                defaultIconImagePath = require(`../../../assets/img/gis/marker/standard9_icon.png`);
                                ele2.backgroundColor_ = "rgba(229,162,91,0.45)";
                            }

                            ele2.requireIco1_ = defaultTextImagePath;
                            ele2.requireIco2_ = defaultIconImagePath;

                            ele2.ico_ = <img src={ele2.requireIco2_} />;

                            ele2.key_ = ele2.mngNo;
                            ele2.gu_ = gugunParseCodeToName(ele2["sggCd"]);
                            ele2.group = group;

                            let contentsKeys = [
                                { val: ele2.address, col: "주소", infoSeq: 1 },
                                { val: ele2.lotNm, col: "명칭", infoSeq: 2 },
                                { val: ele2.totalSpcs, col: "면수", infoSeq: 0 },
                                { val: ele2.mngNo, col: "번호", infoSeq: 3 },
                            ];

                            contentsKeys = contentsKeys.filter(({ val }) => val);

                            ele2.infoData = [];

                            contentsKeys.forEach((ele3, idx) => {
                                if (idx == 0) {
                                    ele2.title_ = {
                                        col: ele3.col,
                                        val: ele3.val,
                                        infoSeq: ele3.infoSeq,
                                    };
                                    ele2.infoData.push(ele2.title_);
                                } else if (idx == 1) {
                                    ele2.con1_ = {
                                        col: ele3.col,
                                        val: ele3.val,
                                        infoSeq: ele3.infoSeq,
                                    };
                                    ele2.infoData.push(ele2.con1_);
                                } else if (idx == 2) {
                                    ele2.con2_ = {
                                        col: ele3.col,
                                        val: ele3.val,
                                        infoSeq: ele3.infoSeq,
                                    };
                                    ele2.infoData.push(ele2.con2_);
                                }
                            });

                            return ele2;
                        });

                        commonLayerList.push({ name: name, value: ele.key, on: false, data: data, group, legendIcon });
                    } else if (ele.key == "ulsanGu") {
                        commonBaseList.push({ key: ele.key, data: data });
                    }
                });

                // 임시 테스트용
                setCommonLayer(commonLayerList);
                setBaseLayer(commonBaseList);
            });
    }, [filterGugun]);

    // 현황 맵 이벤트
    useEffect(() => {
        if (mapObj.map) {
            mapObj.map.on("moveend", () => {
                const layerList = findLayerList(mapObj.map, null, "cluster");
                const currentZoom = mapObj.map.getView().getZoom();
                const maxZoom = mapObj.map.getView().getMaxZoom();

                layerList.forEach((layer) => {
                    const source = layer.getSource();

                    if (currentZoom > maxZoom - 2) {
                        source.setDistance(0);
                    } else {
                        source.setDistance(clusterDist);
                    }
                });
            });
        }
    }, [mapObj]);

    // 구군 변경 감지 이벤트
    useEffect(() => {
        if (mapObj.map) {
            removeLayer(mapObj.map, "baseLayer");
            const findJson = baseLayer.find((ele) => ele.key == "ulsanGu");

            if (findJson) {
                const jsonData = { ...findJson.data };
                const findGuFeature = jsonData.features.find((ele) => ele.properties["SIG_CD"] == filterGugun);

                if (findGuFeature) {
                    const fromProjection = "EPSG:4326";
                    const toProjection = "EPSG:3857";
                    jsonData.features = [findGuFeature];

                    const geoJsonFormat = new GeoJSON();
                    let geoJsonFeatures = geoJsonFormat.readFeatures(jsonData);

                    const transformedFeatures = geoJsonFeatures.map((feature) => {
                        const transformedGeometry = feature.getGeometry().clone();
                        transformedGeometry.transform(fromProjection, toProjection);

                        return new Feature({
                            geometry: transformedGeometry,
                        });
                    });

                    const vectorSource = new VectorSource({
                        features: transformedFeatures,
                    });
                    const layer = new VectorLayer({
                        type: "baseLayer",
                        noneClick: true,
                        source: vectorSource,
                        zIndex: 100,

                        style: function (feature) {
                            borderAnimation(layer, feature, "rgba(110,194,53,1)", 0, 1, 300, 4, 6, (width, color) => {
                                const style = new Style({
                                    fill: new Fill({
                                        color: "rgba(0,0,0,0.01)",
                                    }),
                                    stroke: new Stroke({
                                        color: color,
                                        width: width,
                                    }),
                                });

                                feature.setStyle(style);
                            });
                        },
                    });

                    mapObj.map.addLayer(layer);
                    moveFitLayer(mapObj.map, layer);

                    return () => {
                        const intervalListList = layer.get("intervalList");

                        if (intervalListList) {
                            intervalListList.forEach((id) => {
                                clearInterval(id);
                            });
                        }
                    };
                }
            }
        }
    }, [mapObj, baseLayer, filterGugun]);

    // 레이어 생성
    useEffect(() => {
        if (mapObj.map && commonLayer.length) {
            removeLayer(mapObj.map, "currentLayer");

            // 레이어 설정
            let tmp = [];
            commonLayer.forEach((cl) => {
                const row = createCommonObjectLayer(cl, objectBBOX, filterGugun, filterSearch);

                if (row) {
                    tmp = [...tmp, ...row];
                }
            });

            setSelectLayer([]);
            setRowData(tmp);
        }
    }, [mapObj, objectBBOX, commonLayer, filterGugun, filterSearch]);

    // m 범위 표시 위치 변경
    useEffect(() => {
        const scaleLine = document.querySelector(".ol-scale-line");

        if (scaleLine) {
            if (sideOn) {
                scaleLine.style.left = "400px";
            } else {
                scaleLine.style.left = "8px";
            }
        }
    }, [sideOn]);

    // m 범위 표시 위치 변경
    useEffect(() => {
        const scaleLine = document.querySelector(".ol-scale-line");

        if (scaleLine) {
            scaleLine.style.left = "400px";
        }
    }, []);

    return (
        <>
            <ContentCurrentMenu
                mapObj={mapObj}
                selectLayer={selectLayer}
                setSelectLayer={setSelectLayer}
                filterGugun={filterGugun}
                setFilterGugun={setFilterGugun}
                filterSearch={filterSearch}
                setFilterSearch={setFilterSearch}
                commonLayer={commonLayer}
                setCommonLayer={setCommonLayer}
                sideOn={sideOn}
                setSideOn={setSideOn}
                rowData={rowData}
                subMapData={subMapData}
                setSubMapData={setSubMapData}
            />

            {isLegend ? (
                <div id="layerLegend">
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
                    <div style={{ backgroundColor: "#7da7eb" }} className="legend-category">
                        주차장
                    </div>
                    <div>
                        <ul>
                            {commonLayer
                                .filter((item) => item.group == 1)
                                .map((item, i) => (
                                    <li key={i}>
                                        {item.legendIcon}
                                        {item.name}
                                    </li>
                                ))}
                        </ul>
                    </div>
                    <div style={{ backgroundColor: "#f88888" }} className="legend-category">
                        불법 주정차 단속 현황
                    </div>
                    <div>
                        <ul>
                            {commonLayer
                                .filter((item) => item.group == 2)
                                .map((item, i) => (
                                    <li key={i}>
                                        {item.legendIcon}
                                        {item.name}
                                    </li>
                                ))}
                        </ul>
                    </div>
                    <div style={{ backgroundColor: "#d77fdb" }} className="legend-category">
                        불법 주정차 금지 구역
                    </div>
                    <div>
                        <ul>
                            {commonLayer
                                .filter((item) => item.group == 3)
                                .map((item, i) => (
                                    <li key={i}>
                                        {item.legendIcon}
                                        {item.name}
                                    </li>
                                ))}
                        </ul>
                    </div>
                </div>
            ) : (
                <div
                    className="legend-button"
                    onClick={() => {
                        setIsLegend(true);
                    }}
                >
                    범례
                </div>
            )}

            {DuplInfoComponent}
            {InfoComponent}
        </>
    );
}

export default CurrentMap;
