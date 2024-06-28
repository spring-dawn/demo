import React, { useEffect } from "react";
import { Tooltip } from "antd";
import { gugunParseCodeToName } from "../../../CommonFunction";

// 불법 주정차 단속
function PkIllegal({ gugun, bus, fire, protect }) {
    // 데이터 가져오는 코드
    useEffect(() => {
        fetch("/api/data/illegal/prfmnc")
            .then((response) => response.json())
            .then((apiData) => {
                console.log("final:", apiData);
            });
    }, []);

    const createTable = () => {
        return (
            <tr>
                <td>{gugun == "all" ? "울산광역시" : gugunParseCodeToName(gugun)}</td>
                <td>
                    {gugun === "all" ? (
                        <>
                            {bus.reduce((sum, item) => sum + Number(item.crdnNocs || 0), 0).toLocaleString()}/
                            {bus.reduce((sum, item) => sum + Number(item.levyAmt || 0), 0).toLocaleString()}
                        </>
                    ) : (
                        <>
                            {Number(bus.find((item) => item.sgg === gugun)?.crdnNocs || 0).toLocaleString()}/
                            {Number(bus.find((item) => item.sgg === gugun)?.levyAmt || 0).toLocaleString()}
                        </>
                    )}
                </td>
                <td>
                    {gugun === "all"
                        ? fire.reduce((sum, item) => sum + Number(item.afterCrdnNocs || 0), 0).toLocaleString()
                        : Number(fire.find((item) => item.sgg === gugun)?.afterCrdnNocs || 0).toLocaleString()}
                </td>
                <td>
                    {gugun === "all" ? (
                        <>
                            {protect.reduce((sum, item) => sum + Number(item.nocs || 0), 0).toLocaleString()}/
                            {protect.reduce((sum, item) => sum + Number(item.amt || 0), 0).toLocaleString()}
                        </>
                    ) : (
                        <>
                            {Number(protect.find((item) => item.sgg === gugun)?.nocs || 0).toLocaleString()}/
                            {Number(protect.find((item) => item.sgg === gugun)?.amt || 0).toLocaleString()}
                        </>
                    )}
                </td>
            </tr>
        );
    };

    return (
        <>
            <table className="data_table">
                <thead>
                    <tr>
                        <th colSpan={1}>구분</th>
                        <Tooltip
                            placement="top"
                            title={
                                <div style={{ color: "black" }}>
                                    <b>{"단위 : 건/천원"}</b>
                                </div>
                            }
                            color={"white"}
                        >
                            <th width={"30%"}>버스탑재형 (단속건수/과태료)</th>
                        </Tooltip>
                        <Tooltip
                            placement="top"
                            title={
                                <div style={{ color: "black" }}>
                                    <b>{"단위 : 건"}</b>
                                </div>
                            }
                            color={"white"}
                        >
                            <th width={"30%"}>소화전 주변 (단속건수)</th>
                        </Tooltip>
                        <Tooltip
                            placement="top"
                            title={
                                <div style={{ color: "black" }}>
                                    <b>{"단위 : 천원"}</b>
                                </div>
                            }
                            color={"white"}
                        >
                            <th width={"30%"}>어린이보호구역 (단속건수/과태료)</th>
                        </Tooltip>
                    </tr>
                </thead>
                <tbody>{createTable()}</tbody>
            </table>
        </>
    );
}

export default PkIllegal;
