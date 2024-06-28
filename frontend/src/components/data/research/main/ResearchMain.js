import React, { useState } from "react";

function ResearchMain(props) {
    const [tab, setTab] = useState(1);

    return (
        <div className="research_main y2021">
            <ul className="tab">
                <li
                    onClick={() => {
                        setTab(1);
                    }}
                    className={`${tab === 1 ? "on" : ""}`}
                >
                    2021
                </li>
                {/* <li
                    onClick={() => {
                        setTab(2);
                    }}
                    className={`${tab === 2 ? "on" : ""}`}
                >
                    2024
                </li> */}
            </ul>
            {tab === 1 && (
                <div className="table-container">
                    <img src={require("../../../../assets/img/research/2021/요약서 제목.png")}></img>
                    <h1>주차수급 실태</h1>
                    <div className="table-tb">
                        <div className="table-lr">
                            <h2>주차시설</h2>
                            <table className="data_table" style={{ height: "150px" }}>
                                <thead>
                                    <tr>
                                        <th rowSpan={2} width="10%">
                                            구분
                                        </th>
                                        <th rowSpan={2} width="10%">
                                            합계
                                        </th>
                                        <th colSpan={3} width="30%">
                                            노상
                                        </th>
                                        <th colSpan={3} width="30%">
                                            노외
                                        </th>
                                        <th colSpan={3} width="30%">
                                            부설
                                        </th>
                                    </tr>
                                    <tr>
                                        <th width="10%">소계</th>

                                        <th width="10%">거주자</th>

                                        <th width="10%">
                                            그 외<br />
                                            (공영)
                                        </th>

                                        <th width="10%">소계</th>

                                        <th width="10%">공영</th>

                                        <th width="10%">민영</th>

                                        <th width="10%">소계</th>

                                        <th width="10%">주거</th>

                                        <th width="10%">비주거</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr style={{ fontWeight: "bold" }}>
                                        <td rowSpan={2} width="10%">
                                            울산시 (비율)
                                        </td>
                                        <td width="10%">608,048</td>

                                        <td width="10%">20,257</td>
                                        <td width="10%">14,733</td>
                                        <td width="10%">5,524</td>

                                        <td width="10%">36,124</td>
                                        <td width="10%">11,771</td>
                                        <td width="10%">24,353</td>

                                        <td width="10%">551,667</td>
                                        <td width="10%">381,683</td>
                                        <td width="10%">169,984</td>
                                    </tr>
                                    <tr>
                                        <td width="10%">100%</td>
                                        <td colSpan={3} width="30%">
                                            3%
                                        </td>
                                        <td colSpan={3} width="30%">
                                            6%
                                        </td>
                                        <td colSpan={3} width="30%">
                                            91%
                                        </td>
                                    </tr>
                                </tbody>
                            </table>

                            <h2>주차수요</h2>
                            <table className="data_table">
                                <thead>
                                    <tr>
                                        <th rowSpan={2} width="10%">
                                            구분
                                        </th>
                                        <th rowSpan={2} width="10%">
                                            시간대
                                        </th>
                                        <th rowSpan={2} width="10%">
                                            합계
                                        </th>
                                        <th colSpan={4} width="40%">
                                            노상
                                        </th>
                                        <th colSpan={3} width="30%">
                                            노외
                                        </th>
                                        <th colSpan={3} width="30%">
                                            부설
                                        </th>
                                    </tr>
                                    <tr>
                                        <th width="10%">소계</th>

                                        <th width="10%">구획내</th>

                                        <th width="10%">구획외</th>

                                        <th width="10%">불법</th>

                                        <th width="10%">소계</th>

                                        <th width="10%">공영</th>

                                        <th width="10%">민영</th>

                                        <th width="10%">소계</th>

                                        <th width="10%">주거</th>

                                        <th width="10%">비주거</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr style={{ fontWeight: "bold" }}>
                                        <td rowSpan={4} width="10%">
                                            울산시 (비율)
                                        </td>
                                        <td rowSpan={2} width="10%">
                                            주간
                                        </td>
                                        <td width="10%">369,280</td>

                                        <td width="10%">74,583</td>
                                        <td width="10%">12,918</td>
                                        <td width="10%">43,039</td>
                                        <td width="10%">18,626</td>

                                        <td width="10%">21,231</td>
                                        <td width="10%">7,171</td>
                                        <td width="10%">14,060</td>

                                        <td width="10%">273,466</td>
                                        <td width="10%">169,615</td>
                                        <td width="10%">103,851</td>
                                    </tr>
                                    <tr>
                                        <td width="10%">100%</td>
                                        <td colSpan={4} width="30%">
                                            20%
                                        </td>
                                        <td colSpan={3} width="30%">
                                            6%
                                        </td>
                                        <td colSpan={3} width="30%">
                                            74%
                                        </td>
                                    </tr>
                                    <tr style={{ fontWeight: "bold" }}>
                                        <td rowSpan={2} width="10%">
                                            야간
                                        </td>
                                        <td width="10%">498,202</td>

                                        <td width="10%">75,401</td>
                                        <td width="10%">15,947</td>
                                        <td width="10%">41,521</td>
                                        <td width="10%">17,933</td>

                                        <td width="10%">12,599</td>
                                        <td width="10%">4,835</td>
                                        <td width="10%">7,764</td>

                                        <td width="10%">410,202</td>
                                        <td width="10%">369,755</td>
                                        <td width="10%">40,447</td>
                                    </tr>
                                    <tr>
                                        <td width="10%">100%</td>
                                        <td colSpan={4} width="30%">
                                            15%
                                        </td>
                                        <td colSpan={3} width="30%">
                                            3%
                                        </td>
                                        <td colSpan={3} width="30%">
                                            82%
                                        </td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <div className="table-lr">
                            <h2>주차수급 분석결과</h2>
                            <table className="data_table" style={{ height: "150px" }}>
                                <thead>
                                    <tr>
                                        <th rowSpan={2} width="10%">
                                            구분
                                        </th>
                                        <th rowSpan={2} width="10%">
                                            시간대
                                        </th>
                                        <th rowSpan={2} width="10%">
                                            주차장
                                            <br />
                                            확보율
                                        </th>
                                        <th colSpan={4} width="40%">
                                            주차장이용률
                                        </th>
                                        <th colSpan={2} width="20%">
                                            불법주차율
                                        </th>
                                    </tr>
                                    <tr>
                                        <th width="10%">전체</th>

                                        <th width="10%">노상</th>

                                        <th width="10%">노외</th>

                                        <th width="10%">부설</th>

                                        <th width="10%">
                                            전체수요
                                            <br />
                                            대비 <br />
                                            불법주차
                                            <br />
                                            비율
                                        </th>

                                        <th width="10%">
                                            노상주차
                                            <br />
                                            수요대비 <br />
                                            불법주차
                                            <br />
                                            비율
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr style={{ fontWeight: "bold" }}>
                                        <td rowSpan={2} width="10%">
                                            울산시 <br />
                                            (합계)
                                        </td>
                                        <td width="10%">주간</td>

                                        <td width="10%">164.7%</td>

                                        <td width="10%">50.6%</td>
                                        <td width="10%">63.8%</td>
                                        <td width="10%">58.8%</td>
                                        <td width="10%">49.6%</td>

                                        <td width="10%">5.0%</td>
                                        <td width="10%">25.0%</td>
                                    </tr>
                                    <tr style={{ fontWeight: "bold" }}>
                                        <td width="10%">야간</td>

                                        <td width="10%">122.0%</td>

                                        <td width="10%">72.2%</td>
                                        <td width="10%">78.7%</td>
                                        <td width="10%">34.9%</td>
                                        <td width="10%">74.4%</td>

                                        <td width="10%">3.6%</td>
                                        <td width="10%">23.8%</td>
                                    </tr>
                                </tbody>
                            </table>

                            <h2>주차난 심각지역</h2>
                            <table className="data_table">
                                <thead>
                                    <tr>
                                        <th rowSpan={2} width="10%">
                                            구분
                                        </th>
                                        <th colSpan={2} width="20%">
                                            주간
                                        </th>
                                        <th colSpan={2} width="20%">
                                            야간
                                        </th>
                                    </tr>
                                    <tr>
                                        <th width="10%">
                                            주차장확보율 <br />
                                            100% 미만 블록
                                        </th>

                                        <th width="10%">
                                            불법주차 <br />
                                            50대 이상 블록
                                        </th>

                                        <th width="10%">
                                            주차장확보율 <br />
                                            100% 미만 블록
                                        </th>

                                        <th width="10%">
                                            불법주차 <br />
                                            50대 이상 블록
                                        </th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr style={{ backgroundColor: "#fff0ea", fontWeight: "bold" }}>
                                        <td width="10%">합계</td>

                                        <td width="10%">61</td>
                                        <td width="10%">107</td>

                                        <td width="10%">274</td>
                                        <td width="10%">103</td>
                                    </tr>
                                    <tr>
                                        <td width="10%">중구</td>

                                        <td width="10%">6</td>
                                        <td width="10%">16</td>

                                        <td width="10%">68</td>
                                        <td width="10%">17</td>
                                    </tr>
                                    <tr>
                                        <td width="10%">남구</td>

                                        <td width="10%">11</td>
                                        <td width="10%">44</td>

                                        <td width="10%">85</td>
                                        <td width="10%">45</td>
                                    </tr>
                                    <tr>
                                        <td width="10%">동구</td>

                                        <td width="10%">4</td>
                                        <td width="10%">1</td>

                                        <td width="10%">22</td>
                                        <td width="10%">9</td>
                                    </tr>
                                    <tr>
                                        <td width="10%">북구</td>

                                        <td width="10%">40</td>
                                        <td width="10%">21</td>

                                        <td width="10%">81</td>
                                        <td width="10%">10</td>
                                    </tr>
                                    <tr>
                                        <td width="10%">울주군</td>

                                        <td width="10%">0</td>
                                        <td width="10%">25</td>

                                        <td width="10%">18</td>
                                        <td width="10%">22</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>

                    <h1>주차장 안전관리 실태</h1>
                    <div className="table-tb">
                        <div className="table-rl">
                            <h2>경사진주차장</h2>
                            <table className="data_table" style={{ height: "170px" }}>
                                <thead>
                                    <tr>
                                        <th rowSpan={2} width="10%">
                                            구분
                                        </th>
                                        <th colSpan={2} width="20%">
                                            합계
                                        </th>
                                        <th colSpan={2} width="20%">
                                            노상
                                        </th>
                                        <th colSpan={2} width="20%">
                                            노외
                                        </th>
                                        <th colSpan={2} width="20%">
                                            부설
                                        </th>
                                    </tr>
                                    <tr>
                                        <th width="10%">개소</th>

                                        <th width="10%">면수</th>

                                        <th width="10%">개소</th>

                                        <th width="10%">면수</th>

                                        <th width="10%">개소</th>

                                        <th width="10%">면수</th>

                                        <th width="10%">개소</th>

                                        <th width="10%">면수</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td width="10%">
                                            경사진 <br />
                                            주차장 현황
                                        </td>

                                        <td width="10%" style={{ fontWeight: "bold" }}>
                                            32
                                        </td>
                                        <td width="10%" style={{ fontWeight: "bold" }}>
                                            1,972
                                        </td>

                                        <td width="10%">-</td>
                                        <td width="10%" style={{ fontWeight: "bold" }}>
                                            1,767
                                        </td>

                                        <td width="10%" style={{ fontWeight: "bold" }}>
                                            3
                                        </td>
                                        <td width="10%" style={{ fontWeight: "bold" }}>
                                            17
                                        </td>

                                        <td width="10%" style={{ fontWeight: "bold" }}>
                                            29
                                        </td>
                                        <td width="10%" style={{ fontWeight: "bold" }}>
                                            170
                                        </td>
                                    </tr>
                                    <tr>
                                        <td width="10%">
                                            안전관리 <br />
                                            이행
                                        </td>

                                        <td width="10%">-</td>
                                        <td width="10%">230</td>

                                        <td width="10%">-</td>
                                        <td width="10%">230</td>

                                        <td width="10%">-</td>
                                        <td width="10%">-</td>

                                        <td width="10%">-</td>
                                        <td width="10%">-</td>
                                    </tr>
                                    <tr>
                                        <td width="10%">이행률 (%)</td>

                                        <td width="10%">-</td>
                                        <td width="10%">12%</td>

                                        <td width="10%">-</td>
                                        <td width="10%">13%</td>

                                        <td width="10%">-</td>
                                        <td width="10%">0%</td>

                                        <td width="10%">-</td>
                                        <td width="10%">0%</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>

                        <div className="table-rl">
                            <h2>노외/부설 방범설비 점검</h2>
                            <table className="data_table" style={{ height: "170px" }}>
                                <thead>
                                    <tr>
                                        <th rowSpan={2} width="10%">
                                            구분
                                        </th>
                                        <th rowSpan={2} width="10%">
                                            개소
                                        </th>
                                        <th rowSpan={2} width="10%">
                                            주차 면수
                                        </th>
                                        <th colSpan={6} width="60%">
                                            조사 결과
                                        </th>
                                    </tr>
                                    <tr>
                                        <th width="10%">관리사무소</th>

                                        <th width="10%">관리 인원수</th>

                                        <th width="10%">
                                            방범설비 <br />
                                            적정성
                                        </th>

                                        <th width="10%">CCTV 대수</th>

                                        <th width="10%">화질상태</th>

                                        <th width="10%">촬영보관</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr style={{ fontWeight: "bold" }}>
                                        <td width="10%">합계</td>
                                        <td width="10%">115</td>
                                        <td width="10%">32,959</td>

                                        <td width="10%">
                                            유114
                                            <br />
                                            /무1
                                        </td>
                                        <td width="10%">157인</td>
                                        <td width="10%">
                                            적정114
                                            <br />
                                            /부적정1*
                                        </td>
                                        <td width="10%">3,401대</td>
                                        <td width="10%">
                                            양호114
                                            <br />
                                            /불량1*
                                        </td>
                                        <td width="10%">1개월이상</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}

export default ResearchMain;
