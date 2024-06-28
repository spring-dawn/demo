package com.example.demo.domain.common.file;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.illegal.file.IllData;
import com.example.demo.domain.data.monthlyReport.MrData;
import com.example.demo.domain.data.facility.file.PFData;
import com.example.demo.domain.data.research.floorPlan.FloorPlan;
import com.example.demo.domain.data.research.report.Report;
import com.example.demo.domain.survey.data.RschData;
import com.example.demo.domain.data.research.shp.ShpResult;
import com.example.demo.dto.common.FileInfoDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
@Table(name = "FCT_FILE_INFO_T")
public class FileInfo extends CommonEntity {
    @Id
    @Comment("파일 번호")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_no_seq_generator")
    @SequenceGenerator(name = "file_no_seq_generator", sequenceName = "file_no_seq", allocationSize = 1)
    @Column(name = "FILE_NO")
    private Long id;

    @Comment("원본파일명")
    @Column(name = "FILE_NM")
    private String fileNm;

    @Comment("저장된 파일명")
    @Column(name = "FILE_NM_STORED")
    private String fileNmStored;

    @Comment("파일 위치")
    @Column(name = "FILE_PATH")
    private String filePath;

    /*
    1:N 연관관계. 여럿 추가 가능. 연결된 문서 엔티티 개수만큼 FileInfo 에 외래 키 컬럼이 추가되는 형태.
    연관을 맺을 문서 엔티티(@OneToMany)를 추가하고 적절히 DTO 사용
     */
    @Comment("실태조사 관리카드, 정리서식 파일 업로드 번호")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SRVY_DATA_NO")
    private RschData rschData;

    @Comment("실태조사 SHP 파일 업로드 번호")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RSCH_SHP_NO")
    private ShpResult rschShp;

    @Comment("실태조사 보고서 파일 업로드 번호")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RSCH_REPORT_NO")
    private Report rschReport;

    @Comment("월간보고 파일 업로드 번호")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MNTL_RPT_DATA_NO")
    private MrData mrData;

    @Comment("불법주정차 단속 파일 업로드 번호")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ILGL_PK_DATA_NO")
    private IllData illData;

    @Comment("주차시설 파일 업로드 번호")
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PK_FCLT_DATA_NO")
    private PFData pfData;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RSCH_FP_NO")
    private FloorPlan rschFp;

    /*
    update
     */
    public void updateFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void updateRschShp(ShpResult rschShp) {
        this.rschShp = rschShp;
    }


    /*
    Dto
     */
    public FileInfoDto.FileInfoRes toFileInfoRes() {
        return FileInfoDto.FileInfoRes
                .builder()
                .id(this.id)
                .fileNm(this.fileNm)
                .filePath(this.filePath)
                .fileNmStored(this.fileNmStored)
                .build();
    }

}
