import React, { useEffect, useState } from "react";
import { useRecoilState } from "recoil";
import { SearchState } from "../../../../../Context";

import SearchMulti from "../../../../common/SearchMulti";

function ContentSearch(props) {
    const { setData, sgg, render } = props;
    const [li, setLi] = useState([]);
    const [url, setUrl] = useState([]);
    const [search, setSearch] = useRecoilState(SearchState);
    //console.log(search);
    // console.log(li.find((ele) => ele.id == "sggCd")?.option.find((ele) => ele.name == search.sggCd));

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
                type: "selectMonth",
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

    /*return (
        <SearchMulti
            list={li}
            setData={setData}
            url={
                li.find((ele) => ele.id == "sggCd")?.option?.find((ele) => ele.value == search.sggCd)?.restUrl ||
                "/api/data/mr/status/sggTotal"
            }
        />
    );*/
    return <SearchMulti list={li} setData={setData} url="/api/data/mr/status/total" render={render} />;
}

export default ContentSearch;
