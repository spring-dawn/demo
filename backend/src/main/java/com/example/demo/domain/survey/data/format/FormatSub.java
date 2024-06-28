package com.example.demo.domain.survey.data.format;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.survey.data.pk.RschFmPk;
import com.example.demo.dto.survey.format.FormatSubDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@IdClass(RschFmPk.class)
@Table(name = "DIM_TMPLT_ATCHD_T")
public class FormatSub extends CommonEntity {
     /*
     조사표 정리서식: 부설
    pk: (시군)구코드, 연번, 연도
     */

    @Id
    @Comment("실시연도")
    @Column(name = "YR", length = 4)
    private String year;

    @Id
    @Comment("연번")
    @Column(name = "SN", length = 10)
    private String seq;

    @Id
    @Comment("시군구코드")
    @Column(name = "SGG_CD", length = 10)
    private String sggCd;

    @Id
    @Comment("시설형태")
    @Column(name = "FCLT_PTN", length = 1)
    private String typeF;

    @Comment("주차장 구분")
    @Column(name = "PKLT_TYPE", length = 1)
    private String typeP;

    @Comment("조사구역번호")
    @Column(name = "BLCK_NO", length = 10)
    private String blockNo;

    @Comment("행정동코드")
    @Column(name = "DONG_CD", length = 10)
    private String hjdCd;

    @Comment("법정동코드")
    @Column(name = "STDG_CD", length = 10)
    private String bjdCd;

    @Comment("울주군: 행정리코드")
    @Column(name = "LI_CD", length = 10)
    private String hjlCd;

    // 지번 주소 본번(번지), 부번(호). 공영/민영
    @Comment("지번 주소: 본번(번지)")
    @Column(name = "MNO", length = 10)
    private String lotNoAddr1;

    @Comment("지번 주소: 부번(호)")
    @Column(name = "SNO", length = 10)
    private String lotNoAddr2;

    @Comment("건물명(주차장명)")
    @Column(name = "PKLT_NM", length = 100)
    private String name;

    @Comment("주차면수(주차 가능한 수) 전체")
    @Column(name = "SPCS_TOT", length = 10)
    private String spcsTotal;

    @Comment("주거(아파트, 단독주택, 원룸, 빌라 등): 1, 비주거(주거용도 외 건축물): 2")
    @Column(name = "RESI_YN", length = 1)
    private String isResi;

    @Comment("주용도")
    @Column(name = "MAIN_USG", length = 10)
    private String mainUsage;

    @Comment("유/무료. 1: 유료, 2: 무료")
    @Column(name = "PAY_YN", length = 1)
    private String isPay;

    @Comment("요금")
    @Column(name = "CHAG", length = 30)
    private String pay;

    @Comment("법정유지관리-무단용도변경(면수)")
    @Column(name = "UNATHR_USG_CHG_SPCS", length = 10)
    private String chUsage;

    @Comment("법정유지관리-기능 미유지(면수)")
//    @Column(name = "FNCT_NMNTN_SPCS", length = 10)
    @Column(name = "FNCT_UMNTN_SPCS", length = 10)
    private String dysfunc;

    @Comment("경사여부. 0: 해당없음, 1: 경사")
    @Column(name = "SLP_YN", length = 1)
    private String isSlope;

    @Comment("미끄럼 방지시설 설치여부. 0: 미설치, 1: 설치")
    @Column(name = "SLIP_PRVNT_YN", length = 1)
    private String hasNonSlip;

    @Comment("안내표지 설치여부. 0: 미설치, 1: 설치")
    @Column(name = "INFO_SIGN_YN", length = 1)
    private String hasInfoSign;

    // 방범설비 1: 적정, 2: 부적정
    @Comment("방범설비(1: 적정, 2: 부적정)-cctv")
    @Column(name = "FCLT_CCTV_YN", length = 1)
    private String cctv;

    @Comment("방범설비(1: 적정, 2: 부적정)-모니터")
    @Column(name = "FCLT_MNTR_YN", length = 1)
    private String monitor;

    //    @Comment("방범설비(1: 적정, 2: 부적정)-저장장치")
    @Comment("방범설비(1: 적정, 2: 부적정)-저장장치")
    @Column(name = "SCRTY_FCLT_STRG_YN", length = 1)
    private String backup;

    // 주차대수(주/야): 이륜차 외, 이륜차.
    @Comment("주차대수(주간) 이륜차 외")
    @Column(name = "DY_ETC", length = 10)
    private String spcsNon2wD;

    @Comment("주차대수(주간) 이륜차")
    @Column(name = "DY_TWOWH", length = 10)
    private String spcs2wD;

    @Comment("주차대수(야간) 이륜차 외")
    @Column(name = "NGHT_ETC", length = 10)
    private String spcsNon2wN;

    @Comment("주차대수(야간) 이륜차")
    @Column(name = "NGHT_TWOWH", length = 10)
    private String spcs2wN;

    // 참고사항: 도로명주소, 지번, 호(아니 이걸 왜 또?)
    @Comment("참고사항 도로명주소")
    @Column(name = "STNM_ADDR", length = 10)
    private String roadNmAddr;

    @Comment("참고사항 지번")
    @Column(name = "MNO2", length = 10)
    private String roadNmNo1;

    @Comment("참고사항 호")
    @Column(name = "SNO2", length = 10)
    private String roadNmNo2;

    // 구분 ex) 공영유료, 공영무료, 민영무료, 민영유료 등등 문자열... 뭐라고 해야할지 모르겠다.
    @Comment("구분")
    @Column(name = "PKLT_SE", length = 10)
    private String gubun;

    @Comment("남구: 공동주택. 0/1")
    @Column(name = "APTCPX_YN", length = 1)
    private String isApt;


    // dto
    public FormatSubDto toRes() {
        return FormatSubDto.builder()
                .year(this.year)
                .seq(this.seq)
                .sggCd(this.sggCd)
                .typeP(this.typeP)
                .blockNo(this.blockNo)
                .hjdCd(this.hjdCd)
                .bjdCd(this.bjdCd)
                .hjlCd(this.hjlCd)
                .lotNoAddr1(this.lotNoAddr1)
                .lotNoAddr2(this.lotNoAddr2)
                .name(this.name)
                .spcsTotal(this.spcsTotal)
                .isResi(this.isResi)
                .typeF(this.typeF)
                .mainUsage(this.mainUsage)
                .isPay(this.isPay)
                .pay(this.pay)
                .chUsage(this.chUsage)
                .dysfunc(this.dysfunc)
                .isSlope(this.isSlope)
                .hasNonSlip(this.hasNonSlip)
                .hasInfoSign(this.hasInfoSign)
                .cctv(this.cctv)
                .monitor(this.monitor)
                .backup(this.backup)
                .spcsNon2wD(this.spcsNon2wD)
                .spcs2wD(this.spcs2wD)
                .spcsNon2wN(this.spcsNon2wN)
                .spcs2wN(this.spcs2wN)
                .roadNmAddr(this.roadNmAddr)
                .roadNmNo1(this.roadNmNo1)
                .roadNmNo2(this.roadNmNo2)
                .gubun(this.gubun)
                .isApt(this.isApt)
                .build();
    }

}
