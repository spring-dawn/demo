import React, { useEffect, useState } from "react";
import SearchMulti from "../../common/SearchMulti";

function ContentSearch(props) {
    const { setData, render, sgg } = props;
    const [li, setLi] = useState([]);

    useEffect(() => {
        /**
         * select의 경우 option 항상 같이 가야함
         * required : true / false => default : false
         */
        let liArr = [
            { id: "content", label: "요청사항", type: "input", col: "4" },
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
        ];
        setLi(liArr);
    }, [sgg]);

    return <SearchMulti list={li} setData={setData} render={render} url="/api/system/feedback/search" />;
}

export default ContentSearch;
