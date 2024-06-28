package com.example.demo.domain.data.research.report;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.system.code.Code;
import com.example.demo.dto.data.research.ReportDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@ToString
@Getter // do not create/use `setter` on Entity class
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
@Table(name = "DIM_RSCH_REPORT_T")
public class Report extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RPTP_NO")
    //@Column(name = "REPORT_NO")
    @Comment("보고서_번호")
    private Long reportNo;

    @Column(name = "DATA_NM")
    //@Column(name = "NAME")
    @Comment("데이터_명칭")
    private String name;

    @Column(name = "RMRK")
    @Comment("비고")
    private String rmrk;

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
    @Comment("시군구_명칭")
    private String regName;

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "rschReport")
    private List<FileInfo> attaches = new ArrayList<>();

    // update
    public void addAttaches(List<FileInfo> attaches) {
        this.attaches = attaches;
    }

    public void updateRegCode(Code regCode) {
        this.regCode = regCode.getName();
        this.regName = regCode.getValue();
    }

    public void update(ReportDto.ReportReq reportReq) {
        this.name = reportReq.getName();
        this.rmrk = reportReq.getRmrk();
        this.year = reportReq.getYear();
    }

    public ReportDto.ReportRes toReportRes(){
        return ReportDto.ReportRes.builder()
                .reportNo(reportNo)
                .name(name)
                .year(year)
                .regCode(regCode)
                .regName(regName)
                .rmrk(rmrk)
                .createId(this.createId)
                .createDtm(this.createDtm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .files(attaches != null ? attaches.stream().map(FileInfo::toFileInfoRes).collect(Collectors.toList()) : null)
                .build();
    }
}
