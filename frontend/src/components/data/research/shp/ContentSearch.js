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
            { id: "name", label: "데이터명", type: "input", col: "4" },
            {
                id: "year",
                label: "연도",
                type: "selectYear",
                col: "4",
            },
            {
                id: "reg",
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
                id: "viewYn",
                label: "시각화 여부",
                type: "select",
                option: [
                    { name: "전체", value: "" },
                    { name: "사용", value: "Y" },
                    { name: "미사용", value: "N" },
                ],
                col: "4",
            },
            {
                id: "state",
                label: "업로드 상태",
                type: "select",
                option: [
                    { name: "전체", value: "" },
                    { name: "등록중", value: "1" },
                    { name: "완료", value: "2" },
                    { name: "업로드 실패", value: "-1" },
                ],
                col: "4",
            },
        ];
        setLi(liArr);
    }, [sgg, year]);

    return <SearchMulti list={li} setData={setData} render={render} url="/api/gis/search" />;
}

export default ContentSearch;
