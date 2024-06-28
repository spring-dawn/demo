package com.example.demo.domain.data.facility.read;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.facility.FacilityPk;
import com.example.demo.domain.data.facility.file.PFData;
import com.example.demo.domain.data.standardSet.StandardMng;
import com.example.demo.dto.data.facility.PFPrivateDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@IdClass(FacilityPk.class)
@Table(name = "DIM_PK_FCLT_PRVT_T"
        , indexes = {
        @Index(name = "DIM_PK_FCLT_PRVT_IX01", columnList = "DPCN_CHCK1"),
        @Index(name = "DIM_PK_FCLT_PRVT_IX02", columnList = "DPCN_CHCK2"),
        @Index(name = "DIM_PK_FCLT_PRVT_IX03", columnList = "DPCN_CHCK3"),
        @Index(name = "DIM_PK_FCLT_PRVT_IX04", columnList = "DPCN_CHCK4"),
}
)
public class PFPrivate extends CommonEntity {
    /*
    민영노외주차장 DB화
    공영주차장은 월간보고 공영주차장 시트(4번째)와 내용을 공유하므로 따로 만들지 않습니다.
     */

    @Id
    @Comment("실시연도 ex)2022")
    @Column(name = "YR", length = 4, nullable = false)
    private String year;

    @Id
    @Comment("실시월 01~12")
    @Column(name = "MM", length = 2, nullable = false)
    private String month;

    @Id
    @Comment("구군")
    @Column(name = "SGG_CD", length = 10, nullable = false)
    private String sggCd;

    @Id
    @Comment("1:1 연관 표준관리대장 일련번호. 논리적 연결.")
    @Column(name = "MNG_NO", length = 20, nullable = false)
    private String mngNo;

    @Comment("주차장명")
    @Column(name = "PKLT_NM", length = 50)
    private String lotNm;

    @Comment("주차장 ID")
    @Column(name = "PKLT_ID", length = 50)
    private String lotId;

    @Comment("주차장 유형. 현재는 민영노외(5) 디폴트. ex) 4:노상, 5:노외")
    @Column(name = "PKLT_TYPE", length = 10)
    private String lotType;

    @Comment("지번주소")
    @Column(name = "ADDR", length = 50)
    private String address;

    @Comment("도로명주소")
    @Column(name = "STNM_ADDR", length = 50)
    private String streetAddr;

    @Comment("대표번호")
    @Column(name = "RPRS_TELNO", length = 20)
    private String ceoCellNo;

    @Comment("운영정보")
    @Column(name = "OPER_INFO", columnDefinition = "text")
    private String operateInfo;

    @Comment("위도, y축")
    @Column(name = "LAT", length = 50)
    private String lat;

    @Column(name = "LOT", length = 50)
    @Comment("경도, x축")
    private String lng;

    @Comment("정보수집")
    @Column(name = "CLCT_INFO", length = 100)
    private String collectInfo;

    @Comment("급지(땅의 급수)")
    @Column(name = "LAND_GRD", length = 50)
    private String landRank;

    @Comment("일반 주차면수")
    @Column(name = "SPCS", length = 50)
    private String spcs;

    @Comment("총 주차면수")
    @Column(name = "TOT_SPCS", nullable = false)
    private Long totalSpcs;

    @Comment("장애인 주차면수")
    @Column(name = "PWDBS_PK_SPCS", length = 50)
    private String disabledSpcs;

    @Comment("일반주차대수")
    @Column(name = "PK_CNTOM", length = 50)
    private String spcsIn;

    @Comment("총주차대수")
    @Column(name = "TOT_SPCS_CNTOM", length = 50)
    private String totalSpcsIn;

    @Comment("장애인주차대수")
    @Column(name = "PWDBS_PK_CNTOM", length = 50)
    private String disabledSpcsIn;

    @Comment("등록시간")
    @Column(name = "REG_TM", length = 30)
    private String regiTm;

    //    @Comment("구분. 1:통보, 2:자체파악. 주차장 신고(등록)방식에 따라 분류.")
    @Comment("구분. 주차장 신고(등록)방식에 따라 분류.")
    @Column(name = "REG_TYPE", length = 4)
    private String regiType;


    // 중복 검사 컬럼. 문자열 값을 더하는 순서 반드시 지킬 것.
    @Comment("완전중복 검사용 컬럼. 주차장명+지번주소+총주차면수")
    @Column(name = "DPCN_CHCK1", columnDefinition = "text")
    private String dupChk1;

    @Comment("부분중복 검사용 컬럼. 주차장명+지번주소")
    @Column(name = "DPCN_CHCK2", columnDefinition = "text")
    private String dupChk2;

    @Comment("부분중복 검사용 컬럼. 주차장명+총주차면수")
    @Column(name = "DPCN_CHCK3", columnDefinition = "text")
    private String dupChk3;

    @Comment("부분중복 검사용 컬럼. 지번주소+총주차면수")
    @Column(name = "DPCN_CHCK4", columnDefinition = "text")
    private String dupChk4;

    // 일괄 삭제 연관관계.
    @JsonIgnore
    @Comment("참조하는 주차시설 파일업로드 번호")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_FCLT_DATA_NO")
    private PFData pfData;

    // update
    public void update(PFPrivateDto.Req req){
        this.lotId = req.getLotId();
        this.streetAddr = req.getStreetAddr();
        this.ceoCellNo = req.getCeoCellNo();
        this.operateInfo = req.getOperateInfo();
        this.lat = req.getLat();
        this.lng = req.getLng();
        this.collectInfo = req.getCollectInfo();
        this.landRank = req.getLandRank();
        this.regiTm = req.getRegiTm();
        this.regiType = req.getRegiType();
    }


    // dto
    public PFPrivateDto toRes() {
        return PFPrivateDto.builder()
                .year(this.year)
                .month(this.month)
                .sggCd(this.sggCd)
                .mngNo(this.mngNo)
                //
                .spcs(this.spcs)
                .totalSpcs(this.totalSpcs)
                .disabledSpcs(this.disabledSpcs)
                .spcsIn(this.spcsIn)
                .totalSpcsIn(this.totalSpcsIn)
                .disabledSpcsIn(this.disabledSpcsIn)
                .lotNm(this.lotNm)
                .lotId(this.lotId)
                .lotType(this.lotType)
                .address(this.address)
                .streetAddr(this.streetAddr)
                .ceoCellNo(this.ceoCellNo)
                .operateInfo(this.operateInfo)
                .lat(this.lat)
                .lng(this.lng)
                .collectInfo(this.collectInfo)
                .landRank(this.landRank)
                .regiTm(this.regiTm)
                .regiType(this.regiType)
                //
//                .dupChk1(this.dupChk1)
//                .dupChk2(this.dupChk2)
//                .dupChk3(this.dupChk3)
//                .dupChk4(this.dupChk4)
                .createDtm(this.createDtm.toString())
                .build();
    }


}
