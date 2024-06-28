package com.example.demo.domain.data.residentXy;

import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.system.code.Code;
import com.example.demo.dto.data.research.ShpResultDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

//@ToString
@Getter // do not create/use `setter` on Entity class
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
@Table(name = "DIM_RESI_XY_T")
public class ResidentXy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RESI_PKLT_PSTN_NO")
    //@Column(name = "RESI_XY_NO")
    @Comment("거주자 PK")
    private Long resiXyNo;

    @Column(name = "SGG")
    //@Column(name = "SGG")
    @Comment("소속구군")
    private String sgg;

    @Column(name = "GROUP_LABEL")
    //@Column(name = "GROUP_LABEL")
    @Comment("구획라벨")
    private String groupLabel;

    @Column(name = "DONG")
    //@Column(name = "DONG")
    @Comment("소속동")
    //@Comment("소속동")
    private String dong;

    @Column(name = "DONG_TELNO")
    //@Column(name = "DONG_CELL_NUM")
    @Comment("동전화번호")
    private String dongCellNum;

    @Column(name = "OGDP_LABEL")
    //@Column(name = "AREA_LABEL")
    @Comment("소속구간")
    private String areaLabel;

    @Column(name = "SEQ")
    //@Column(name = "SEQ")
    @Comment("정렬순서")
    private Integer seq;

    @Column(name = "USE_TYPE")
    //@Column(name = "USE_TYPE")
    @Comment("용도구분")
    private String useType;

    @Column(name = "TRGT_TYPE")
    //@Column(name = "CHARACTERISTICS_TYPE")
    @Comment("대상특성")
    private String characteristicsType;

    @Column(name = "BILL_TYPE")
    //@Column(name = "CHARGING_TYPE")
    @Comment("과금구분")
    private String chargingType;

    @Column(name = "USE_START_DAY")
    //@Column(name = "USE_TIME_START")
    @Comment("사용 시작일")
    private String useStartDay;

    @Column(name = "USE_END_DAY")
    //@Column(name = "USE_TIME_END")
    @Comment("사용 종료일")
    private String useEndDay;

    @Column(name = "XU_START_DAY")
    //@Column(name = "PRIVATE_TIME_START")
    @Comment("전용 시작일")
    private String privateStartTime;

    @Column(name = "XU_END_DAY")
    //@Column(name = "PRIVATE_TIME_END")
    @Comment("전용 종료일")
    private String privateEndTime;

    @Column(name = "XU_USE_HR")
    //@Column(name = "PRIVATE_USE_TIME")
    @Comment("전용사용시간")
    private String privateUseTime;

    @Column(name = "CSTRN_START_DAY")
    //@Column(name = "WORK_START_TIME")
    @Comment("공사시작일")
    private String workStartTime;

    @Column(name = "CSTRN_END_DAY")
    //@Column(name = "WORK_END_TIME")
    @Comment("공사종료일")
    private String workEndTime;

    @Column(name = "USE_STTS")
    //@Column(name = "USE_STATE")
    @Comment("사용상태")
    private String useState;

    @Column(name = "PSBLTY_USE_HR")
    //@Column(name = "USE_TIME")
    @Comment("사용가능시간")
    private String useTime;

    @Column(name = "RMRK")
    //@Column(name = "RMRK")
    @Comment("특이사항")
    private String rmrk;

    @Column(name = "LOT")
    //@Column(name = "X")
    @Comment("x")
    private String x;

    @Column(name = "LAT")
    //@Column(name = "Y")
    @Comment("y")
    private String y;

    @Column(name = "ADDR")
    //@Column(name = "ADDR")
    @Comment("지번주소")
    private String addr;

    public void initExcelRow(HashMap<String, String> params) {
        this.groupLabel = params.get("구획라벨");
        this.dong = params.get("소속동");
        this.dongCellNum = params.get("동전화번호");
        this.areaLabel = params.get("소속구간");
        this.seq = Integer.valueOf(params.get("정렬순서"));
        this.useType = params.get("용도구분");
        this.characteristicsType = params.get("대상특성");
        this.chargingType = params.get("과금구분");
        this.useStartDay = params.get("사용시작일");
        this.useEndDay = params.get("사용종료일");
        this.privateStartTime = params.get("전용시작일");
        this.privateEndTime = params.get("전용종료일");
        this.privateUseTime = params.get("전용사용시간");
        this.workStartTime = params.get("공사시작일");
        this.workEndTime = params.get("공사종료일");
        this.useState = params.get("사용상태");
        this.useTime = params.get("사용가능시간");
        this.rmrk = params.get("특이사항");
        this.x = params.get("경도");
        this.y = params.get("위도");
        this.addr = params.get("지번주소");
    }
}
