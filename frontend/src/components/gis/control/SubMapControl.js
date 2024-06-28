import React, { useEffect, useRef, useState } from "react";
import { Control } from "ol/control";
import { removeLayer } from "../CommonGisFunction";
import { Tile as TileLayer } from "ol/layer";
import mapOption from "../mapOption";

function CustomBackgroundControl({ map }) {
    const [mapBgLayer, setMapBgLayer] = useState("vBase"); // 선택 베이스 지도
    const ref = useRef(null);

    class CustomControl extends Control {
        constructor({ ref }) {
            super({
                element: ref.target.current,
                target: null,
            });
        }
    }

    useEffect(() => {
        if (map && ref.target) {
            const customControl = new CustomControl({ ref: ref });
            map.addControl(customControl);

            return () => {
                map.removeControl(customControl);
            };
        }
    }, [map, ref]);

    // 배경지도 변경
    useEffect(() => {
        if (map) {
            removeLayer(map, "tileLayer");

            const mapLayer = new TileLayer({
                source: mapOption.mapBgList[mapBgLayer].source,
                type: "tileLayer",
                zIndex: -1,
            });

            map.addLayer(mapLayer);
        }
    }, [map, mapBgLayer]);

    return {
        BackgroundControl: (
            <div className={"ol-control background-control"} ref={ref}>
                <ul>
                    {Object.keys(mapOption.mapBgList).map((key) => {
                        return (
                            <li
                                key={key}
                                onClick={() => {
                                    setMapBgLayer(key);
                                }}
                            >
                                <img src={mapOption.mapBgList[key].Image} />
                                <p>{mapOption.mapBgList[key].name}</p>
                            </li>
                        );
                    })}
                </ul>
            </div>
        ),
        mapBgLayer,
        setMapBgLayer,
    };
}

export default CustomBackgroundControl;
