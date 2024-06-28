import { Overlay } from "ol";
import { GeoJSON } from "ol/format";
import { Cluster as ClusterSource, TileWMS, Vector as VectorSource } from "ol/source";
import { Tile as TileLayer, Vector as VectorLayer } from "ol/layer";
import { Circle as CircleStyle, Fill, Icon, Stroke, Style, Text } from "ol/style";
import { getArea } from "ol/sphere";
import { get as getProj, transform } from "ol/proj";
import { getCenter, extend } from "ol/extent";
import { Control } from "ol/control";

// rgba => hex 투명도는 제외
export function rgbaToHex(rgbaColor) {
    // Remove the "rgba(" prefix and the closing ")" and split into components
    const components = rgbaColor
        .slice(5, -1)
        .split(",")
        .map((component) => parseInt(component.trim()));

    // Convert each RGB component to a two-digit hex value
    const hexComponents = components.map((component) => {
        const hex = component.toString(16);
        return hex.length === 1 ? "0" + hex : hex; // Ensure two digits
    });

    // Combine the hex components into a hex color string
    const hexColor = "#" + hexComponents.join("").slice(0, -2);

    return hexColor;
}

// 16진수 색상을 RGB 배열로 변환
export function hexToRGB(hex) {
    return [parseInt(hex.slice(1, 3), 16), parseInt(hex.slice(3, 5), 16), parseInt(hex.slice(5, 7), 16)];
}

// RGB 배열을 16진수 색상으로 변환
export function RGBToHex(rgb) {
    return (
        "#" +
        rgb
            .map((x) => {
                const hex = x.toString(16);
                return hex.length === 1 ? "0" + hex : hex;
            })
            .join("")
    );
}

// SLD 스타일 객체 생성
export function createSldBody(color, table, type) {
    if (type == "MULTIPOLYGON") {
        return `<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld">
                <NamedLayer>
                  <Name>${table}</Name>
                  <UserStyle>
                    <FeatureTypeStyle>
                      <Rule>
                        <PolygonSymbolizer>
                          <Fill>
                            <CssParameter name="fill">${color}</CssParameter> <!-- Change this color code to the desired fill color -->
                          </Fill>
                          <Stroke>
                            <CssParameter name="stroke">#000000</CssParameter> <!-- Set the border color to black -->
                            <CssParameter name="stroke-width">1</CssParameter> <!-- Set the border width (adjust as needed) -->
                          </Stroke>
                        </PolygonSymbolizer>
                      </Rule>
                    </FeatureTypeStyle>
                  </UserStyle>
                </NamedLayer>
              </StyledLayerDescriptor>`;
    } else if (type == "MULTILINESTRING") {
        return `<StyledLayerDescriptor version="1.0.0" xmlns="http://www.opengis.net/sld">
                <NamedLayer>
                  <Name>${table}</Name>
                  <UserStyle>
                    <FeatureTypeStyle>
                      <Rule>
                        <PolygonSymbolizer>
                          <Stroke>
                            <CssParameter name="stroke">${color}</CssParameter> <!-- Set the border color to black -->
                            <CssParameter name="stroke-width">2</CssParameter> <!-- Set the border width (adjust as needed) -->
                          </Stroke>
                        </PolygonSymbolizer>
                      </Rule>
                    </FeatureTypeStyle>
                  </UserStyle>
                </NamedLayer>
              </StyledLayerDescriptor>`;
    }
}

// 실태조사 레이어 생성
export function createObjectLayer(map, ele, type, bbox, customFilter) {
    const shpInfo = ele.shpInfo;
    const layer = ele.layer;
    const featureType = shpInfo.featureType;
    const color = shpInfo.color;
    const canvas = shpInfo.canvas;

    // features 생성
    const geoJsonFormat = new GeoJSON();
    let geoJsonFeatures = geoJsonFormat.readFeatures(layer);

    // bbox 영역 필터링
    if (bbox) {
        const filteredFeatures = overlapFilterByFeature(geoJsonFeatures, bbox);
        geoJsonFeatures = filteredFeatures;
    }

    // 커스텀 필터링
    if (customFilter) {
        geoJsonFeatures = geoJsonFeatures.filter((feature) => customFilter(feature));
    }

    // 키 설정
    geoJsonFeatures.forEach((feature) => {
        const data = { ...feature.values_ };
        data.key_ = feature.id_;
        feature.set("data", data);
    });

    const vectorSource = new VectorSource({
        projection: "EPSG:3857",
        features: geoJsonFeatures,
    });

    let objectLayer = null;

    if (featureType == "POINT") {
        const rowStyle = new Style({
            image: new CircleStyle({
                radius: 5,
                fill: new Fill({
                    color: "rgba(0,0,0,0.01)",
                }),
                stroke: new Stroke({
                    color: color,
                    width: 4,
                }),
            }),
        });

        const clusterSource = new ClusterSource({
            distance: 50,
            source: vectorSource,
        });

        objectLayer = new VectorLayer({
            source: clusterSource,
            type: type,
            layerType: "cluster",
            shpInfo: ele.shpInfo,
            zIndex: ele.shpInfo.zindex,
            selectStyle: {
                style: (layer, feature) =>
                    selectAnimation(layer, feature, (sWidth, sColor) => {
                        let style;
                        const size = feature.get("features").length;
                        const totalFeatures = geoJsonFeatures.length;
                        const proportion = size / totalFeatures;
                        const scalingFactor = 20; // Experiment with different scaling factors
                        const radius = Math.pow(proportion, 1 / 3) * scalingFactor + 10;
                        const currentZoom = map.getView().getZoom();
                        const maxZoom = map.getView().getMaxZoom();

                        if (currentZoom > maxZoom - 2) {
                            clusterSource.setDistance(0);
                            style = rowStyle;
                        } else {
                            clusterSource.setDistance(50);
                            style = new Style({
                                zIndex: 500,
                                image: new CircleStyle({
                                    radius: radius,
                                    fill: new Fill({
                                        color: color,
                                    }),
                                    stroke: new Stroke({
                                        color: sColor,
                                        width: sWidth,
                                    }),
                                }),
                                text: new Text({
                                    text: size.toString(),
                                    fill: new Fill({
                                        color: "#ffffff",
                                    }),
                                    stroke: new Stroke({
                                        color: "rgba(0,0,0,0.5)",
                                        width: 1,
                                    }),
                                    font: "14px sans-serif",
                                }),
                            });
                        }

                        return style;
                    }),
                clear: (layer) => {
                    const intervalList = layer.get("intervalList");

                    if (intervalList) {
                        intervalList.forEach((id) => {
                            clearInterval(id);
                        });
                    }
                },
            },

            style: function (feature) {
                let style;
                const size = feature.get("features").length;
                const totalFeatures = geoJsonFeatures.length;
                const proportion = size / totalFeatures;
                const scalingFactor = 20; // Experiment with different scaling factors
                const radius = Math.pow(proportion, 1 / 3) * scalingFactor + 10;
                const currentZoom = map.getView().getZoom();
                const maxZoom = map.getView().getMaxZoom();

                let imageStyle;

                if (!canvas) {
                    imageStyle = new CircleStyle({
                        radius: radius,
                        fill: new Fill({
                            color: color,
                        }),
                        stroke: new Stroke({
                            color: "#000000",
                            width: 1,
                        }),
                    });
                } else {
                    imageStyle = createCanvasIcon(canvas.color1, canvas.color2);
                }

                if (currentZoom > maxZoom - 2) {
                    clusterSource.setDistance(0);

                    style = rowStyle;
                } else {
                    clusterSource.setDistance(50);
                    style = new Style({
                        image: imageStyle,
                        text: new Text({
                            text: size.toString(),
                            fill: new Fill({
                                color: "#ffffff",
                            }),
                            stroke: new Stroke({
                                color: "rgba(0,0,0,0.5)",
                                width: 1,
                            }),
                            font: "14px sans-serif",
                        }),
                    });
                }

                feature.setStyle(style);

                return style;
            },
        });
    } else {
        let filColor = "rgba(51,47,47,0.01)";
        const pkOverlayList = [];
        if (shpInfo.type == "주차장" && (shpInfo.subType == "부설" || shpInfo.subType == "노외")) {
            const currentZoom = map.getView().getZoom();
            const maxZoom = map.getView().getMaxZoom();

            geoJsonFeatures.forEach((feature) => {
                let geometry = feature.getGeometry();
                let areaSquareMeters = getArea(geometry, { projection: getProj("EPSG:3857") });

                const extent = feature.getGeometry().getExtent();
                const center = getCenter(extent);
                const name = feature.get("주차장명");
                const val = feature.get("주차면수 전체") || 0;
                const val2WN = feature.get("이륜차 대수(야간)") || 0;
                const valNon2WN = feature.get("이륜차 외 주차 대수(야간)") || 0;
                const val2WD = feature.get("이륜차 대수(주간)") || 0;
                const valNon2WD = feature.get("이륜차 외 주차 대수(주간)") || 0;

                if (val > 30) {
                    const divNode = document.createElement("div");
                    divNode.className = `ol-pk_overlay ${currentZoom > maxZoom - 2 ? "on" : ""}`;
                    divNode.innerHTML = `
                        <div class="total">${val} (${parseInt(val2WD) + parseInt(valNon2WD)}/${
                        parseInt(val2WN) + parseInt(valNon2WN)
                    })</div>
                    `;

                    let overlay = new Overlay({
                        position: center,
                        element: divNode,
                        offset: [-30, -20],
                        type: "pk",
                    });

                    pkOverlayList.push(overlay);
                    map.addOverlay(overlay);
                }
            });
        }

        objectLayer = new VectorLayer({
            source: vectorSource,
            type: type,
            layerType: "vector",
            shpInfo: shpInfo,
            zIndex: shpInfo.zindex,
            noneClick: shpInfo.subType == "블럭경계",
            selectStyle: {
                type: "animation",
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
                        color: filColor,
                    }),
                    stroke: new Stroke({
                        color: color,
                        width: 2,
                    }),
                });

                if (shpInfo.subType == "블럭경계") {
                    let blockName = feature.values_["블럭명"] || feature.values_["JS_NAME"];

                    const textStyle = new Text({
                        text: blockName,
                        font: `10px Calibri,sans-serif`,
                        fill: new Fill({
                            color: "#ffffff",
                        }),
                        padding: [4, 4, 4, 4],
                        stroke: new Stroke({
                            color: "#000000",
                            width: 5,
                        }),
                    });

                    style.setText(textStyle);
                }

                feature.setStyle(style);

                return style;
            },
        });
    }

    return objectLayer;
}

export function createCanvasIcon(color1, color2) {
    const width = 30;
    const height = 30;
    const canvas = document.createElement("canvas");
    canvas.width = width;
    canvas.height = height;
    const context = canvas.getContext("2d");

    // Create a rounded path
    const radius = 15;
    context.beginPath();
    context.moveTo(radius, 0);
    context.arcTo(width, 0, width, height, radius);
    context.arcTo(width, height, 0, height, radius);
    context.arcTo(0, height, 0, 0, radius);
    context.arcTo(0, 0, width, 0, radius);
    context.closePath();
    context.clip();

    // Draw the gradient
    const gradient = context.createLinearGradient(10, 0, width, 20);
    gradient.addColorStop(0, color1);
    gradient.addColorStop(1, color2);
    context.fillStyle = gradient;
    context.fillRect(0, 0, width, height);

    // Draw the border
    const borderWidth = 1.5; // Adjust the border width as needed
    context.lineWidth = borderWidth;
    context.strokeStyle = "#000"; // Set the border color
    context.stroke();

    return new Icon({
        img: canvas,
        imgSize: [width, height],
    });
}

// 실태조사 이미지 레이어 생성
export function createImageLayer(map, ele, type) {
    const table = ele.shpInfo.tableName;
    const color = ele.shpInfo.color;
    const featureType = ele.shpInfo.featureType;
    const fillColor = rgbaToHex(color);

    const preloadSource = new TileWMS({
        url: "http://geoserver.atech1221.com/geoserver/postgres/wms",
        params: {
            LAYERS: `postgres:${table}`,
            FORMAT: "image/png",
            VERSION: "1.3.0",
            SLD_BODY: createSldBody(fillColor, table, featureType),
        },
    });

    const preloadLayer = new TileLayer({
        source: preloadSource,
        visible: true, // Set it to not visible
        type: type,
        zIndex: ele.shpInfo.zindex,
    });

    return preloadLayer;
}

// 레이어 삭제 (type 검색)
export function removeLayer(map, layerType) {
    const existingLayers = map.getLayers().getArray();
    for (let i = existingLayers.length - 1; i >= 0; i--) {
        const layer = existingLayers[i];

        if (layer.get("type") == layerType) {
            map.removeLayer(layer);
        }
    }
}

// 레이어 삭제 (type 검색)
export function removeOverlay(map, overlayType) {
    const overlays = map.getOverlays();

    for (let i = overlays.get("length") - 1; i >= 0; i--) {
        const overlay = overlays.item(i);

        if (overlay?.options.type == overlayType) {
            map.removeOverlay(overlay);
        }
    }
}

// 겹치는 피쳐 필터링 (피쳐 사용)
export function overlapFilterByFeature(features, filterFeature) {
    const filteredFeatures = [];
    for (const feature of features) {
        // 피처의 Geometry 가져오기
        const geometry = feature.getGeometry();

        // 피처의 bbox 가져오기
        const featureBbox = geometry.getExtent();

        if (filterFeature.getGeometry().intersectsExtent(featureBbox)) {
            // 겹치면 해당 피처를 선택
            filteredFeatures.push(feature);
        }
    }

    return filteredFeatures;
}

// 레이어 리스트 찾기
export function findLayerList(map, type, layerType) {
    const layers = [];

    const existingLayers = map.getLayers().getArray();
    for (let i = existingLayers.length - 1; i >= 0; i--) {
        const layer = existingLayers[i];

        if (type && layer.get("type") == type) {
            layers.push(layer);
        }

        if (layerType && layer.get("layerType") == layerType) {
            layers.push(layer);
        }
    }

    return layers;
}

// 레이어 위치로 화면 이동
export function moveFitLayer(map, layer) {
    const source = layer.getSource();
    const view = map.getView();

    function handleSourceChange() {
        const extent = source.getExtent();

        if (extent.every(isFinite)) {
            const maxZoom = view.getZoomForResolution(view.getResolutionForExtent(extent));

            view.fit(extent, {
                size: map.getSize(),
                maxZoom: maxZoom - 0.2,
            });
        }
    }

    handleSourceChange();
}

// 피쳐 위치로 화면 이동
export function moveFitFeature(map, feature) {
    const geometry = feature.getGeometry();
    const view = map.getView();

    function handleFeatureChange() {
        const extent = geometry.getExtent();

        if (extent.every(isFinite)) {
            const maxZoom = view.getZoomForResolution(view.getResolutionForExtent(extent));

            view.fit(extent, {
                size: map.getSize(),
                maxZoom: maxZoom - 0.2,
            });
        }
    }
    handleFeatureChange();
}

// 피쳐 클릭강조 이벤트
export function featureHighlightHandler(map, selectLayer, duplColList) {
    const restoreLayerStyles = [];

    selectLayer.forEach((ele) => {
        map.renderSync();
        const layer = ele.layer;
        const feature = ele.feature;
        let originStyleFn = layer.getStyle();
        let originStyle = feature.getStyle();
        let selectStyle = layer.get("selectStyle");
        if (!originStyle || !selectStyle || !originStyleFn) return;

        layer.setStyle((feature) => {
            let features = [];
            if (layer.get("layerType") == "cluster") {
                features = feature.get("features");
            } else {
                features.push(feature);
            }

            const findF = features.find((f) => {
                return f.get("data").key_ == duplColList[0][0].selectData.clusterFeature.get("data").key_;
            });

            if (findF) {
                selectStyle.style(layer, feature);
            } else {
                const newStyle = originStyleFn(feature);
                feature.setStyle(newStyle);
            }
        });

        layer
            .getSource()
            .getFeatures()
            .forEach((feature) => {
                let features = [];
                if (layer.get("layerType") == "cluster") {
                    features = feature.get("features");
                } else {
                    features.push(feature);
                }

                const findF = features.find((f) => {
                    return f.get("data").key_ == duplColList[0][0].selectData.clusterFeature.get("data").key_;
                });

                if (findF) {
                    selectStyle.style(layer, feature);
                }
            });

        restoreLayerStyles.push({
            layer,
            originStyleFn,
            originStyle,
            clear: selectStyle.clear,
        });
    });

    return () => {
        restoreLayerStyles.forEach(({ layer, originStyleFn, originStyle, clear }) => {
            layer.setStyle(originStyleFn);

            layer
                .getSource()
                .getFeatures()
                .forEach((feature) => {
                    const newStyle = originStyleFn(feature);
                    feature.setStyle(newStyle);
                });

            if (clear) {
                clear(layer);
            }
        });
    };
}

// 선택된 피쳐 애니메이션
export function selectAnimation(layer, feature, styleFn) {
    let animationIntervalId;
    const duration = 200;
    const initialStrokeWidth = 2;
    const finalStrokeWidth = 5;
    let increasing = true;
    let currentStrokeWidth = initialStrokeWidth;
    let color = "#226010";

    animationIntervalId = setInterval(() => {
        try {
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

            if (!styleFn) {
                let clonedStyle = feature.getStyle().clone();
                clonedStyle.setZIndex(500);
                clonedStyle.getStroke().setWidth(currentStrokeWidth);
                clonedStyle.getStroke().setColor(color);

                feature.setStyle(clonedStyle);
            } else {
                let clonedStyle = styleFn(currentStrokeWidth, color);
                feature.setStyle(clonedStyle);
            }
        } catch (e) {
            console.log(e);
        }
    }, duration / 10);

    let intervalList = [];

    if (layer.get("intervalList")) {
        intervalList = layer.get("intervalList");
        intervalList.push(animationIntervalId);
    } else {
        intervalList.push(animationIntervalId);
    }

    layer.set("intervalList", intervalList);
}

export function colorTxtToObject(color) {
    if (color) {
        const matches = color.match(/(\d+(\.\d+)?)/g);
        const [r, g, b, a] = matches.map(parseFloat);

        const rgbaObject = {
            rgb: {
                r,
                g,
                b,
                a,
            },
            rgbToText: ({ r, g, b, a }) => {
                return `rgba(${r},${g},${b},${a})`;
            },
        };
        return rgbaObject;
    } else {
        return {
            rgb: {
                r: 255,
                g: 255,
                b: 255,
                a: 1,
            },
            rgbToText: ({ r, g, b, a }) => {
                return `rgba(${r},${g},${b},${a})`;
            },
        };
    }
}

// 선택된 피쳐 애니메이션
export function borderAnimation(
    layer,
    feature,
    rgbaColor,
    rgbaInitA,
    rgbaFinalA,
    duration,
    initialStrokeWidth,
    finalStrokeWidth,
    styleFn
) {
    let animationIntervalId;
    let increasing = true;
    let increasing2 = true;
    let currentStrokeWidth = initialStrokeWidth;
    let currentColor = rgbaColor;

    animationIntervalId = setInterval(() => {
        try {
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

            if (increasing2) {
                const rgbaObj = colorTxtToObject(currentColor);
                rgbaObj.rgb.a += 0.02;
                rgbaObj.rgb.a = rgbaObj.rgb.a.toFixed(2);
                currentColor = rgbaObj.rgbToText(rgbaObj.rgb);

                if (rgbaObj.rgb.a >= rgbaFinalA) {
                    increasing2 = false;
                }
            } else {
                const rgbaObj = colorTxtToObject(currentColor);
                rgbaObj.rgb.a -= 0.02;
                rgbaObj.rgb.a = rgbaObj.rgb.a.toFixed(2);
                currentColor = rgbaObj.rgbToText(rgbaObj.rgb);

                if (rgbaObj.rgb.a <= rgbaInitA) {
                    increasing2 = true;
                }
            }

            styleFn(currentStrokeWidth, currentColor);
        } catch (e) {
            console.log(e);
        }
    }, duration / 10);

    let intervalList = [];

    if (layer.get("intervalList")) {
        intervalList = layer.get("intervalList");
        intervalList.push(animationIntervalId);
    } else {
        intervalList.push(animationIntervalId);
    }

    layer.set("intervalList", intervalList);
}

// 전화번호 하이푼 넣기
export function formatPhoneNumber(phoneNumber) {
    const cleaned = phoneNumber.replace(/\D/g, ""); // Remove non-numeric characters

    if (cleaned.length === 10) {
        // Format 10-digit number
        return cleaned.replace(/(\d{3})(\d{3})(\d{4})/, "$1-$2-$3");
    } else if (cleaned.length === 11) {
        // Format 11-digit number
        return cleaned.replace(/(\d{3})(\d{4})(\d{4})/, "0$1-$2-$3");
    }

    // Return original if the format doesn't match
    return phoneNumber;
}

// 구군코드 => 구군명칭
export function gugunParseCodeToName(code) {
    if (code == "31110") return "중구";
    if (code == "31140") return "남구";
    if (code == "31170") return "동구";
    if (code == "31200") return "북구";
    if (code == "31710") return "울주군";
}

// 커스텀 컨트롤 등록
export function initCustomControl(map, options) {
    class CustomControl extends Control {
        constructor(options) {
            super({
                element: options.target.current,
                target: "",
            });
        }
    }

    map.addControl(new CustomControl(options));
}

export function createEmptyFeatureCollection() {
    const featureCollectionTemplate = {
        type: "FeatureCollection",
        features: [],
    };

    return featureCollectionTemplate;
}
