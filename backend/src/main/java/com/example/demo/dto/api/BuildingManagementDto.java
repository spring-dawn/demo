package com.example.demo.dto.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.springframework.web.bind.annotation.RequestParam;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
public class BuildingManagementDto {

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BuildingManagementRes {
        private Long id;
        private Integer sigunguCd;
        private Integer bjdongCd;
        private Integer bun;
        private Integer ji;
        private String bldNm;
        private String indrAutoArea;
        private String indrAutoUtcnt;
        private String indrMechArea;
        private String indrMechUtcnt;
        private String oudrAutoArea;
        private String oudrAutoUtcnt;
        private String oudrMechArea;
        private String oudrMechUtcnt;
        private String mainPurpsCdNm;
        private String crtnDay;
        private String platPlc;
        private String strctCdNm;
        private String etcPurps;
        private String dongNm;
        private String mgmBldrgstPk;
        private String lat; // 위도
        private String lot; // 경도
        private boolean check;
        //
        private String naBjdongCd;
        private String naMainBun;
        private String naRoadCd;
        private String naSubBun;
        private String seq;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BuildingManagementReq {
        private String year;
        private String month;
        private String sggCd;
        private String dongCode;
        private String bunCode;
        private String jiCode;
    }
}
