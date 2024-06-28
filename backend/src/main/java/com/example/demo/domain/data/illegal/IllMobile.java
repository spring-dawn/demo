package com.example.demo.domain.data.illegal;


import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.illegal.pk.IllFixedPk;
import com.example.demo.domain.data.illegal.pk.IllMobilePk;
import com.example.demo.dto.data.illegal.IllFixedDto;
import com.example.demo.dto.data.illegal.IllMobileDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.HashMap;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Embeddable
@Table(name = "DIM_ILL_MOBILE_T") // 이동식
@IdClass(IllMobilePk.class)
public class IllMobile extends CommonEntity {
    @Id
    @Column(name = "YEAR")
    @Comment("년도")
    private String year;

    @Id
    @Column(name = "MONTH")
    @Comment("월")
    private String month;

    @Id
    @Column(name = "SGG")
    @Comment("구군코드")
    private String sgg;

    @Id
    @Column(name = "PRCHS_YMD")
    @Comment("구입일자")
    private String prchsYmd;

    @Column(name = "SEQ")
    @Comment("연번")
    private String seq;


    @Column(name = "VHCL_NM")
    @Comment("차량")
    private String vhclNm;

    @Column(name = "CRDN_PRD")
    @Comment("단속기간")
    private String crdnPrd;

    @Column(name = "CRDN_CTR_M")
    @Comment("단속기준(분)")
    private String crdnCtrM;

    @Column(name = "CRDN_NOCS")
    @Comment("단속건수")
    private Integer crdnNocs;

    @Column(name = "RMRK")
    @Comment("비고")
    private String rmrk;

    @Column(name = "LEVY_AMT")
    @Comment("부과금액")
    private Integer levyAmt;

    @Column(name = "CLCTN_NOCS")
    @Comment("징수건수")
    private Integer clctnNocs;

    @Column(name = "CLCTN_AMT")
    @Comment("징수금액")
    private Integer clctnAmt;

    public void initExcelRow(HashMap<String, String> params, String year) {
        this.seq = params.get("연번");
        this.vhclNm = params.get("차량");
        this.prchsYmd = params.get("구입일자");
        this.crdnPrd = params.get("단속시간");
        this.crdnCtrM = params.get("단속기준(분)");

        Integer crdnNocsDataInt = null;
        String crdnNocsData = params.get(year + "년");
        crdnNocsData = crdnNocsData.replaceAll("\\s|\\u00A0", "");
        crdnNocsDataInt = crdnNocsData.isEmpty() ? 0 : Integer.parseInt(crdnNocsData.replaceAll(",", ""));

        this.crdnNocs = crdnNocsDataInt;
    }

    public IllMobileDto toRes() {
        return IllMobileDto.builder()
                .seq(this.seq)
                .year(this.year)
                .month(this.month)
                .sgg(this.sgg)
                .vhclNm(this.vhclNm)
                .prchsYmd(this.prchsYmd)
                .crdnPrd(this.crdnPrd)
                .crdnCtrM(this.crdnCtrM)
                .crdnNocs(this.crdnNocs)
                .rmrk(this.rmrk)
                .levyAmt(this.levyAmt)
                .clctnNocs(this.clctnNocs)
                .clctnAmt(this.clctnAmt)
                .build();
    }
}
