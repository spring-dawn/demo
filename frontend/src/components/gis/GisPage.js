import React, { useState, useEffect } from "react";
import { useParams } from "react-router-dom";
import proj4 from "proj4/dist/proj4";
import { register } from "ol/proj/proj4";

import Gis from "./Gis";

// import "ol/ol.css";
// import "../../assets/scss/gis.scss";

// GIS 페이지 관리 (메인 페이지, 서브 페이지, 화면 분할)
function GisPage() {
    const [subMapData, setSubMapData] = useState(null); // 지도 분할 수
    const { blank_tab, blank_year } = useParams(); // 새창 분류, 연도

    const updatedBlankTab = blank_tab || (subMapData && subMapData.blank_tab) || null;
    const updatedBlankYear = blank_year || (subMapData && subMapData.blank_year) || null;

    // 좌표계 추가
    proj4.defs([
        ["EPSG:5187", "+proj=tmerc +lat_0=38 +lon_0=129 +k=1 +x_0=200000 +y_0=600000 +ellps=GRS80 +units=m +no_defs"],
    ]);
    register(proj4);

    return (
        <div id="gisPage" className={`${blank_tab && blank_year ? "blank" : ""}`}>
            <Gis
                mapId={"mainMap"}
                subMapData={subMapData}
                setSubMapData={setSubMapData}
                blank_tab={updatedBlankTab}
                blank_year={updatedBlankYear}
            />
            {subMapData && (
                <>
                    <div className="border"></div>
                    <Gis mapId={"subMap"} blank_tab={updatedBlankTab} blank_year={updatedBlankYear} />
                </>
            )}
        </div>
    );
}

export default GisPage;
