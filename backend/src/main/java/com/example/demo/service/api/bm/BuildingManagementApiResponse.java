package com.example.demo.service.api.bm;

import lombok.Data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@XmlRootElement(name = "response")
public class BuildingManagementApiResponse {
    private Header header;
    private Body body;

    @Data
    @XmlRootElement
    public static class Header {
        private String resultCode;
        private String resultMsg;
    }

    @Data
    @XmlRootElement
    public static class Body {
        private Items items;
        private String numOfRows;
        private String pageNo;
        private String totalCount;
    }

    @Data
    @XmlRootElement
    public static class Items {
        private List<Item> item;
    }

    @Data
    @XmlRootElement
    public static class Item {
        private String archArea;
        private String atchBldArea;
        private String atchBldCnt;
        private String bcRat;
        private String bjdongCd;
        private String bldNm;
        private String block;
        private String bun;
        private String bylotCnt;
        private String crtnDay;
        private String engrEpi;
        private String engrGrade;
        private String engrRat;
        private String etcPurps;
        private String fmlyCnt;
        private String gnBldCert;
        private String gnBldGrade;
        private String hhldCnt;
        private String hoCnt;
        private String indrAutoArea;
        private String indrAutoUtcnt;
        private String indrMechArea;
        private String indrMechUtcnt;
        private String itgBldCert;
        private String itgBldGrade;
        private String ji;
        private String lot;
        private String mainBldCnt;
        private String mainPurpsCd;
        private String mainPurpsCdNm;
        private String mgmBldrgstPk;
        private String naBjdongCd;
        private String naMainBun;
        private String naRoadCd;
        private String naSubBun;
        private String naUgrndCd;
        private String newOldRegstrGbCd;
        private String newOldRegstrGbCdNm;
        private String newPlatPlc;
        private String oudrAutoArea;
        private String oudrAutoUtcnt;
        private String oudrMechArea;
        private String oudrMechUtcnt;
        private String platArea;
        private String platGbCd;
        private String platPlc;
        private String pmsDay;
        private String pmsnoGbCd;
        private String pmsnoGbCdNm;
        private String pmsnoKikCd;
        private String pmsnoKikCdNm;
        private String pmsnoYear;
        private String regstrGbCd;
        private String regstrGbCdNm;
        private String regstrKindCd;
        private String regstrKindCdNm;
        private String rnum;
        private String sigunguCd;
        private String splotNm;
        private String stcnsDay;
        private String totArea;
        private String totPkngCnt;
        private String useAprDay;
        private String vlRat;
        private String vlRatEstmTotArea;
        private String strctCdNm;
        private String dongNm;
    }
}




