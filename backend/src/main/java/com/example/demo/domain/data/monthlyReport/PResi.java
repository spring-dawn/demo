package com.example.demo.domain.data.monthlyReport;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.monthlyReport.pk.PStatusPk;
import com.example.demo.dto.data.monthlyReport.PResiDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIM_PS_RSDT_T")
@IdClass(PStatusPk.class)
public class PResi extends CommonEntity {
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

    @Comment("전월 주차면수")
    @Column(name = "PREV_SPCS")
    private Long prevSpaces;

    @Comment("신규")
    @Column(name = "NEW_SPCS")
    private Long newSpaces;

    @Comment("삭제")
    @Column(name = "DEL_SPCS")
    //@Column(name = "LOST_SPCS")
    private Long lostSpaces;

    @Comment("되살리기")
    @Column(name = "BRBK_SPCS")
    //@Column(name = "RE_SPCS")
    private Long reSpaces;

    @Comment("증감")
    @Column(name = "INCDEC")
    private double variance;

    @Comment("금월 주차면수")
    @Column(name = "THSMM_SPCS")
    private Long thisSpaces;

    @Comment("금월 주차장 면적(㎡)")
    @Column(name = "THSMM_AREA")
    private double thisArea;

    @Comment("주차장 증감 사유별 현황")
    @Column(name = "THSMM_RSN", columnDefinition = "text")
    private String varianceReason;

    @Comment("비고(미사용면수)")
    @Column(name = "NONUSE")
    private double nonUse;

    @Comment("사용가능면수(금월주차면수 - 미사용면수)")
    @Column(name = "INUSE")
    private double inUse;

    // 일괄 삭제 연관관계.
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MNTL_RPT_DATA_NO")
    private MrData mrData;

    // dto
    public PResiDto toRes() {
        return PResiDto.builder()
                .year(this.year)
                .month(this.month)
                .sggCd(this.sggCd)
                .prevSpaces(this.prevSpaces)
                .newSpaces(this.newSpaces)
                .lostSpaces(this.lostSpaces)
                .reSpaces(this.reSpaces)
                .variance(this.variance)
                .thisSpaces(this.thisSpaces)
                .thisArea(this.thisArea)
                .varianceReason(this.varianceReason)
                .nonUse(this.nonUse)
                .inUse(this.inUse)
                .createDtm(this.createDtm == null ? null : this.createDtm.toString().substring(0, 10))
                .build();
    }
}
