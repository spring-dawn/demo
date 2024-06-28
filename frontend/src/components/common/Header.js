import React, { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";

import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faBars, faUser } from "@fortawesome/free-solid-svg-icons";
import { useRecoilValue } from "recoil";
import { windowSizeSelector } from "../../Context";
import { Drawer, Space, Button } from "antd";

function Header(props) {
    const { menu, toggle, on } = props;
    const windowSize = useRecoilValue(windowSizeSelector);
    const [open, setOpen] = useState(false);

    const location = useLocation();

    // sidebar toggle
    const clickBar = () => (on ? toggle(false) : toggle(true));

    const [isAuthorized, setIsAuthorized] = useState();
    useEffect(() => {
        localStorage.getItem("isAuthorized") === "Y" ? setIsAuthorized(true) : setIsAuthorized(false);
    }, [isAuthorized]);

    const onLogout = () => {
        fetch("/api/signout").catch((error) => {
            console.log(error);
        });
        localStorage.clear();
        localStorage.setItem("isAuthorized", "N");
        window.location = "/login";
    };

    return (
        <>
            <nav className="navbar" id="gnb">
                <div className="container flxCnt" style={{ height: "100%" }}>
                    <h1 className="navbar-header" id="logo">
                        <a href="/">
                            <img src={require("../../assets/img/common/ul_logo2.png")} />
                            스마트주차행정시스템
                        </a>
                    </h1>

                    {windowSize.width > 1400 ? (
                        <>
                            <div className="gnb">
                                <ul className="menu">
                                    {menu.map((menu) => {
                                        let target = menu.children.length !== 0 ? menu.children[0] : menu;
                                        target = target.children.length !== 0 ? target.children[0] : target;

                                        return (
                                            <li
                                                className={
                                                    location.pathname.split("/")[1] == menu.url.split("/")[1]
                                                        ? "on"
                                                        : ""
                                                }
                                                key={target.id}
                                            >
                                                <Link to={target.url} id={target.id}>
                                                    {menu.name}
                                                </Link>
                                            </li>
                                        );
                                    })}
                                </ul>
                            </div>

                            <div className="navbar_quick">
                                {/* <a className="btn_menu" id="navbarShowHide" onClick={clickBar}>
                                  <FontAwesomeIcon icon={faBars} />
                                </a> */}

                                <ul>
                                    {!isAuthorized ? (
                                        <>
                                            <li>
                                                <Link to="/login">로그인</Link>
                                            </li>
                                            <li>
                                                <Link to="/signup">회원가입</Link>
                                            </li>
                                        </>
                                    ) : (
                                        <>
                                            {/* <li>
                                        <a>
                                            <FontAwesomeIcon icon={faUser} />
                                            <ul>
                                                <li></li>
                                            </ul>
                                        </a>
                                    </li> */}
                                            <li>
                                                <a onClick={onLogout}>로그아웃</a>
                                            </li>
                                        </>
                                    )}
                                </ul>

                                {/* <a className="btn_menu" id="navbarShowHide" onClick={clickBar}>
                            <FontAwesomeIcon icon={faBars} />
                        </a> */}
                            </div>
                        </>
                    ) : (
                        <div className="gnb_mobile">
                            <FontAwesomeIcon icon={faBars} onClick={() => setOpen(!open)} />

                            <Drawer
                                title=""
                                placement={"right"}
                                width={350}
                                onClose={() => setOpen(false)}
                                open={open}
                                extra={
                                    <Space>
                                        <Button onClick={onLogout}>로그아웃</Button>
                                    </Space>
                                }
                            >
                                {menu.map((menu) => {
                                    let target = menu.children.length !== 0 ? menu.children[0] : menu;
                                    target = target.children.length !== 0 ? target.children[0] : target;

                                    return (
                                        <div
                                            className={
                                                location.pathname.split("/")[1] == menu.url.split("/")[1]
                                                    ? "drawer_menu on"
                                                    : "drawer_menu"
                                            }
                                            key={target.id}
                                        >
                                            <Link to={target.url} id={target.id}>
                                                {menu.name}
                                            </Link>
                                        </div>
                                    );
                                })}
                            </Drawer>
                        </div>
                    )}
                </div>
            </nav>
        </>
    );
}

export default Header;
