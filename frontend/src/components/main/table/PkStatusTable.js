import React from "react";
import { gugunParseCodeToName } from "../../../CommonFunction";
import { Tooltip } from "antd";

// 주차장 확보 현황
function PkStatusTable({ gugun, monthData }) {
    return (
        <>
            <table className="data_table">
                <thead>
                    <tr>
                        <th rowSpan={2} width="10%">
                            구분
                        </th>
                        <th colSpan={2} width="30%">
                            계
                        </th>
                        <th colSpan={2} width="20%">
                            공영
                        </th>
                        <th colSpan={2} width="20%">
                            민영
                        </th>
                        <th colSpan={2} width="20%">
                            부설
                        </th>
                    </tr>
                    <tr>
                        <th width="15%">개소</th>
                        <Tooltip
                            placement="top"
                            title={
                                <div style={{ color: "black" }}>
                                    <b>{"단위 : 면"}</b>
                                </div>
                            }
                            color={"white"}
                        >
                            <th width="15%">면수</th>
                        </Tooltip>

                        <th width="10%">개소</th>
                        <Tooltip
                            placement="top"
                            title={
                                <div style={{ color: "black" }}>
                                    <b>{"단위 : 면"}</b>
                                </div>
                            }
                            color={"white"}
                        >
                            <th width="10%">면수</th>
                        </Tooltip>

                        <th width="10%">개소</th>
                        <Tooltip
                            placement="top"
                            title={
                                <div style={{ color: "black" }}>
                                    <b>{"단위 : 면"}</b>
                                </div>
                            }
                            color={"white"}
                        >
                            <th width="10%">면수</th>
                        </Tooltip>

                        <th width="10%">개소</th>
                        <Tooltip
                            placement="top"
                            title={
                                <div style={{ color: "black" }}>
                                    <b>{"단위 : 면"}</b>
                                </div>
                            }
                            color={"white"}
                        >
                            <th width="10%">면수</th>
                        </Tooltip>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>{gugun == "all" ? "울산광역시" : gugunParseCodeToName(gugun)}</td>
                        <td>{(monthData.TOTAL_L_SUM || 0).toLocaleString()}</td>
                        <td>{(monthData.TOTAL_S_SUM || 0).toLocaleString()}</td>

                        <td>{(monthData.PBL_L_SUBTOTAL || 0).toLocaleString()}</td>
                        <td>{(monthData.PBL_S_SUBTOTAL || 0).toLocaleString()}</td>

                        <td>{(monthData.PRV_L_SUM || 0).toLocaleString()}</td>
                        <td>{(monthData.PRV_S_SUM || 0).toLocaleString()}</td>

                        <td>{(monthData.SUB_L_SUBTOTAL || 0).toLocaleString()}</td>
                        <td>{(monthData.SUB_S_SUBTOTAL || 0).toLocaleString()}</td>
                    </tr>
                </tbody>
            </table>
        </>
    );
}

export default PkStatusTable;
