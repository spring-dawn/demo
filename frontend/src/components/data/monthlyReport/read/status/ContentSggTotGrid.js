import { React, useEffect, useState, useCallback } from "react";
import { Tooltip } from "antd";
export default function ContentSggTotGrid(props) {
    const { mode, data, setFormData, search } = props;
    const [sumValue, setSumValue] = useState([]);
    const checkInput = (index, side) => {
        if (index === 4 || index === 5 || index === 7 || index === 8 || index === 10 || index === 11)
            return (
                <div>
                    <input type="number" defaultValue={side.toLocaleString()} />
                </div>
            );
        else {
            return <div>{side.toLocaleString()}</div>;
        }
    };
    useEffect(() => {
        const tableGrid = document.querySelector(".tableWrap .content");
        let flag = mode == "on" ? false : true;

        tableGrid.querySelectorAll("input").forEach((input) => {
            input.readOnly = flag;
        });
    }, [mode]);

    return (
        <div className="tableWrap tableWrap2">
            {/* 엑셀 템플릿 */}
            <form className="content">
                {/* 1행 */}
                <div className="col_3 row_3"></div>
                <div className="col_2">합계</div>
                <div className="col_2">중구</div>
                <div className="col_2">남구</div>
                <div className="col_2">동구</div>
                <div className="col_2">북구</div>
                <div className="col_2">울주군</div>
                <Tooltip
                    placement="top"
                    title={<div style={{ color: "black", fontWeight: "bold" }}>{"단위 : 개"}</div>}
                    color={"white"}
                >
                    <div className="row_2">개소수</div>
                </Tooltip>
                <Tooltip
                    placement="top"
                    title={<div style={{ color: "black", fontWeight: "bold" }}>{"단위 : 대"}</div>}
                    color={"white"}
                >
                    <div className="row_2">
                        주차
                        <br />
                        대수
                    </div>
                </Tooltip>
                <Tooltip
                    placement="top"
                    title={<div style={{ color: "black", fontWeight: "bold" }}>{"단위 : 개"}</div>}
                    color={"white"}
                >
                    <div className="row_2">개소수</div>
                </Tooltip>
                <Tooltip
                    placement="top"
                    title={<div style={{ color: "black", fontWeight: "bold" }}>{"단위 : 대"}</div>}
                    color={"white"}
                >
                    <div className="row_2">
                        주차
                        <br />
                        대수
                    </div>
                </Tooltip>
                <Tooltip
                    placement="top"
                    title={<div style={{ color: "black", fontWeight: "bold" }}>{"단위 : 개"}</div>}
                    color={"white"}
                >
                    <div className="row_2">개소수</div>
                </Tooltip>
                <Tooltip
                    placement="top"
                    title={<div style={{ color: "black", fontWeight: "bold" }}>{"단위 : 대"}</div>}
                    color={"white"}
                >
                    <div className="row_2">
                        주차
                        <br />
                        대수
                    </div>
                </Tooltip>
                <Tooltip
                    placement="top"
                    title={<div style={{ color: "black", fontWeight: "bold" }}>{"단위 : 개"}</div>}
                    color={"white"}
                >
                    <div className="row_2">개소수</div>
                </Tooltip>
                <Tooltip
                    placement="top"
                    title={<div style={{ color: "black", fontWeight: "bold" }}>{"단위 : 대"}</div>}
                    color={"white"}
                >
                    <div className="row_2">
                        주차
                        <br />
                        대수
                    </div>
                </Tooltip>
                <Tooltip
                    placement="top"
                    title={<div style={{ color: "black", fontWeight: "bold" }}>{"단위 : 개"}</div>}
                    color={"white"}
                >
                    <div className="row_2">개소수</div>
                </Tooltip>
                <Tooltip
                    placement="top"
                    title={<div style={{ color: "black", fontWeight: "bold" }}>{"단위 : 대"}</div>}
                    color={"white"}
                >
                    <div className="row_2">
                        주차
                        <br />
                        대수
                    </div>
                </Tooltip>
                <Tooltip
                    placement="top"
                    title={<div style={{ color: "black", fontWeight: "bold" }}>{"단위 : 개"}</div>}
                    color={"white"}
                >
                    <div className="row_2">개소수</div>
                </Tooltip>
                <Tooltip
                    placement="top"
                    title={<div style={{ color: "black", fontWeight: "bold" }}>{"단위 : 대"}</div>}
                    color={"white"}
                >
                    <div className="row_2">
                        주차
                        <br />
                        대수
                    </div>
                </Tooltip>
                <div className="col_3">총계</div>
                {/* 데이터행 */}
                {/*{dataTableSet1.tbody[0].sides.map((side, index) => checkInput(index, side))}*/}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.TOTAL_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.TOTAL_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.TOTAL_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.TOTAL_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.TOTAL_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.TOTAL_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.TOTAL_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.TOTAL_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.TOTAL_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.TOTAL_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.TOTAL_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.TOTAL_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div className="row_6 vrlr subTitle">공영주차장</div>
                <div className="col_2 subTitle">소계</div>
                {/* 데이터행 */}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PBL_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PBL_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PBL_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PBL_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PBL_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PBL_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PBL_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PBL_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PBL_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PBL_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PBL_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PBL_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div className="row_3">노상</div>
                <div>유료</div>
                {/* 데이터행 */}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PBLRD_PAY_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PBLRD_PAY_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PBLRD_PAY_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PBLRD_PAY_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PBLRD_PAY_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PBLRD_PAY_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PBLRD_PAY_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PBLRD_PAY_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PBLRD_PAY_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PBLRD_PAY_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PBLRD_PAY_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PBLRD_PAY_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div>무료</div>
                {/* 데이터행 */}
                {/*dataTableSet1.tbody[3].sides.map((side, index) => checkInput(index, side))*/}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PBLRD_FREE_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PBLRD_FREE_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PBLRD_FREE_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PBLRD_FREE_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PBLRD_FREE_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PBLRD_FREE_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PBLRD_FREE_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PBLRD_FREE_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PBLRD_FREE_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PBLRD_FREE_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PBLRD_FREE_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PBLRD_FREE_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div>거주자</div>
                {/* 데이터행 */}
                {/*dataTableSet1.tbody[4].sides.map((side, index) => checkInput(index, side))*/}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PBLRD_RESI_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PBLRD_RESI_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PBLRD_RESI_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PBLRD_RESI_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PBLRD_RESI_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PBLRD_RESI_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PBLRD_RESI_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PBLRD_RESI_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PBLRD_RESI_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PBLRD_RESI_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PBLRD_RESI_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PBLRD_RESI_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div className="row_2">노외</div>
                <div>유료</div>
                {/* 데이터행 */}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PBLOUT_PAY_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PBLOUT_PAY_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PBLOUT_PAY_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PBLOUT_PAY_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PBLOUT_PAY_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PBLOUT_PAY_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PBLOUT_PAY_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PBLOUT_PAY_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PBLOUT_PAY_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PBLOUT_PAY_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PBLOUT_PAY_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PBLOUT_PAY_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div>무료</div>
                {/* 데이터행 */}
                {/*dataTableSet1.tbody[6].sides.map((side, index) => checkInput(index, side))*/}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PBLOUT_FREE_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PBLOUT_FREE_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PBLOUT_FREE_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PBLOUT_FREE_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PBLOUT_FREE_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PBLOUT_FREE_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PBLOUT_FREE_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PBLOUT_FREE_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PBLOUT_FREE_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PBLOUT_FREE_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PBLOUT_FREE_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PBLOUT_FREE_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div className="col_3 subTitle">민영주차장</div>
                {/* 데이터행 */}
                {/*dataTableSet1.tbody[7].sides.map((side, index) => checkInput(index, side))*/}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/*주차대수계*/}
                {/* 데이터행 */}
                <div className="row_5 vrlr subTitle">부설주차장</div>
                <div className="col_2 subTitle">소계</div>
                {/* 데이터행 */}
                {/*dataTableSet1.tbody[8].sides.map((side, index) => checkInput(index, side))*/}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.SUB_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.SUB_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.SUB_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.SUB_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.SUB_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.SUB_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.SUB_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.SUB_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.SUB_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.SUB_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.SUB_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.SUB_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div className="row_2">자주식</div>
                <div>노면식</div>
                {/* 데이터행 */}
                {/*dataTableSet1.tbody[9].sides.map((side, index) => checkInput(index, side))*/}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.SUBSE_SUR_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.SUBSE_SUR_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.SUBSE_SUR_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.SUBSE_SUR_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.SUBSE_SUR_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.SUBSE_SUR_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.SUBSE_SUR_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.SUBSE_SUR_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.SUBSE_SUR_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.SUBSE_SUR_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.SUBSE_SUR_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.SUBSE_SUR_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div>조립식</div>
                {/* 데이터행 */}
                {/*dataTableSet1.tbody[10].sides.map((side, index) => checkInput(index, side))*/}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.SUBSE_MOD_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.SUBSE_MOD_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.SUBSE_MOD_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.SUBSE_MOD_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.SUBSE_MOD_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.SUBSE_MOD_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.SUBSE_MOD_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.SUBSE_MOD_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.SUBSE_MOD_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.SUBSE_MOD_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.SUBSE_MOD_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.SUBSE_MOD_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div className="row_2">기계식</div>
                <div>부속</div>
                {/* 데이터행 */}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.SUBAU_ATT_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.SUBAU_ATT_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.SUBAU_ATT_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.SUBAU_ATT_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.SUBAU_ATT_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.SUBAU_ATT_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.SUBAU_ATT_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.SUBAU_ATT_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.SUBAU_ATT_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.SUBAU_ATT_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.SUBAU_ATT_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.SUBAU_ATT_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div>전용</div>
                {/* 데이터행 */}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.SUBAU_PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.SUBAU_PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.SUBAU_PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.SUBAU_PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.SUBAU_PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.SUBAU_PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.SUBAU_PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.SUBAU_PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.SUBAU_PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.SUBAU_PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.SUBAU_PRV_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.SUBAU_PRV_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div className="row_3 vrlr subTitle">자가주차장</div>
                <div className="col_2 subTitle">소계</div>
                {/* 데이터행 */}
                {/*dataTableSet1.tbody[13].sides.map((side, index) => checkInput(index, side))*/}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.OWN_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.OWN_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.OWN_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.OWN_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.OWN_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.OWN_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.OWN_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.OWN_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.OWN_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.OWN_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.OWN_L_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.OWN_S_SUBTOTAL.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div className="col_2">단독주택</div>
                {/* 데이터행 */}
                {/*dataTableSet1.tbody[14].sides.map((side, index) => checkInput(index, side))*/}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.OWN_HOME_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.OWN_HOME_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.OWN_HOME_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.OWN_HOME_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.OWN_HOME_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.OWN_HOME_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.OWN_HOME_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.OWN_HOME_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.OWN_HOME_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.OWN_HOME_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.OWN_HOME_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.OWN_HOME_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                <div className="col_2">공동주택</div>
                {/* 데이터행 */}
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.OWN_APT_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["sggTotal"]?.OWN_APT_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.OWN_APT_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["jungguTotal"]?.OWN_APT_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.OWN_APT_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["namguTotal"]?.OWN_APT_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.OWN_APT_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["dongguTotal"]?.OWN_APT_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.OWN_APT_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["bukguTotal"]?.OWN_APT_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.OWN_APT_L_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                <div className="valueDiv">
                    {data != null
                        ? data["uljuTotal"]?.OWN_APT_S_SUM.toLocaleString("ko-KR", { maximumFractionDigits: 1 })
                        : null}
                </div>
                {/* 데이터행 */}
                {/* 끝 */}
            </form>
        </div>
    );
}
