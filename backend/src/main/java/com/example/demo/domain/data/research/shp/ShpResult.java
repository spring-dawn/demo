package com.example.demo.domain.data.research.shp;

import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.system.code.Code;
import com.example.demo.dto.data.research.ShpResultDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@ToString
@Getter // do not create/use `setter` on Entity class
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
@Table(name = "DIM_RSCH_SHP_T")
public class ShpResult {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "result_no_seq_generator")
    @SequenceGenerator(name = "result_no_seq_generator", sequenceName = "result_no_seq", allocationSize = 1)
    @Column(name = "AHVMT_NO")
    //@Column(name = "RESULT_NO")
    @Comment("성과품_번호")
    private Long resultNo;

    @Column(name = "DATA_NM")
    //@Column(name = "NAME")
    @Comment("데이터_명칭")
    private String name;

    @Column(name = "YR")
    //@Column(name = "YEAR")
    @Comment("연도")
    private String year;

    @Column(name = "SGG_CD")
    //@Column(name = "REG_CODE")
    @Comment("시군구_코드")
    private String regCode;

    @Column(name = "SGG_NM")
    //@Column(name = "REG_NAME")
    @Comment("시군구_번호")
    private String regName;

    @Column(name = "TYPE")
    @Comment("구분")
    private String type;

    @Column(name = "TYPE2")
    //@Column(name = "SUB_TYPE")
    @Comment("구분2")
    private String subType;

    @Column(name = "EPSG")
    @Comment("좌표계")
    private String epsg;

    @Column(name = "COLOR")
    @Comment("메인_색상")
    private String color;

    @Column(name = "RMRK")
    @Comment("비고")
    private String rmrk;

    @Column(name = "VIEW_YN")
    @Comment("시각화_여부")
    private String viewYn;

    @Column(name = "CARD_YN")
    @Comment("관리카드_시각화_여부")
    private String cardYn;

    @Column(name = "AHVMT_TBL_NM")
    //@Column(name = "TABLE_NAME")
    @Comment("성과품_테이블_명칭")
    private String tableName;

    @Column(name = "STTS")
    //@Column(name = "STATE")
    @Comment("상태")
    @ColumnDefault("0")
    private Integer state;

    @Column(name = "ERR_MSG")
    //@Column(name = "ERROR")
    @Comment("오류_메시지")
    private String error;

    @Column(name = "FETR_TYPE")
    //@Column(name = "FEATURE_TYPE")
    @Comment("feature_구분")
    private String featureType;

    // RschData : FileInfo = 1 : N
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "rschShp")
    private List<FileInfo> attaches = new ArrayList<>();

    public void addAttaches(List<FileInfo> attaches) {
        this.attaches = attaches;
    }

    public void updateRegCode(Code regCode) {
        this.regCode = regCode.getName();
        this.regName = regCode.getValue();
    }

    public void updateState(Integer state, String msg) {
        this.state = state;
        this.error = msg;
    }

    public void updateTableName(String tablePath) {
        this.tableName = tablePath;
    }

    public void updateType(String featureType) {
        this.featureType = featureType;
    }

    public void update(ShpResultDto.ShpResultReq shpResultReq) {
        this.rmrk = shpResultReq.getRmrk();
        this.color = shpResultReq.getColor();
        this.viewYn = shpResultReq.getViewYn();
        this.cardYn = shpResultReq.getCardYn();
        this.epsg = shpResultReq.getEpsg();
        this.name = shpResultReq.getName();
        this.year = shpResultReq.getYear();
        this.type = shpResultReq.getType();
        this.subType = shpResultReq.getSubType();
    }

    public ShpResultDto.ShpResultRes toShpResultRes(){
        return ShpResultDto.ShpResultRes.builder()
                .resultNo(resultNo)
                .name(name)
                .year(year)
                .regCode(regCode)
                .regName(regName)
                .type(type)
                .subType(subType)
                .epsg(epsg)
                .color(color)
                .rmrk(rmrk)
                .viewYn(viewYn)
                .cardYn(cardYn)
                .tableName(tableName)
                .state(state)
                .featureType(featureType)
                .files(attaches != null ? attaches.stream().map(FileInfo::toFileInfoRes).collect(Collectors.toList()) : null)
                .build();
    }
}
