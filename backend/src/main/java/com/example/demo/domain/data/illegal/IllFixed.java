package com.example.demo.domain.data.illegal;


import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.illegal.pk.IllFixedPk;
import com.example.demo.dto.data.illegal.IllFixedDto;
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
@Table(name = "DIM_ILL_FIXED_T") // 고정식
@IdClass(IllFixedPk.class)
public class IllFixed extends CommonEntity {
    @Id
    @Column(name = "SEQ")
    @Comment("연번")
    private String seq;

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

    @Column(name = "CRDN_BRNCH")
    @Comment("단속지점")
    private String crdnBrnch;

    @Column(name = "INSTL_YMD")
    @Comment("설치일자")
    private String instlYmd;

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

    @Column(name = "LON")
    @Comment("x")
    private String lon;

    @Column(name = "LAT")
    @Comment("y")
    private String lat;

    public void initExcelRow(HashMap<String, String> params, String year) {
        this.seq = params.get("연번");
        this.crdnBrnch = params.get("단속지점");
        this.instlYmd = params.get("설치일자");
        this.crdnPrd = params.get("단속시간");
        this.crdnCtrM = params.get("단속");
        this.lon = params.get("경도");
        this.lat = params.get("위도");

        String crdnNocsData = params.get(year + "년");
        Integer crdnNocsDataInt = crdnNocsData.isEmpty() ? 0 : Integer.parseInt(crdnNocsData.replaceAll(",", ""));
        this.crdnNocs = crdnNocsDataInt;
    }

    public IllFixedDto toRes() {
        return IllFixedDto.builder()
                .seq(this.seq)
                .year(this.year)
                .month(this.month)
                .sgg(this.sgg)
                .crdnBrnch(this.crdnBrnch)
                .instlYmd(this.instlYmd)
                .crdnPrd(this.crdnPrd)
                .crdnCtrM(this.crdnCtrM)
                .crdnNocs(this.crdnNocs)
                .rmrk(this.rmrk)
                .levyAmt(this.levyAmt)
                .clctnNocs(this.clctnNocs)
                .clctnAmt(this.clctnAmt)
                .lon(this.lon)
                .lat(this.lat)
                .build();
    }
}
