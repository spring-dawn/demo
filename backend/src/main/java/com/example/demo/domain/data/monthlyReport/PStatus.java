package com.example.demo.domain.data.monthlyReport;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.monthlyReport.pk.PStatusPk;
import com.example.demo.dto.data.monthlyReport.PStatusDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIM_PS_T")
@IdClass(PStatusPk.class)
/*
public class PStatus extends CommonEntity {

 */
    /*
    Parking Status(PS) 주차장 (확보) 현황
    합계 내용은 클라이언트에서 연산, 사용자가 직접 금월 데이터를 입력합니다.
     */
/*
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PS_NO")
    @Comment("월간현황 PK")
    private Long psNo;

    @Id
    @Comment("실시연도 ex)2022")
    @Column(name = "YR", length = 4, nullable = false)
    private String year;

    @Id
    @Comment("실시월 01, 02~12")
    @Column(name = "MONTH", length = 2, nullable = false)
    private String month;

    @Id
    @Comment("구군")
    @Column(name = "SGG_CD", length = 10, nullable = false)
    private String sggCd;

*/
    /*
   TODO: 사용자 입력 값. String(varchar) 으로 받지 말 것.
   공영노상유료: 개소수 증감, 주차대수 증감, 면적 증감
   공영노상무료: 개소수 증감, 주차대수 증감, 면적 증감
   공영거주자무료: 개소수 증감, 주차대수 증감, 면적 증감
   공영노외유료: 개소수 증감, 주차대수 증감, 면적 증감
   공영노외무료: 개소수 증감, 주차대수 증감, 면적 증감

   민영: 개소수 증감, 주차대수 증감, 면적 증감

   부설 자주식 노면식 개소수/주차대수/면적 증감
   부설 자주식 조립식 ...
   부설 기계식 부속 ...
   부설 기계식 전용 ...

   자가 단독주택 ...
   자가 공동주택 ...
    */
    // 대분류 - 중분류 - 개소수 L/주차대수 S/면적 A - 증 I/감 D
/*
    @Column
    private double PBLRD_PAY_L_I;
    @Column
    private double PBLRD_PAY_L_D;
    @Column
    private double PBLRD_PAY_S_I;
    @Column
    private double PBLRD_PAY_S_D;
    @Column
    private double PBLRD_PAY_A_I;
    @Column
    private double PBLRD_PAY_A_D;

    @Column
    private double PBLRD_FREE_L_I;
    @Column
    private double PBLRD_FREE_L_D;
    @Column
    private double PBLRD_FREE_S_I;
    @Column
    private double PBLRD_FREE_S_D;
    @Column
    private double PBLRD_FREE_A_I;
    @Column
    private double PBLRD_FREE_A_D;

    @Column
    private double PBLRD_RESI_L_I;
    @Column
    private double PBLRD_RESI_L_D;
    @Column
    private double PBLRD_RESI_S_I;
    @Column
    private double PBLRD_RESI_S_D;
    @Column
    private double PBLRD_RESI_A_I;
    @Column
    private double PBLRD_RESI_A_D;

    @Column
    private double PBLOUT_PAY_L_I;
    @Column
    private double PBLOUT_PAY_L_D;
    @Column
    private double PBLOUT_PAY_S_I;
    @Column
    private double PBLOUT_PAY_S_D;
    @Column
    private double PBLOUT_PAY_A_I;
    @Column
    private double PBLOUT_PAY_A_D;

    @Column
    private double PBLOUT_FREE_L_I;
    @Column
    private double PBLOUT_FREE_L_D;
    @Column
    private double PBLOUT_FREE_S_I;
    @Column
    private double PBLOUT_FREE_S_D;
    @Column
    private double PBLOUT_FREE_A_I;
    @Column
    private double PBLOUT_FREE_A_D;

    @Column
    private double PRV_L_I;
    @Column
    private double PRV_L_D;
    @Column
    private double PRV_S_I;
    @Column
    private double PRV_S_D;
    @Column
    private double PRV_A_I;
    @Column
    private double PRV_A_D;


    @Column
    private double SUBSE_SUR_L_I;
    @Column
    private double SUBSE_SUR_L_D;
    @Column
    private double SUBSE_SUR_S_I;
    @Column
    private double SUBSE_SUR_S_D;
    @Column
    private double SUBSE_SUR_A_I;
    @Column
    private double SUBSE_SUR_A_D;


    @Column
    private double SUBSE_MOD_L_I;
    @Column
    private double SUBSE_MOD_L_D;
    @Column
    private double SUBSE_MOD_S_I;
    @Column
    private double SUBSE_MOD_S_D;
    @Column
    private double SUBSE_MOD_A_I;
    @Column
    private double SUBSE_MOD_A_D;

    @Column
    private double SUBAU_ATT_L_I;
    @Column
    private double SUBAU_ATT_L_D;
    @Column
    private double SUBAU_ATT_S_I;
    @Column
    private double SUBAU_ATT_S_D;
    @Column
    private double SUBAU_ATT_A_I;
    @Column
    private double SUBAU_ATT_A_D;

    @Column
    private double SUBAU_PRV_L_I;
    @Column
    private double SUBAU_PRV_L_D;
    @Column
    private double SUBAU_PRV_S_I;
    @Column
    private double SUBAU_PRV_S_D;
    @Column
    private double SUBAU_PRV_A_I;
    @Column
    private double SUBAU_PRV_A_D;

    @Column
    private double OWN_HOME_L_I;
    @Column
    private double OWN_HOME_L_D;
    @Column
    private double OWN_HOME_S_I;
    @Column
    private double OWN_HOME_S_D;
    @Column
    private double OWN_HOME_A_I;
    @Column
    private double OWN_HOME_A_D;

    @Column
    private double OWN_APT_L_I;
    @Column
    private double OWN_APT_L_D;
    @Column
    private double OWN_APT_S_I;
    @Column
    private double OWN_APT_S_D;
    @Column
    private double OWN_APT_A_I;
    @Column
    private double OWN_APT_A_D;

    @Comment("날짜비교용 컬럼")
    @Column(name = "LOCAL_DT")
    private LocalDate localDt;


    // 일괄 삭제 연관관계.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MNTL_RPT_DATA_NO")
    private MrData mrData;


    // update
    // dto
    public PStatusDto toRes() {
        return PStatusDto.builder()
                .year(this.year)
                .month(this.month)
                .sggCd(this.sggCd)
                .PBLRD_PAY_L_I(this.PBLRD_PAY_L_I)
                .PBLRD_PAY_L_D(this.PBLRD_PAY_L_D)
                .PBLRD_PAY_S_I(this.PBLRD_PAY_S_I)
                .PBLRD_PAY_S_D(this.PBLRD_PAY_S_D)
                .PBLRD_PAY_A_I(this.PBLRD_PAY_A_I)
                .PBLRD_PAY_A_D(this.PBLRD_PAY_A_D)
                .PBLRD_FREE_L_I(this.PBLRD_FREE_L_I)
                .PBLRD_FREE_L_D(this.PBLRD_FREE_L_D)
                .PBLRD_FREE_S_I(this.PBLRD_FREE_S_I)
                .PBLRD_FREE_S_D(this.PBLRD_FREE_S_D)
                .PBLRD_FREE_A_I(this.PBLRD_FREE_A_I)
                .PBLRD_FREE_A_D(this.PBLRD_FREE_A_D)
                .PBLRD_RESI_L_I(this.PBLRD_RESI_L_I)
                .PBLRD_RESI_L_D(this.PBLRD_RESI_L_D)
                .PBLRD_RESI_S_I(this.PBLRD_RESI_S_I)
                .PBLRD_RESI_S_D(this.PBLRD_RESI_S_D)
                .PBLRD_RESI_A_I(this.PBLRD_RESI_A_I)
                .PBLRD_RESI_A_D(this.PBLRD_RESI_A_D)
                .PBLOUT_PAY_L_I(this.PBLOUT_PAY_L_I)
                .PBLOUT_PAY_L_D(this.PBLOUT_PAY_L_D)
                .PBLOUT_PAY_S_I(this.PBLOUT_PAY_S_I)
                .PBLOUT_PAY_S_D(this.PBLOUT_PAY_S_D)
                .PBLOUT_PAY_A_I(this.PBLOUT_PAY_A_I)
                .PBLOUT_PAY_A_D(this.PBLOUT_PAY_A_D)
                .PBLOUT_FREE_L_I(this.PBLOUT_FREE_L_I)
                .PBLOUT_FREE_L_D(this.PBLOUT_FREE_L_D)
                .PBLOUT_FREE_S_I(this.PBLOUT_FREE_S_I)
                .PBLOUT_FREE_S_D(this.PBLOUT_FREE_S_D)
                .PBLOUT_FREE_A_I(this.PBLOUT_FREE_A_I)
                .PBLOUT_FREE_A_D(this.PBLOUT_FREE_A_D)
                .PRV_L_I(this.PRV_L_I)
                .PRV_L_D(this.PRV_L_D)
                .PRV_S_I(this.PRV_S_I)
                .PRV_S_D(this.PRV_S_D)
                .PRV_A_I(this.PRV_A_I)
                .PRV_A_D(this.PRV_A_D)
                .SUBSE_SUR_L_I(this.SUBSE_SUR_L_I)
                .SUBSE_SUR_L_D(this.SUBSE_SUR_L_D)
                .SUBSE_SUR_S_I(this.SUBSE_SUR_S_I)
                .SUBSE_SUR_S_D(this.SUBSE_SUR_S_D)
                .SUBSE_SUR_A_I(this.SUBSE_SUR_A_I)
                .SUBSE_SUR_A_D(this.SUBSE_SUR_A_D)
                .SUBSE_MOD_L_I(this.SUBSE_MOD_L_I)
                .SUBSE_MOD_L_D(this.SUBSE_MOD_L_D)
                .SUBSE_MOD_S_I(this.SUBSE_MOD_S_I)
                .SUBSE_MOD_S_D(this.SUBSE_MOD_S_D)
                .SUBSE_MOD_A_I(this.SUBSE_MOD_A_I)
                .SUBSE_MOD_A_D(this.SUBSE_MOD_A_D)
                .SUBAU_ATT_L_I(this.SUBAU_ATT_L_I)
                .SUBAU_ATT_L_D(this.SUBAU_ATT_L_D)
                .SUBAU_ATT_S_I(this.SUBAU_ATT_S_I)
                .SUBAU_ATT_S_D(this.SUBAU_ATT_S_D)
                .SUBAU_ATT_A_I(this.SUBAU_ATT_A_I)
                .SUBAU_ATT_A_D(this.SUBAU_ATT_A_D)
                .SUBAU_PRV_L_I(this.SUBAU_PRV_L_I)
                .SUBAU_PRV_L_D(this.SUBAU_PRV_L_D)
                .SUBAU_PRV_S_I(this.SUBAU_PRV_S_I)
                .SUBAU_PRV_S_D(this.SUBAU_PRV_S_D)
                .SUBAU_PRV_A_I(this.SUBAU_PRV_A_I)
                .SUBAU_PRV_A_D(this.SUBAU_PRV_A_D)
                .OWN_HOME_L_I(this.OWN_HOME_L_I)
                .OWN_HOME_L_D(this.OWN_HOME_L_D)
                .OWN_HOME_S_I(this.OWN_HOME_S_I)
                .OWN_HOME_S_D(this.OWN_HOME_S_D)
                .OWN_HOME_A_I(this.OWN_HOME_A_I)
                .OWN_HOME_A_D(this.OWN_HOME_A_D)
                .OWN_APT_L_I(this.OWN_APT_L_I)
                .OWN_APT_L_D(this.OWN_APT_L_D)
                .OWN_APT_S_I(this.OWN_APT_S_I)
                .OWN_APT_S_D(this.OWN_APT_S_D)
                .OWN_APT_A_I(this.OWN_APT_A_I)
                .OWN_APT_A_D(this.OWN_APT_A_D)
                .localDt(this.localDt)
                .createDtm(this.createDtm == null ? null : this.createDtm.toString().substring(0, 10))
                .build();
    }

}*/



public class PStatus extends CommonEntity {
    /*
    Parking Status(PS) 주차장 (확보) 현황
    합계 내용은 클라이언트에서 연산, 사용자가 직접 금월 데이터를 입력합니다.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MNTL_RPT_NO")
    //@Column(name = "PS_NO")
    @Comment("월간현황 PK")
    private Long psNo;

    @Id
    @Comment("실시연도 ex)2022")
    @Column(name = "YR", length = 4, nullable = false)
    private String year;

    @Id
    @Comment("실시월 01, 02~12")
    //@Column(name = "MONTH", length = 2, nullable = false)
    @Column(name = "MM", length = 2, nullable = false)
    private String month;

    @Id
    @Comment("구군")
    @Column(name = "SGG_CD", length = 10, nullable = false)
    private String sggCd;


    /*
   TODO: 사용자 입력 값. String(varchar) 으로 받지 말 것.
   공영노상유료: 개소수 증감, 주차대수 증감, 면적 증감
   공영노상무료: 개소수 증감, 주차대수 증감, 면적 증감
   공영거주자무료: 개소수 증감, 주차대수 증감, 면적 증감
   공영노외유료: 개소수 증감, 주차대수 증감, 면적 증감
   공영노외무료: 개소수 증감, 주차대수 증감, 면적 증감

   민영: 개소수 증감, 주차대수 증감, 면적 증감

   부설 자주식 노면식 개소수/주차대수/면적 증감
   부설 자주식 조립식 ...
   부설 기계식 부속 ...
   부설 기계식 전용 ...

   자가 단독주택 ...
   자가 공동주택 ...
    */
    // 대분류 - 중분류 - 개소수 L/주차대수 S/면적 A - 증 I/감 D

    @Comment("공영 노상 유료 개소수 증가")
    @Column(name = "PBLMN_ROAD_PAY_LT_INCRS")
    private double PBLRD_PAY_L_I;
    @Comment("공영 노상 유료 개소수 감소")
    @Column(name = "PBLMN_ROAD_PAY_LT_DCRS")
    private double PBLRD_PAY_L_D;
    @Comment("공영 노상 유료 주차대수 증가")
    @Column(name = "PBLMN_ROAD_PAY_CNTOM_INCRS")
    private double PBLRD_PAY_S_I;
    @Comment("공영 노상 유료 주차대수 감소")
    @Column(name = "PBLMN_ROAD_PAY_CNTOM_DCRS")
    private double PBLRD_PAY_S_D;
    @Comment("공영 노상 유료 면적 증가")
    @Column(name = "PBLMN_ROAD_PAY_AREA_INCRS")
    private double PBLRD_PAY_A_I;
    @Comment("공영 노상 유료 면적 감소")
    @Column(name = "PBLMN_ROAD_PAY_AREA_DCRS")
    private double PBLRD_PAY_A_D;

    @Comment("공영 노상 무료 개소수 증가")
    @Column(name = "PBLMN_ROAD_FREE_LT_INCRS")
    private double PBLRD_FREE_L_I;
    @Comment("공영 노상 무료 개소수 감소")
    @Column(name = "PBLMN_ROAD_FREE_LT_DCRS")
    private double PBLRD_FREE_L_D;
    @Comment("공영 노상 무료 주차대수 증가")
    @Column(name = "PBLMN_ROAD_FREE_CNTOM_INCRS")
    private double PBLRD_FREE_S_I;
    @Comment("공영 노상 무료 주차대수 감소")
    @Column(name = "PBLMN_ROAD_FREE_CNTOM_DCRS")
    private double PBLRD_FREE_S_D;
    @Comment("공영 노상 무료 면적 증가")
    @Column(name = "PBLMN_ROAD_FREE_AREA_INCRS")
    private double PBLRD_FREE_A_I;
    @Comment("공영 노상 무료 면적 감소")
    @Column(name = "PBLMN_ROAD_FREE_AREA_DCRS")
    private double PBLRD_FREE_A_D;

    @Comment("공영 노상 거주자 개소수 증가")
    @Column(name = "PBLMN_ROAD_RSDT_LT_INCRS")
    private double PBLRD_RESI_L_I;
    @Comment("공영 노상 거주자 개소수 감소")
    @Column(name = "PBLMN_ROAD_RSDT_LT_DCRS")
    private double PBLRD_RESI_L_D;
    @Comment("공영 노상 거주자 주차대수 증가")
    @Column(name = "PBLMN_ROAD_RSDT_CNTOM_INCRS")
    private double PBLRD_RESI_S_I;
    @Comment("공영 노상 거주자 주차대수 감소")
    @Column(name = "PBLMN_ROAD_RSDT_CNTOM_DCRS")
    private double PBLRD_RESI_S_D;
    @Comment("공영 노상 거주자 면적 증가")
    @Column(name = "PBLMN_ROAD_RSDT_AREA_INCRS")
    private double PBLRD_RESI_A_I;
    @Comment("공영 노상 거주자 면적 감소")
    @Column(name = "PBLMN_ROAD_RSDT_AREA_DCRS")
    private double PBLRD_RESI_A_D;

    @Comment("공영 노외 유료 개소수 증가")
    @Column(name = "PBLMN_OFFST_PAY_LT_INCRS")
    private double PBLOUT_PAY_L_I;
    @Comment("공영 노외 유료 개소수 감소")
    @Column(name = "PBLMN_OFFST_PAY_LT_DCRS")
    private double PBLOUT_PAY_L_D;
    @Comment("공영 노외 유료 주차대수 증가")
    @Column(name = "PBLMN_OFFST_PAY_CNTOM_INCRS")
    private double PBLOUT_PAY_S_I;
    @Comment("공영 노외 유료 주차대수 감소")
    @Column(name = "PBLMN_OFFST_PAY_CNTOM_DCRS")
    private double PBLOUT_PAY_S_D;
    @Comment("공영 노외 유료 면적 증가")
    @Column(name = "PBLMN_OFFST_PAY_AREA_INCRS")
    private double PBLOUT_PAY_A_I;
    @Comment("공영 노외 유료 면적 감소")
    @Column(name = "PBLMN_OFFST_PAY_AREA_DCRS")
    private double PBLOUT_PAY_A_D;

    @Comment("공영 노외 무료 개소수 증가")
    @Column(name = "PBLMN_OFFST_FREE_LT_INCRS")
    private double PBLOUT_FREE_L_I;
    @Comment("공영 노외 무료 개소수 감소")
    @Column(name = "PBLMN_OFFST_FREE_LT_DCRS")
    private double PBLOUT_FREE_L_D;
    @Comment("공영 노외 무료 주차대수 증가")
    @Column(name = "PBLMN_OFFST_FREE_CNTOM_INCRS")
    private double PBLOUT_FREE_S_I;
    @Comment("공영 노외 무료 주차대수 감소")
    @Column(name = "PBLMN_OFFST_FREE_CNTOM_DCRS")
    private double PBLOUT_FREE_S_D;
    @Comment("공영 노외 무료 면적 증가")
    @Column(name = "PBLMN_OFFST_FREE_AREA_INCRS")
    private double PBLOUT_FREE_A_I;
    @Comment("공영 노외 무료 면적 감소")
    @Column(name = "PBLMN_OFFST_FREE_AREA_DCRS")
    private double PBLOUT_FREE_A_D;

    @Comment("민영 개소수 증가")
    @Column(name = "PVTMN_LT_INCRS")
    private double PRV_L_I;
    @Comment("민영 개소수 감소")
    @Column(name = "PVTMN_LT_DCRS")
    private double PRV_L_D;
    @Comment("민영 주차대수 증가")
    @Column(name = "PVTMN_CNTOM_INCRS")
    private double PRV_S_I;
    @Comment("민영 주차대수 감소")
    @Column(name = "PVTMN_CNTOM_DCRS")
    private double PRV_S_D;
    @Comment("민영 면적 증가")
    @Column(name = "PVTMN_AREA_INCRS")
    private double PRV_A_I;
    @Comment("민영 면적 감소")
    @Column(name = "PVTMN_AREA_DCRS")
    private double PRV_A_D;

    @Comment("부설 자주식 노면식 개소수 증가")
    @Column(name = "ATCHD_SFPRPL_SRFC_LT_INCRS")
    private double SUBSE_SUR_L_I;
    @Comment("부설 자주식 노면식 개소수 감소")
    @Column(name = "ATCHD_SFPRPL_SRFC_LT_DCRS")
    private double SUBSE_SUR_L_D;
    @Comment("부설 자주식 노면식 주차대수 증가")
    @Column(name = "ATCHD_SFPRPL_SRFC_CNTOM_INCRS")
    private double SUBSE_SUR_S_I;
    @Comment("부설 자주식 노면식 주차대수 감소")
    @Column(name = "ATCHD_SFPRPL_SRFC_CNTOM_DCRS")
    private double SUBSE_SUR_S_D;
    @Comment("부설 자주식 노면식 면적 증가")
    @Column(name = "ATCHD_SFPRPL_SRFC_AREA_INCRS")
    private double SUBSE_SUR_A_I;
    @Comment("부설 자주식 노면식 면적 감소")
    @Column(name = "ATCHD_SFPRPL_SRFC_AREA_DCRS")
    private double SUBSE_SUR_A_D;

    @Comment("부설 자주식 자주식 개소수 증가")
    @Column(name = "ATCHD_SFPRPL_ASBL_LT_INCRS")
    private double SUBSE_MOD_L_I;
    @Comment("부설 자주식 자주식 개소수 감소")
    @Column(name = "ATCHD_SFPRPL_ASBL_LT_DCRS")
    private double SUBSE_MOD_L_D;
    @Comment("부설 자주식 자주식 주차대수 증가")
    @Column(name = "ATCHD_SFPRPL_ASBL_CNTOM_INCRS")
    private double SUBSE_MOD_S_I;
    @Comment("부설 자주식 자주식 주차대수 감소")
    @Column(name = "ATCHD_SFPRPL_ASBL_CNTOM_DCRS")
    private double SUBSE_MOD_S_D;
    @Comment("부설 자주식 자주식 면적 증가")
    @Column(name = "ATCHD_SFPRPL_ASBL_AREA_INCRS")
    private double SUBSE_MOD_A_I;
    @Comment("부설 자주식 자주식 면적 감소")
    @Column(name = "ATCHD_SFPRPL_ASBL_AREA_DCRS")
    private double SUBSE_MOD_A_D;

    @Comment("부설 기계식 부속 개소수 증가")
    @Column(name = "ATCHD_MCNCL_ANX_LT_INCRS")
    private double SUBAU_ATT_L_I;
    @Comment("부설 기계식 부속 개소수 감소")
    @Column(name = "ATCHD_MCNCL_ANX_LT_DCRS")
    private double SUBAU_ATT_L_D;
    @Comment("부설 기계식 부속 주차대수 증가")
    @Column(name = "ATCHD_MCNCL_ANX_CNTOM_INCRS")
    private double SUBAU_ATT_S_I;
    @Comment("부설 기계식 부속 주차대수 감소")
    @Column(name = "ATCHD_MCNCL_ANX_CNTOM_DCRS")
    private double SUBAU_ATT_S_D;
    @Comment("부설 기계식 부속 면적 증가")
    @Column(name = "ATCHD_MCNCL_ANX_AREA_INCRS")
    private double SUBAU_ATT_A_I;
    @Comment("부설 기계식 부속 면적 감소")
    @Column(name = "ATCHD_MCNCL_ANX_AREA_DCRS")
    private double SUBAU_ATT_A_D;

    @Comment("부설 기계식 전용 개소수 증가")
    @Column(name = "ATCHD_MCNCL_PRVT_LT_INCRS")
    private double SUBAU_PRV_L_I;
    @Comment("부설 기계식 전용 개소수 감소")
    @Column(name = "ATCHD_MCNCL_PRVT_LT_DCRS")
    private double SUBAU_PRV_L_D;
    @Comment("부설 기계식 전용 주차대수 감소")
    @Column(name = "ATCHD_MCNCL_PRVT_CNTOM_INCRS")
    private double SUBAU_PRV_S_I;
    @Comment("부설 기계식 전용 주차대수 증가")
    @Column(name = "ATCHD_MCNCL_PRVT_CNTOM_DCRS")
    private double SUBAU_PRV_S_D;
    @Comment("부설 기계식 전용 면적 증가")
    @Column(name = "ATCHD_MCNCL_PRVT_AREA_INCRS")
    private double SUBAU_PRV_A_I;
    @Comment("부설 기계식 전용 면적 감소")
    @Column(name = "ATCHD_MCNCL_PRVT_AREA_DCRS")
    private double SUBAU_PRV_A_D;

    @Comment("자가 단독주택 개소수 증가")
    @Column(name = "OWNR_HOME_LT_INCRS")
    private double OWN_HOME_L_I;
    @Comment("자가 단독주택 개소수 감소")
    @Column(name = "OWNR_HOME_LT_DCRS")
    private double OWN_HOME_L_D;
    @Comment("자가 단독주택 주차대수 증가")
    @Column(name = "OWNR_HOME_CNTOM_INCRS")
    private double OWN_HOME_S_I;
    @Comment("자가 단독주택 주차대수 감소")
    @Column(name = "OWNR_HOME_CNTOM_DCRS")
    private double OWN_HOME_S_D;
    @Comment("자가 단독주택 면적 증가")
    @Column(name = "OWNR_HOME_AREA_INCRS")
    private double OWN_HOME_A_I;
    @Comment("자가 단독주택 면적 감소")
    @Column(name = "OWNR_HOME_AREA_DCRS")
    private double OWN_HOME_A_D;

    @Comment("자가 공용주택 개소수 증가")
    @Column(name = "OWNR_APT_LT_INCRS")
    private double OWN_APT_L_I;
    @Comment("자가 공용주택 개소수 감소")
    @Column(name = "OWNR_APT_LT_DCRS")
    private double OWN_APT_L_D;
    @Comment("자가 공용주택 주차대수 증가")
    @Column(name = "OWNR_APT_CNTOM_INCRS")
    private double OWN_APT_S_I;
    @Comment("자가 공용주택 주차대수 감소")
    @Column(name = "OWNR_APT_CNTOM_DCRS")
    private double OWN_APT_S_D;
    @Comment("자가 공용주택 면적 증가")
    @Column(name = "OWNR_APT_AREA_INCRS")
    private double OWN_APT_A_I;
    @Comment("자가 공용주택 면적 감소")
    @Column(name = "OWNR_APT_AREA_DCRS")
    private double OWN_APT_A_D;

    @Comment("날짜비교용 컬럼")
    @Column(name = "REG_YM")
    private LocalDate localDt;


    // 일괄 삭제 연관관계.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MNTL_RPT_DATA_NO")
    private MrData mrData;


    // update
    // dto
    public PStatusDto toRes() {
        return PStatusDto.builder()
                .year(this.year)
                .month(this.month)
                .sggCd(this.sggCd)
                .PBLRD_PAY_L_I(this.PBLRD_PAY_L_I)
                .PBLRD_PAY_L_D(this.PBLRD_PAY_L_D)
                .PBLRD_PAY_S_I(this.PBLRD_PAY_S_I)
                .PBLRD_PAY_S_D(this.PBLRD_PAY_S_D)
                .PBLRD_PAY_A_I(this.PBLRD_PAY_A_I)
                .PBLRD_PAY_A_D(this.PBLRD_PAY_A_D)
                .PBLRD_FREE_L_I(this.PBLRD_FREE_L_I)
                .PBLRD_FREE_L_D(this.PBLRD_FREE_L_D)
                .PBLRD_FREE_S_I(this.PBLRD_FREE_S_I)
                .PBLRD_FREE_S_D(this.PBLRD_FREE_S_D)
                .PBLRD_FREE_A_I(this.PBLRD_FREE_A_I)
                .PBLRD_FREE_A_D(this.PBLRD_FREE_A_D)
                .PBLRD_RESI_L_I(this.PBLRD_RESI_L_I)
                .PBLRD_RESI_L_D(this.PBLRD_RESI_L_D)
                .PBLRD_RESI_S_I(this.PBLRD_RESI_S_I)
                .PBLRD_RESI_S_D(this.PBLRD_RESI_S_D)
                .PBLRD_RESI_A_I(this.PBLRD_RESI_A_I)
                .PBLRD_RESI_A_D(this.PBLRD_RESI_A_D)
                .PBLOUT_PAY_L_I(this.PBLOUT_PAY_L_I)
                .PBLOUT_PAY_L_D(this.PBLOUT_PAY_L_D)
                .PBLOUT_PAY_S_I(this.PBLOUT_PAY_S_I)
                .PBLOUT_PAY_S_D(this.PBLOUT_PAY_S_D)
                .PBLOUT_PAY_A_I(this.PBLOUT_PAY_A_I)
                .PBLOUT_PAY_A_D(this.PBLOUT_PAY_A_D)
                .PBLOUT_FREE_L_I(this.PBLOUT_FREE_L_I)
                .PBLOUT_FREE_L_D(this.PBLOUT_FREE_L_D)
                .PBLOUT_FREE_S_I(this.PBLOUT_FREE_S_I)
                .PBLOUT_FREE_S_D(this.PBLOUT_FREE_S_D)
                .PBLOUT_FREE_A_I(this.PBLOUT_FREE_A_I)
                .PBLOUT_FREE_A_D(this.PBLOUT_FREE_A_D)
                .PRV_L_I(this.PRV_L_I)
                .PRV_L_D(this.PRV_L_D)
                .PRV_S_I(this.PRV_S_I)
                .PRV_S_D(this.PRV_S_D)
                .PRV_A_I(this.PRV_A_I)
                .PRV_A_D(this.PRV_A_D)
                .SUBSE_SUR_L_I(this.SUBSE_SUR_L_I)
                .SUBSE_SUR_L_D(this.SUBSE_SUR_L_D)
                .SUBSE_SUR_S_I(this.SUBSE_SUR_S_I)
                .SUBSE_SUR_S_D(this.SUBSE_SUR_S_D)
                .SUBSE_SUR_A_I(this.SUBSE_SUR_A_I)
                .SUBSE_SUR_A_D(this.SUBSE_SUR_A_D)
                .SUBSE_MOD_L_I(this.SUBSE_MOD_L_I)
                .SUBSE_MOD_L_D(this.SUBSE_MOD_L_D)
                .SUBSE_MOD_S_I(this.SUBSE_MOD_S_I)
                .SUBSE_MOD_S_D(this.SUBSE_MOD_S_D)
                .SUBSE_MOD_A_I(this.SUBSE_MOD_A_I)
                .SUBSE_MOD_A_D(this.SUBSE_MOD_A_D)
                .SUBAU_ATT_L_I(this.SUBAU_ATT_L_I)
                .SUBAU_ATT_L_D(this.SUBAU_ATT_L_D)
                .SUBAU_ATT_S_I(this.SUBAU_ATT_S_I)
                .SUBAU_ATT_S_D(this.SUBAU_ATT_S_D)
                .SUBAU_ATT_A_I(this.SUBAU_ATT_A_I)
                .SUBAU_ATT_A_D(this.SUBAU_ATT_A_D)
                .SUBAU_PRV_L_I(this.SUBAU_PRV_L_I)
                .SUBAU_PRV_L_D(this.SUBAU_PRV_L_D)
                .SUBAU_PRV_S_I(this.SUBAU_PRV_S_I)
                .SUBAU_PRV_S_D(this.SUBAU_PRV_S_D)
                .SUBAU_PRV_A_I(this.SUBAU_PRV_A_I)
                .SUBAU_PRV_A_D(this.SUBAU_PRV_A_D)
                .OWN_HOME_L_I(this.OWN_HOME_L_I)
                .OWN_HOME_L_D(this.OWN_HOME_L_D)
                .OWN_HOME_S_I(this.OWN_HOME_S_I)
                .OWN_HOME_S_D(this.OWN_HOME_S_D)
                .OWN_HOME_A_I(this.OWN_HOME_A_I)
                .OWN_HOME_A_D(this.OWN_HOME_A_D)
                .OWN_APT_L_I(this.OWN_APT_L_I)
                .OWN_APT_L_D(this.OWN_APT_L_D)
                .OWN_APT_S_I(this.OWN_APT_S_I)
                .OWN_APT_S_D(this.OWN_APT_S_D)
                .OWN_APT_A_I(this.OWN_APT_A_I)
                .OWN_APT_A_D(this.OWN_APT_A_D)
                .localDt(this.localDt)
                .createDtm(this.createDtm == null ? null : this.createDtm.toString().substring(0, 10))
                .build();
    }

}
