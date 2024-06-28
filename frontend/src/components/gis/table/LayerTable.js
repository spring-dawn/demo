import React, { useState, useEffect, useRef } from "react";

function LayerTable({ dataStore, isNight }) {
    const [tmpDataStore, setTmpDataStore] = useState({});

    useEffect(() => {
        const tmp = {};

        Object.keys(dataStore).forEach((key) => {
            tmp[key] = String(dataStore[key]).toLocaleString();
        });

        setTmpDataStore(tmp);
    }, [dataStore]);

    return (
        <table className="data_table">
            <tbody>
                <tr className="col_group2">
                    <td colSpan={2} className="header1">
                        주차시설(노상)
                    </td>
                    <td colSpan={2} className="header1">
                        주차시설(노외)
                    </td>
                    <td colSpan={2} className="header1">
                        주차시설(부설)
                    </td>
                    <td colSpan={2} className="header1" rowSpan={3}>
                        주차시설 합계
                    </td>
                </tr>
                <tr className="col_group2">
                    <td colSpan={1} className="header2">
                        거주자
                    </td>
                    <td colSpan={1}>{tmpDataStore["pfRDResi"]}</td>
                    <td colSpan={1} className="header2">
                        공영
                    </td>
                    <td colSpan={1}>{tmpDataStore["pfOutPub"]}</td>
                    <td colSpan={1} className="header2">
                        주거
                    </td>
                    <td colSpan={1}>{tmpDataStore["pfSubResi"]}</td>
                </tr>
                <tr className="col_group2">
                    <td colSpan={1} className="header2">
                        그외
                    </td>
                    <td colSpan={1}>{tmpDataStore["pfRDEtc"]}</td>
                    <td colSpan={1} className="header2">
                        민영
                    </td>
                    <td colSpan={1}>{tmpDataStore["pfOutPri"]}</td>
                    <td colSpan={1} className="header2">
                        비주거
                    </td>
                    <td colSpan={1}>{tmpDataStore["pfSubNonRegi"]}</td>
                </tr>
                <tr className="col_group2">
                    <td colSpan={1} className="header2">
                        소계
                    </td>
                    <td colSpan={1}>{tmpDataStore["pfRDSum"]}</td>
                    <td colSpan={1} className="header2">
                        소계
                    </td>
                    <td colSpan={1}>{tmpDataStore["pfOutSum"]}</td>
                    <td colSpan={1} className="header2">
                        소계
                    </td>
                    <td colSpan={1}>{tmpDataStore["pfSubSum"]}</td>
                    <td colSpan={2}>{tmpDataStore["pfTotal"]}</td>
                </tr>

                <tr className="col_group3">
                    <td colSpan={7} className="header3 ">
                        {isNight ? "야간" : "주간"}
                    </td>
                </tr>
                <tr className="col_group3">
                    <td colSpan={2} className="header1">
                        주사수요(노상)
                    </td>
                    <td colSpan={2} className="header1">
                        주차수요(노외)
                    </td>
                    <td colSpan={2} className="header1">
                        주차수요(부설)
                    </td>
                    <td colSpan={2} rowSpan={4} className="header1">
                        주차수요 합계
                    </td>
                </tr>
                <tr className="col_group3">
                    <td colSpan={1} className="header2">
                        구획내
                    </td>
                    <td colSpan={1}>{tmpDataStore["pdRDIn"]}</td>
                    <td colSpan={1} className="header2">
                        공영
                    </td>
                    <td colSpan={1}>{tmpDataStore["pdOutPub"]}</td>
                    <td colSpan={1} className="header2">
                        주거
                    </td>
                    <td colSpan={1}>{tmpDataStore["pdSubResi"]}</td>
                </tr>
                <tr className="col_group3">
                    <td colSpan={1} className="header2">
                        구획외
                    </td>
                    <td colSpan={1}>{tmpDataStore["pdRDOut"]}</td>
                    <td colSpan={1} rowSpan={2} className="header2">
                        민영
                    </td>
                    <td colSpan={1} rowSpan={2}>
                        {tmpDataStore["pdOutPri"]}
                    </td>
                    <td colSpan={1} rowSpan={2} className="header2">
                        비주거
                    </td>
                    <td colSpan={1} rowSpan={2}>
                        {tmpDataStore["pdSubNonRegi"]}
                    </td>
                </tr>
                <tr className="col_group3">
                    <td className="header2">불법</td>
                    <td>{tmpDataStore["pdRDIll"]}</td>
                </tr>
                <tr className="col_group3">
                    <td colSpan={1} className="header2">
                        소계
                    </td>
                    <td colSpan={1}>{tmpDataStore["pdRDSum"]}</td>
                    <td colSpan={1} className="header2">
                        소계
                    </td>
                    <td colSpan={1}>{tmpDataStore["pdOutSum"]}</td>
                    <td colSpan={1} className="header2">
                        소계
                    </td>
                    <td colSpan={1}>{tmpDataStore["pdSubSum"]}</td>
                    <td colSpan={2}>{tmpDataStore["pdTotal"]}</td>
                </tr>

                <tr className="col_group3">
                    <td colSpan={4} className="header1">
                        주차장 확보율(%)
                    </td>
                    <td colSpan={4} className="header1">
                        주차장 과부족(대)
                    </td>
                </tr>
                <tr className="col_group3">
                    <td colSpan={4}>{tmpDataStore.PK1}%</td>
                    <td colSpan={4}>{tmpDataStore.PK2}</td>
                </tr>

                <tr className="col_group3">
                    <td colSpan={2} className="header1">
                        주차장 이용률
                    </td>
                    <td colSpan={2} className="header1">
                        불법 주차율
                    </td>
                    <td colSpan={4} className="header1">
                        유휴 부설주차규모
                    </td>
                </tr>
                <tr className="col_group3">
                    <td colSpan={1} className="header2">
                        전체
                    </td>
                    <td colSpan={1}>{tmpDataStore.PK3}%</td>
                    <td colSpan={1} rowSpan={2} className="header2">
                        전체수요 대비
                    </td>
                    <td colSpan={1} rowSpan={2}>
                        {tmpDataStore.PK7}%
                    </td>
                    <td colSpan={2} rowSpan={4} className="header2">
                        전체
                    </td>
                    <td colSpan={1} rowSpan={4}>
                        {tmpDataStore.PK9}
                    </td>
                </tr>
                <tr className="col_group3">
                    <td colSpan={1} className="header2">
                        노상
                    </td>
                    <td colSpan={1}>{tmpDataStore.PK4}%</td>
                </tr>
                <tr className="col_group3">
                    <td colSpan={1} className="header2">
                        노외
                    </td>
                    <td colSpan={1}>{tmpDataStore.PK5}%</td>
                    <td colSpan={1} rowSpan={2} className="header2">
                        노상수요 대비
                    </td>
                    <td colSpan={1} rowSpan={2}>
                        {tmpDataStore.PK8}%
                    </td>
                </tr>
                <tr className="col_group3">
                    <td colSpan={1} className="header2">
                        부설
                    </td>
                    <td colSpan={1}>{tmpDataStore.PK6}%</td>
                </tr>
            </tbody>
        </table>
    );
}

export default LayerTable;
