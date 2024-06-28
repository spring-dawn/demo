package com.example.demo.domain.data.facility.read;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.facility.FacilityPk;
import com.example.demo.domain.data.facility.file.PFData;
import com.example.demo.dto.data.facility.PFOpenDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@IdClass(FacilityPk.class)
@Table(name = "DIM_PK_FCLT_OPEN_T"
        , indexes = {
        @Index(name = "DIM_PK_FCLT_OPEN_IX01", columnList = "DPCN_CHCK1"),
        @Index(name = "DIM_PK_FCLT_OPEN_IX02", columnList = "DPCN_CHCK2"),
        @Index(name = "DIM_PK_FCLT_OPEN_IX03", columnList = "DPCN_CHCK3"),
        @Index(name = "DIM_PK_FCLT_OPEN_IX04", columnList = "DPCN_CHCK4"),
}
)
public class PFOpen extends CommonEntity {
    /*
    사유지 개방, 부설 개방 주차장
    데이터가 적으므로 1개 테이블로 관리하고 화면상에 보여줄 때는 사유지/부설 구분합니다.
    month 월 정보는 없음.
     */

    @Id
    @Comment("실시연도")
    @Column(name = "YR", length = 4, nullable = false)
    private String year;

    @Id
    @Comment("실시월 01~12")
    @Column(name = "MM", length = 2, nullable = false)
    private String month;

    @Id
    @Comment("시군구코드")
    @Column(name = "SGG_CD", length = 10, nullable = false)
    private String sggCd;

    @Id
    @Comment("1:1 연관 표준관리대장 일련번호. 논리적 연결.")
    @Column(name = "MNG_NO", length = 20, nullable = false)
    private String mngNo;

    @Comment("연번")
    @Column(name = "SN", length = 10)
    private String seq;

    @Comment("표준관리대장 기준 주차장 유형. 8: 부설개방, 9: 사유지개방")
    @Column(name = "PKLT_TYPE", length = 1)
    private String lotType;

    @Comment("구분(주차장명)")
    @Column(name = "PKLT_NM", length = 50)
    private String lotNm;

    @Comment("(지번)주소")
    @Column(name = "ADDR", length = 100)
    private String address;

    @Comment("개방 면수")
    @Column(name = "OPEN_SPCS", nullable = false)
    private Long spcs;

    @Comment("면적(㎡)")
    @Column(name = "AREA", length = 20)
    private String area;

    @Comment("개방 시간")
    @Column(name = "OPEN_HR", columnDefinition = "text")
    private String openTm;

    @Comment("개방 요일")
    @Column(name = "OPEN_DY", length = 50)
    private String openDay;

    @Comment("위도, y축")
    @Column(name = "LAT", length = 20)
    private String lat;

    @Comment("경도, x축")
    @Column(name = "LOT", length = 20)
    private String lng;

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
    @ManyToOne(fetch = FetchType.LAZY)
    @Comment("참조하는 주차시설 파일업로드 번호")
    @JoinColumn(name = "PK_FCLT_DATA_NO")
    private PFData pfData;

    /*
    update
     */
    public void update(PFOpenDto.Req req) {
        // 기본키 제외.
//        this.lotType = req.getLotType();
//        this.lotNm = req.getLotNm();
//        this.address = req.getAddress();
//        this.spcs = req.getSpcs();
        this.area = req.getArea();
        this.openTm = req.getOpenTm();
        this.openDay = req.getOpenDay();
        this.lat = req.getLat();
        this.lng = req.getLng();
        this.seq = req.getSeq();
    }


    /*
    dto
     */
    public PFOpenDto toRes() {
        return PFOpenDto.builder()
                .spcs(this.spcs)
                .year(this.year)
                .month(this.month)
                .sggCd(this.sggCd)
                .seq(this.seq)
                .lotNm(this.lotNm)
                .address(this.address)
                .area(this.area)
                .openTm(this.openTm)
                .openDay(this.openDay)
                .lat(this.lat)
                .lng(this.lng)
                .lotType(this.lotType)
                .mngNo(this.mngNo)
                .createDtm(this.createDtm.toString())
                .build();
    }
}
