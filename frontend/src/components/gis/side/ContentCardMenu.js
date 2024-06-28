import React, { useEffect, useState } from "react";
import { Menu, Checkbox, Radio, Input, Tree, Progress, Space, Select as AntSelect } from "antd";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFolder, faMapLocationDot } from "@fortawesome/free-solid-svg-icons";
import { gugunParseCodeToName } from "../CommonGisFunction";
import { useCodeTree } from "../../../CommonHook";

// 관리 카드 GIS 사이드 메뉴
function ContentCardMenu(props) {
    const [cardTreeCheckedKeys, setCardTreeCheckedKeys] = useState([]);
    const { filterGugun, setFilterGugun } = { ...props };
    const [searchValue, setSearchValue] = useState("");
    const [filterYear, setFilterYear] = useState(props.blank_year);

    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [filterGugun] });

    const changeMapData = (props, submitData) => {
        let filterSubmitData = submitData.filter((ele) => !props.mapData[ele.tree0]);

        if (!filterSubmitData.length) {
            const save = { ...props.mapData };

            Object.keys(save).forEach((key) => {
                save[key].on = false;
            });

            submitData.forEach((ele) => {
                save[ele.tree0].on = true;
            });

            props.setMapData(save);
        } else {
            fetch("/api/gis/search-object", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify(filterSubmitData),
            })
                .then((res) => res.json())
                .then((json) => {
                    const save = { ...props.mapData };

                    json.data.forEach((ele, idx) => {
                        save[filterSubmitData[idx].tree0] = ele;
                    });

                    Object.keys(save).forEach((key) => {
                        save[key].on = false;
                    });

                    submitData.forEach((ele) => {
                        save[ele.tree0].on = true;
                    });

                    props.setMapData(save);
                });
        }
    };

    useEffect(() => {
        const tmpCardTreeCheckedKeys = props.selectBlock.map((ele) => {
            const keyVal = `ele^${ele.block.replace("\n", "")}`;

            return keyVal;
        });

        setCardTreeCheckedKeys(tmpCardTreeCheckedKeys);
    }, [props.selectBlock]);

    const createCardTree = (cardData, cardTreeCheckedKeys, searchValue) => {
        // 데이터 필터링 및 트리 구성
        const tmpData = {};
        let treeData = [];

        // 주간 데이터만 필터링
        let filteredCardData = cardData.filter((ele) => ele.dayNight == "주간");

        // 기본 tree 구성 생성
        filteredCardData.forEach((ele, index) => {
            if (!tmpData[ele.hjDong]) {
                tmpData[ele.hjDong] = { children: [] };
                treeData.push({
                    title: ele.hjDong,
                    key: `hjDong^${ele.hjDong}`,
                    icon: <FontAwesomeIcon icon={faFolder} color={"#fcd35f"} />,
                    children: tmpData[ele.hjDong].children,
                });
            }

            tmpData[ele.hjDong].children.push({
                title: ele.block,
                icon: <FontAwesomeIcon icon={faMapLocationDot} color={"rgba(77,138,215,0.94)"} />,
                key: `ele^${ele.block.replace("\n", "")}`,
            });
        });

        // 트리 데이터 정렬
        treeData.sort((a, b) => {
            if (a.title < b.title) {
                return 1;
            }
            if (a.title > b.title) {
                return -1;
            }
            return 0;
        });

        // 체크박스 변경 이벤트 핸들러
        const onCheck = (checkedKeysValue) => {
            // 체크된 항목에 해당하는 데이터를 필터링
            let nodeList = checkedKeysValue.filter((ele) => {
                const tree = ele.split("^");
                if (tree[0] === "ele") {
                    return true;
                }
            });

            const filterData = cardData.filter((ele) => {
                const result = nodeList.find((node) => {
                    const split = node.split("^");
                    return ele.block === split[1];
                });

                return result ? true : false;
            });

            // 선택된 블록 데이터 설정
            props.setSelectBlock(filterData);
        };

        // 부모 키 찾기
        const getParentKey = (key, tree) => {
            let parentKey;
            for (let i = 0; i < tree.length; i++) {
                const node = tree[i];
                if (node.children) {
                    if (node.children.some((item) => item.key === key)) {
                        parentKey = node.key;
                    } else if (getParentKey(key, node.children)) {
                        parentKey = getParentKey(key, node.children);
                    }
                }
            }
            return parentKey;
        };

        // 검색어 입력 변경 이벤트 핸들러
        const onChange = (e) => {
            const { value } = e.target;
            setSearchValue(value);
        };

        const style = props.menuWidth && props.menuHeight ? { width: props.menuWidth, height: props.menuHeight } : {};

        return (
            <div className="contentTree scroll" style={{ maxHeight: "60vh" }}>
                <Tree
                    checkable
                    showIcon
                    treeData={treeData}
                    onCheck={onCheck}
                    checkedKeys={cardTreeCheckedKeys}
                    // defaultExpandedKeys={treeData.map((ele) => ele.key)}
                />
            </div>
        );
    };

    return (
        <div className={"contentMenuWrap"}>
            {createCardTree(
                props.cardData.filter(
                    (ele) => ele.year == filterYear && ele.sggNm == gugunParseCodeToName(filterGugun)
                ),
                cardTreeCheckedKeys,
                searchValue
            )}
        </div>
    );
}

export default ContentCardMenu;
