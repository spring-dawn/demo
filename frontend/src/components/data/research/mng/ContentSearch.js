import React, { useEffect, useState } from "react";
import SearchMulti from "../../../common/SearchMulti";

function ContentSearch(props) {
    const { setData, render, sgg, year } = props;

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
                id: "rschType",
                label: "유형",
                type: "select",
                option: [
                    { name: "전체", value: "" },
                    { name: "관리카드", value: "0" },
                    { name: "정리 서식", value: "1" },
                ],
                col: "4",
            },
            {
                id: "collectYn",
                label: "데이터화",
                type: "select",
                option: [
                    { name: "전체", value: "" },
                    { name: "완료", value: "Y" },
                    { name: "대기", value: "N" },
                ],
                col: "4",
            },
            { id: "dataNm", label: "데이터명", type: "input", col: "4" },
        ];
        setLi(liArr);
    }, [sgg, year]);
    return <SearchMulti list={li} setData={setData} render={render} url="/api/data/rsch/data/search" />;
}

export default ContentSearch;
