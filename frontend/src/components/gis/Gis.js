import React, { useState, useEffect, useRef } from "react";
import GisMap from "./GisMap";
import { useGisSize } from "./CommonGisHook";
import { atom } from "recoil";

function Gis({ mapId, subMapData, setSubMapData, blank_tab, blank_year }) {
    const ref = useRef(); // 사용자 지정 필터링 컨트롤 ref
    const [selectedSearchTab, setSelectedSearchTab] = useState(blank_tab ? blank_tab : "current"); // 선택한 탭
    const { parentSize, setParentSize, setSizeRender, sizeRender } = useGisSize({ ref: ref, deps: [subMapData] });

    return (
        <div className="mapWrap" key={mapId} ref={ref}>
            <div className={"mapBody"}>
                <GisMap
                    mapId={mapId}
                    parentSize={parentSize}
                    subMapData={subMapData}
                    setSubMapData={setSubMapData}
                    selectedSearchTab={selectedSearchTab}
                    blank_year={blank_year}
                    setSizeRender={setSizeRender}
                />
            </div>
        </div>
    );
}

export default Gis;
