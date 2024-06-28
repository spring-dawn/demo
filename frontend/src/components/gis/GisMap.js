import React, { useState, useEffect, useRef } from "react";

import { Map as OlMap, Feature } from "ol";
import { defaults as controlDefaults, ScaleLine } from "ol/control";

import mapOption from "./mapOption";
import CurrentMap from "./map/CurrentMap";
import LayerMap from "./map/LayerMap";
import CardMap from "./map/CardMap";
import CustomBackgroundControl from "./control/BackgroundControl";
import CustomFilterRangeControl from "./control/FilterRangeControl";

// GIS 공통 기능 관리 (맵 기본 생성, 상단 유틸, 공통 레이어)
function GisMap({ mapId, subMapData, setSubMapData, selectedSearchTab, blank_year, parentSize, setSizeRender }) {
    const [gisMenuMode, setGisMenuMode] = useState("고정"); // GIS 사이드 메뉴 모드
    const [mapObj, setMapObj] = useState({}); // 맵 객체

    // 실태조사, 관리카드 탭 같이 사용
    const [mapData, setMapData] = useState({}); // 실태조사 데이터 (캐싱작업)

    // 공통 컨트롤러
    const { range, setRange, drawOn, setDrawOn, objectBBOX, setObjectBBOX, FilterRangeControl } =
        CustomFilterRangeControl({ map: mapObj.map });
    const { mapBgLayer, setMapBgLayer, isHide, setIsHide, BackgroundControl } = CustomBackgroundControl({
        map: mapObj.map,
    });

    // 맵 생성
    let map;
    useEffect(() => {
        // 기본 맵 생성
        map = new OlMap({
            controls: controlDefaults().extend([new ScaleLine({ units: "metric" })]),
            target: mapId,
        });

        // 기본 뷰 생성
        map.setView(mapOption.view);

        setMapObj({ map: map });

        // 뷰 사이즈 버그로 일정 시간 후에 화면 꽉차게
        const intervalId = setInterval(() => {
            map.updateSize();
            setSizeRender((prev) => !prev);
        }, 500);

        setRange(-1);

        return () => {
            map.setTarget(undefined);
            clearInterval(intervalId);
        };
    }, [selectedSearchTab]);

    // gis 반응형
    useEffect(() => {
        if (mapId == "mainMap" && parentSize.width < 1200) {
            setIsHide(true);
        } else if (parentSize.width < 700) {
            setIsHide(true);
        } else {
            setIsHide(false);
        }
    }, [parentSize.width]);

    return (
        <div className={`gis_wrap`}>
            {/* 공통 */}
            <div id={mapId} className="map_view" style={{ width: "100%", height: "100%" }}></div>
            {FilterRangeControl}
            {BackgroundControl}

            {/* 현황 */}
            {selectedSearchTab == "current" && (
                <CurrentMap
                    parentSize={parentSize}
                    mapObj={mapObj}
                    gisMenuMode={gisMenuMode}
                    setRange={setRange}
                    objectBBOX={objectBBOX}
                    drawOn={drawOn}
                    subMapData={subMapData}
                    setSubMapData={setSubMapData}
                />
            )}
            {/* 실태조사 */}
            {selectedSearchTab == "layer" && (
                <LayerMap
                    parentSize={parentSize}
                    mapObj={mapObj}
                    gisMenuMode={gisMenuMode}
                    setRange={setRange}
                    objectBBOX={objectBBOX}
                    drawOn={drawOn}
                    blank_year={blank_year}
                    mapData={mapData}
                    setMapData={setMapData}
                />
            )}
            {/* 관리카드 */}
            {/*{selectedSearchTab == "card" && (*/}
            {/*    <CardMap*/}
            {/*        mapObj={mapObj}*/}
            {/*        gisMenuMode={gisMenuMode}*/}
            {/*        setRange={setRange}*/}
            {/*        objectBBOX={objectBBOX}*/}
            {/*        drawOn={drawOn}*/}
            {/*        blank_year={blank_year}*/}
            {/*        mapData={mapData}*/}
            {/*        setMapData={setMapData}*/}
            {/*    />*/}
            {/*)}*/}
        </div>
    );
}

export default GisMap;
