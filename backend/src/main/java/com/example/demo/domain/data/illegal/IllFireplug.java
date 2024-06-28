package com.example.demo.domain.data.illegal;


import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.illegal.pk.IllFireplugPk;
import com.example.demo.domain.data.illegal.pk.IllMobilePk;
import com.example.demo.dto.data.illegal.IllFireplugDto;
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
@Table(name = "DIM_ILL_FIREPLUG_T") // 소화전
@IdClass(IllFireplugPk.class)
public class IllFireplug extends CommonEntity {
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

    @Column(name = "NOP")
    @Comment("개소수")
    private Integer nop;

    @Column(name = "CRDN_YMD")
    @Comment("단속일자(년월)")
    private Integer crdnYmd;

    @Column(name = "BEFORE_CRDN_NOCS")
    @Comment("단속건수(전월)")
    private Integer beforeCrdnNocs;

    @Column(name = "CRDN_NOCS")
    @Comment("단속건수(금월)")
    private Integer crdnNocs;

    @Column(name = "AFTER_CRDN_NOCS")
    @Comment("단속건수(누계)")
    private Integer afterCrdnNocs;

    public void initExcelRow(HashMap<String, String> params, String year) {
        this.nop = params.get("개소수") == null || params.get("개소수").isEmpty() ? 0 :  Integer.parseInt(params.get("개소수").replaceAll(",", ""));
        this.beforeCrdnNocs = params.get("전월") == null || params.get("전월").isEmpty() ? 0 :  Integer.parseInt(params.get("전월").replaceAll(",", ""));
        this.crdnNocs = params.get("금월") == null || params.get("금월").isEmpty() ? 0 :  Integer.parseInt(params.get("금월").replaceAll(",", ""));
        this.afterCrdnNocs = params.get("누계") == null || params.get("누계").isEmpty() ? 0 :  Integer.parseInt(params.get("누계").replaceAll(",", ""));
    }

    public IllFireplugDto toRes() {
        return IllFireplugDto.builder()
                .year(this.year)
                .month(this.month)
                .sgg(this.sgg)
                .nop(this.nop)
                .crdnYmd(this.crdnYmd)
                .beforeCrdnNocs(this.beforeCrdnNocs)
                .crdnNocs(this.crdnNocs)
                .afterCrdnNocs(this.afterCrdnNocs)
                .build();
    }
}
