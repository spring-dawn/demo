import React, { useEffect, useState } from "react";

import SearchMulti from "../../common/SearchMulti";

function ContentSearch(props) {
    const { setData, sgg } = props;

    const [li, setLi] = useState([]);
    useEffect(() => {
        /**
         * select의 경우 option 항상 같이 가야함
         * required : true / false => default : false
         */
        let liArr = [
            { id: "userId", label: "아이디", type: "input", col: "4" },
            { id: "userNm", label: "성명", type: "input", col: "4" },
            { id: "email", label: "이메일", type: "input", col: "4" },
            {
                id: "useYn",
                label: "상태",
                type: "select",
                option: [
                    { name: "전체", value: "" },
                    { name: "사용", value: "Y" },
                    { name: "미사용", value: "N" },
                ],
                col: "4",
            },
            {
                id: "agency",
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
            // {
            //     id: "admYn",
            //     label: "관리자여부",
            //     type: "select",
            //     option: [
            //         { name: "전체", value: "" },
            //         { name: "관리자", value: "Y" },
            //         { name: "일반사용자", value: "N" },
            //     ],
            //     col: "4",
            // },
        ];
        setLi(liArr);
    }, [sgg]);

    return <SearchMulti list={li} setData={setData} url="/api/system/user/search" />;
}

export default ContentSearch;
