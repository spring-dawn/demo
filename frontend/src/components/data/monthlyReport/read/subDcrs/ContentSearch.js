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
                    { name: "01", value: "01" },
                    { name: "02", value: "02" },
                    { name: "03", value: "03" },
                    { name: "04", value: "04" },
                    { name: "05", value: "05" },
                    { name: "06", value: "06" },
                    { name: "07", value: "07" },
                    { name: "08", value: "08" },
                    { name: "09", value: "09" },
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
                        value: reg.name,
                        name: reg.value,
                    })),
                ],
                col: "4",
            },
        ];
        setLi(liArr);
    }, [sgg]);

    return <SearchMulti list={li} setData={setData} url="/api/data/mr/decrease/search" />;
}

export default ContentSearch;
