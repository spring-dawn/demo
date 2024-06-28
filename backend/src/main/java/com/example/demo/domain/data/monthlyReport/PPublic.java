package com.example.demo.domain.data.monthlyReport;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.facility.file.PFData;
import com.example.demo.domain.data.monthlyReport.pk.PPublicPk;
import com.example.demo.domain.data.monthlyReport.pk.PStatusPk;
import com.example.demo.dto.data.monthlyReport.PPublicDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIM_PS_PBL_T", indexes = {
        @Index(name = "DIM_PS_PBL_IX01", columnList = "DPCN_CHCK1"),
        @Index(name = "DIM_PS_PBL_IX02", columnList = "DPCN_CHCK2"),
        @Index(name = "DIM_PS_PBL_IX03", columnList = "DPCN_CHCK3"),
        @Index(name = "DIM_PS_PBL_IX04", columnList = "DPCN_CHCK4"),
})
@IdClass(PPublicPk.class)
public class PPublic extends CommonEntity {
    /*
    공영주차장 현황
     */
    //@Id
    @Column(name = "PKLT_PBLMN_NO")
    //@Column(name = "PS_PBL_NO")
    //@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SEQ")
    private Long seq;

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

    @Comment("주차장명")
    @Column(name = "PKLT_NM", length = 50)
    private String name;

    @Comment("설치일자")
    @Column(name = "INSTL_YMD", length = 12)
    private String installDt;

    @Comment("위치")
    @Column(name = "LCTN", length = 100)
    private String location;

    @Comment("운영시간_평일")
    @Column(name = "OPER_WKDY", length = 30)
    private String wh;

    @Comment("운영시간_토요일")
    @Column(name = "OPER_STDY", length = 30)
    private String whSaturday;

    @Comment("운영시간_공휴일")
    @Column(name = "OPER_HLDY", length = 30)
    private String whHoliday;

    @Comment("휴무일")
    @Column(name = "CSDY", length = 10)
    private String dayOff;

    @Comment("유/무료. Y:유료, N: 무료")
    @Column(name = "PAY_YN", length = 1)
    private String payYn;

    @Comment("1시간 요금")
    @Column(name = "PAY_HR", length = 10)
    private String pay4Hour;

    @Comment("1일 요금")
    @Column(name = "PAY_DAY", length = 10)
    private String pay4Day;

    @Comment("전체 주차면수")
    @Column(name = "TOT_SPCS")
    private Long totalSpaces;

    @Comment("일반 주차면수")
    @Column(name = "SPCS")
    private Long spaces;

    @Comment("장애인 전용 주차구획")
    @Column(name = "XU_PWDBS")
    private Long forDisabled;

    @Comment("경차 전용 주차구획")
    @Column(name = "XU_LWVH")
    private Long forLight;

    @Comment("임산부 전용 주차구획")
    @Column(name = "XU_GRVD")
    private Long forPregnant;

    @Comment("버스 전용 주차구획")
    @Column(name = "XU_BUS")
    private Long forBus;

    @Comment("전기차 전용 주차구획")
    @Column(name = "XU_ETMTVC")
    private Long forElectric;

    @Comment("주차장 형태 (노상/노외/부설)")
    @Column(name = "PKLT_PTN", length = 2)
    private String roadYn;

    @Comment("거주자 유무 Y / N")
    @Column(name = "RSDT_YN", length = 1)
    private String resiYn;

    @Comment("주차장 유형 1자리. 1:공영노상, 2:공영노외, 3:공영부설, 4:민영노상, 5:민영노외, 6:민영부설, 7:부설, 8:부설개방, 9:사유지개방")
    @Column(name = "PKLT_TYPE", length = 2)
    private String lotType;

    @Comment("소유")
    @Column(name = "OWNR", length = 30)
    private String owner;

    @Comment("운영기관")
    @Column(name = "MNGAGC", length = 10)
    private String agency;

    @Comment("비고")
    @Column(name = "RMRK", columnDefinition = "text")
    private String comment;

    //추가컬럼 -sks
    @Comment("연번")
    @Column(name = "SN")
    private String serial_number;
    @Comment("지목")
    @Column(name = "LDCG")
    private String point_out;
    @Comment("면적")
    @Column(name = "AREA")
    private double area;
    @Comment("친환경 전용 주차구획")
    @Column(name = "XU_ECFRD")
    private Long forEcho;
    @Comment("어르신 전용 주차구획")
    @Column(name = "XU_ELDR")
    private Long forElderly;

    //중복검사용
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

    @Id
    @Comment("1:1 연관 표준관리대장 일련번호. 논리적 연결.")
    @Column(name = "MNG_NO", length = 20, nullable = false)
    private String mngNo;

    @Comment("lon")
    @Column(name = "LOT", length = 20)
    //@Column(name = "LON", length = 20)
    private String lon;

    @Comment("lat")
    @Column(name = "LAT", length = 20)
    private String lat;


    // 일괄 삭제 연관관계.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MNTL_RPT_DATA_NO")
    private MrData mrData;


    // update
    public void update(PPublicDto req) {
        this.seq = req.getSeq();
        this.year = req.getYear();
        this.month = req.getMonth();
        this.sggCd = req.getSggCd();
        this.name = req.getName();
        this.installDt = req.getInstallDt();
        this.location = req.getLocation();
        this.wh = req.getWh();
        this.whSaturday = req.getWhSaturday();
        this.whHoliday = req.getWhHoliday();
        this.dayOff = req.getDayOff();
        this.payYn = req.getPayYn();
        this.pay4Hour = req.getPay4Hour();
        this.pay4Day = req.getPay4Day();
        this.totalSpaces = req.getSpaces() + req.getForDisabled() + req.getForLight() + req.getForPregnant() + req.getForBus() + req.getForElectric() + req.getForEcho() + req.getForElderly() ;
        this.spaces = req.getSpaces();
        this.forDisabled = req.getForDisabled();
        this.forLight = req.getForLight();
        this.forPregnant = req.getForPregnant();
        this.forBus = req.getForBus();
        this.forElectric = req.getForElectric();
        this.roadYn = req.getRoadYn();
        this.resiYn = req.getResiYn();
        this.lotType = req.getLotType();
        this.owner = req.getOwner();
        this.agency = req.getAgency();
        this.comment = req.getComment();
        this.serial_number = req.getSerial_number();
        this.point_out = req.getPoint_out();
        this.area = req.getArea();
        this.forEcho = req.getForEcho();
        this.forElderly = req.getForElderly();
        this.dupChk1 = req.getDupChk1();
        this.dupChk2 = req.getDupChk2();
        this.dupChk3 = req.getDupChk3();
        this.dupChk4 = req.getDupChk4();
        this.mngNo = req.getMngNo();
        this.lon = req.getLon();
        this.lat = req.getLat();


    }

    // dto
    public PPublicDto toRes() {
        return PPublicDto.builder()
                .id(this.id)
                .seq(this.seq)
                .year(this.year)
                .month(this.month)
                .sggCd(this.sggCd)
                .name(this.name)
                .installDt(this.installDt)
                .location(this.location)
                .wh(this.wh)
                .whSaturday(this.whSaturday)
                .whHoliday(this.whHoliday)
                .dayOff(this.dayOff)
                .payYn(this.payYn)
                .pay4Hour(this.pay4Hour)
                .pay4Day(this.pay4Day)
                .totalSpaces(this.totalSpaces)
                .spaces(this.spaces)
                .forDisabled(this.forDisabled)
                .forLight(this.forLight)
                .forPregnant(this.forPregnant)
                .forElectric(this.forElectric)
                .forBus(this.forBus)
                .roadYn(this.roadYn)
                .resiYn(this.resiYn)
                .lotType(this.lotType)
                .owner(this.owner)
                .agency(this.agency)
                .comment(this.comment)
                .createDtm(this.createDtm == null ? null : this.createDtm.toString().substring(0, 10))
                .serial_number(this.serial_number)
                .point_out(this.point_out)
                .area(this.area)
                .forEcho(this.forEcho)
                .forElderly(this.forElderly)
                .dupChk1(this.dupChk1)
                .dupChk2(this.dupChk2)
                .dupChk3(this.dupChk3)
                .dupChk4(this.dupChk4)
                .mngNo(this.mngNo)
                .lon(this.lon)
                .lat(this.lat)
                .build();
    }

}
