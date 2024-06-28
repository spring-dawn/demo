import React from "react";
import { Tooltip } from "antd";
import { gugunParseCodeToName } from "../../../CommonFunction";

// 주차장 확보율
function PkSecurementRate({ gugun, monthData, carData }) {
    const createTable = () => {
        // 차량등록대수
        const carNum = carData.cnt || 0;

        // 주차장면수
        const parkNum = monthData.TOTAL_S_SUM || 0;

        // 주차장 확보율
        let rate = (parkNum / carNum) * 100;

        if (!carNum || !parkNum) {
            rate = 0;
        }

        return (
            <tr>
                <td>{gugun == "all" ? "울산광역시" : gugunParseCodeToName(gugun)}</td>
                <td>{Number(carNum).toLocaleString()}</td>
                <td>{(monthData.TOTAL_S_SUM || 0).toLocaleString()}</td>
                <td>{rate.toFixed(1)}</td>
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
                                    <b>{"단위 : 대"}</b>
                                </div>
                            }
                            color={"white"}
                        >
                            <th width={"30%"}>차량등록대수</th>
                        </Tooltip>
                        <Tooltip
                            placement="top"
                            title={
                                <div style={{ color: "black" }}>
                                    <b>{"단위 : 면"}</b>
                                </div>
                            }
                            color={"white"}
                        >
                            <th width={"30%"}>주차장 면수</th>
                        </Tooltip>
                        <Tooltip
                            placement="top"
                            title={
                                <div style={{ color: "black" }}>
                                    <b>{"단위 : %"}</b>
                                </div>
                            }
                            color={"white"}
                        >
                            <th width={"30%"}>주차장 확보율</th>
                        </Tooltip>
                    </tr>
                </thead>
                <tbody>{createTable()}</tbody>
            </table>
        </>
    );
}

export default PkSecurementRate;
