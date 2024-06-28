import { dateTimestampInSeconds } from "@sentry/utils";
import React, { useEffect, useState } from "react";
import { Navigate, useLocation } from "react-router-dom";

import Layout from "./Layout";
import NotFound from "./error/NotFound";
import { blockParseCodeToName } from "../../CommonFunction";

function AuthRoute(props) {
    const { to, name } = props;
    const location = useLocation();

    // 3. 현재 url 에 대한 권한 확인 요청을 한다
    // 3-1. 권한이 필요한데 로그인을 하지 않았다 => 로그인 페이지 리다이렉트
    // 3-2. 권한이 필요한데 로그인을 했고 권한이 부족하다 => 권한없다 페이지 만들기
    // 3-3. 권한이 필요한데 고르인을 했고 권한이 충분하다 => 그냥 띄우기

    // 1. 로그인 체크(sessionStorage)
    const [isLogin, setLoginStatus] = useState(localStorage.getItem("isAuthorized") === "Y" ? true : false);

    // 2. 로그인 OK, 권한 없음
    // 2-1. 메뉴 권한 체크(마이메뉴에 있는지 없는지로 체크)
    const [mymenu, setMymenu] = useState([]);
    const [allow, setAllow] = useState(true);
    const [arr, setArr] = useState([]);

    useEffect(() => {
        fetch("/api/mymenu")
            .then((res) => {
                return res.json();
            })
            .then((data) => {
                let tmpArr = [];

                // 메뉴
                setMymenu(data);

                const createTree = (arr, data) => {
                    data.forEach((item) => {
                        arr.push(item.url);

                        if (item.children.length !== 0) {
                            createTree(arr, item.children);
                        }
                    });
                };

                createTree(tmpArr, data);

                // url
                if (tmpArr.length !== 0) {
                    if (tmpArr.indexOf(to) !== -1) {
                        setAllow(true);
                    } else {
                        setAllow(false);
                    }
                }
            })
            .catch((err) => {
                if (localStorage.getItem("isAuthorized") === "Y") {
                    localStorage.setItem("isAuthorized", "N");
                    setLoginStatus(false);
                    setAllow(false);

                    alert("세션이 만료되어 로그아웃합니다.");
                }
            });
    }, [isLogin]);

    // 2-2. 세션 끊김 처리
    useEffect(() => {
        const intervalId = setInterval(() => {
            fetch("/api/system/user/check/session")
                .then((response) => response.json())
                .then((apiData) => {
                    if (!apiData) {
                        localStorage.clear();
                        localStorage.setItem("isAuthorized", "N");
                        setLoginStatus(false);
                        setAllow(false);

                        alert("세션이 만료되어 로그아웃합니다.");
                    }
                });
        }, [120000]);

        return () => {
            clearInterval(intervalId);
        };
    }, []);

    return (
        <div>
            {isLogin ? (
                !allow ? (
                    <NotFound errStatus="401" errMsg="권한이 없어 접근할 수 없는 페이지입니다." />
                ) : (
                    <Layout menu={mymenu} tit={name} />
                )
            ) : (
                <Navigate to="/login" replace />
            )}
        </div>
    );
}

export default AuthRoute;
