package com.example.demo.domain.data.monthlyReport;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.data.facility.read.PFOpen;
import com.example.demo.domain.data.facility.read.PFPrivate;
import com.example.demo.dto.data.monthlyReport.MrDataDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIM_MR_DATA_T", indexes = {
        @Index(name = "DIM_MR_DATA_IX01", columnList = "YR"),
        @Index(name = "DIM_MR_DATA_IX02", columnList = "MM"),
        @Index(name = "DIM_MR_DATA_IX03", columnList = "SGG_CD")
})
public class MrData extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MNTL_RPT_DATA_NO")
    private Long id;

    @Comment("실시연도 ex)2022")
    @Column(name = "YR", length = 4, nullable = false)
    private String year;

    @Comment("실시월 01~12")
    @Column(name = "MM", length = 2, nullable = false)
    private String month;

    @Comment("구군")
    @Column(name = "SGG_CD", length = 10, nullable = false)
    private String sggCd;

    @Comment("데이터명")
    @Column(name = "DATA_NM", length = 100)
    private String dataNm;

    @Comment("비고")
    @Column(name = "RMRK", columnDefinition = "text")
    private String comment;

    @Comment("엑셀 데이터 적재 여부")
    @Column(name = "DATA_YN", length = 1, nullable = false)
    @ColumnDefault("'N'")
    private String collectYn;

    @Comment("데이터 중복 수준 체크. 0:중복 없음, 1:부분 중복, 2:완전 중복(DB화 불가)")
    @Column(name = "DPCN_TYPE", length = 1, nullable = false)
    private String dupType;

    @Comment("중복일 경우 중복위치 안내. 중복 데이터의 주차장명을 우선 표기(확장 가능성 있음)")
    @Column(name = "DPCN_INFO", columnDefinition = "text")
    private String dupInfo;


    // MrData : FileInfo = 1 : N
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "mrData")
    private List<FileInfo> attaches = new ArrayList<>();

    //    =========================================== DB화 원본파일이 삭제되면 그에 파생된 DB 데이터 일괄 삭제.
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "mrData")
    private List<PPublic> publicList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "mrData")
    private List<PResi> resiList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "mrData")
    private List<PStatus> statusList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "mrData")
    private List<PSubDcrs> subDcrsList = new ArrayList<>();

    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "mrData")
    private List<PSubIncrs> subIncrsList = new ArrayList<>();
    //    ===========================================

    // update
    public void addAttaches(List<FileInfo> attaches) {
        this.attaches = attaches;
    }

    public void updateCollectYn(String collectYn) {
        this.collectYn = collectYn;
    }
    //public void update(MrDataDto.Req req){
    //    this.year = req.getYear();
    //    this.month = req.getMonth();
    //    this.sggCd = req.getSggCd();
    //    this.dataNm = req.getDataNm();
    //    this.comment = req.getComment();
    //}

    public void update(MrDataDto.Req req){
        this.year = req.getYear();
        this.month = req.getMonth();
        this.sggCd = req.getSggCd();
        this.dataNm = req.getDataNm();
        this.comment = req.getComment();
    }

    // dto
    public MrDataDto toRes() {
        return MrDataDto.builder()
                .id(this.id)
                .year(this.year)
                .month(this.month)
                .sggCd(this.sggCd)
                .dataNm(this.dataNm)
                .comment(this.comment)
                .collectYn(this.collectYn)
                .dupType(this.dupType)
                .dupInfo(this.dupInfo)
                .files(this.attaches == null ?
                        null : this.attaches.stream().map(FileInfo::toFileInfoRes).collect(Collectors.toList()))
                //.attaches(this.attaches == null ?
                //        null : this.attaches.stream().map(FileInfo::toFileInfoRes).collect(Collectors.toList()))
                .createDtm(this.createDtm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .createId(this.createId)
                .build();
    }

}
