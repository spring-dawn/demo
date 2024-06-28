package com.example.demo.domain.api.building_management;

import com.example.demo.domain.CommonEntity;
import com.example.demo.dto.api.BuildingManagementDto;
import com.example.demo.dto.common.FileInfoDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

import static com.example.demo.atech.MyUtil.hasInteger;

//@ToString
@Getter // do not create/use `setter` on Entity class
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
@Table(name = "DIM_API_BM_T", indexes = {
        //@Index(name = "DIM_API_BM_T_IX01", columnList = "JI"),
        @Index(name = "DIM_API_BM_T_IX01", columnList = "SNO"),
        //@Index(name = "DIM_API_BM_T_IX02", columnList = "BUN"),
        @Index(name = "DIM_API_BM_T_IX02", columnList = "MNO"),
        //@Index(name = "DIM_API_BM_T_IX03", columnList = "BJDONG_CD"),
        @Index(name = "DIM_API_BM_T_IX03", columnList = "STDG_CD"),
        //@Index(name = "DIM_API_BM_T_IX04", columnList = "SIGUNGU_CD")
        @Index(name = "DIM_API_BM_T_IX04", columnList = "SGG_CD")
})
public class BuildingManagement extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BDRG_NO")
    //@Column(name = "BM_NO")
    @Comment("건축물대장_번호")
    private Long bmNo;


    @Column(name = "BLDG_NM")
    //@Column(name = "BLD_NM")
    @Comment("건물_명칭")
    private String bldNm;


    @Column(name = "SGG_CD", length = 5, columnDefinition = "int")
    //@Column(name = "SIGUNGU_CD", length = 5, columnDefinition = "int")
    @Comment("시군구_코드")
    private Integer sigunguCd;

    @Column(name = "STDG_CD", length = 5, columnDefinition = "int")
    //@Column(name = "BJDONG_CD", length = 5, columnDefinition = "int")
    @Comment("법정동_코드")
    private Integer bjdongCd;

    @Column(name = "MNO", length = 4, columnDefinition = "int")
    //@Column(name = "BUN", length = 4, columnDefinition = "int")
    @Comment("본번")
    private Integer bun;

    @Column(name = "SNO", length = 4, columnDefinition = "int")
    //@Column(name = "JI", length = 4, columnDefinition = "int")
    @Comment("부번")
    private Integer ji;

    @Column(name = "BDRG_CRTN_DAY")
    //@Column(name = "CRTN_DAY")
    @Comment("관리대장_생성_일자")
    private String crtnDay;

    @Column(name = "INDR_SFPRPL_AREA")
    //@Column(name = "INDR_AUTO_AREA")
    @Comment("옥내_자주식_면적(㎡)")
    private String indrAutoArea;

    @Column(name = "INDR_SFPRPL_CNTOM")
    //@Column(name = "INDR_AUTO_UTCNT")
    @Comment("옥내_자주식_대수(대)")
    private String indrAutoUtcnt;

    @Column(name = "INDR_MCNCL_AREA")
    //@Column(name = "INDR_MECH_AREA")
    @Comment("옥내_기계식_면적(㎡)")
    private String indrMechArea;

    @Column(name = "INDR_MCNCL_CNTOM")
    //@Column(name = "INDR_MECH_UTCNT")
    @Comment("옥내_기계식_대수(대)")
    private String indrMechUtcnt;

    @Column(name = "MAIN_USG_CD_NM")
    //@Column(name = "MAIN_PURPS_CD_NM")
    @Comment("주요_용도_코드_명칭")
    private String mainPurpsCdNm;

    @Column(name = "MNG_BDRG_NO")
    //@Column(name = "MGM_BLDRGST_PK")
    @Comment("관리_건축물대장_번호")
    private String mgmBldrgstPk;

    @Column(name = "NEW_ADDR_STDG_CD")
    //@Column(name = "NA_BJDONG_CD")
    @Comment("신규_주소_법정동_코드")
    private String naBjdongCd;

    @Column(name = "NEW_ADDR_MNO")
    //@Column(name = "NA_MAIN_BUN")
    @Comment("신규_주소_본번")
    private String naMainBun;

    @Column(name = "NEW_ADDR_ROAD_CD")
    //@Column(name = "NA_ROAD_CD")
    @Comment("신규_주소_도로_코드")
    private String naRoadCd;

    @Column(name = "NEW_ADDR_SNO")
    //@Column(name = "NA_SUB_BUN")
    @Comment("신규_주소_부번")
    private String naSubBun;

    @Column(name = "STNM_SIPSTN")
    //@Column(name = "NEW_PLAT_PLC")
    @Comment("도로명_대지위치")
    private String newPlatPlc;

    @Column(name = "OTDR_SFPRPL_AREA")
    //@Column(name = "OUDR_AUTO_AREA")
    @Comment("옥외_자주식_면적(㎡)")
    private String oudrAutoArea;

    @Column(name = "OTDR_SFPRPL_CNTOM")
    //@Column(name = "OUDR_AUTO_UTCNT")
    @Comment("옥외_자주식_대수(대)")
    private String oudrAutoUtcnt;

    @Column(name = "OTDR_MCNCL_AREA")
    //@Column(name = "OUDR_MECH_AREA")
    @Comment("옥외_기계식_면적(㎡)")
    private String oudrMechArea;

    @Column(name = "OTDR_MCNCL_CNTOM")
    //@Column(name = "OUDR_MECH_UTCNT")
    @Comment("옥외_기계식_대수(대)")
    private String oudrMechUtcnt;

    @Column(name = "SIPSTN")
    //@Column(name = "PLAT_PLC")
    @Comment("대지위치")
    private String platPlc;

    @Column(name = "STRCT_CD_NM")
    //@Column(name = "STRCT_CD_NM")
    @Comment("구조물_코드_명칭")
    private String strctCdNm;

    @Column(name = "ETC_USG")
    //@Column(name = "ETC_PURPS")
    @Comment("기타용도")
    private String etcPurps;

    @Column(name = "DONG_NM")
    //@Column(name = "DONG_NM")
    @Comment("동명칭")
    private String dongNm;

    @Column(name = "SN", columnDefinition = "int")
    //@Column(name = "RNUM", columnDefinition = "int")
    @Comment("일련번호")
    private Integer rnum;

    @Column(name = "LOT")
    //@Column(name = "LON")
    @Comment("경도")
    private String lon;

    @Column(name = "LAT")
    //@Column(name = "LAT")
    @Comment("위도")
    private String lat;

    public BuildingManagementDto.BuildingManagementRes toRes() {
        return BuildingManagementDto.BuildingManagementRes.builder()
                .id(bmNo)  // Assuming id in your DTO corresponds to BM_NO in your entity
                .sigunguCd(sigunguCd)
                .bjdongCd(bjdongCd)
                .bun(bun)
                .ji(ji)
                .bldNm(bldNm)
                .platPlc(platPlc)
                .etcPurps(etcPurps)
                .indrAutoArea(indrAutoArea)
                .indrAutoUtcnt(indrAutoUtcnt)
                .indrMechArea(indrMechArea)
                .indrMechUtcnt(indrMechUtcnt)
                .oudrAutoArea(oudrAutoArea)
                .oudrAutoUtcnt(oudrAutoUtcnt)
                .oudrMechArea(oudrMechArea)
                .oudrMechUtcnt(oudrMechUtcnt)
                .lat(lat)
                .lot(lon)
                .mainPurpsCdNm(mainPurpsCdNm)
                .mgmBldrgstPk(mgmBldrgstPk)
                .crtnDay(crtnDay)
                .naBjdongCd(naBjdongCd)
                .naMainBun(naMainBun)
                .naRoadCd(naRoadCd)
                .naSubBun(naSubBun)
                .seq(String.valueOf(rnum))
                .build();
    }
}
