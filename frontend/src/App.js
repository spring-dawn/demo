import React, { useEffect, useState } from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import * as Sentry from "@sentry/react";
import { BrowserTracing } from "@sentry/tracing";
import { useRecoilState } from "recoil";

import "antd/dist/antd.min.css";
import "react-grid-layout/css/styles.css";
import "react-resizable/css/styles.css";
import "ag-grid-community/styles/ag-grid.css";
import "ag-grid-community/styles/ag-theme-alpine.css";
import "slick-carousel/slick/slick.css";
import "slick-carousel/slick/slick-theme.css";
import "react-datepicker/dist/react-datepicker.css";

import "./assets/scss/grid.scss";
import "./assets/scss/common.scss";

import "./assets/scss/grid.scss";
import "./assets/scss/main.scss";
import "./assets/scss/modal.scss";
import "./assets/scss/login.scss";
import "./assets/scss/signup.scss";

import "ol/ol.css";
import "./assets/scss/gis.scss";
import "./assets/css/monthlyReport.css";

import "./assets/scss/analy.scss";
import "./assets/scss/prfmnc.scss";

import "./assets/scss/myPage.scss";

import "./assets/scss/research.scss";

import loadingGif from "./assets/img/common/loading.svg";

import Login from "./components/login/Login.js";
import Signup from "./components/signup/Signup";
import AuthRoute from "./components/common/AuthRoute";
import NotFound from "./components/common/error/NotFound";
import { windowSizeState, loadingState } from "./Context";
import GisPage from "./components/gis/GisPage";

// Sentry.init({
//     dsn: "http://a5a2a907c9d64867952577eed3e08f90@172.30.1.244:9000/3",
//     integrations: [new BrowserTracing()],
//     tracesSampleRate: 1.0,
// });

function App() {
    const [page, setPage] = useState([]);
    const [windowSize, setWindowSize] = useRecoilState(windowSizeState);
    const [loading, setLoading] = useRecoilState(loadingState);

    // 마운트될 때 이벤트 리스너를 등록 & 언마운트될 때 이벤트 리스너를 제거
    useEffect(() => {
        const handleResize = () => {
            setWindowSize({
                width: window.innerWidth,
                height: window.innerHeight,
            });
        };

        window.addEventListener("resize", handleResize);

        return () => {
            window.removeEventListener("resize", handleResize);
        };
    }, [setWindowSize]);

    useEffect(() => {
        fetch("/api/menus")
            .then((res) => {
                return res.json();
            })
            .then((m) => {
                let arr = [];
                const getRouteArr = (menus, arr) => {
                    menus.forEach((menu) => {
                        arr.push(menu);

                        if (menu.children.length !== 0) {
                            getRouteArr(menu.children, arr);
                        }
                    });
                };
                getRouteArr(m, arr);
                // console.log("라우팅 목록:", arr);
                setPage(arr);
            });
    }, []);

    return (
        <div className="App">
            {loading && (
                <>
                    <div className="loading_wrap"></div>
                    <div className="loading">
                        <img src={loadingGif} />
                        <div>데이터를 불러오고 있습니다.</div>
                    </div>
                </>
            )}
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<Navigate to="/main" replace />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/signup" element={<Signup />} />

                    {page.map((menu) => {
                        // 단일 페이지가 아닌 경유지 url은 라우팅 제외, tab 을 보유한 메뉴는 경유지 아닌 실제 페이지이므로 라우팅.
                        if (menu.children.length === 0 || menu.children[0].tabYn == "Y") {
                            return (
                                <Route
                                    key={menu.url}
                                    path={menu.url}
                                    element={<AuthRoute to={menu.url} name={menu.name} />}
                                />
                            );
                        } else {
                            // 하위 메뉴를 가지고 있으나 전부 비활성화인 경우 단일 페이지로 판단, 라우팅.
                            let cnt = 0;
                            menu.children.forEach((child) => {
                                if (child.useYn == "Y") cnt += 1;
                            });
                            if (cnt == 0) {
                                return (
                                    <Route
                                        key={menu.url}
                                        path={menu.url}
                                        element={<AuthRoute to={menu.url} name={menu.name} />}
                                    />
                                );
                            }
                        }
                    })}

                    {/* 새창 GIS 라우터 */}
                    <Route path="/blank_gis/:blank_tab/:blank_year" element={<GisPage />} />

                    <Route path="*" element={<NotFound errStatus="404" errMsg="페이지를 찾을 수 없습니다." />} />
                </Routes>
            </BrowserRouter>
        </div>
    );
}

export default App;
