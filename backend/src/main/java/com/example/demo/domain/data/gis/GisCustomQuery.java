package com.example.demo.domain.data.gis;

import com.example.demo.dto.GeoJSONResponse;
import com.example.demo.dto.system.CodeDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class GisCustomQuery {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    // 안내표시 설치여부
    private String caseHasInfoSign(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .info_sign_yn = '0' THEN '미설치' " +
                " WHEN " + as + " .info_sign_yn = '1' THEN '설치' " +
                " ELSE " + as + " .info_sign_yn " +
                " END AS \"안내표지 설치여부\" " + nextComma;
    }

    // 미끄럼방지 설치여부
    private String caseHasNonSlip(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .slip_prvnt_yn = '0' THEN '미설치' " +
                " WHEN " + as + " .slip_prvnt_yn = '1' THEN '설치' " +
                " ELSE " + as + " .slip_prvnt_yn " +
                " END AS \"미끄럼 방지시설 설치여부\" " + nextComma;
    }

    // 미끄럼방지 설치여부
    private String caseIsPay(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .pay_yn = '1' THEN '유료' " +
                " WHEN " + as + " .pay_yn = '2' THEN '무료' " +
                " ELSE " + as + " .pay_yn " +
                " END AS \"유/무료\" " + nextComma;
    }

    // 주차장 구분
    private String caseTypeP(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .pklt_type = '1' THEN '노상' " +
                " WHEN " + as + " .pklt_type = '2' THEN '노외' " +
                " WHEN " + as + " .pklt_type = '3' THEN '부설' " +
                " ELSE " + as + " .pklt_type " +
                " END AS \"주차장 구분\" " + nextComma;
    }

    // 경사여부
    private String caseIsSlope(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .slp_yn = '0' THEN '해당없음' " +
                " WHEN " + as + " .slp_yn = '1' THEN '경사' " +
                " ELSE " + as + " .pklt_type " +
                " END AS \"경사여부\" " + nextComma;
    }

    // 적불법여부(야간)
    private String caseIsLegalN(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .nght_lgl_yn = '1' THEN '적법(구획 내)' " +
                " WHEN " + as + " .nght_lgl_yn = '2' THEN '적법(구획 외)' " +
                " WHEN " + as + " .nght_lgl_yn = '3' THEN '불법' " +
                " ELSE " + as + " .nght_lgl_yn " +
                " END AS \"적/불법여부(야간)\" " + nextComma;
    }

    // 적불법여부(주간)
    private String caseIsLegalD(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .dy_lgl_yn = '1' THEN '적법(구획 내)' " +
                " WHEN " + as + " .dy_lgl_yn = '2' THEN '적법(구획 외)' " +
                " WHEN " + as + " .dy_lgl_yn = '3' THEN '불법' " +
                " ELSE " + as + " .dy_lgl_yn " +
                " END AS \"적/불법여부(주간)\" " + nextComma;
    }

    // 적불법여부
    private String caseIsLegal(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .lgl_yn = '1' THEN '적법(구획 내)' " +
                " WHEN " + as + " .lgl_yn = '2' THEN '적법(구획 외)' " +
                " WHEN " + as + " .lgl_yn = '3' THEN '불법' " +
                " ELSE " + as + " .lgl_yn " +
                " END AS \"적/불법여부\" " + nextComma;
    }

    // 방법설비-저장장치
    private String caseBackup(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .scrty_fclt_strg_yn = '1' THEN '적정' " +
                " WHEN " + as + " .scrty_fclt_strg_yn = '2' THEN '부적정' " +
                " ELSE " + as + " .scrty_fclt_strg_yn " +
                " END AS \"방범설비-저장장치\" " + nextComma;
    }

    // 방법설비-CCTV
    private String caseCctv(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .fclt_cctv_yn = '1' THEN '적정' " +
                " WHEN " + as + " .fclt_cctv_yn = '2' THEN '부적정' " +
                " ELSE " + as + " .fclt_cctv_yn " +
                " END AS \"방범설비-cctv\" " + nextComma;
    }

    // 방법설비-모니터
    private String caseMonitor(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .fclt_mntr_yn = '1' THEN '적정' " +
                " WHEN " + as + " .fclt_mntr_yn = '2' THEN '부적정' " +
                " ELSE " + as + " .fclt_mntr_yn " +
                " END AS \"방범설비-모니터\" " + nextComma;
    }

    // 공영/민영 구분
    private String caseIsPub(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .pblmn_yn = '1' THEN '공영' " +
                " WHEN " + as + " .pblmn_yn = '2' THEN '민영' " +
                " ELSE " + as + " .pblmn_yn " +
                " END AS \"공영/민영\" " + nextComma;
    }

    // 조사시간대
    private String caseRschTime(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .dy_nght = '1' THEN '주간' " +
                " WHEN " + as + " .dy_nght = '2' THEN '야간' " +
                " ELSE " + as + " .dy_nght " +
                " END AS \"조사시간대\" " + nextComma;
    }

    // 주거/비주거 구분
    private String caseIsResi(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .resi_yn = '1' THEN '주거' " +
                " WHEN " + as + " .resi_yn = '2' THEN '비주거' " +
                " ELSE " + as + " .resi_yn " +
                " END AS \"주거/비주거\" " + nextComma;
    }

    // 공통주택
    private String caseIsApt(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .aptcpx_yn = '0' THEN '해당없음' " +
                " WHEN " + as + " .aptcpx_yn = '1' THEN '해당' " +
                " ELSE " + as + " .aptcpx_yn " +
                " END AS \"공동주택\" " + nextComma;
    }

    // 차종
    private String caseTypeC(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .vhcl_type = '1' THEN '승용,승합,소형화물' " +
                " WHEN " + as + " .vhcl_type = '2' THEN '대형화물,특수' " +
                " ELSE " + as + " .vhcl_type " +
                " END AS \"차종\" " + nextComma;
    }

    // 삭제유무
    private String caseRmYn(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .del_yn = '0' THEN '삭제' " +
                " WHEN " + as + " .del_yn = '1' THEN '존재' " +
                " ELSE " + as + " .del_yn " +
                " END AS \"삭제유무\" " + nextComma;
    }

    // 시설형태
    private String caseTypeFacility(String as, String nextComma) {
        return  " CASE " +
                " WHEN " + as + " .fclt_ptn  = '1' THEN '평행' " +
                " WHEN " + as + " .fclt_ptn  = '2' THEN '직각' " +
                " WHEN " + as + " .fclt_ptn  = '3' THEN '대향' " +
                " WHEN " + as + " .fclt_ptn  = '4' THEN '개구리' " +
                " WHEN " + as + " .fclt_ptn  = '5' THEN '지평식(일반자주식)' " +
                " WHEN " + as + " .fclt_ptn  = '6' THEN '지하식' " +
                " WHEN " + as + " .fclt_ptn  = '7' THEN '건축물식(주차타워)' " +
                " WHEN " + as + " .fclt_ptn  = '8' THEN '기계식' " +
                " WHEN " + as + " .fclt_ptn  = '9' THEN '그외' " +
                " ELSE " + as + " .fclt_ptn  " +
                " END AS \"시설형태\" " + nextComma;
    }

    // 주차수요 주/야간 대수 쿼리
    private String getParkingCountQuery(String t3JoinCol, String addWhere, String nextComma) {
        return  "       (SELECT t3.cntom_twowh\n" +
                "           FROM public.\"dim_tmplt_dmnd_etc_t\" AS t3\n" +
                "           WHERE t2.sn = " + t3JoinCol + " AND t3.dy_nght = '1'\n" +
                addWhere +
                "           LIMIT 1\n" +
                "       ) AS \"이륜차 대수(주간)\", " +
                "       (SELECT t3.cntom_twowh\n" +
                "           FROM public.\"dim_tmplt_dmnd_etc_t\" AS t3\n" +
                "           WHERE t2.sn = " + t3JoinCol + " AND t3.dy_nght = '2'\n" +
                addWhere +
                "           LIMIT 1\n" +
                "       ) AS \"이륜차 대수(야간)\", " +
                "       (SELECT t3.cntom_etc\n" +
                "           FROM public.\"dim_tmplt_dmnd_etc_t\" AS t3\n" +
                "           WHERE t2.sn = " + t3JoinCol + " AND t3.dy_nght = '1'\n" +
                addWhere +
                "           LIMIT 1\n" +
                "       ) AS \"이륜차 외 주차 대수(주간)\", " +
                "       (SELECT t3.cntom_etc\n" +
                "           FROM public.\"dim_tmplt_dmnd_etc_t\" AS t3\n" +
                "           WHERE t2.sn = " + t3JoinCol + " AND t3.dy_nght = '2'\n" +
                addWhere +
                "           LIMIT 1\n" +
                "       ) AS \"이륜차 외 주차 대수(야간)\" " + nextComma;
    }

    public GeoJSONResponse customGeoJsonQuery(
            String tableName,
            String type,
            String subType,
            String year,
            String regCode,
            String epsg,
            CodeDto dongCode
    ) throws JsonProcessingException {
        StringBuilder query = new StringBuilder();
        query.append(
                "SELECT JSONB_BUILD_OBJECT("
        );
        query.append(
                "'id', fid, " +
                "'geometry_name', 'the_geom', " +
                "'type', 'Feature', " +
                "'properties', to_jsonb(inputs) - 'the_geom' - 'fid', " +
                "'geometry', ST_AsGeoJSON(ST_Transform(ST_SetSRID(the_geom," + epsg + "), 3857))::JSONB) "
        );
        query.append(
                "FROM ("
        );

        // 노상 주차장 JOIN
        if (type.equals("주차장") && subType.equals("노상")) {
            String whereDong = "";

            if (dongCode != null) {
                whereDong = " AND (t2.stdg_cd LIKE CONCAT('%', :dong, '%') OR t2.dong_cd LIKE CONCAT('%', :dong, '%')) ";
            }

            query.append(
                "    SELECT " +
                            "        t1.fid, " +
                            "        t1.the_geom, " +
                            "        t2.pk_spcs AS \"주차면수 전체\", " +
                            "        t2.pk_spcs_cm AS \"주차면수 일반\", " +
                            "        t2.pk_spcs_pwdbs AS \"주차면수 장애인\", " +
                            "        t2.pk_spcs_elct AS \"주차면수 전기차\", " +
                            "        t2.pk_spcs_etc AS \"주차면수 기타\", " +
                            "        t2.pk_cntom AS \"주차대수\", " +
                            "        t2.dy_vhcl_type AS \"주차대수(주간) 차종\", " +
                            "        t2.nght_vhcl_type AS \"주차대수(야간) 차종)\", " +
                            "        t2.sn AS \"연번\", " +
                            "        t2.blck_no AS \"조사구역번호\", " +
                            "        t2.pklt_nm AS \"주차장명\", " +
                            "        t2.sgg_cd AS \"시군구코드\", " +
                            "        t2.stdg_cd AS \"법정동코드\", " +
                            "        t2.dong_cd AS \"행정동코드\", " +
                            "        t2.li_cd AS \"행정리코드\", " +
                            "        t2.chag AS \"요금\", " +
                            "        t2.dy_blck_sn AS \"주간 블럭별 연번\", " +
                            "        t2.nght_blck_sn AS \"야간 블럭별 연번\", " +
                            caseHasInfoSign("t2", ",") +
                            caseHasNonSlip("t2", ",") +
                            caseIsLegalD("t2", ",") +
                            caseIsLegalN("t2", ",") +
                            caseIsPay("t2", ",") +
                            caseIsSlope("t2", ",") +
                            caseTypeFacility("t2", ",") +
                            caseTypeP("t2", "") +
                            "    FROM public.\"" + tableName + "\" AS t1 " +
                            "    JOIN public.\"dim_tmplt_rd_t\" AS t2 ON t1.연번::text = t2.sn " +
                            "    AND t2.yr = :yr AND t2.sgg_cd = :sgg " + whereDong +
                            ") inputs; "
            );
        }

        // 노외 주차장 조인
        else if (type.equals("주차장") && subType.equals("노외")) {
            String t3JoinCol = "";

            if (regCode.equals("31140")) {
                t3JoinCol = " t3.sn ";
            } else {
                t3JoinCol = " REPLACE(t3.sn, '노외', '') ";
            }

            String whereDong = "";
            String whereDong2 = "";

            if (dongCode != null) {
                whereDong = " AND (t2.stdg_cd LIKE CONCAT('%', :dong, '%') OR t2.dong_cd LIKE CONCAT('%', :dong, '%')) ";
                whereDong2 = " AND (t3.stdg_cd LIKE CONCAT('%', :dong, '%') OR t3.dong_cd LIKE CONCAT('%', :dong, '%')) ";
            }

            query.append(
                    "    SELECT " +
                            "        t1.fid, " +
                            "        t1.the_geom, " +
                            "        t2.spcs_tot AS \"주차면수 전체\", " +
                            "        t2.sn AS \"연번\", " +
                            "        t2.blck_no AS \"조사구역번호\", " +
                            "        t2.pklt_nm AS \"주차장명\", " +
                            "        t2.sgg_cd AS \"시군구코드\", " +
                            "        t2.stdg_cd AS \"법정동코드\", " +
                            "        t2.dong_cd AS \"행정동코드\", " +
                            "        t2.mno AS \"주소 번지\", " +
                            "        t2.sno AS \"주소 호\", " +
                            "        t2.li_cd AS \"행정리코드\", " +
                            "        t2.chag AS \"요금\", " +
                            caseIsPay("t2", ",") +
                            caseBackup("t2", ",") +
                            caseCctv("t2", ",") +
                            caseMonitor("t2", ",") +
                            caseHasInfoSign("t2", ",") +
                            caseHasNonSlip("t2", ",") +
                            caseIsSlope("t2", ",") +
                            caseIsPub("t2", ",") +
                            caseTypeFacility("t2", ",") +
                            caseTypeP("t2", ",") +
                            getParkingCountQuery(t3JoinCol,"    AND t3.yr = :yr AND t3.sgg_cd = :sgg " + whereDong2 , "") +
                            "    FROM public.\"" + tableName + "\" AS t1 " +
                            "    JOIN public.\"dim_tmplt_offst_t\" AS t2 ON t1.연번::text = t2.sn " +
                            "    AND t2.yr = :yr AND t2.sgg_cd = :sgg " + whereDong +
                            ") inputs; "
            );
        }

        // 부설 주차장 조인
        else if (type.equals("주차장") && subType.equals("부설")) {
            String joinCol = "연번";

            String t3JoinCol = "";

            if (regCode.equals("31140") || regCode.equals("31200")) {
                t3JoinCol = " t3.sn ";
            } else {
                t3JoinCol = " REPLACE(t3.sn, '부설', '') ";
            }

            String whereDong = "";
            String whereDong2 = "";

            if (dongCode != null) {
                whereDong = " AND (t2.stdg_cd LIKE CONCAT('%', :dong, '%') OR t2.dong_cd LIKE CONCAT('%', :dong, '%')) ";
                whereDong2 = " AND (t3.stdg_cd LIKE CONCAT('%', :dong, '%') OR t3.dong_cd LIKE CONCAT('%', :dong, '%')) ";
            }

            query.append(
                    "    SELECT " +
                            "        t1.fid, " +
                            "        t1.the_geom, " +
                            "        t2.spcs_tot AS \"주차면수 전체\", " +
                            "        t2.unathr_usg_chg_spcs AS \"무단용도(면수)\", " +
                            "        t2.fnct_umntn_spcs AS \"기능 미유지(면수)\", " +
                            "        t2.sn AS \"연번\", " +
                            "        t2.blck_no AS \"조사구역번호\", " +
                            "        t2.pklt_nm AS \"주차장명\", " +
                            "        t2.sgg_cd AS \"시군구코드\", " +
                            "        t2.stdg_cd AS \"법정동코드\", " +
                            "        t2.dong_cd AS \"행정동코드\", " +
                            "        t2.mno AS \"주소 번지\", " +
                            "        t2.sno AS \"주소 호\", " +
                            "        t2.li_cd AS \"행정리코드\", " +
                            "        t2.pklt_se AS \"구분\", " +
                            "        t2.main_usg AS \"주용도\", " +
                            caseIsPay("t2", ",") +
                            caseBackup("t2", ",") +
                            caseCctv("t2", ",") +
                            caseMonitor("t2", ",") +
                            caseHasInfoSign("t2", ",") +
                            caseHasNonSlip("t2", ",") +
                            caseIsSlope("t2", ",") +
                            caseIsApt("t2", ",") +
                            caseIsResi("t2", ",") +
                            caseTypeFacility("t2", ",") +
                            caseTypeP("t2", ",") +
                            getParkingCountQuery(t3JoinCol,"    AND t3.yr = :yr AND t3.sgg_cd = :sgg " + whereDong2,"") +
                            "    FROM public.\"" + tableName + "\" AS t1 " +
                            "    JOIN public.\"dim_tmplt_atchd_t\" AS t2 ON t1." + joinCol + "::text = t2.sn " +
                            "    AND t2.yr = :yr AND t2.sgg_cd = :sgg " + whereDong +
                            ") inputs; "
            );
        }

        // 노상 수요 JOIN
        else if (type.equals("수요") && subType.equals("노상 수요")) {
            String joinCol = "연번";

            // 임시
            String addJoinCol = " ";

            // 2021년
            if (regCode.equals("31140")) {
                if (tableName == "SHP_RESULT_247") {
                    // 야간
                    addJoinCol = " AND t2.dy_nght = '2' ";

                } else if (tableName == "SHP_RESULT_509") {
                    // 주간
                    addJoinCol = " AND t2.dy_nght = '1' ";

                }
            }

            String whereDong = "";

            if (dongCode != null) {
                whereDong = " AND (t2.stdg_cd LIKE CONCAT('%', :dong, '%') OR t2.stdg_cd LIKE CONCAT('%', :dongKo, '%')) ";
            }

            query.append(
                    "    SELECT " +
                            "        t1.fid, " +
                            "        t1.the_geom, " +
                            "        t2.sn AS \"연번\", " +
                            "        t2.blck_no AS \"조사구역번호\", " +
                            "        t2.sgg_cd AS \"시군구코드\", " +
                            "        t2.stdg_cd AS \"법정동코드\", " +
                            "        t2.dong_cd AS \"행정동코드\", " +
                            "        t2.vhcl_no AS \"차량번호\", " +
                            caseTypeC("t2", ",") +
                            caseRmYn("t2", ",") +
                            caseIsLegal("t2", ",") +
                            caseRschTime("t2", "") +
                            "    FROM public.\"" + tableName + "\" AS t1 " +
                            "    JOIN public.\"dim_tmplt_dmnd_rd_t\" AS t2 ON t1." + joinCol + "::text = t2.sn " +
                            "    AND t2.yr = :yr AND t2.sgg_cd = :sgg " + addJoinCol + whereDong +
                            ") inputs; "
            );
        }

        // 기타
        else {
            query.append(
                    "    SELECT * " +
                            "    FROM public.\"" + tableName + "\" AS t1 " +
                            ") inputs; "
            );
        }

        HashMap<String, Object> param = new HashMap<>();
        param.put("yr", year);
        param.put("sgg", regCode);
        param.put("dong", dongCode != null ? dongCode.getName().replace(regCode , "") : "");
        param.put("dongKo", dongCode != null ? dongCode.getValue() : "");

        List<Map<String, Object>> result = namedParameterJdbcTemplate.queryForList(query.toString(), param);

        GeoJSONResponse customGeoJSONResponse = new GeoJSONResponse();
        customGeoJSONResponse.setFeatures(new ArrayList<GeoJSONResponse.Feature>());
        customGeoJSONResponse.setType("FeatureCollection");
        customGeoJSONResponse.setTimeStamp(LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        customGeoJSONResponse.setCrs(new GeoJSONResponse.Crs("name",new HashMap<>()));

        for (Map<String, Object> jsonMap : result) {
            Object json = jsonMap.get("jsonb_build_object");
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            GeoJSONResponse.Feature feature = objectMapper.readValue(String.valueOf(json), GeoJSONResponse.Feature.class);
            feature.setId(tableName + "." + feature.getId());
            customGeoJSONResponse.getFeatures().add(feature);
        }

        return customGeoJSONResponse;
    }

}
