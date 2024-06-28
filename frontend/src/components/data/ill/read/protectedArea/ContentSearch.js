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

    return <SearchMulti list={li} setData={setData} url="/api/data/illegal/protected/search" />;
}

export default ContentSearch;
