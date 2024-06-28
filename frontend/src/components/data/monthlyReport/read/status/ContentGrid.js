import { React, useEffect, useState, useCallback } from "react";
import { Tooltip } from "antd";
export default function ContentGrid(props) {
    const { mode, data, setFormData } = props;
    const [sumValue, setSumValue] = useState({});
    const [formInputData, setFormInputData] = useState({});

    const handleInputChange = (event) => {
        const { name, value } = event.target;

        const formData = {
            ...formInputData,
            status: {
                ...formInputData.status,
                [name]: Number(value),
            },
        };

        setFormInputData(formData);
        setFormData(formData);
    };

    const valueFormat = (key) => {
        if (!formInputData.status) {
            return "";
        } else {
            return formInputData.status[key];
        }
    };

    useEffect(() => {
        setFormInputData(data ? { ...data } : {});
    }, [data]);

    useEffect(() => {
        if (formInputData?.status) {
            const sum = {};
            const prev = formInputData.prevMonth;
            const status = formInputData.status;
            const typeArr = ["PBL", "PRV", "SUB", "OWN"];

            // 금월실적 단일 ROW (계) 계산
            Object.keys(status).forEach((key) => {
                if (key.slice(-1) == "I") {
                    const keyValue = key.slice(0, -2);

                    sum[keyValue + "_SUM"] = status[keyValue + "_I"] - status[keyValue + "_D"];
                }
            });

            // 금월실적 소계 (증, 감) 계산
            typeArr.forEach((type) => {
                let typeLISum = 0;
                let typeLDSum = 0;
                let typeSISum = 0;
                let typeSDSum = 0;
                let typeAISum = 0;
                let typeADSum = 0;

                Object.keys(status).forEach((key) => {
                    if (key.includes(type) && key.includes("L_I")) {
                        typeLISum += status[key];
                    } else if (key.includes(type) && key.includes("L_D")) {
                        typeLDSum += status[key];
                    } else if (key.includes(type) && key.includes("S_I")) {
                        typeSISum += status[key];
                    } else if (key.includes(type) && key.includes("S_D")) {
                        typeSDSum += status[key];
                    } else if (key.includes(type) && key.includes("A_I")) {
                        typeAISum += status[key];
                    } else if (key.includes(type) && key.includes("A_D")) {
                        typeADSum += status[key];
                    }
                });

                sum[type + "_L_I_SUM"] = typeLISum;
                sum[type + "_L_D_SUM"] = typeLDSum;
                sum[type + "_S_I_SUM"] = typeSISum;
                sum[type + "_S_D_SUM"] = typeSDSum;
                sum[type + "_A_I_SUM"] = typeAISum;
                sum[type + "_A_D_SUM"] = typeADSum;
            });

            // 금월실적 소계 (계) 계산
            typeArr.forEach((type) => {
                sum[type + "_L_SUM"] = sum[type + "_L_I_SUM"] - sum[type + "_L_D_SUM"];
                sum[type + "_S_SUM"] = sum[type + "_S_I_SUM"] - sum[type + "_S_D_SUM"];
                sum[type + "_A_SUM"] = sum[type + "_A_I_SUM"] - sum[type + "_A_D_SUM"];
            });

            // 금월실적 총계 (증, 감, 계) 계산
            sum["L_SUM"] = typeArr.reduce((sumValue, type) => sumValue + sum[type + "_L_SUM"], 0);
            sum["L_I_SUM"] = typeArr.reduce((sumValue, type) => sumValue + sum[type + "_L_I_SUM"], 0);
            sum["L_D_SUM"] = typeArr.reduce((sumValue, type) => sumValue + sum[type + "_L_D_SUM"], 0);
            sum["S_SUM"] = typeArr.reduce((sumValue, type) => sumValue + sum[type + "_S_SUM"], 0);
            sum["S_I_SUM"] = typeArr.reduce((sumValue, type) => sumValue + sum[type + "_S_I_SUM"], 0);
            sum["S_D_SUM"] = typeArr.reduce((sumValue, type) => sumValue + sum[type + "_S_D_SUM"], 0);
            sum["A_SUM"] = typeArr.reduce((sumValue, type) => sumValue + sum[type + "_A_SUM"], 0);
            sum["A_I_SUM"] = typeArr.reduce((sumValue, type) => sumValue + sum[type + "_A_I_SUM"], 0);
            sum["A_D_SUM"] = typeArr.reduce((sumValue, type) => sumValue + sum[type + "_A_D_SUM"], 0);

            // 금월 누계

            setSumValue(sum);
        } else {
            setSumValue({});
        }
    }, [data, formInputData]);

    return (
        <div className="tableWrap">
            {/* 엑셀 템플릿 */}
            <form className="content">
                {/* 1행 */}
                <div className="col_3 row_3"></div>
                <div className="col_3">전월누계</div>
                <div className="col_9">금월실적</div>
                <div className="col_3">금월누계</div>
                <Tooltip placement="top" title={<div style={{ color: "white" }}>{"단위 : 개"}</div>} color={"#2b77ad"}>
                    <div className="row_2">개소수</div>
                </Tooltip>
                <Tooltip placement="top" title={<div style={{ color: "white" }}>{"단위 : 대"}</div>} color={"#2b77ad"}>
                    <div className="row_2">주차대수</div>
                </Tooltip>
                <Tooltip placement="top" title={<div style={{ color: "white" }}>{"단위 : ㎡"}</div>} color={"#2b77ad"}>
                    <div className="row_2">면적</div>
                </Tooltip>
                <Tooltip placement="top" title={<div style={{ color: "white" }}>{"단위 : 개"}</div>} color={"#2b77ad"}>
                    <div className="col_3">개소수</div>
                </Tooltip>
                <Tooltip placement="top" title={<div style={{ color: "white" }}>{"단위 : 대"}</div>} color={"#2b77ad"}>
                    <div className="col_3">주차대수</div>
                </Tooltip>
                <Tooltip placement="top" title={<div style={{ color: "white" }}>{"단위 : ㎡"}</div>} color={"#2b77ad"}>
                    <div className="col_3">면적</div>
                </Tooltip>
                <Tooltip placement="top" title={<div style={{ color: "white" }}>{"단위 : 개"}</div>} color={"#2b77ad"}>
                    <div className="row_2">개소수</div>
                </Tooltip>
                <Tooltip placement="top" title={<div style={{ color: "white" }}>{"단위 : 대"}</div>} color={"#2b77ad"}>
                    <div className="row_2">주차대수</div>
                </Tooltip>
                <Tooltip placement="top" title={<div style={{ color: "white" }}>{"단위 : ㎡"}</div>} color={"#2b77ad"}>
                    <div className="row_2">면적</div>
                </Tooltip>
                <div>계</div>
                <div>증</div>
                <div>감</div>
                <div>계</div>
                <div>증</div>
                <div>감</div>
                <div>계</div>
                <div>증</div>
                <div>감</div>
                <div className="col_3">총계</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"] != null
                            ? formInputData["prevMonth"]?.TOTAL_L_SUM.toLocaleString("ko-KR", {
                                  maximumFractionDigits: 1,
                              })
                            : null
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.TOTAL_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.TOTAL_A_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.L_SUM}</div>
                <div className="valueDiv">{sumValue.L_I_SUM}</div>
                <div className="valueDiv">{sumValue.L_D_SUM}</div>
                <div className="valueDiv">{sumValue.S_SUM}</div>
                <div className="valueDiv">{sumValue.S_I_SUM}</div>
                <div className="valueDiv">{sumValue.S_D_SUM}</div>
                <div className="valueDiv">{sumValue.A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">{sumValue.A_I_SUM?.toFixed(1)}</div>
                <div className="valueDiv">{sumValue.A_D_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.TOTAL_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.TOTAL_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.TOTAL_A_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="row_6 vrlr subTitle">공영주차장</div>
                <div className="col_2 subTitle">소계</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBL_L_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBL_S_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBL_A_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.PBL_L_SUM}</div>
                <div className="valueDiv">{sumValue.PBL_L_I_SUM}</div>
                <div className="valueDiv">{sumValue.PBL_L_D_SUM}</div>
                <div className="valueDiv">{sumValue.PBL_S_SUM}</div>
                <div className="valueDiv">{sumValue.PBL_S_I_SUM}</div>
                <div className="valueDiv">{sumValue.PBL_S_D_SUM}</div>
                <div className="valueDiv">{sumValue.PBL_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">{sumValue.PBL_A_I_SUM?.toFixed(1)}</div>
                <div className="valueDiv">{sumValue.PBL_A_D_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBL_L_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBL_S_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBL_A_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="row_3">노상</div>
                <div>유료</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLRD_PAY_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLRD_PAY_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLRD_PAY_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.PBLRD_PAY_L_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_PAY_L_I"
                        value={valueFormat("PBLRD_PAY_L_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_PAY_L_D"
                        value={valueFormat("PBLRD_PAY_L_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.PBLRD_PAY_S_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_PAY_S_I"
                        value={valueFormat("PBLRD_PAY_S_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_PAY_S_D"
                        value={valueFormat("PBLRD_PAY_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.PBLRD_PAY_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_PAY_A_I"
                        value={valueFormat("PBLRD_PAY_A_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_PAY_A_D"
                        value={valueFormat("PBLRD_PAY_A_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLRD_PAY_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLRD_PAY_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLRD_PAY_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div>무료</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLRD_FREE_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLRD_FREE_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLRD_FREE_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.PBLRD_FREE_L_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_FREE_L_I"
                        value={valueFormat("PBLRD_FREE_L_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_FREE_L_D"
                        value={valueFormat("PBLRD_FREE_L_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.PBLRD_FREE_S_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_FREE_S_I"
                        value={valueFormat("PBLRD_FREE_S_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_FREE_S_D"
                        value={valueFormat("PBLRD_FREE_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.PBLRD_FREE_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_FREE_A_I"
                        value={valueFormat("PBLRD_FREE_A_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_FREE_A_D"
                        value={valueFormat("PBLRD_FREE_A_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLRD_FREE_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLRD_FREE_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLRD_FREE_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div>거주자</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLRD_RESI_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLRD_RESI_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLRD_RESI_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.PBLRD_RESI_L_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_RESI_L_I"
                        value={valueFormat("PBLRD_RESI_L_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_RESI_L_D"
                        value={valueFormat("PBLRD_RESI_L_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.PBLRD_RESI_S_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_RESI_S_I"
                        value={valueFormat("PBLRD_RESI_S_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_RESI_S_D"
                        value={valueFormat("PBLRD_RESI_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.PBLRD_RESI_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_RESI_A_I"
                        value={valueFormat("PBLRD_RESI_A_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLRD_RESI_A_D"
                        value={valueFormat("PBLRD_RESI_A_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLRD_RESI_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLRD_RESI_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLRD_RESI_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="row_2">노외</div>
                <div>유료</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLOUT_PAY_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLOUT_PAY_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLOUT_PAY_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.PBLOUT_PAY_L_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLOUT_PAY_L_I"
                        value={valueFormat("PBLOUT_PAY_L_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLOUT_PAY_L_D"
                        value={valueFormat("PBLOUT_PAY_L_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.PBLOUT_PAY_S_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLOUT_PAY_S_I"
                        value={valueFormat("PBLOUT_PAY_S_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLOUT_PAY_S_D"
                        value={valueFormat("PBLOUT_PAY_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.PBLOUT_PAY_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLOUT_PAY_A_I"
                        value={valueFormat("PBLOUT_PAY_A_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLOUT_PAY_A_D"
                        value={valueFormat("PBLOUT_PAY_A_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLOUT_PAY_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLOUT_PAY_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLOUT_PAY_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div>무료</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLOUT_FREE_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLOUT_FREE_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PBLOUT_FREE_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.PBLOUT_FREE_L_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLOUT_FREE_L_I"
                        value={valueFormat("PBLOUT_FREE_L_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLOUT_FREE_L_D"
                        value={valueFormat("PBLOUT_FREE_L_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.PBLOUT_FREE_S_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLOUT_FREE_S_I"
                        value={valueFormat("PBLOUT_FREE_S_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLOUT_FREE_S_D"
                        value={valueFormat("PBLOUT_FREE_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.PBLOUT_FREE_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLOUT_FREE_A_I"
                        value={valueFormat("PBLOUT_FREE_A_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PBLOUT_FREE_A_D"
                        value={valueFormat("PBLOUT_FREE_A_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLOUT_FREE_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLOUT_FREE_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PBLOUT_FREE_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="col_3 subTitle">민영주차장</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.PRV_A_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.PRV_L_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PRV_L_I"
                        value={valueFormat("PRV_L_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PRV_L_D"
                        value={valueFormat("PRV_L_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.PRV_S_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PRV_S_I"
                        value={valueFormat("PRV_S_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PRV_S_D"
                        value={valueFormat("PRV_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.PRV_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PRV_A_I"
                        value={valueFormat("PRV_A_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="PRV_A_D"
                        value={valueFormat("PRV_A_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.PRV_A_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="row_5 vrlr subTitle">부설주차장</div>
                <div className="col_2 subTitle">소계</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUB_L_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUB_S_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUB_A_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.SUB_L_SUM}</div>
                <div className="valueDiv">{sumValue.SUB_L_I_SUM}</div>
                <div className="valueDiv">{sumValue.SUB_L_D_SUM}</div>
                <div className="valueDiv">{sumValue.SUB_S_SUM}</div>
                <div className="valueDiv">{sumValue.SUB_S_I_SUM}</div>
                <div className="valueDiv">{sumValue.SUB_S_D_SUM}</div>
                <div className="valueDiv">{sumValue.SUB_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">{sumValue.SUB_A_I_SUM?.toFixed(1)}</div>
                <div className="valueDiv">{sumValue.SUB_A_D_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUB_L_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUB_S_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUB_A_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                {/* 데이터행 */}
                <div className="row_2">자주식</div>
                <div>노면식</div>
                {/* 데이터행 */}
                {/*formInputDataTableSet1.tbody[9].sides.map((side, index) => checkInput(index, side))*/}
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUBSE_SUR_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUBSE_SUR_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUBSE_SUR_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.SUBSE_SUR_L_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBSE_SUR_L_I"
                        value={valueFormat("SUBSE_SUR_L_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBSE_SUR_L_D"
                        value={valueFormat("SUBSE_SUR_L_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.SUBSE_SUR_S_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBSE_SUR_S_I"
                        value={valueFormat("SUBSE_SUR_S_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBSE_SUR_S_D"
                        value={valueFormat("SUBSE_SUR_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.SUBSE_SUR_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBSE_SUR_A_I"
                        value={valueFormat("SUBSE_SUR_A_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBSE_SUR_A_D"
                        value={valueFormat("SUBSE_SUR_A_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUBSE_SUR_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUBSE_SUR_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUBSE_SUR_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                {/* 데이터행 */}
                <div>조립식</div>
                {/* 데이터행 */}
                {/*formInputDataTableSet1.tbody[10].sides.map((side, index) => checkInput(index, side))*/}
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUBSE_MOD_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUBSE_MOD_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUBSE_MOD_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.SUBSE_MOD_L_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBSE_MOD_L_I"
                        value={valueFormat("SUBSE_MOD_L_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBSE_MOD_L_D"
                        value={valueFormat("SUBSE_MOD_L_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.SUBSE_MOD_S_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBSE_MOD_S_I"
                        value={valueFormat("SUBSE_MOD_S_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBSE_MOD_S_D"
                        value={valueFormat("SUBSE_MOD_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.SUBSE_MOD_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBSE_MOD_A_I"
                        value={valueFormat("SUBSE_MOD_A_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBSE_MOD_A_D"
                        value={valueFormat("SUBSE_MOD_A_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUBSE_MOD_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUBSE_MOD_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUBSE_MOD_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="row_2">기계식</div>
                <div>부속</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUBAU_ATT_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUBAU_ATT_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUBAU_ATT_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.SUBAU_ATT_L_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBAU_ATT_L_I"
                        value={valueFormat("SUBAU_ATT_L_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBAU_ATT_L_D"
                        value={valueFormat("SUBAU_ATT_L_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.SUBAU_ATT_S_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBAU_ATT_S_I"
                        value={valueFormat("SUBAU_ATT_S_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBAU_ATT_S_D"
                        value={valueFormat("SUBAU_ATT_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.SUBAU_ATT_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBAU_ATT_A_I"
                        value={valueFormat("SUBAU_ATT_A_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBAU_ATT_A_D"
                        value={valueFormat("SUBAU_ATT_A_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUBAU_ATT_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUBAU_ATT_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUBAU_ATT_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div>전용</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUBAU_PRV_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUBAU_PRV_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.SUBAU_PRV_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.SUBAU_PRV_L_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBAU_PRV_L_I"
                        value={valueFormat("SUBAU_PRV_L_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBAU_PRV_L_D"
                        value={valueFormat("SUBAU_PRV_L_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.SUBAU_PRV_S_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBAU_PRV_S_I"
                        value={valueFormat("SUBAU_PRV_S_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBAU_PRV_S_D"
                        value={valueFormat("SUBAU_PRV_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.SUBAU_PRV_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBAU_PRV_A_I"
                        value={valueFormat("SUBAU_PRV_A_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="SUBAU_PRV_A_D"
                        value={valueFormat("SUBAU_PRV_A_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUBAU_PRV_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUBAU_PRV_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.SUBAU_PRV_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                {/* 데이터행 */}
                <div className="row_3 vrlr subTitle">자가주차장</div>
                <div className="col_2 subTitle">소계</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.OWN_L_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.OWN_S_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.OWN_A_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.OWN_L_SUM}</div>
                <div className="valueDiv">{sumValue.OWN_L_I_SUM}</div>
                <div className="valueDiv">{sumValue.OWN_L_D_SUM}</div>
                <div className="valueDiv">{sumValue.OWN_S_SUM}</div>
                <div className="valueDiv">{sumValue.OWN_S_I_SUM}</div>
                <div className="valueDiv">{sumValue.OWN_S_D_SUM}</div>
                <div className="valueDiv">{sumValue.OWN_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">{sumValue.OWN_A_I_SUM?.toFixed(1)}</div>
                <div className="valueDiv">{sumValue.OWN_A_D_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.OWN_L_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.OWN_S_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.OWN_A_SUBTOTAL.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="col_2">단독주택</div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.OWN_HOME_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.OWN_HOME_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.OWN_HOME_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.OWN_HOME_L_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="OWN_HOME_L_I"
                        value={valueFormat("OWN_HOME_L_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="OWN_HOME_L_D"
                        value={valueFormat("OWN_HOME_L_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.OWN_HOME_S_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="OWN_HOME_S_I"
                        value={valueFormat("OWN_HOME_S_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="OWN_HOME_S_D"
                        value={valueFormat("OWN_HOME_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.OWN_HOME_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="OWN_HOME_A_I"
                        value={valueFormat("OWN_HOME_A_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="OWN_HOME_A_D"
                        value={valueFormat("OWN_HOME_A_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.OWN_HOME_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.OWN_HOME_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.OWN_HOME_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                {/* 데이터행 */}
                <div className="col_2">공동주택</div>
                {/* 데이터행 */}
                {/*formInputDataTableSet1.tbody[15].sides.map((side, index) => checkInput(index, side))*/}
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.OWN_APT_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.OWN_APT_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["prevMonth"]?.OWN_APT_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">{sumValue.OWN_APT_L_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="OWN_APT_L_I"
                        value={valueFormat("OWN_HOME_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="OWN_APT_L_D"
                        value={valueFormat("OWN_APT_L_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.OWN_APT_S_SUM}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="OWN_APT_S_I"
                        value={valueFormat("OWN_APT_S_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="OWN_APT_S_D"
                        value={valueFormat("OWN_APT_S_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">{sumValue.OWN_APT_A_SUM?.toFixed(1)}</div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="OWN_APT_A_I"
                        value={valueFormat("OWN_APT_A_I")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    <input
                        className="valueDiv"
                        type="text"
                        name="OWN_APT_A_D"
                        value={valueFormat("OWN_APT_A_D")}
                        onChange={handleInputChange}
                    />
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.OWN_APT_L_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.OWN_APT_S_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                <div className="valueDiv">
                    {formInputData != null
                        ? formInputData["thisMonth"]?.OWN_APT_A_SUM.toLocaleString("ko-KR", {
                              maximumFractionDigits: 1,
                          })
                        : null}
                </div>
                {/* 데이터행 */}
                {/* 끝 */}
            </form>
        </div>
    );
}
