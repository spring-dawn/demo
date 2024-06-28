package com.example.demo.domain.data.illegal.read;

import com.example.demo.domain.CommonEntity;
import com.example.demo.dto.data.illegal.IllegalDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

import static com.example.demo.atech.MyUtil.createDtm2Str;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIM_ILGL_PK_T")
public class Illegal extends CommonEntity {
    /*
    [240216] 개편된 새올 시스템에 따른 불법주정차 단속 원천 데이터 적재 테이블
    적발대장 엑셀 파일을 받아 DB화 합니다.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ILGL_PK_NO")
    private Long id;

    @Comment("실시연도 ex)2022")
    @Column(name = "YR", length = 4, nullable = false)
    private String year;

    @Comment("실시월 01~12")
    @Column(name = "MM", length = 2, nullable = false)
    private String month;

    @Comment("처리상태")
    @Column(name = "PRCS_STTS", length = 10)
    private String processStat;
    @Comment("위반일자")
    @Column(name = "VLTN_YMD", length = 30)
    private String violationDt;
    @Comment("증거번호")
    @Column(name = "EVT_NO", length = 100)
    private String evidenceNo;
    @Comment("차량번호")
    @Column(name = "VHCL_NO", length = 30)
    private String carNo;
    @Comment("위반시각")
    @Column(name = "VLTN_TM", length = 30)
    private String violationDtm;
    @Comment("위반장소")
    @Column(name = "VLTN_PLC_NM", length = 200)
    private String violationPlace;
    @Comment("위반자 성명")
    @Column(name = "VLTR_NM", length = 20)
    private String violatorNm;
    @Comment("주민등록번호")
    @Column(name = "RRNO", length = 14)
    private String residentRegiNo;
    @Comment("어린이보호구역")
    @Column(name = "SC_ZONE", length = 1)
    private String schoolZone;
    @Comment("과태료")
    @Column(name = "FFLNG")
    private Integer fflng;  // 과태료
    @Comment("사진")
    @Column(name = "PIC", columnDefinition = "text")
    private String pic;
    @Comment("요청자료")
    @Column(name = "REQ_DATA", columnDefinition = "text")
    private String requestData;
    @Comment("명부생성")
    @Column(name = "RLL_CRT", length = 1)
    private String isRll;
    @Comment("코드1. 동명 컬럼 구분용으로 임시 이름.")
    @Column(name = "CD1", length = 30)
    private String code1;

    @Comment("코드2. 동명 컬럼 구분용으로 임시 이름.")
    @Column(name = "CD2", length = 30)
    private String code2;
    @Comment("위반법조항명")
    @Column(name = "VLTN_LC_NM", length = 40)
    private String violatedLaw;
    @Comment("차종")
    @Column(name = "CARMDL_NM", length = 40)
    private String carType;
    @Comment("차량명")
    @Column(name = "VHCL_NM", length = 40)
    private String carNm;
    @Comment("적재량")
    @Column(name = "LDAG")
    private Integer capacity;
    @Comment("단속원 성명")
    @Column(name = "RGTR_FLNM", length = 20)
    private String inspectorNm;
    @Comment("카메라명")
    @Column(name = "CMR_NM", length = 40)
    private String cameraNm;
    @Comment("위반사항 기타")
    @Column(name = "VLTN_MTTR_ETC")
    private String etc;
    @Comment("비고: 특이사항")
    @Column(name = "EXCPTN_MTTR")
    private String comment;
    @Comment("과태료명")
    @Column(name = "FFNLG_NM", length = 40)
    private String fflngNm;
    @Comment("법적 상태")
    @Column(name = "LGST", length = 50)
    private String legalStat;
    @Comment("단속 형태")
    @Column(name = "CRDN_PTN", length = 20)
    private String crdnPtn; //crackdown
    @Comment("사전통지일")
    @Column(name = "PRNT_DAY", length = 30)
    private String preNoticeDay;
    @Comment("사전통지납부기한")
    @Column(name = "PRNT_PAY_TERM", length = 30)
    private String preNoticePayTerm;
    @Comment("체납사유")
    @Column(name = "NPMNT_RSN")
    private String nonPayReason;
    @Comment("무적차량여부")
    @Column(name = "UNRL_VHCL_YN", length = 1)
    private String isUnrlCar;
    @Comment("특수위반장소")
    @Column(name = "SPCL_VLTN_PLC", length = 200)
    private String specialVioPlace;
    @Comment("위반자 주소")
    @Column(name = "VLTR_ADDR", length = 200)
    private String violatorAddr;

    // dto
    public IllegalDto toRes() {
        return IllegalDto.builder()
                .id(this.id)
                .year(this.year)
                .month(this.month)
                .fflng(this.fflng)
                .violationDt(this.violationDt)
                .preNoticeDay(this.preNoticeDay)
                .preNoticePayTerm(this.preNoticePayTerm)
                .violationDtm(this.violationDtm)
                .processStat(this.processStat)
                .evidenceNo(this.evidenceNo)
                .carNo(this.carNo)
                .violationPlace(this.violationPlace)
                .violatorNm(this.violatorNm)
                .residentRegiNo(this.residentRegiNo)
                .schoolZone(this.schoolZone)
                .pic(this.pic)
                .requestData(this.requestData)
                .isRll(this.isRll)
                .code1(this.code1)
                .code2(this.code2)
                .violatedLaw(this.violatedLaw)
                .carType(this.carType)
                .carNm(this.carNm)
                .capacity(this.capacity)
                .inspectorNm(this.inspectorNm)
                .cameraNm(this.cameraNm)
                .etc(this.etc)
                .comment(this.comment)
                .fflngNm(this.fflngNm)
                .legalStat(this.legalStat)
                .crdnPtn(this.crdnPtn)
                .nonPayReason(this.nonPayReason)
                .isUnrlCar(this.isUnrlCar)
                .specialVioPlace(this.specialVioPlace)
                .violatorAddr(this.violatorAddr)
                .createDtm(createDtm2Str(this.createDtm))
                .build();
    }


}
