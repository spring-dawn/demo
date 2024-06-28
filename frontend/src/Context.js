import { atom, selector } from "recoil";

// 윈도우 너비와 높이를 관리할 Recoil
export const windowSizeState = atom({
    key: "windowSizeState",
    default: { width: window.innerWidth, height: window.innerHeight },
});

// 윈도우 크기를 계산하는 Recoil 선택기
export const windowSizeSelector = selector({
    key: "windowSizeSelector",
    get: ({ get }) => {
        const windowSize = get(windowSizeState);
        return windowSize;
    },
});

// 로딩 상태를 관리할 Recoil
export const loadingState = atom({
    key: "loadingState",
    default: false,
});

// 서브맵 유무를 관리할 Recoil
export const subMapState = atom({
    key: "subMapState",
    default: false,
});

// 공통 Search 컴퍼넌트 현재 검색 상태
export const SearchState = atom({
    key: "searchState",
    default: {},
});
