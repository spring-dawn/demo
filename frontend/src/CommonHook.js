import { useEffect, useState, useMemo, useRef } from "react";
import { useRecoilState, atom } from "recoil";
import axios from "axios";
import { FunnelChart } from "recharts";

const cachedCodeState = atom({
    key: "cachedCodeState",
    default: {},
});

// 코드 트리 hook
export function useCodeTree({ parentNm, deps }) {
    const [tree, set] = useState([]);
    const [cachedResult, setCachedResult] = useRecoilState(cachedCodeState);

    useEffect(() => {
        if (cachedResult[parentNm]) {
            set(cachedResult[parentNm]);
        } else {
            axios
                .get(`/api/system/code/codes/search/${parentNm}`)
                .then((res) => {
                    const json = res.data;

                    const sortedChildren = json.children && json.children.sort((a, b) => a.name - b.name);
                    set(sortedChildren);
                    setCachedResult({ ...cachedResult, [parentNm]: sortedChildren });
                })
                .catch((err) => {});
        }
    }, [parentNm, cachedResult, setCachedResult]);

    return { tree, set };
}

/**
 * true: 편집권 있음(등록 가능), false: 없음
 * @param {String} url 현재 접속 경로
 */
export function checkEditRight(url) {
    const [editRight, setEditRight] = useState(false);
    const cachedResult = useRef({});

    const userInfo = JSON.parse(localStorage.getItem("user"));

    useEffect(() => {
        if (cachedResult.current[url] !== undefined) {
            setEditRight(cachedResult.current[url]);
        } else {
            axios("/api/system/user/check/edit/" + userInfo.roleId + "?url=" + url)
                .then((res) => {
                    setEditRight(res.data);
                    cachedResult.current[url] = res.data;
                })
                .catch((err) => console.log(err.response.data.message));
        }
    }, [url]);

    return editRight;
}

/**
 * 수정, 삭제 권한 판단. true: 권한 있음, false: 없음.
 * @param {boolean} hasEdit 편집권 유무
 */
export function checkUpdateAndDeleteRight(hasEdit) {
    const userInfo = JSON.parse(localStorage.getItem("user"));
    return hasEdit && userInfo.roleEncodedNm.includes("관리자");
}

/**
 * 시 관리자(최고 관리자) 여부 판단
 * @returns true: 시 관리자, false: 그 외
 */
export function isFirstAdmin() {
    const userInfo = JSON.parse(localStorage.getItem("user"));
    return userInfo.roleEncodedNm.includes("시 관리자");
}
