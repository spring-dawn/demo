import React, { useState, useEffect, useRef } from "react";
import { Overlay } from "ol";
import { featureHighlightHandler, moveFitFeature } from "./CommonGisFunction";
import ObjectDuplInfo from "./info/ObjectDuplInfo";
import ObjectInfo from "./info/ObjectInfo";
import { getArea } from "ol/sphere";
import { get as getProj } from "ol/proj";

// 인포창, 중복 인포창 생성 및 설정
export function useInfoOverlay(map, drawOn) {
    const [selectLayer, setSelectLayer] = useState([]); // 선택 레이어
    const [objectInfoCol, setObjectInfoCol] = useState([]); // 인포창 데이터
    const [objectDuplInfoCol, setObjectDuplInfoCol] = useState([]); // 중복 클릭 데이터
    const [objectDuplInfoRef, setObjectDuplInfoRef] = useState(); // 중복 창 dom
    const [objectInfoRef, setObjectInfoRef] = useState(); // 중복 창 dom

    function setInteractionEvt(element, overlay, handler) {
        handler.addEventListener("mousedown", function (evt) {
            // Calculate the offset between the mouse position and the overlay position
            const overlayPosition = overlay.getPosition();

            if (!overlayPosition || !overlayPosition.length) return;

            const offset = [
                overlayPosition[0] - map.getEventCoordinate(evt)[0],
                overlayPosition[1] - map.getEventCoordinate(evt)[1],
            ];

            function move(evt) {
                // Set the overlay position based on the mouse movement and the initial offset
                overlay.setPosition([
                    map.getEventCoordinate(evt)[0] + offset[0],
                    map.getEventCoordinate(evt)[1] + offset[1],
                ]);
            }

            function end(evt) {
                window.removeEventListener("mousemove", move);
                window.removeEventListener("mouseup", end);
            }

            window.addEventListener("mousemove", move);
            window.addEventListener("mouseup", end);
        });
    }

    const getPointCenterCoordinate = (pointFeature) => {
        return pointFeature.getGeometry().getCoordinates();
    };

    const getPolygonCenterCoordinate = (polygonFeature) => {
        // Calculate the center coordinate based on the polygon's geometry
        // This is just an example; you may need a more complex logic based on your requirements
        const polygonCoordinates = polygonFeature.getGeometry().getCoordinates()[0];
        const centerX = polygonCoordinates.reduce((sum, coord) => sum + coord[0], 0) / polygonCoordinates.length;
        const centerY = polygonCoordinates.reduce((sum, coord) => sum + coord[1], 0) / polygonCoordinates.length;

        return [centerX, centerY];
    };

    const getMultiPolygonCenterCoordinate = (multiPolygonFeature) => {
        // Extract coordinates from all polygons within the MultiPolygon
        const allPolygonCoordinates = multiPolygonFeature.getGeometry().getCoordinates();

        // Flatten the array of coordinates
        const flatCoordinates = allPolygonCoordinates.flat(3);

        // Calculate the average X and Y coordinates
        const centerX =
            flatCoordinates.reduce((sum, coord, index) => (index % 2 === 0 ? sum + coord : sum), 0) /
            (flatCoordinates.length / 2);
        const centerY =
            flatCoordinates.reduce((sum, coord, index) => (index % 2 !== 0 ? sum + coord : sum), 0) /
            (flatCoordinates.length / 2);

        return [centerX, centerY];
    };

    // 피쳐 상세창 생성
    function createInfoData(resultList) {
        const duplColList = [];

        // 그룹핑
        const groupObj = {};

        resultList.forEach((result) => {
            groupObj[`${result.layer.values_.shpInfo.name}`] = [];
        });

        resultList.forEach((result) => {
            groupObj[`${result.layer.values_.shpInfo.name}`].push(result);
        });

        // 중첩, 선택 레이어 col 값 저장
        Object.keys(groupObj).forEach((key) => {
            groupObj[key].forEach((result, groupIdx) => {
                let layer_ = result.layer;
                let feature_ = result.feature;
                let features_ = [];

                const layerType = layer_.get("layerType");
                const shpInfo = layer_.values_.shpInfo;

                if (layerType == "cluster") {
                    features_ = feature_.values_.features;
                } else if (layerType == "vector") {
                    features_.push(feature_);
                }

                const isSingle = features_.length == 1;

                features_.forEach((f, fIdx) => {
                    const colList = [];
                    const colIdx = features_.length > 1 ? fIdx : groupIdx;

                    colList.push({
                        keyName: isSingle ? shpInfo.name : `${shpInfo.name}_${colIdx + 1}`,
                        key: true,
                        keyId: f.ol_uid,
                        selectData: { feature: feature_, clusterFeature: f, layer: layer_, style: feature_.getStyle() },
                    });

                    Object.keys(f.values_)
                        .sort((a, b) => {
                            if (a === "주차면수 전체") return -1;
                            if (b === "주차면수 전체") return 1;
                            return 0;
                        })
                        .forEach((col) => {
                            if (col == "geometry") return;
                            if (col == "data") return;

                            const val = f.values_[col];

                            if (val === 0 || val) {
                                colList.push({ col: col, val: val });
                            }
                        });

                    duplColList.push(colList);
                });
            });
        });

        return duplColList;
    }

    // 클릭 이벤트 관리
    const handleClickEvt = (map, evt) => {
        // 기본 초기화
        let resultList = [];
        let currentList = [];

        // 선택된 feature
        map.forEachFeatureAtPixel(evt.pixel, function (feature, layer) {
            if (!layer || layer.get("noneClick")) return;

            resultList.push({ feature, layer });
        });

        // 선택된 레이어가 있을 경우
        if (resultList.length) {
            const lonLat = evt.coordinate;
            const duplColList = createInfoData(resultList);

            // 선택된 레이어가 다수일 경우
            if (duplColList.length > 1) {
                setObjectDuplInfoCol(duplColList);
            } else {
                setSelectLayer([duplColList[0][0].selectData]);
            }
        } else {
            setObjectDuplInfoCol([]);
        }
    };

    useEffect(() => {
        if (map && !drawOn) {
            let mapClickHandler = (e) => handleClickEvt(map, e);

            map.on("click", mapClickHandler);

            return () => {
                if (map) {
                    map.un("click", mapClickHandler);
                }
            };
        }
    }, [map, drawOn]);

    // 중첩 레이어 오버레이 초기화
    useEffect(() => {
        if (map && objectDuplInfoRef && objectInfoRef) {
            let objectDuplInfo = new Overlay({
                element: objectDuplInfoRef.current,
                type: "duplInfo",
            });

            let objectInfo = new Overlay({
                element: objectInfoRef.current,
                type: "info",
            });

            setInteractionEvt(objectInfoRef.current, objectInfo, objectInfoRef.current.querySelector(".table_header"));

            map.addOverlay(objectDuplInfo);
            map.addOverlay(objectInfo);
        }
    }, [map, objectInfoRef, objectDuplInfoRef]);

    // 선택 레이어 변경 감지
    useEffect(() => {
        if (map) {
            // 인포 오버레이 가져오기
            const overlays = map.getOverlays();
            const objectInfo = overlays.getArray().find((overlay) => {
                return overlay.options.type == "info";
            });

            // info 설정
            let duplColList;
            if (selectLayer.length && objectInfo) {
                let selectFeature = selectLayer[0].clusterFeature;
                duplColList = createInfoData(selectLayer);

                const findData = duplColList.find((ele) => ele[0].keyId == selectFeature.ol_uid);

                setObjectInfoCol(findData);

                // move 클릭 시
                if (selectLayer[0].isMove) {
                    const source = selectLayer[0].layer.getSource();
                    const targetKey = selectLayer[0].key_;

                    let find;

                    source.getFeatures().forEach((f) => {
                        if (f.get("features")) {
                            f.get("features").forEach((f2) => {
                                if (f2.get("data").key_ == targetKey) {
                                    find = f2;
                                }
                            });
                        } else if (f.get("data").key_ == targetKey) {
                            find = f;
                        }
                    });

                    // 해당 위치로 이동
                    moveFitFeature(map, find);

                    selectFeature = find;
                }

                let centerCoordinate;

                if (selectFeature.getGeometry().getType() === "Point") {
                    centerCoordinate = getPointCenterCoordinate(selectFeature);
                } else if (selectFeature.getGeometry().getType() === "Polygon") {
                    centerCoordinate = getPolygonCenterCoordinate(selectFeature);
                } else if (selectFeature.getGeometry().getType() === "MultiPolygon") {
                    centerCoordinate = getMultiPolygonCenterCoordinate(selectFeature);
                }

                objectInfo.setPosition(centerCoordinate);
                objectInfoRef.current.style.display = "block";
            } else {
                setObjectInfoCol([]);
                objectInfo.setPosition(null);
                objectInfoRef.current.style.display = "none";
            }

            // 클릭 강조 이벤트
            const resetFn = featureHighlightHandler(map, selectLayer, duplColList);

            return () => {
                resetFn();
            };
        }
    }, [selectLayer]);

    // 중첩 레이어 데이터 변경 감지
    useEffect(() => {
        if (map) {
            // 중복 레이어 오버레이 가져오기
            const overlays = map.getOverlays();
            const objectDuplInfo = overlays.getArray().find((overlay) => {
                return overlay.options.type == "duplInfo";
            });

            if (objectDuplInfoCol.length) {
                const selectFeature = objectDuplInfoCol[0][0].selectData.feature;

                let centerCoordinate;

                if (selectFeature.getGeometry().getType() === "Point") {
                    centerCoordinate = getPointCenterCoordinate(selectFeature);
                } else if (selectFeature.getGeometry().getType() === "Polygon") {
                    centerCoordinate = getPolygonCenterCoordinate(selectFeature);
                } else if (selectFeature.getGeometry().getType() === "MultiPolygon") {
                    centerCoordinate = getMultiPolygonCenterCoordinate(selectFeature);
                }

                objectDuplInfo.setPosition(centerCoordinate);
                objectDuplInfo.setOffset([0, -5]);
                objectDuplInfoRef.current.style.display = "block";
            } else {
                objectDuplInfo.setPosition(null);
                objectDuplInfoRef.current.style.display = "none";
            }
        }
    }, [objectDuplInfoCol]);

    return {
        InfoComponent: (
            <ObjectInfo
                setObjectInfoRef={setObjectInfoRef}
                setObjectInfoCol={setObjectInfoCol}
                objectInfoCol={objectInfoCol}
                setSelectLayer={setSelectLayer}
                map={map}
            />
        ),
        DuplInfoComponent: (
            <ObjectDuplInfo
                setObjectDuplInfoRef={setObjectDuplInfoRef}
                setSelectLayer={setSelectLayer}
                objectDuplInfoCol={objectDuplInfoCol}
                setObjectDuplInfoCol={setObjectDuplInfoCol}
            />
        ),
        selectLayer,
        setSelectLayer,
        objectInfoCol,
        setObjectInfoCol,
        objectDuplInfoCol,
        setObjectDuplInfoCol,
        objectDuplInfoRef,
        setObjectDuplInfoRef,
        objectInfoRef,
        setObjectInfoRef,
    };
}

// ref 사이즈 측정
export function useGisSize({ ref, deps }) {
    const [sizeRender, setSizeRender] = useState(false);
    const [parentSize, setParentSize] = useState({ width: window.innerWidth, height: window.innerHeight }); // 맵 컨테이너 크기

    // 맵 컴포넌트 크기 업데이트
    const updateMaxHeight = () => {
        const parentElement = ref.current;
        if (parentElement) {
            const parentHeight = parentElement.clientHeight;
            const parentWidth = parentElement.clientWidth;
            setParentSize({ width: parentWidth, height: parentHeight });
        }
    };

    // 맵 컨테이너 크기 업데이트 이벤트
    useEffect(() => {
        if (ref.current) {
            updateMaxHeight(); // Initial calculation
            window.addEventListener("resize", updateMaxHeight);

            return () => {
                window.removeEventListener("resize", updateMaxHeight);
            };
        }
    }, [ref, sizeRender, ...(deps || [])]);

    return { parentSize, setParentSize, sizeRender, setSizeRender };
}
