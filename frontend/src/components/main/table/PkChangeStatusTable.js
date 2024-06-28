import React, { useState, useEffect } from "react";

// 주차장 변경 현황
function PkStatusTable({ gugun, monthData }) {
    // 공영 개소수
    const pblrdL = Object.keys(monthData).length
        ? monthData?.PBLRD_PAY_L_I +
          monthData?.PBLRD_FREE_L_I +
          monthData?.PBLRD_RESI_L_I +
          monthData?.PBLOUT_PAY_L_I +
          monthData?.PBLOUT_FREE_L_I -
          monthData?.PBLRD_PAY_L_D -
          monthData?.PBLRD_FREE_L_D -
          monthData?.PBLRD_RESI_L_D -
          monthData?.PBLOUT_PAY_L_D -
          monthData?.PBLOUT_FREE_L_D
        : 0;

    // 공영 주차면수
    const pblrdS = Object.keys(monthData).length
        ? monthData?.PBLRD_PAY_S_I +
          monthData?.PBLRD_FREE_S_I +
          monthData?.PBLRD_RESI_S_I +
          monthData?.PBLOUT_PAY_S_I +
          monthData?.PBLOUT_FREE_S_I -
          monthData?.PBLRD_PAY_S_D -
          monthData?.PBLRD_FREE_S_D -
          monthData?.PBLRD_RESI_S_D -
          monthData?.PBLOUT_PAY_S_D -
          monthData?.PBLOUT_FREE_S_D
        : 0;

    // 민영 개소수
    const prvL = Object.keys(monthData).length ? monthData?.PRV_L_I - monthData?.PRV_L_D : 0;

    // 민영 주차면수
    const prvS = Object.keys(monthData).length ? monthData?.PRV_S_I - monthData?.PRV_S_D : 0;

    // 부설 개소수
    const subseL = Object.keys(monthData).length
        ? monthData?.SUBSE_SUR_L_I +
          monthData?.SUBSE_MOD_L_I +
          monthData?.SUBAU_ATT_L_I +
          monthData?.SUBAU_PRV_L_I -
          monthData?.SUBSE_SUR_L_D -
          monthData?.SUBSE_MOD_L_D -
          monthData?.SUBAU_ATT_L_D -
          monthData?.SUBAU_PRV_L_D
        : 0;

    // 부설 주차면수
    const subseS = Object.keys(monthData).length
        ? monthData?.SUBSE_SUR_S_I +
          monthData?.SUBSE_MOD_S_I +
          monthData?.SUBAU_ATT_S_I +
          monthData?.SUBAU_PRV_S_I -
          monthData?.SUBSE_SUR_S_D -
          monthData?.SUBSE_MOD_S_D -
          monthData?.SUBAU_ATT_S_D -
          monthData?.SUBAU_PRV_S_D
        : 0;

    return (
        <>
            <table className="data_table2">
                <tbody>
                    <tr>
                        <th width={"30%"}>공영</th>
                        <td>{`${pblrdL.toLocaleString()}개소 ${pblrdS.toLocaleString()}면 증가`}</td>
                    </tr>
                    <tr>
                        <th>민영</th>
                        <td>{`${prvL.toLocaleString()}개소 ${prvS.toLocaleString()}면 증가`}</td>
                    </tr>
                    <tr>
                        <th>부설</th>
                        <td>{`${subseL.toLocaleString()}개소 ${subseS.toLocaleString()}면 증가`}</td>
                    </tr>
                </tbody>
            </table>
        </>
    );
}

export default PkStatusTable;
