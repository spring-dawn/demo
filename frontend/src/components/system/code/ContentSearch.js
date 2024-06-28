import React, { useEffect, useState } from "react";

import SearchMulti from "../../common/SearchMulti";

function ContentSearch(props) {
    const { setData } = props;

    const [li, setLi] = useState([]);
    useEffect(() => {
        /**
         * select의 경우 option 항상 같이 가야함
         * required : true / false => default : false
         */
        let liArr = [
            { id: "name", label: "코드명", type: "input", col: "4" },
            { id: "value", label: "코드값", type: "input", col: "4" },
        ];
        setLi(liArr);
    }, []);

    return <SearchMulti list={li} setData={setData} url="/api/system/code/codes" />;
}

export default ContentSearch;
