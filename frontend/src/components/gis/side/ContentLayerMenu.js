import React, { useEffect, useState } from "react";
import { Menu, Checkbox, Radio, Input, Tree, Progress, Space, Select as AntSelect } from "antd";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMap, faLayerGroup, faLocationDot, faFolder, faMapLocationDot } from "@fortawesome/free-solid-svg-icons";
import { useCodeTree } from "../../../CommonHook";
import { Switch } from "antd";
import { useRecoilState } from "recoil";
import { loadingState } from "../../../Context";

// 실태 조사 사이드 메뉴
function ContentLayerMenu(props) {
    const { filterGugun, setFilterGugun } = { ...props };
    const { filterDong, setFilterDong } = { ...props };
    const [filterYear, setFilterYear] = useState(props.blank_year);
    const [shpData, setShpData] = useState([]);
    const [loading, setLoading] = useRecoilState(loadingState);
    const [checkState, setCheckState] = useState([
        { key: "블럭경계", type: "베이스", subType: "블럭경계", on: false },
        { key: "주차시설", type: "주차장", on: false },
        { key: "주차수요(주간)", type: "수요", on: false, group: "수요", isNight: false },
        { key: "주차수요(야간)", type: "수요", on: false, group: "수요", isNight: true },
    ]);

    // 지역 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [filterGugun] });

    const storedUserInfo = localStorage.getItem("user");
    let filterSgg = sgg;
    if (storedUserInfo) {
        let userInfo = JSON.parse(storedUserInfo);

        if (userInfo.agency != "31000") {
            filterSgg = filterSgg.filter((ele) => ele.name == userInfo.agency);
        }
    }

    const switchChange = (switchName, checked, type, subType, isNight) => {
        // 스위치 check 상태 변경
        let tmpCheckState = checkState.map((ele) => {
            if (ele.key == switchName) {
                ele.on = checked;
            }

            return ele;
        });

        setCheckState(tmpCheckState);

        // layer 데이터 변경
        const tmpMapData = { ...props.mapData };

        const tableNameList = shpData
            .filter((ele) => ele.type == type && (!subType || ele.subType == subType))
            .map((ele) => ele.tableName);

        let emptyCnt = 0;

        tableNameList.forEach((ele) => {
            const dataKey = `${ele}_${filterDong}`;
            const cacheData = props.mapData[dataKey];

            if (!cacheData) {
                tmpMapData[dataKey] = {};
                emptyCnt++;
            }

            tmpMapData[dataKey].on = checked;

            if (type == "수요") {
                tmpMapData[dataKey].isNight = isNight;
            }
        });

        if (emptyCnt == 0) {
            props.setMapData(tmpMapData);
        } else {
            setLoading(true);
            fetch(`/api/gis/search-object?dong=${filterDong}`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(tableNameList.map((ele) => ({ tree0: ele }))),
            })
                .then((res) => res.json())
                .then((json) => {
                    json.data.forEach((ele, idx) => {
                        const dataKey = `${tableNameList[idx]}_${filterDong}`;

                        tmpMapData[dataKey] = { ...tmpMapData[dataKey], ...ele };
                    });

                    props.setMapData(tmpMapData);
                    setLoading(false);
                });
        }
    };

    useEffect(() => {
        fetch("/api/gis/shp", {
            method: "GET",
        })
            .then((res) => res.json())
            .then((data) => {
                let origin = data.filter((ele) => ele.viewYn == "Y" && ele.state == "2");
                origin = origin.filter((ele) => ele.year == filterYear && ele.regCode == filterGugun);
                setShpData(origin);

                const tmp = { ...props.mapData };

                Object.keys(tmp).forEach((key) => {
                    tmp[key].on = false;
                });

                props.setMapData(tmp);
            });
    }, [filterYear, filterGugun, filterDong]);

    // useEffect(() => {
    //     checkState.forEach((ele) => {
    //         switchChange(ele.key, ele.on, ele.type, ele.subType, ele.isNight);
    //     });
    // }, [filterGugun, filterDong]);

    useEffect(() => {
        setCheckState(
            checkState.map((ele) => {
                ele.on = false;
                return ele;
            })
        );
    }, [filterGugun, filterDong]);

    useEffect(() => {
        setFilterDong("");
    }, [filterGugun]);

    useEffect(() => {
        props.setLayerCheckBox(checkState);
    }, [checkState]);

    return (
        <>
            <ul className="contentLayerWrap" key={`contentLayerWrap_${filterGugun}`}>
                <div className="contentFilter">
                    <Space wrap>
                        <AntSelect
                            options={filterSgg.map((reg) => ({ value: reg.name, label: reg.value }))}
                            value={filterGugun}
                            onChange={(value) => setFilterGugun(value)}
                        />
                    </Space>
                    <Space wrap>
                        <AntSelect
                            options={[
                                { value: "", label: "전체" },
                                ...(sgg.find((ele) => ele.name === filterGugun)?.children || []).map((dong) => ({
                                    value: dong.name,
                                    label: dong.value,
                                })),
                            ]}
                            value={filterDong}
                            onChange={(value) => setFilterDong(value)}
                        />
                    </Space>
                </div>
                {checkState.map((ele) => {
                    return (
                        <li key={ele.key}>
                            <div>{ele.key}</div>
                            <div className="switch">
                                <Switch
                                    checked={ele.on}
                                    onChange={(checked) => {
                                        if (ele.group == "수요") {
                                            const tmpCheckState = checkState.map((ele) => {
                                                if (ele.group == "수요") {
                                                    ele.on = false;
                                                }

                                                return ele;
                                            });

                                            setCheckState(tmpCheckState);
                                        }

                                        switchChange(ele.key, checked, ele.type, ele.subType, ele.isNight);
                                    }}
                                />
                            </div>
                        </li>
                    );
                })}
                {/*<li className="">*/}
                {/*    <div>블럭경계</div>*/}
                {/*    <div className="switch">*/}
                {/*        <Switch*/}
                {/*            checked={checkState.find((ele) => ele.key == "블럭경계").on}*/}
                {/*            onChange={(checked) => switchChange("블럭경계", checked, "베이스", "블럭경계")}*/}
                {/*        />*/}
                {/*    </div>*/}
                {/*</li>*/}
                {/*<li className="">*/}
                {/*    <div>주차시설</div>*/}
                {/*    <div className="switch">*/}
                {/*        <Switch*/}
                {/*            checked={checkState.find((ele) => ele.key == "주차시설").on}*/}
                {/*            onChange={(checked) => switchChange("주차시설", checked, "주차장")}*/}
                {/*        />*/}
                {/*    </div>*/}
                {/*</li>*/}
                {/*<li className="">*/}
                {/*    <div>주차수요(주간)</div>*/}
                {/*    <div className="switch">*/}
                {/*        <Switch*/}
                {/*            checked={checkState.find((ele) => ele.key == "주차수요(주간)").on}*/}
                {/*            onChange={(checked) => {*/}
                {/*                const tmpCheckState = checkState.map((ele) => {*/}
                {/*                    if (ele.group == "수요") {*/}
                {/*                        ele.on = false;*/}
                {/*                    }*/}

                {/*                    return ele;*/}
                {/*                });*/}

                {/*                setCheckState(tmpCheckState);*/}

                {/*                switchChange("주차수요(주간)", checked, "수요", undefined, false);*/}
                {/*            }}*/}
                {/*        />*/}
                {/*    </div>*/}
                {/*</li>*/}
                {/*<li className="">*/}
                {/*    <div>주차수요(야간)</div>*/}
                {/*    <div className="switch">*/}
                {/*        <Switch*/}
                {/*            checked={checkState.find((ele) => ele.key == "주차수요(야간)").on}*/}
                {/*            onChange={(checked) => {*/}
                {/*                const tmpCheckState = checkState.map((ele) => {*/}
                {/*                    if (ele.group == "수요") {*/}
                {/*                        ele.on = false;*/}
                {/*                    }*/}

                {/*                    return ele;*/}
                {/*                });*/}

                {/*                setCheckState(tmpCheckState);*/}

                {/*                switchChange("주차수요(야간)", checked, "수요", undefined, true);*/}
                {/*            }}*/}
                {/*        />*/}
                {/*    </div>*/}
                {/*</li>*/}
            </ul>
        </>
    );
}

export default ContentLayerMenu;
