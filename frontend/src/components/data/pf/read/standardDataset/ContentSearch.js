import React, { useEffect, useState } from "react";

import SearchMulti from "../../../../common/SearchMulti";

function ContentSearch(props) {
    const { setData, sgg } = props;

    const [li, setLi] = useState([]);
    useEffect(() => {
        /**
         * select의 경우 option 항상 같이 가야함
         * required : true / false => default : false
         */
        let liArr = [
            // {
            //     id: "year",
            //     label: "연도",
            //     type: "selectYear",
            //     col: "4",
            // },
            // {
            //     id: "month",
            //     label: "월",
            //     type: "select",
            //     option: [
            //         { name: "전체", value: "" },
            //         { name: "01", value: "01" },
            //         { name: "02", value: "02" },
            //         { name: "03", value: "03" },
            //         { name: "04", value: "04" },
            //         { name: "05", value: "05" },
            //         { name: "06", value: "06" },
            //         { name: "07", value: "07" },
            //         { name: "08", value: "08" },
            //         { name: "09", value: "09" },
            //         { name: "10", value: "10" },
            //         { name: "11", value: "11" },
            //         { name: "12", value: "12" },
            //     ],
            //     col: "4",
            // },
            {
                id: "lotType",
                label: "주차유형",
                type: "select",
                option: [
                    { name: "전체", value: "" },
                    { name: "공영노상", value: "1" },
                    { name: "공영노외", value: "2" },
                    // { name: "공영부설", value: "3" },
                    // { name: "민영노상", value: "4" },
                    { name: "민영노외", value: "5" },
                    // { name: "민영부설", value: "6" },
                    // { name: "부설", value: "7" },
                    { name: "부설개방", value: "8" },
                    { name: "사유지개방", value: "9" },
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
                        value: reg.name,
                        name: reg.value,
                    })),
                ],
                col: "4",
            },
            {
                id: "mngNo",
                label: "일련번호",
                type: "input",
                col: "4",
            },
            {
                id: "lotNm",
                label: "주차장명",
                type: "input",
                col: "4",
            },
            {
                id: "minSpcs",
                label: "최소주차면수",
                type: "input",
                type2: "number",
                col: "4",
            },
            {
                id: "maxSpcs",
                label: "최대주차면수",
                type: "input",
                type2: "number",
                col: "4",
            },
        ];
        setLi(liArr);
    }, [sgg]);

    return <SearchMulti list={li} setData={setData} url="/api/data/facility/standard/search" />;
}

export default ContentSearch;
