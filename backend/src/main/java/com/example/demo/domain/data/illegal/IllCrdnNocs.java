package com.example.demo.domain.data.illegal;


import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.illegal.pk.IllCrdnNocsPk;
import com.example.demo.domain.data.illegal.pk.IllFixedPk;
import com.example.demo.dto.data.illegal.IllCrdnNocsDto;
import com.example.demo.dto.data.illegal.IllCrdnPrfmncDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Embeddable
@Table(name = "DIM_ILL_CRDN_NOCS_T") // 단속 건수 총괄
@IdClass(IllCrdnNocsPk.class)
public class IllCrdnNocs extends CommonEntity {
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
    @Column(name = "GUBUN")
    @Comment("구분")
    private String gubun;

    @Column(name = "CRDN_CAR")
    @Comment("승용")
    private Integer crdnCar;

    @Column(name = "CRDN_VAN")
    @Comment("승합")
    private Integer crdnVan;

    @Column(name = "CRDN_TRUCK")
    @Comment("화물")
    private Integer crdnTruck;

    @Column(name = "SUM")
    @Comment("소계")
    private Integer sum;

    @Column(name = "CRDN_ETC")
    @Comment("기타")
    private Integer crdnEtc;

    @Column(name = "AMT")
    @Comment("금액")
    private Integer amt;

    // dto
    public IllCrdnNocsDto toRes() {
        return IllCrdnNocsDto.builder()
                .year(this.year)
                .month(this.month)
                .sgg(this.sgg)
                .gubun(this.gubun)
                .crdnCar(this.crdnCar)
                .crdnVan(this.crdnVan)
                .crdnTruck(this.crdnTruck)
                .sum(this.sum)
                .crdnEtc(this.crdnEtc)
                .amt(this.amt)
                .build();
    }
}
