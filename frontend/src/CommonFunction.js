// 구군코드 => 구군명칭
import { useEffect } from "react";

export function gugunParseCodeToName(code) {
    if (code == "all" || code == "31000") return "울산광역시";
    if (code == "31110") return "중구";
    if (code == "31140") return "남구";
    if (code == "31170") return "동구";
    if (code == "31200") return "북구";
    if (code == "31710") return "울주군";
}

// 실태조사 관리카드 그룹 sum
export function cardDataGroupSum(dataList) {
    // PK4 = dataStore.pdRDIn / dataStore.pfRDSum;
    // PK7 = (dataStore.pdRDOut + dataStore.pdRDIll) / dataStore.pdTotal;
    // 주차장
    // "pfRDResi" = 노상 거주자: 노상_거주자 레이어 [주차면수 전체] 합계
    // "pfRDEtc" = 노상 그외: 노상_공영 레이어 [주차면수 전체] 합계
    // "pfRDSum" = 노상 소계: 노상거주자 + 노상 그외
    // "pfOutPub" = 노외 공영: 노외 레이어 [공영/민영 = 공영] [주차면수 전체] 합계
    // "pfOutPri" = 노외 민영: 노외 레이어 [공영/민영 = 민영] [주차면수 전체] 합계
    // "pfOutSum" = 노외 소계: 노외 공영 + 노외 민영
    // "pfSubResi" = 부설 주거: 부설 레이어 [주거/비주거 = 주거] [주차면수 전체] 합계
    // "pfSubNonRegi" = 부설 비주거: 부설 레이어 [주거/비주거 = 비주거] [주차면수 전체] 합계
    // "pfSubSum" = 부설 소계: 부설 주거 + 부설 비주거
    // "pfTotal" = 합계: 노상 소계 + 노외 소계 + 부설 소계
    // 수요
    // "pdRDIn" = 노상 구획내: 적법 구획내 레이어 feature size
    // "pdRDOut" = 노상 구획외: 적법 구획외 레이어 feature size
    // "pdRDIll" = 노상 불법: 불법 레이어 feature size
    // "pdRDSum" = 노상 소계: 노상 구획내 + 노상 구획외 + 노상 불법
    // "pdOutPub = 노외 공영:
    // "pdOutPri = 노외 민영:
    // "pdOutSum" = 노외 소계: 노외 수요 레이어 feature size
    // "pdSubResi = 부설 주거:
    // "pdSubNonRegi = 부설 비주거:
    // "pdSubSum" = 부설 소계: 부설 수요 구획외 레이어 feature size
    // "pdTotal" = 합계: 노상 소계 + 노외 소계 + 부설 소계
    // PK1 = 주차장 확보율
    // PK2 = 주차장 과부족(대)
    // PK3 = 주차장 이용률 전체
    // PK4 = 주차장 이용률 노상
    // PK5 = 주차장 이용률 노외
    // PK6 = 주차장 이용률 부설
    // PK7 = 불법 주차율 전체수요 대비
    // PK8 = 불법 주차율 노상수요 대비
    // PK9 = 유휴 부설주차규모 전체

    const colList = [
        "pop",
        "households",
        "vehicleCnt",
        "emptyLands",
        "emptyArea",
        "pfRDResi",
        "pfOutPub",
        "pfSubResi",
        "pfRDEtc",
        "pfOutPri",
        "pfSubNonRegi",
        "pfRDSum",
        "pfOutSum",
        "pfSubSum",
        "pfTotal",
        "pdRDIn",
        "pdOutPub",
        "pdSubResi",
        "pdRDOut",
        "pdRDIll",
        "pdOutPri",
        "pdSubNonRegi",
        "pdRDSum",
        "pdOutSum",
        "pdSubSum",
        "pdTotal",
    ];

    const dataStore = {};

    colList.forEach((key) => {
        dataStore[key] = dataList.reduce((accumulator, item) => {
            let val = !item[key] || item[key] == "-" ? 0 : item[key];
            return accumulator + parseInt(val);
        }, 0);
    });

    let PK1 = 0,
        PK2 = 0,
        PK3 = 0,
        PK4 = 0,
        PK5 = 0,
        PK6 = 0,
        PK7 = 0,
        PK8 = 0,
        PK9 = 0,
        PK10 = 0,
        PK11 = 0;

    PK1 = dataStore.pfTotal / dataStore.pdTotal;
    PK2 = dataStore.pfTotal - dataStore.pdTotal;
    PK3 = (dataStore.pdRDIn + dataStore.pdOutSum + dataStore.pdSubSum) / dataStore.pfTotal;
    PK4 = dataStore.pdRDIn / dataStore.pfRDSum;
    PK5 = dataStore.pdOutSum / dataStore.pfOutSum;
    PK6 = dataStore.pdSubSum / dataStore.pfSubSum;
    PK7 = (dataStore.pdRDOut + dataStore.pdRDIll) / dataStore.pdTotal;
    PK8 = dataStore.pdRDIll / dataStore.pdRDSum;
    PK9 = dataStore.pfSubSum - dataStore.pdSubSum;
    PK10 = dataStore.pfSubResi - dataStore.pdSubResi;
    PK11 = dataStore.pfSubNonRegi - dataStore.pdSubNonRegi;

    PK1 = isFinite(PK1) ? PK1 : 0;
    PK2 = isFinite(PK2) ? PK2 : 0;
    PK3 = isFinite(PK3) ? PK3 : 0;
    PK4 = isFinite(PK4) ? PK4 : 0;
    PK5 = isFinite(PK5) ? PK5 : 0;
    PK6 = isFinite(PK6) ? PK6 : 0;
    PK7 = isFinite(PK7) ? PK7 : 0;
    PK8 = isFinite(PK8) ? PK8 : 0;
    PK9 = isFinite(PK9) ? PK9 : 0;
    PK10 = isFinite(PK10) ? PK10 : 0;
    PK11 = isFinite(PK11) ? PK11 : 0;

    PK1 = parseFloat((PK1 * 100).toFixed(1));
    PK3 = parseFloat((PK3 * 100).toFixed(1));
    PK4 = parseFloat((PK4 * 100).toFixed(1));
    PK5 = parseFloat((PK5 * 100).toFixed(1));
    PK6 = parseFloat((PK6 * 100).toFixed(1));
    PK7 = parseFloat((PK7 * 100).toFixed(1));
    PK8 = parseFloat((PK8 * 100).toFixed(1));

    return {
        ...dataStore,
        PK1,
        PK2,
        PK3,
        PK4,
        PK5,
        PK6,
        PK7,
        PK8,
        PK9,
        PK10,
        PK11,
    };
}

// 구군코드 => 구군명칭
export function blockParseCodeToName(block) {
    block = block.replaceAll("SJ1", "신정1동");
    block = block.replaceAll("SJ2", "신정2동");
    block = block.replaceAll("SJ3", "신정3동");
    block = block.replaceAll("SJ4", "신정4동");
    block = block.replaceAll("SJ5", "신정5동");
    block = block.replaceAll("DD", "달동");
    block = block.replaceAll("SS", "삼산동");
    block = block.replaceAll("SH", "삼호동");
    block = block.replaceAll("MG", "무거동");
    block = block.replaceAll("OD", "옥동");
    block = block.replaceAll("YJ", "야음장생포동");
    block = block.replaceAll("DH", "대현동");
    block = block.replaceAll("SU", "수암동");
    block = block.replaceAll("SA", "선암동");

    return block;
}

export function getUserRole() {
    const userJSON = localStorage.getItem("user");

    if (userJSON) {
        const userInfo = JSON.parse(userJSON);
        const { roleNm } = userInfo;

        return roleNm;
    }
}
