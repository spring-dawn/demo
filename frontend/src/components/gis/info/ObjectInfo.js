import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faCircleRight, faXmark } from "@fortawesome/free-solid-svg-icons";
import React, { useEffect, useRef } from "react";
import { moveFitFeature } from "../CommonGisFunction";

function ObjectInfo({ map, setObjectInfoRef, objectInfoCol, setObjectInfoCol, setSelectLayer }) {
    const ref = useRef(null); // 인포창 dom

    useEffect(() => {
        setObjectInfoRef(ref);
    }, [ref]);

    return (
        <div id="objectInfo" ref={ref} className={!objectInfoCol.length ? "hide" : ""}>
            <div className="table_header">
                <div className="table_tit">
                    {objectInfoCol.find((ele) => ele.key) ? objectInfoCol.find((ele) => ele.key).keyName : ""}
                    <div
                        className="move"
                        onClick={() => {
                            const selectData = objectInfoCol.find((ele) => ele.key).selectData;
                            const feature = selectData.clusterFeature;

                            moveFitFeature(map, feature);
                        }}
                    >
                        <FontAwesomeIcon icon={faCircleRight} />
                    </div>
                </div>
                <div style={{ display: "flex", alignItems: "center" }}>
                    {objectInfoCol.find((ele) => ele.key) &&
                        objectInfoCol.find((ele) => ele.key).selectData.layer.values_.shpInfo["출처"] && (
                            <div style={{ fontSize: "0.7em", marginRight: "6px", color: "black" }}>{`출처: ${
                                objectInfoCol.find((ele) => ele.key).selectData.layer.values_.shpInfo["출처"]
                            }`}</div>
                        )}
                    <FontAwesomeIcon
                        icon={faXmark}
                        style={{ cursor: "pointer" }}
                        onClick={() => {
                            setObjectInfoCol([]);
                            setSelectLayer([]);
                        }}
                    />
                </div>
            </div>
            <ul className="table scroll">
                {objectInfoCol.map((ele, idx) => {
                    if (ele.key) return;

                    return (
                        <li key={ele.col}>
                            <div>{ele.col}</div>
                            <div>{ele.val}</div>
                        </li>
                    );
                })}
            </ul>
        </div>
    );
}

export default ObjectInfo;
