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
} from "recharts";

// 주차방 변경 현황
function MainChart2({ monthData }) {
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
        <ResponsiveContainer>
            <ComposedChart
                data={[
                    { name: "공영", data1: pblrdL, data2: pblrdS },
                    { name: "민영", data1: prvL, data2: prvS },
                    { name: "부설", data1: subseL, data2: subseS },
                ]}
                margin={{
                    top: 30,
                    bottom: -8,
                }}
            >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="name" />
                <YAxis
                    yAxisId="left"
                    name="weight"
                    orientation="left"
                    stroke="#000000"
                    tickFormatter={(value) => {
                        return value.toLocaleString();
                    }}
                >
                    <Label value="(면수)" style={{ textAnchor: "middle" }} position={"top"} offset={15} />
                </YAxis>
                <YAxis yAxisId="right" name="weight" orientation="right" stroke="#000000">
                    <Label value="(개소)" style={{ textAnchor: "end" }} position={"top"} offset={15} />
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
                    name={"개소"}
                    yAxisId="right"
                    type="monotone"
                    dataKey="data1"
                    // fill="url(#colorGradient1)"
                    fill="#00A5B5"
                    stroke="#1d82b7"
                    activeDot={{ r: 8 }}
                    barSize={50}
                />
                <Line
                    name={"증가"}
                    yAxisId="left"
                    type="monotone"
                    dataKey="data2"
                    stroke="#2ca8ff"
                    strokeWidth={4}
                    dot={{ r: 5 }}
                />
            </ComposedChart>
        </ResponsiveContainer>
    );
}

export default MainChart2;
