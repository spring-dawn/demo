import React, { useEffect, useRef, useState } from "react";
import { Control } from "ol/control";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faPencil, faXmark } from "@fortawesome/free-solid-svg-icons";
import { Select as AntSelect, Space } from "antd";
import { removeLayer } from "../CommonGisFunction";
import Circle from "ol/geom/Circle";
import { Feature } from "ol";
import { Circle as CircleStyle, Fill, Stroke, Style } from "ol/style";
import { Point } from "ol/geom";
import { Vector as VectorLayer } from "ol/layer";
import { Vector as VectorSource } from "ol/source";
import Draw, { createBox } from "ol/interaction/Draw";
import radius_ico from "../../../assets/img/gis/radius.png";
import { get as getProj, transform } from "ol/proj";

function CustomFilterRangeControl({ map }) {
    const [range, setRange] = useState(-1); // 선택 필터링 값
    const [drawOn, setDrawOn] = useState(false); // 사용자 지정 필터링 선택 시 그리기 모드 유무
    const [drawMode, setDrawMode] = useState("Circle"); // 사용자 지정 필터링 선택 시 선택된 그리기 도형
    const [objectBBOX, setObjectBBOX] = useState(null); // 필터링 영역 feature
    const [active, setActive] = useState(false); // 선택 필터링 값
    const ref = useRef(null);

    class CustomControl extends Control {
        constructor({ ref }) {
            super({
                element: ref.target.current,
                target: null,
            });
        }
    }

    // 거리당 반경 bbox 계산기
    function centerRangeBBoxCulc(event, map, range, projection) {
        const epsg5187 = getProj(projection);
        const view = map.getView();
        // const center = view.getCenter();

        // Access the drawn feature from the event
        const feature = event.feature;
        const geometry = feature.getGeometry();

        // Calculate the center of the drawn geometry
        const center = geometry.getExtent();

        // Bounding Box의 가로 및 세로 크기 계산
        const halfWidth = range;
        const halfHeight = range;

        // 좌상단 및 우하단 좌표 계산
        const leftTop = [center[0] - halfWidth, center[1] + halfHeight];
        const rightBottom = [center[0] + halfWidth, center[1] - halfHeight];

        // 좌표 변환
        let leftTopLonLat;
        let rightBottomLonLat;
        if (projection === "EPSG:3857") {
            leftTopLonLat = leftTop;
            rightBottomLonLat = rightBottom;
        } else {
            leftTopLonLat = transform(leftTop, "EPSG:3857", epsg5187);
            rightBottomLonLat = transform(rightBottom, "EPSG:3857", epsg5187);
        }

        return [leftTopLonLat[0], rightBottomLonLat[1], rightBottomLonLat[0], leftTopLonLat[1]];
    }

    // 사용자 지정 필터링 도형 생성 함수
    const rangeInteraction = (map, drawMode) => {
        map.getInteractions()
            .getArray()
            .forEach(function (interaction) {
                if (interaction instanceof Draw) {
                    map.removeInteraction(interaction);
                }
            });

        let draw;

        if (!isNaN(drawMode)) {
            draw = new Draw({
                type: "Point",
            });

            map.addInteraction(draw);

            // 원 그리기가 끝났을 때 실행될 함수
            draw.on("drawend", function (event) {
                // 그리기 모드 비활성화
                map.removeInteraction(draw);

                const bbox = centerRangeBBoxCulc(event, map, drawMode, "EPSG:3857");

                // Bounding Box의 중심 좌표와 반경 계산
                const center = [(bbox[0] + bbox[2]) / 2, (bbox[1] + bbox[3]) / 2];

                // circle의 지름을 range 값으로 설정
                const circle = new Circle(center, drawMode * 1.2);

                // 나머지 코드는 그대로 유지
                const circleFeature = new Feature(circle);

                // 중앙에 위치하는 점을 나타내는 Point 피처 생성
                const centerPoint = new Feature(new Point(center));

                // 원 피처 스타일링
                circleFeature.setStyle(
                    new Style({
                        fill: new Fill({
                            color: "rgba(195,206,239,0.11)", // 채우기 색상
                        }),
                        stroke: new Stroke({
                            color: "blue", // 테두리 색상
                            width: 2, // 테두리 두께
                        }),
                    })
                );

                // Point 피처 스타일링
                centerPoint.setStyle(
                    new Style({
                        image: new CircleStyle({
                            radius: 5,
                            fill: new Fill({
                                color: "blue", // 점의 채우기 색상
                            }),
                            stroke: new Stroke({
                                color: "white", // 점의 테두리 색상
                                width: 1, // 점의 테두리 두께
                            }),
                        }),
                    })
                );

                const source = new VectorSource({
                    features: [circleFeature, centerPoint],
                });

                const vector = new VectorLayer({
                    source: source,
                    zIndex: 500,
                    type: "rangeLayer",
                    noneClick: true,
                });

                map.addLayer(vector);

                setObjectBBOX(circleFeature);
                setDrawOn(false);
            });
        } else {
            let geometryFunction;
            let type;

            if (drawMode == "Circle") {
                type = "Circle";
            } else if (drawMode == "Box") {
                type = "Circle";
                geometryFunction = createBox();
            } else if (drawMode == "Polygon") {
                type = "Polygon";
            }

            const source = new VectorSource({ wrapX: false });

            const vector = new VectorLayer({
                source: source,
                zIndex: 500,
                type: "rangeLayer",
                noneClick: true,
            });

            map.addLayer(vector);

            draw = new Draw({
                source: source,
                type: type,
                geometryFunction: geometryFunction,
            });

            map.addInteraction(draw);

            // 원 그리기가 끝났을 때 실행될 함수
            draw.on("drawend", function (event) {
                // 그리기 모드 비활성화
                map.removeInteraction(draw);

                // 원이 그려진 피처
                const feature = event.feature;

                feature.setStyle(
                    new Style({
                        fill: new Fill({
                            color: "rgba(195,206,239,0.11)", // 채우기 색상
                        }),
                        stroke: new Stroke({
                            color: "blue", // 테두리 색상
                            width: 2, // 테두리 두께
                        }),
                    })
                );

                setObjectBBOX(feature);
                setDrawOn(false);
            });
        }
    };

    useEffect(() => {
        if (map && ref.target) {
            const customControl = new CustomControl({ ref: ref });
            map.addControl(customControl);

            return () => {
                map.removeControl(customControl);
            };
        }
    }, [map, ref]);

    // 표시 범위 레이어 변경
    useEffect(() => {
        if (map) {
            if (range == 0) {
                removeLayer(map, "rangeLayer");
                setObjectBBOX(null);

                map.getInteractions()
                    .getArray()
                    .forEach(function (interaction) {
                        if (interaction instanceof Draw) {
                            map.removeInteraction(interaction);
                        }
                    });
            } else if (drawOn) {
                removeLayer(map, "rangeLayer");
                setObjectBBOX(null);

                rangeInteraction(map, drawMode);
            }
        }
    }, [range, drawOn, drawMode]);

    useEffect(() => {
        if (map) {
            removeLayer(map, "rangeLayer");
            setObjectBBOX(null);
        }
    }, [drawMode]);

    return {
        FilterRangeControl: (
            <div className={"ol-control filter-range-control"} ref={ref}>
                <div className={`${active ? "wrap on" : "wrap"}`}>
                    <div className={"user_filter"}>
                        <div className={`${drawOn ? "ic on" : "ic"}`}>
                            <FontAwesomeIcon
                                icon={faPencil}
                                onClick={() => {
                                    setDrawOn(true);
                                    setRange(-1);
                                }}
                            />
                        </div>
                        <Space wrap>
                            <AntSelect
                                style={{ width: 212 }}
                                options={[
                                    { name: "100m", value: 100 },
                                    { name: "300m", value: 300 },
                                    { name: "500m", value: 500 },
                                    { name: "700m", value: 700 },
                                    { name: "1km", value: 1000 },
                                    { name: "원", value: "Circle" },
                                    { name: "박스", value: "Box" },
                                    { name: "자유", value: "Polygon" },
                                ].map(({ value, name }) => {
                                    return { value: value, label: name };
                                })}
                                value={drawMode}
                                onChange={(value) => setDrawMode(value)}
                            />
                        </Space>
                        <div className="ic">
                            <FontAwesomeIcon
                                icon={faXmark}
                                onClick={() => {
                                    setRange(0);
                                    setDrawOn(false);
                                }}
                            />
                        </div>
                    </div>
                </div>
                <div
                    className={`ico`}
                    onClick={() => {
                        setActive(!active);
                        setRange(0);
                        setDrawOn(false);
                    }}
                >
                    <img src={radius_ico} />
                </div>
            </div>
        ),
        range,
        setRange,
        objectBBOX,
        setObjectBBOX,
        drawMode,
        setDrawMode,
        drawOn,
        setDrawOn,
    };
}

export default CustomFilterRangeControl;
