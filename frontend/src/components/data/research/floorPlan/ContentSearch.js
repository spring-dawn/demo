import React, { useEffect, useState } from "react";
import SearchMulti from "../../../common/SearchMulti";
import moment from "moment";

function ContentSearch(props) {
    const { setData, render, sgg } = props;
    const [li, setLi] = useState([]);
    const startYear = 2020;
    const endYear = 2050;
    const years = Array.from({ length: endYear - startYear + 1 }, (_, index) => startYear + index);

    useEffect(() => {
        /**
         * select의 경우 option 항상 같이 가야함
         * required : true / false => default : false
         */
        let liArr = [
            { id: "name", label: "데이터명", type: "input", col: "4" },
            {
                id: "year",
                label: "연도",
                type: "selectYear",
                col: "4",
            },
            {
                id: "regCode",
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
        ];
        setLi(liArr);
    }, [sgg]);

    return <SearchMulti list={li} setData={setData} render={render} url="/api/data/floorPlan/search" />;
}

export default ContentSearch;
