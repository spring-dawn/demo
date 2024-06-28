import React, { useEffect, useState } from "react";
import { gugunParseCodeToName } from "../../../../CommonFunction";
import axios from "axios";
import { Tooltip } from "antd";

function CrdnNocsTable({ type }) {
    const [data, setData] = useState([]);

    useEffect(() => {
        axios.get("/api/data/illegal/nocs").then((res) => {
            setData(res.data.sort((a, b) => a.sgg - b.sgg));
        });
    }, []);

    return (
        <table className="data_table">
            <colgroup>
                <col style={{ width: "10%" }} />
                <col style={{ width: "15%" }} />
                <col style={{ width: "15%" }} />
                <col style={{ width: "15%" }} />
                <col style={{ width: "15%" }} />
                <col style={{ width: "15%" }} />
                {/* <col style={{ width: "15%" }} /> */}
            </colgroup>
            <thead>
                <tr style={{ height: "45px" }}>
                    <Tooltip
                        placement="top"
                        title={
                            <div style={{ color: "black" }}>
                                <b>{"단위 : 건"}</b>
                            </div>
                        }
                        color={"white"}
                    >
                        <th colSpan={type.name == "견인건수" ? 7 : 6}>{`${type.name}`}</th>
                    </Tooltip>
                </tr>
                <tr>
                    <th>구분</th>
                    <th>소계</th>
                    <th>승용</th>
                    <th>승합</th>
                    <th>화물</th>
                    <th>기타</th>
                    {type.name == "견인건수" && <th>금액(천원)</th>}
                </tr>
            </thead>
            <tbody>
                {data
                    .filter((ele) => ele.gubun == type.name)
                    .map((ele, idx) => {
                        return (
                            <tr key={idx}>
                                <td>{gugunParseCodeToName(ele.sgg)}</td>
                                <td>{ele.sum.toLocaleString()}</td>
                                <td>{ele.crdnCar.toLocaleString()}</td>
                                <td>{ele.crdnVan.toLocaleString()}</td>
                                <td>{ele.crdnTruck.toLocaleString()}</td>
                                <td>{ele.crdnEtc.toLocaleString()}</td>
                                {type.name == "견인건수" && <td>{ele.amt || 0}</td>}
                            </tr>
                        );
                    })}
            </tbody>
        </table>
    );
}

export default CrdnNocsTable;
