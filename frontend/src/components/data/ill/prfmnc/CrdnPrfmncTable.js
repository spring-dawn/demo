import React, { useState, useEffect } from "react";
import axios from "axios";
import { gugunParseCodeToName } from "../../../../CommonFunction";
import { Tooltip } from "antd";

function CrdnPrfmncTable({ type }) {
    const [data, setData] = useState([]);

    useEffect(() => {
        axios.get("/api/data/illegal/prfmnc").then((res) => {
            setData(res.data.sort((a, b) => a.sgg - b.sgg));
        });
    }, []);

    const createAllDataTable = (data) => {
        const groupedDataBySgg = {};

        data.forEach((ele) => {
            const sgg = ele.sgg;

            if (!groupedDataBySgg[sgg]) {
                groupedDataBySgg[sgg] = {
                    crdnNocs: 0,
                    levyAmt: 0,
                    clctnNocs: 0,
                    clctnAmt: 0,
                    crdnNope: 0,
                };
            }

            groupedDataBySgg[sgg].crdnNocs += ele.crdnNocs || 0;
            groupedDataBySgg[sgg].levyAmt += ele.levyAmt || 0;
            groupedDataBySgg[sgg].clctnNocs += ele.clctnNocs || 0;
            groupedDataBySgg[sgg].clctnAmt += ele.clctnAmt || 0;
            groupedDataBySgg[sgg].crdnNope += ele.crdnNope || 0;
        });

        const finalRows = Object.keys(groupedDataBySgg).map((sgg, idx) => {
            const ele = groupedDataBySgg[sgg];

            return (
                <tr key={idx}>
                    <td>{gugunParseCodeToName(sgg)}</td>
                    <td>{ele.crdnNocs.toLocaleString()}</td>
                    <td>{ele.levyAmt.toLocaleString()}</td>
                    <td>{ele.clctnNocs.toLocaleString()}</td>
                    <td>{ele.clctnAmt.toLocaleString()}</td>
                    <td>{ele.levyAmt !== 0 ? ((ele.clctnAmt / ele.levyAmt) * 100).toFixed(1) + "%" : "0.0"}</td>
                </tr>
            );
        });

        return finalRows;
    };

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
                                <b>{"단위 : 건, 천원"}</b>
                            </div>
                        }
                        color={"white"}
                    >
                        <th colSpan={type.name == "인력단속" ? 7 : 6}>{`${type.name}`}</th>
                    </Tooltip>
                </tr>
                <tr>
                    <th>구분</th>
                    <th>단속건수</th>
                    <th>부과액</th>
                    <th>징수건수</th>
                    <th>징수액</th>
                    {type.name == "인력단속" && <th>인력단속(명)</th>}
                    <th>징수율</th>
                </tr>
            </thead>
            <tbody>
                {type.name != "총괄"
                    ? data
                          .filter((ele) => ele.gubun == type.name)
                          .map((ele, idx) => {
                              return (
                                  <tr key={idx}>
                                      <td>{gugunParseCodeToName(ele.sgg)}</td>
                                      <td>{ele.crdnNocs.toLocaleString()}</td>
                                      <td>{ele.levyAmt.toLocaleString()}</td>
                                      <td>{ele.clctnNocs.toLocaleString()}</td>
                                      <td>{ele.clctnAmt.toLocaleString()}</td>
                                      {type.name == "인력단속" && <td>{ele.crdnNope || 0}</td>}
                                      <td>
                                          {" "}
                                          {ele.levyAmt !== 0
                                              ? ((ele.clctnAmt / ele.levyAmt) * 100).toFixed(1) + "%"
                                              : "0.0"}
                                      </td>
                                  </tr>
                              );
                          })
                    : createAllDataTable(data)}
            </tbody>
        </table>
    );
}

export default CrdnPrfmncTable;
