import React, { useEffect, useState } from "react";
import { Menu, Checkbox, Radio, Input, Tree, Progress, Space, Select as AntSelect } from "antd";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faMap, faLayerGroup, faLocationDot, faFolder, faMapLocationDot } from "@fortawesome/free-solid-svg-icons";
import { useCodeTree } from "../../../CommonHook";

// 실태 조사 사이드 메뉴
function ContentLayerMenu(props) {
    const [filterYear, setFilterYear] = useState(props.blank_year);
    const { filterGugun, setFilterGugun } = { ...props };
    const [originData, setOriginData] = useState([]);
    const [cardTreeCheckedKeys, setCardTreeCheckedKeys] = useState([]);
    const [searchValue, setSearchValue] = useState("");
    const [progress, setProgress] = useState(100);

    // 지역 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    // 체크박스 변경 핸들러
    const onCheck = (checkedKeysValue, props, first) => {
        if (progress < 100) {
            alert("데이터를 가져오는 중입니다...");
            return;
        }

        setCardTreeCheckedKeys(checkedKeysValue);

        const submitData = checkedKeysValue
            .filter((ele) => {
                const split = ele.split("^");

                if (split[0] == "ele") {
                    return true;
                } else {
                    return false;
                }
            })
            .map((ele) => {
                return { tree0: ele.split("^")[1] };
            });

        // 이미 존재하는 레이어는 생략
        let filterSubmitData = submitData.filter((ele) => !props.mapData[ele.tree0]);

        if (!filterSubmitData.length) {
            const save = { ...props.mapData };

            Object.keys(save).forEach((key) => {
                save[key].on = false;
            });

            submitData.forEach((ele) => {
                save[ele.tree0].on = true;
                save[ele.tree0].first = first;
            });

            props.setMapData(save);
        } else {
            // 체크 shp 검색
            let pro = 0;
            const proIntervalId = setInterval(() => {
                if (pro >= 99) {
                    pro = 99;
                } else {
                    pro += 1;
                }

                setProgress(pro);
            }, [100]);

            fetch("/api/gis/search-object", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(filterSubmitData),
            })
                .then((res) => res.json())
                .then((json) => {
                    console.log(json);

                    const save = { ...props.mapData };

                    json.data.forEach((ele, idx) => {
                        save[filterSubmitData[idx].tree0] = ele;
                    });

                    Object.keys(save).forEach((key) => {
                        save[key].on = false;
                    });

                    submitData.forEach((ele) => {
                        save[ele.tree0].on = true;
                        save[ele.tree0].first = first;
                    });

                    props.setMapData(save);
                    setProgress(100);
                    clearInterval(proIntervalId);
                });
        }
    };

    const createMenuTree = (originData, props, cardTreeCheckedKeys, searchValue, progress) => {
        // 데이터 필터링 및 트리 구성
        const tmpData = {};
        let treeData = [];

        // 기본 tree 구성 생성
        originData.forEach((ele, index) => {
            if (!tmpData[ele.type]) {
                tmpData[ele.type] = { children: [] };
                treeData.push({
                    title: ele.type,
                    key: `type^${ele.type}^${ele.resultNo}`,
                    icon: <FontAwesomeIcon icon={faFolder} color={"#fcd35f"} />,
                    children: tmpData[ele.type].children,
                });
            }

            tmpData[ele.type].children.push({
                title: ele.name,
                icon: <FontAwesomeIcon icon={faMapLocationDot} color={"rgba(77,138,215,0.94)"} />,
                key: `ele^${ele.tableName}`,
            });
        });

        const onChange = (e) => {
            const { value } = e.target;
            setSearchValue(value);
        };

        const defaultExpandedKeys = [];

        const getDefaultExpandedKeys = (data, arr) => {
            arr.push(data.key);

            if (data.children) {
                data.children.forEach((ele) => getDefaultExpandedKeys(ele, arr));
            }
        };

        treeData.forEach((element) => {
            getDefaultExpandedKeys(element, defaultExpandedKeys);
        });

        return (
            <>
                <div className="contentFilter">
                    <Space wrap>
                        <AntSelect
                            options={sgg.map((reg) => ({ value: reg.name, label: reg.value }))}
                            value={filterGugun}
                            onChange={(value) => setFilterGugun(value)}
                        />
                    </Space>
                </div>
                <div style={{ display: "flex", justifyContent: "center", alignItems: "center", padding: "4px" }}>
                    {progress < 100 && (
                        <Progress
                            style={{ width: "99%", marginTop: "-8px" }}
                            strokeColor={{
                                "0%": "#108ee9",
                                "100%": "#87d068",
                            }}
                            percent={progress}
                        />
                    )}
                </div>
                <div className="contentTree scroll" style={{ maxHeight: "calc(100vh - 98px)" }}>
                    <Tree
                        key={`ContentLayerMenu`}
                        showIcon
                        checkable
                        treeData={treeData}
                        onCheck={(checkedKeysValue, e) => onCheck(checkedKeysValue, props)}
                        checkedKeys={cardTreeCheckedKeys}
                        selectedKeys={[]}
                        defaultExpandedKeys={defaultExpandedKeys}
                    />
                </div>
            </>
        );
    };

    useEffect(() => {
        fetch("/api/gis/shp", {
            method: "GET",
        })
            .then((res) => res.json())
            .then((data) => {
                let origin = data.filter((ele) => ele.viewYn == "Y" && ele.state == "2");

                origin = origin.filter((ele) => ele.year == filterYear && ele.regCode == filterGugun);

                const tmp = { ...props.mapData };

                Object.keys(tmp).forEach((key) => {
                    tmp[key].on = false;
                });

                props.setMapData(tmp);
                setOriginData(origin);

                // 실태조사 기본 체크
                const defaultCheck = origin
                    .filter(
                        (ele) => ele.type == "베이스" && ele.subType == "블럭경계" && ele.featureType == "MULTIPOLYGON"
                    )
                    .map((ele) => {
                        const keyVal = `ele^${ele.tableName}`;

                        return keyVal;
                    });

                onCheck(defaultCheck, props, true);
            });
    }, [filterYear, filterGugun]);

    return (
        <div className={"contentMenuWrap"} key={`layer_${originData.map((ele) => ele.resultNo).join(".")}`}>
            {createMenuTree(originData, props, cardTreeCheckedKeys, searchValue, progress)}
        </div>
    );
}

export default ContentLayerMenu;
