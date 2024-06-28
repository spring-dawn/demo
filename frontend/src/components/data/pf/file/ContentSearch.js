import React, { useEffect, useState } from "react";
import SearchMulti from "../../../common/SearchMulti";

export default function ContentSearch(props) {
    const { setData, render, sgg } = props;

    const [li, setLi] = useState([]);
    useEffect(() => {
        /**
         * select의 경우 option 항상 같이 가야함
         * required : true / false => default : false
         */
        let liArr = [
            {
                id: "year",
                label: "연도",
                type: "selectYear",
                col: "4",
            },
            {
                id: "month",
                label: "월",
                type: "select",
                option: [
                    { name: "전체", value: "" },
                    { name: "1", value: "01" },
                    { name: "2", value: "02" },
                    { name: "3", value: "03" },
                    { name: "4", value: "04" },
                    { name: "5", value: "05" },
                    { name: "6", value: "06" },
                    { name: "7", value: "07" },
                    { name: "8", value: "08" },
                    { name: "9", value: "09" },
                    { name: "10", value: "10" },
                    { name: "11", value: "11" },
                    { name: "12", value: "12" },
                ],
                col: "4",
            },
            {
                id: "sggCd",
                label: "구군",
                type: "select",
                option: [
                    { name: "전체", value: "" },
                    ...sgg.map((reg) => ({
                        name: reg.value,
                        value: reg.name,
                    })),
                ],
                col: "4",
            },
            {
                // 표준관리대장 기준. 1:공영노상, 2:공영노외, 3:공영부설, 4:민영노상, 5:민영노외, 6:민영부설, 7:부설, 8:부설개방, 9:사유지개방
                id: "lotType",
                label: "주차유형",
                type: "select",
                option: [
                    // { label: "공영주차장", value: "1" },
                    { name: "전체", value: "" },
                    { name: "민영(노외)주차장", value: "5" },
                    // { name: "노외주차장", value: 3 },
                    // { name: "부설주차장", value: 4 },
                    { name: "부설개방 주차장", value: "8" },
                    { name: "사유지개방 주차장", value: "9" },
                ],
                col: "4",
            },
            {
                id: "collectYn",
                label: "데이터승인",
                type: "select",
                option: [
                    { name: "전체", value: "" },
                    { name: "완료", value: "Y" },
                    { name: "대기", value: "N" },
                    { name: "반려", value: "X" },
                ],
                col: "4",
            },
            { id: "dataNm", label: "데이터명", type: "input", col: "4" },
        ];
        setLi(liArr);
    }, [sgg]);
    return <SearchMulti list={li} setData={setData} render={render} url="/api/data/facility/file/search" />;
}
