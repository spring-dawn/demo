package com.example.demo.domain.data.research.shp;

import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

//@ToString
@Getter // do not create/use `setter` on Entity class
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
@Table(name = "DIM_RSCH_SHP_OPTION")
public class ShpResultOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OPT_NO")
    //@Column(name = "OPTION_NO")
    @Comment("옵션_번호")
    private Long optionNo;

    @Column(name = "TYPE2")
    //@Column(name = "SUB_TYPE")
    @Comment("구분2")
    private String subType;

    @Column(name = "COLOR")
    @Comment("색상")
    private String color;

    @Column(name = "ICON")
    @Comment("아이콘")
    private String icon;

    @Column(name = "Z_INDEX")
    @Comment("zIndex")
    private String zIndex;
}
