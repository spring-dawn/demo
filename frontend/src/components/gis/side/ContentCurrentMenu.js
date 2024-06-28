import React, { useEffect, useState } from "react";
import { Menu, Checkbox, Radio, Input, Tree, Progress, Space, Select as AntSelect } from "antd";
import { Pagination, Card } from "antd";
import { findLayerList } from "../CommonGisFunction";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faFireExtinguisher, faLocationDot, faCircleRight } from "@fortawesome/free-solid-svg-icons";
import GisCheckBoxUtile from "../utile/GisCheckBoxUtile";
import { useCodeTree } from "../../../CommonHook";
import filter_ico from "../../../assets/img/gis/filter.png";
import multi_ico from "../../../assets/img/gis/multi.png";
import blank_ico from "../../../assets/img/gis/blank.png";

// 현황 GIS 사이드 메뉴
function ContentCurrentMenu({
    filterGugun,
    setFilterGugun,
    filterSearch,
    setFilterSearch,
    mapObj,
    setSelectLayer,
    selectLayer,
    commonLayer,
    setCommonLayer,
    rowData,
    sideOn,
    setSideOn,
    subMapData,
    setSubMapData,
}) {
    const [page, setPage] = useState(1);
    const [perPage, setPerPage] = useState(8);
    const [selectHistory, setSelectHistory] = useState("layer"); // 선택된 히스토리 분류
    const [historyYearOptions, setHistoryYearOptions] = useState([]); // 히스토리 연도 배열
    const [selectHistoryYear, setSelectHistoryYear] = useState(""); // 선택된 히스토리 연도
    const [layerGroupList, setLayerGroupList] = useState([]); // 실태 조사 새창 그룹
    const [cardGroupList, setCardGroupList] = useState([]); // 관리 카드 새창 그룹

    // 지역 코드 가져오기
    const { tree: sgg, set: setSgg } = useCodeTree({ parentNm: "31000", deps: [] });

    const storedUserInfo = localStorage.getItem("user");
    let filterSgg = sgg;
    if (storedUserInfo) {
        let userInfo = JSON.parse(storedUserInfo);

        if (userInfo.agency != "31000") {
            filterSgg = filterSgg.filter((ele) => ele.name == userInfo.agency);
        }
    }

    const highlightVariable = (text, search) => {
        const regex = new RegExp(`(${search})`, "gi");
        return text.replace(regex, '<span style="background-color: yellow;">$1</span>');
    };

    function truncateText(text) {
        const maxLength = 20;

        if (text) {
            text = String(text);
        } else {
            return "데이터 없음";
        }

        if (text.trim(" ") === "") {
            return "데이터 없음";
        }

        if (text.length > maxLength) {
            return `${text.substring(0, maxLength)}...`;
        }

        return text;
    }

    // 등록된 실태조사, 관리카드 그룹 가져오기
    useEffect(() => {
        fetch("/api/gis/shp", {
            method: "GET",
        })
            .then((res) => res.json())
            .then((data) => {
                let origin = data.filter((ele) => ele.viewYn == "Y" && ele.state == "2");
                const layerGroupList = [...new Set(origin.map((ele) => ele.year))].sort((a, b) => b - a);
                const cardGroupList = [
                    ...new Set(
                        origin.filter((ele) => ele.subType == "블럭경계" && ele.cardYn == "Y").map((ele) => ele.year)
                    ),
                ].sort((a, b) => b - a);

                setLayerGroupList(layerGroupList);
                setCardGroupList(cardGroupList);
            });
    }, []);

    // 현재 페이지 초기화
    useEffect(() => {
        setPage(1);
    }, [rowData, commonLayer, filterSearch]);

    // 선택된 히스토리에 따른 year 옵션 값 변경
    useEffect(() => {
        const yearOptions =
            selectHistory == "layer"
                ? layerGroupList.map((ele) => ({ label: `${ele}년`, value: ele }))
                : cardGroupList.map((ele) => ({ label: `${ele}년`, value: ele }));

        setSelectHistoryYear(yearOptions.length ? yearOptions[0].value : "");
        setHistoryYearOptions(yearOptions);
    }, [layerGroupList, cardGroupList, selectHistory]);

    return (
        <div className={`mapContent static current ${sideOn ? "side_on" : ""}`}>
            <div className="mapContBody">
                <div className={`contentMenuWrap ${sideOn ? "hide_on" : ""}`}>
                    <div className="contentFilter">
                        <Space wrap>
                            <AntSelect
                                options={filterSgg.map((reg) => ({ value: reg.name, label: reg.value }))}
                                value={filterGugun}
                                onChange={(value) => setFilterGugun(value)}
                            />
                        </Space>
                        <Input
                            style={{ height: 32, width: 170, fontSize: "12px" }}
                            value={filterSearch}
                            placeholder="검색어를 입력해주세요."
                            onChange={(e) => {
                                const { value } = e.target;
                                setFilterSearch(value);
                            }}
                            size="large"
                        />
                    </div>
                    <ul className="contentPagingData scroll">
                        {rowData
                            .sort((a, b) => {
                                if (a.group !== b.group) {
                                    return a.group - b.group;
                                } else {
                                    return a.title_.val.localeCompare(b.title_.val);
                                }
                            })
                            .slice((page - 1) * perPage, (page - 1) * perPage + perPage)
                            .map((ele, idx) => {
                                return (
                                    <li
                                        key={ele.key_ + `_${idx}`}
                                        onClick={({ target }) => {
                                            let isMove = false;
                                            if (target.closest(".move")) {
                                                isMove = true;
                                            }

                                            const layerList = findLayerList(mapObj.map, "currentLayer");

                                            layerList.forEach((layer) => {
                                                let feature;
                                                let clusterFeature;

                                                layer
                                                    .getSource()
                                                    .getFeatures()
                                                    .forEach((f) => {
                                                        if (f.get("features")) {
                                                            f.get("features").forEach((f2) => {
                                                                if (f2.get("data").key_ == ele.key_) {
                                                                    feature = f;
                                                                    clusterFeature = f2;
                                                                }
                                                            });
                                                        } else if (f.get("data").key_ == ele.key_) {
                                                            feature = f;
                                                            clusterFeature = f;
                                                        }
                                                    });

                                                if (feature) {
                                                    setSelectLayer([
                                                        {
                                                            key_: ele.key_,
                                                            layer,
                                                            feature,
                                                            clusterFeature,
                                                            style: feature.getStyle(),
                                                            isMove: isMove,
                                                        },
                                                    ]);
                                                }
                                            });
                                        }}
                                    >
                                        <div
                                            className="header con"
                                            style={{ backgroundColor: ele.backgroundColor_ || "none" }}
                                        >
                                            <div className="ico">{ele.ico_}</div>
                                            <div
                                                className="tit"
                                                dangerouslySetInnerHTML={{
                                                    __html: highlightVariable(
                                                        truncateText(ele.title_.val),
                                                        filterSearch
                                                    ),
                                                }}
                                            ></div>
                                            <div className="move" style={{ marginLeft: "auto" }}>
                                                <FontAwesomeIcon
                                                    icon={faCircleRight}
                                                    color={"rgba(77,104,255,0.83)"}
                                                    fontSize={20}
                                                />
                                            </div>
                                        </div>
                                        <div className="body con">
                                            <p>
                                                <span className="col">{truncateText(ele.con1_.col)}: </span>
                                                <span
                                                    className="val"
                                                    dangerouslySetInnerHTML={{
                                                        __html: highlightVariable(
                                                            truncateText(ele.con1_.val),
                                                            filterSearch
                                                        ),
                                                    }}
                                                ></span>
                                            </p>
                                            <p>
                                                <span className="col">{truncateText(ele.con2_.col)}: </span>
                                                <span
                                                    className="val"
                                                    dangerouslySetInnerHTML={{
                                                        __html: highlightVariable(
                                                            truncateText(ele.con2_.val),
                                                            filterSearch
                                                        ),
                                                    }}
                                                ></span>
                                            </p>
                                        </div>
                                    </li>
                                );
                            })}
                    </ul>
                    <div className="contentPaging">
                        <Pagination
                            size="small"
                            total={rowData.length}
                            defaultPageSize={perPage}
                            current={page}
                            onChange={(page, pageSize) => {
                                setPage(page);
                                setPerPage(pageSize);
                            }}
                            showSizeChanger={false}
                        />
                    </div>
                    <ul className={"currentOptions"}>
                        <li className="filter">
                            <div className={"filter_btn"}>
                                <img src={filter_ico} />
                            </div>
                            <div className={"list_wrap"}>
                                <ul className="layer_list">
                                    <h3>주차장</h3>
                                    <ul className="type1">
                                        {commonLayer
                                            .filter(({ group }) => group == "1")
                                            .map(({ value, name }) => {
                                                return (
                                                    <li
                                                        key={value}
                                                        className={`${
                                                            commonLayer.find((ele) => ele.value == value && ele.on)
                                                                ? "on"
                                                                : ""
                                                        }`}
                                                        onClick={() => {
                                                            const tmp = [...commonLayer];
                                                            tmp.forEach((ele) => {
                                                                if (value == ele.value) {
                                                                    ele.on = !ele.on;
                                                                }
                                                            });

                                                            setCommonLayer(tmp);
                                                        }}
                                                    >
                                                        {name}
                                                    </li>
                                                );
                                            })}
                                    </ul>
                                    <h3>불법주정차 단속 현황</h3>
                                    <ul className="type2">
                                        {commonLayer
                                            .filter(({ group }) => group == "2")
                                            .map(({ value, name }) => {
                                                return (
                                                    <li
                                                        key={value}
                                                        className={`${
                                                            commonLayer.find((ele) => ele.value == value && ele.on)
                                                                ? "on"
                                                                : ""
                                                        }`}
                                                        onClick={() => {
                                                            const tmp = [...commonLayer];
                                                            tmp.forEach((ele) => {
                                                                if (value == ele.value) {
                                                                    ele.on = !ele.on;
                                                                }
                                                            });

                                                            setCommonLayer(tmp);
                                                        }}
                                                    >
                                                        {name}
                                                    </li>
                                                );
                                            })}
                                    </ul>
                                    <h3>불법주정차 금지 구역</h3>
                                    <ul className="type3">
                                        {commonLayer
                                            .filter(({ group }) => group == "3")
                                            .map(({ value, name }) => {
                                                return (
                                                    <li
                                                        key={value}
                                                        className={`${
                                                            commonLayer.find((ele) => ele.value == value && ele.on)
                                                                ? "on"
                                                                : ""
                                                        }`}
                                                        onClick={() => {
                                                            const tmp = [...commonLayer];
                                                            tmp.forEach((ele) => {
                                                                if (value == ele.value) {
                                                                    ele.on = !ele.on;
                                                                }
                                                            });

                                                            setCommonLayer(tmp);
                                                        }}
                                                    >
                                                        {name}
                                                    </li>
                                                );
                                            })}
                                    </ul>
                                </ul>
                            </div>
                        </li>
                        <ul className="other">
                            <li className="history_type">
                                실태조사
                                {/*<AntSelect*/}
                                {/*    style={{ width: "100%", height: "100%" }}*/}
                                {/*    value={selectHistory}*/}
                                {/*    options={[*/}
                                {/*        { label: "실태조사", value: "layer" },*/}
                                {/*        { label: "관리카드", value: "card" },*/}
                                {/*    ]}*/}
                                {/*    onChange={(value) => {*/}
                                {/*        setSelectHistory(value);*/}
                                {/*        setSelectHistoryYear("");*/}
                                {/*    }}*/}
                                {/*/>*/}
                            </li>
                            <li className="history_year">
                                <AntSelect
                                    style={{ width: "100%", height: "100%" }}
                                    value={selectHistoryYear}
                                    options={historyYearOptions}
                                    onChange={(value) => {
                                        setSelectHistoryYear(value);
                                    }}
                                />
                            </li>
                            <li>
                                <span className="solid_line"></span>
                            </li>
                            <li
                                className="map_division"
                                onClick={() => {
                                    setSubMapData((current) =>
                                        current ? null : { blank_tab: selectHistory, blank_year: selectHistoryYear }
                                    );
                                }}
                            >
                                <img src={multi_ico} />
                            </li>
                            <li>
                                <span className="solid_line"></span>
                            </li>
                            <li
                                className="blank_btn"
                                onClick={() => {
                                    const newWindow = window.open(
                                        `/blank_gis/${selectHistory}/${selectHistoryYear}`,
                                        "_blank",
                                        `width=${window.width},height=${window.height}`
                                    );

                                    if (newWindow) {
                                        newWindow.focus();
                                    }
                                }}
                            >
                                <img src={blank_ico} />
                            </li>
                        </ul>
                    </ul>

                    <button
                        className="hide_btn"
                        onClick={() => {
                            setSideOn(!sideOn);
                        }}
                    >
                        {sideOn ? "◀" : "▶"}
                    </button>
                </div>
            </div>
        </div>
    );
}

export default ContentCurrentMenu;
