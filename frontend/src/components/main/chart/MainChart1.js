import {
    LineChart,
    Line,
    BarChart,
    ComposedChart,
    Bar,
    Cell,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
    Label,
    LabelList,
    defs,
    linearGradient,
    stop,
} from "recharts";

// 12개월 추이
function MainChart1({ gugun, data }) {
    return (
        <ResponsiveContainer>
            <ComposedChart
                data={data}
                margin={{
                    top: 30,
                    right: -10,
                    bottom: -8,
                    left: 10,
                }}
            >
                <defs>
                    <linearGradient id="colorGradient1" x1="0" y1="0" x2="0" y2="1">
                        {/* <stop offset="0%" stopColor="#008CCE" stopOpacity={1} /> */}
                        <stop offset="0%" stopColor="#36236B" stopOpacity={1} />
                        <stop offset="100%" stopColor="#C60026" stopOpacity={1} />
                    </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="baseDate" />
                <YAxis
                    yAxisId="left"
                    name="weight"
                    orientation="left"
                    stroke="#000000"
                    domain={[
                        (dataMin) => Math.floor(dataMin / 10000) * 10000,
                        (dataMax) => Math.ceil(dataMax / 10000) * 10000,
                    ]}
                    tickFormatter={(value) => {
                        return value.toLocaleString();
                    }}
                >
                    <Label value="(대,면)" style={{ textAnchor: "middle" }} position={"top"} offset={15} />
                </YAxis>
                <YAxis
                    yAxisId="right"
                    name="weight"
                    orientation="right"
                    stroke="#000000"
                    domain={([dataMin, dataMax]) => {
                        let min = Math.floor(dataMin / 10) * 10;
                        let max = Math.ceil(dataMax / 10) * 10;

                        min = min > 80 ? 80 : min;
                        max = max < 130 ? 130 : max;

                        return [min, max];
                    }}
                    // domain={[(dataMin) => Math.ceil(dataMin / 10) * 10, (dataMax) => Math.floor(dataMax / 10) * 10]}
                >
                    <Label value="(%)" style={{ textAnchor: "end" }} position={"top"} offset={15} />
                </YAxis>
                <Tooltip
                    formatter={(value, name, props) => {
                        if (props.dataKey === "rate") {
                            return `${value}%`;
                        }
                        return value.toLocaleString();
                    }}
                />
                <Legend />
                <Bar
                    name={"주차장 확보율"}
                    yAxisId="right"
                    type="monotone"
                    dataKey="rate"
                    fill="#00A5B5"
                    stroke="#1d82b7"
                    activeDot={{ r: 8 }}
                    barSize={50}
                />
                <Line
                    name={"주차장 면수"}
                    yAxisId="left"
                    type="monotone"
                    dataKey="spcsArea"
                    stroke="#2ca8ff"
                    strokeWidth={4}
                    dot={{ r: 5 }}
                />
                <Line
                    name={"차량등록대수"}
                    yAxisId="left"
                    type="monotone"
                    dataKey="spcsCount"
                    stroke="#ab8d58"
                    strokeWidth={4}
                    dot={{ r: 5 }}
                />
            </ComposedChart>
        </ResponsiveContainer>
    );
}

export default MainChart1;
