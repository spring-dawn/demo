import React, { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { Menu } from "antd";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { library } from "@fortawesome/fontawesome-svg-core";
import {
    faAngleRight,
    faAngleDown,
    faAngleLeft,
    faHouse,
    faDatabase,
    faMapLocationDot,
    faChartPie,
    faGear,
} from "@fortawesome/free-solid-svg-icons";

function Sidebar(props) {
    const { menu, isShow, path, title, toggle, on } = props;
    const navigate = useNavigate();
    const [selectKeys, setSelectKeys] = useState([]);
    const [openKeys, setOpenKeys] = useState([]);

    // icon
    library.add(faHouse, faDatabase, faMapLocationDot, faChartPie, faGear);

    const [target, setTarget] = useState({});
    const onShowChild = (e) => {
        e.target.classList.toggle("on");

        let obj = {
            id: e.target.id,
            show: e.target.className === "on" ? true : false,
        };
        setTarget(obj);
    };

    const findCurrentMenu = (menuData, currentPath) => {
        for (const item of menuData) {
            // 같으면 해당 메뉴 반환
            if (item.url === currentPath) {
                return item;
            }

            // 다르면 자식 메뉴에서 메뉴 찾기
            if (item.children.length > 0) {
                const subMenu = findCurrentMenu(item.children, currentPath);
                if (subMenu) {
                    return subMenu;
                }
            }
        }
        return null;
    };

    function convertToMenu(data) {
        return data.map((item) => {
            const { id, name, url, children } = item;

            // 하위 메뉴가 탭인 경우 사이드바에 출력하지 않는다
            if (children.length > 0 && children[0].tabYn == "N") {
                return (
                    <Menu.SubMenu key={url} title={name}>
                        {convertToMenu(children)}
                    </Menu.SubMenu>
                );
            } else {
                return <Menu.Item key={url}>{name}</Menu.Item>;
            }
        });
    }

    const onOpenChange = (keys) => {
        setOpenKeys(keys);
    };

    useEffect(() => {
        const currentMenu = findCurrentMenu(menu, path);

        // 상위 메뉴 순차적으로 전부 선택 표시
        if (currentMenu) {
            let pathArray = currentMenu.url.split("/").filter(function (el) {
                return el !== "";
            });

            let resultArray = [];
            let currentPath = "";

            pathArray.forEach(function (key) {
                currentPath += "/" + key;
                resultArray.push(currentPath);
            });

            setSelectKeys(resultArray);
        }
    }, [menu, path]);

    useEffect(() => {
        const defaultOpenKeys = [];

        const openKeysTree = (arr, data) => {
            data.forEach((menu) => {
                defaultOpenKeys.push(menu.url);

                if (menu.children.length > 0) {
                    openKeysTree(arr, menu.children);
                }
            });
        };

        openKeysTree(defaultOpenKeys, menu);

        setOpenKeys(defaultOpenKeys);
    }, [menu]);

    return (
        <>
            <div id="sidebarWrap" className={isShow ? "show" : "hide"}>
                <aside id="leftsidebar">
                    <div className="profile">
                        <h2>{title}</h2>
                    </div>

                    <div className="menu">
                        <ul className="list">
                            {menu.length !== 0 && (
                                <Menu
                                    mode="inline"
                                    selectedKeys={selectKeys}
                                    onOpenChange={onOpenChange}
                                    openKeys={openKeys}
                                    onClick={(e) => {
                                        navigate(e.key);
                                    }}
                                >
                                    {convertToMenu(menu)}
                                </Menu>
                            )}
                        </ul>
                    </div>
                </aside>
            </div>

            {/* <a className="sidebarTgl" onClick={onToggleSidebar}>
                {on ? <FontAwesomeIcon icon={faAngleLeft} /> : <FontAwesomeIcon icon={faAngleRight} />}
            </a> */}
        </>
    );
}

export default Sidebar;
