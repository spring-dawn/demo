package com.example.demo.domain.data.illegal;


import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.illegal.pk.IllBusMountedPk;
import com.example.demo.domain.data.illegal.pk.IllMobilePk;
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
@Table(name = "DIM_ILL_BUS_MOUNTED_T") // 버스탑재형
@IdClass(IllBusMountedPk.class)
public class IllBusMounted extends CommonEntity {

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

    @Column(name = "crdn_prd")
    @Comment("단속시간")
    private String crdnRrd;

    @Column(name = "crdn_crtr_m")
    @Comment("단속기준(분)")
    private String crdnCrtrM;

    @Column(name = "crdnNocs")
    @Comment("단속건수")
    private Integer crdnNocs;

    @Column(name = "CRDN_CAR")
    @Comment("승용")
    private Integer crdnCar;

    @Column(name = "CRDN_TAXI")
    @Comment("택시(승용에포함)")
    private Integer crdnTaxi;

    @Column(name = "CRDN_VAN")
    @Comment("승합")
    private Integer crdnVan;

    @Column(name = "CRDN_TRUCK")
    @Comment("화물")
    private Integer crdnTruck;

    @Column(name = "CRDN_ETC")
    @Comment("기타")
    private Integer crdnEtc;

    @Column(name = "LEVY_AMT")
    @Comment("부과액(단위:천원)")
    private Integer levyAmt;

    @Column(name = "CLCTN_NOCS")
    @Comment("징수건수")
    private Integer clctnNocs;

    @Column(name = "clctn_amt")
    @Comment("징수액")
    private Integer clctnAmt;

    public void initExcelRow1(HashMap<String, String> params, String year) {
        this.crdnRrd = params.get("단속시간");
        this.crdnCrtrM = params.get("단속기준");

        String crdnNocsData = params.get(year + "년");
        Integer crdnNocsDataInt = crdnNocsData == null ? 0 : Integer.parseInt(crdnNocsData.replaceAll(",", ""));
        this.crdnNocs = crdnNocsDataInt;
    }

    public void initExcelRow2(HashMap<String, String> params, String year) {
        this.crdnCar = params.get("승용") == null ? 0 :  Integer.parseInt(params.get("승용").replaceAll(",", ""));
        this.crdnVan = params.get("승합") == null ? 0 :  Integer.parseInt(params.get("승합").replaceAll(",", ""));
        this.crdnTruck = params.get("화물") == null ? 0 :  Integer.parseInt(params.get("화물").replaceAll(",", ""));
        this.crdnEtc = params.get("기타") == null ? 0 :  Integer.parseInt(params.get("기타").replaceAll(",", ""));
        this.crdnTaxi = params.get("택시") == null ? 0 :  Integer.parseInt(params.get("택시").replaceAll(",", ""));
    }
}
