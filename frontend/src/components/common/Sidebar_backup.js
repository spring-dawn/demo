import React, { useEffect, useState } from "react";
import { Link } from "react-router-dom";

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
    const { menu, isShow, toggle, on } = props;

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

    const onToggleSidebar = (e) => {
        console.log(on);
        on ? toggle(false) : toggle(true);
    };

    return (
        <>
            <div id="sidebarWrap" className={isShow ? "show" : "hide"}>
                <aside id="leftsidebar">
                    <div className="menu">
                        <ul className="list">
                            {menu.map((menu) => {
                                return (
                                    <li className="flxCnt" onClick={onShowChild} key={menu.id} id={menu.id}>
                                        <Link to={menu.children.length !== 0 ? "" : menu.url} id={menu.id}>
                                            <FontAwesomeIcon icon={["fa", menu.ico]} />
                                            {menu.name}
                                            <span style={{ float: "right" }}>
                                                {menu.children.length !== 0 ? (
                                                    target.id == menu.id && target.show ? (
                                                        <FontAwesomeIcon icon={faAngleDown} />
                                                    ) : (
                                                        <FontAwesomeIcon icon={faAngleRight} />
                                                    )
                                                ) : null}
                                            </span>
                                        </Link>
                                        <ul className="ml-menu">
                                            {menu.children.length !== 0 &&
                                                menu.children.map((child) => {
                                                    return (
                                                        <li key={child.name}>
                                                            <Link to={child.url}>{child.name}</Link>
                                                        </li>
                                                    );
                                                })}
                                        </ul>
                                    </li>
                                );
                            })}
                        </ul>
                    </div>
                </aside>
            </div>

            <a className="sidebarTgl" onClick={onToggleSidebar}>
                {on ? <FontAwesomeIcon icon={faAngleLeft} /> : <FontAwesomeIcon icon={faAngleRight} />}
            </a>
        </>
    );
}

export default Sidebar;
