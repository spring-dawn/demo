import React, { useState, useRef, useEffect } from "react";
import {
    ResponsiveContainer,
    ScatterChart,
    XAxis,
    YAxis,
    Tooltip,
    Scatter,
    Cell,
    ReferenceLine,
    ReferenceArea,
    Label,
} from "recharts";

function AnalyChart({
    data,
    xName,
    yName,
    selectChart,
    sendCustom,
    setClickTable,
    clickTable,
    selectGugun,
    selectDay,
    selectDong,
}) {
    const chartRef = useRef(null);
    const [isSelecting, setIsSelecting] = useState(false);
    const [startPosition, setStartPosition] = useState({ x: 0, y: 0 });
    const [endPosition, setEndPosition] = useState({ x: 0, y: 0 });

    const { xMin, xAvg, xMax, yMin, yAvg, yMax } = { ...sendCustom };
    const xGap = xAvg * 0.03;
    const yGap = yAvg * 0.05;

    const filteredXData = data.filter((item) => item.xData >= xMin && item.xData <= xMax);

    const filteredYData = filteredXData.filter((item) => item.yData >= yMin && item.yData <= yMax);

    const selectionBox = {
        x: Math.min(startPosition.x, endPosition.x),
        y: Math.min(startPosition.y, endPosition.y),
        width: Math.abs(endPosition.x - startPosition.x),
        height: Math.abs(endPosition.y - startPosition.y),
    };

    const handleMouseDown = (e) => {
        if (e.target.classList.contains("recharts-symbols")) {
            setIsSelecting(false);
            setClickTable([e.target.id]);
            return;
        } else {
            setIsSelecting(true);
            const rect = chartRef.current.getBoundingClientRect();

            setStartPosition({
                x: e.clientX - rect.left,
                y: e.clientY - rect.top,
            });
            setEndPosition({
                x: e.clientX - rect.left,
                y: e.clientY - rect.top,
            });
        }
    };

    const handleMouseMove = (e) => {
        if (!isSelecting) return;
        console.log("무브");

        const rect = chartRef.current.getBoundingClientRect();

        setEndPosition({
            x: e.clientX - rect.left,
            y: e.clientY - rect.top,
        });
    };

    const handleMouseUp = (e) => {
        setIsSelecting(false);
        if (!isSelecting) return;
        console.log("업");

        const minX = Math.min(startPosition.x, endPosition.x);
        const maxX = Math.max(startPosition.x, endPosition.x);
        const minY = Math.min(startPosition.y, endPosition.y);
        const maxY = Math.max(startPosition.y, endPosition.y);

        const scatter = chartRef.current.querySelector(".recharts-scatter");
        const symbols = scatter.querySelectorAll(".recharts-scatter-symbol path");

        const filterSymbols = [...symbols].filter((ele) => {
            const dataX = ele.getAttribute("x");
            const dataY = ele.getAttribute("y");

            return dataX >= minX && dataX <= maxX && dataY >= minY && dataY <= maxY;
        });

        const keys = filterSymbols.map((ele) => {
            return ele.id;
        });

        setClickTable(keys);
    };

    useEffect(() => {
        setClickTable([]);
    }, [selectGugun, selectDay, selectDong]);

    return (
        <>
            <div
                ref={chartRef}
                onMouseDown={handleMouseDown}
                onMouseMove={handleMouseMove}
                onMouseUp={handleMouseUp}
                style={{ width: "100%", height: "100%", position: "relative" }}
                id="analyChartPage"
            >
                <ResponsiveContainer width="100%" id="analyChartPage" ref={chartRef}>
                    <ScatterChart
                        width={400}
                        margin={{
                            top: 30,
                            right: 20,
                            bottom: -10,
                            left: -40,
                        }}
                    >
                        {/* X축 */}
                        <XAxis
                            type="number"
                            dataKey="xData"
                            domain={[xMin, xMax]}
                            name="주차장 확보율"
                            style={{ display: "none" }}
                            ticks={[xMin, xAvg, xMax]}
                        />

                        {/* X축 */}
                        <YAxis
                            type="number"
                            dataKey="yData"
                            domain={[yMin, yMax]}
                            name="주차장 이용률"
                            style={{ display: "none" }}
                            ticks={[yMin, yAvg, yMax]}
                        />

                        {/* 툴팁 */}
                        {isSelecting === false && clickTable.length === 0 && (
                            <Tooltip
                                cursor={{ strokeDasharray: "3 3" }}
                                content={(data) => {
                                    const key = data?.payload?.[0]?.payload?.key;
                                    const xData = data?.payload?.[0]?.payload?.xData;
                                    const yData = data?.payload?.[0]?.payload?.yData;

                                    return (
                                        <div
                                            style={{
                                                backgroundColor: "#ffffff",
                                                padding: 12,
                                                borderRadius: 8,
                                                boxShadow: "0px 10px 34px -15px rgba(0, 0, 0, 0.24)",
                                            }}
                                        >
                                            <p
                                                style={{ fontWeight: 900, fontSize: "1.1em", color: "#DE3163" }}
                                            >{`${key}`}</p>
                                            <p style={{ fontWeight: 800 }}>
                                                {`${xName} : `}
                                                <span style={{ fontWeight: 400 }}>{parseFloat(xData).toFixed(1)}</span>
                                            </p>
                                            <p style={{ fontWeight: 800 }}>
                                                {`${yName} : `}
                                                <span style={{ fontWeight: 400 }}>{parseFloat(yData).toFixed(1)}</span>
                                            </p>
                                        </div>
                                    );
                                }}
                            />
                        )}

                        {/* X 가이드라인 */}
                        <ReferenceLine x={xAvg} stroke="#1890ff" strokeDasharray={5} />

                        {/* Y 가이드라인 */}
                        <ReferenceLine y={yAvg} stroke="#1890ff" strokeDasharray={5} />

                        {/* X 가이드라인 설명 */}
                        <ReferenceLine
                            segment={[
                                { x: xMax - xGap * 3.5, y: yAvg - yGap * 0.7 },
                                { x: xMax - xGap * 3.5, y: yAvg - yGap * 0.7 },
                            ]}
                        >
                            <Label position={"bottom"} stroke={"#000000"}>
                                {selectChart == 1 ? "주차장확보율" : "불법주차율"}
                            </Label>
                        </ReferenceLine>

                        {/* Y 가이드라인 설명 */}
                        <ReferenceLine
                            segment={[
                                { x: xAvg, y: yMax },
                                { x: xAvg, y: yMax },
                            ]}
                        >
                            <Label position={"top"} stroke={"#000000"}>
                                {selectChart == 1 ? "주차장이용률" : "유휴주차공간"}
                            </Label>
                        </ReferenceLine>

                        {/* 1 사분면 */}
                        <ReferenceLine
                            segment={[
                                selectChart == 1
                                    ? { x: (xMin + xAvg) / 2, y: (yAvg + yMax) / 2 }
                                    : { x: (xMin + xAvg) / 2, y: (yMin + yMax) / 2 },
                                selectChart == 1
                                    ? { x: (xMin + xAvg) / 2, y: (yAvg + yMax) / 2 }
                                    : { x: (xMin + xAvg) / 2, y: (yMin + yMax) / 2 },
                            ]}
                        >
                            <Label position={"top"} stroke={"#999999"}>
                                {selectChart === "1" ? "유형 1" : "유형 4"}
                            </Label>
                            <Label position={"bottom"} stroke={"#999999"}>
                                {selectChart === "1" ? "(주차장 설치 및 보조)" : "(주차환경 양호)"}
                            </Label>
                        </ReferenceLine>

                        {/* 2 사분면 */}
                        <ReferenceLine
                            segment={[
                                { x: (xAvg + xMax) / 2, y: (yAvg + yMax) / 2 },
                                { x: (xAvg + xMax) / 2, y: (yAvg + yMax) / 2 },
                            ]}
                        >
                            <Label position={"top"} stroke={"#999999"}>
                                {selectChart === "1" ? "유형 4" : "유형 2"}
                            </Label>
                            <Label position={"bottom"} stroke={"#999999"}>
                                {selectChart === "1" ? "(주차환경 양호)" : "(주차공유 활성화)"}
                            </Label>
                        </ReferenceLine>

                        {/* 3 사분면 */}
                        <ReferenceLine
                            segment={[
                                { x: (xMin + xAvg) / 2, y: (yMin + yAvg) / 2 },
                                { x: (xMin + xAvg) / 2, y: (yMin + yAvg) / 2 },
                            ]}
                        >
                            <Label position={"top"} stroke={"#999999"}>
                                {selectChart === "1" ? "유형 2" : " "}
                            </Label>
                            <Label position={"bottom"} stroke={"#999999"}>
                                {selectChart === "1" ? "(주차공유 활성화)" : " "}
                            </Label>
                        </ReferenceLine>

                        {/* 4 사분면 */}
                        <ReferenceLine
                            segment={[
                                { x: (xAvg + xMax) / 2, y: (yMin + yAvg) / 2 },
                                { x: (xAvg + xMax) / 2, y: (yMin + yAvg) / 2 },
                            ]}
                        >
                            <Label position={"top"} stroke={"#999999"}>
                                {selectChart === "1" ? "유형 2 · 유형 3" : "유형 1 · 유형 3"}
                            </Label>
                            <Label position={"bottom"} stroke={"#999999"}>
                                {selectChart === "1"
                                    ? "(주차공유 활성화 · 주차 수요관리, 단속강화)"
                                    : "(주차장 설치 및 보조 · 주차 수요 관리, 단속강화)"}
                            </Label>
                        </ReferenceLine>

                        {/* 1 사분면 색상 */}
                        <ReferenceArea
                            x1={xMin + xGap}
                            x2={xAvg - xGap}
                            y1={selectChart === "1" ? yAvg + yGap : yMin + yGap}
                            y2={yMax - yGap}
                            fill={selectChart === "1" ? "#FFEAEA" : "#D9E5FF"}
                            fillOpacity={0.2}
                            radius={[10, 10, 10, 10]}
                        />

                        {/* 2 사분면 색상 */}
                        <ReferenceArea
                            x1={xAvg + xGap}
                            x2={xMax - xGap}
                            y1={yAvg + yGap}
                            y2={yMax - yGap}
                            fill={selectChart === "1" ? "#D9E5FF" : "#f8f8c0"}
                            fillOpacity={0.2}
                            radius={[10, 10, 10, 10]}
                        />
                        {/* 3 사분면 색상 */}
                        <ReferenceArea
                            x1={xMin + xGap}
                            x2={xAvg - xGap}
                            y1={yMin + yGap}
                            y2={selectChart === "1" ? yAvg - yGap : yMax - yGap}
                            fill={selectChart === "1" ? "#f8f8c0" : "#D9E5FF"}
                            fillOpacity={0.2}
                            radius={[10, 10, 10, 10]}
                        />
                        {/* 4 사분면 색상 */}
                        <ReferenceArea
                            x1={xAvg + xGap}
                            x2={xMax - xGap}
                            y1={yMin + yGap}
                            y2={yAvg - yGap}
                            fill={selectChart === "1" ? "#aeeca5" : "#aeeca5"}
                            fillOpacity={0.2}
                            radius={[10, 10, 10, 10]}
                        />

                        {/* 데이터 */}
                        <Scatter data={filteredYData} fill="#8884d8">
                            {filteredYData.map((a, i) => (
                                <Cell
                                    key={`cell-${a.key}`}
                                    stroke={"#656565"}
                                    fill={clickTable.includes(a.key) ? "#FF7171" : "#8884d8"}
                                    size={clickTable.includes(a.key) ? 400 : 300}
                                    className={`${clickTable.includes(a.key) ? "highlighted" : ""}`}
                                    id={a.key}
                                />
                            ))}
                        </Scatter>
                    </ScatterChart>
                </ResponsiveContainer>
                {isSelecting && (
                    <svg
                        style={{
                            position: "absolute",
                            left: 0,
                            top: 0,
                            width: "100%",
                            height: "100%",
                            pointerEvents: "none",
                        }}
                    >
                        <rect
                            x={selectionBox.x}
                            y={selectionBox.y}
                            width={selectionBox.width}
                            height={selectionBox.height}
                            fill="#018b9844"
                            rx="10"
                            ry="10"
                            stroke="#01646d44"
                            strokeWidth="2"
                            strokeDasharray="1, 3"
                        />
                    </svg>
                )}
            </div>
        </>
    );
}

export default AnalyChart;
