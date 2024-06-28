package com.example.demo.domain.data.facility.file;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.data.facility.read.PFOpen;
import com.example.demo.domain.data.facility.read.PFPrivate;
import com.example.demo.dto.data.UploadDataDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIM_PK_FCLT_DATA_T", indexes = {
        @Index(name = "DIM_PK_FCLT_DATA_IX01", columnList = "YR"),
        @Index(name = "DIM_PK_FCLT_DATA_IX02", columnList = "MM"),
        @Index(name = "DIM_PK_FCLT_DATA_IX03", columnList = "SGG_CD")
})
public class PFData extends CommonEntity {
    @Id
    @Comment("주차시설 파일업로드 번호")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK_FCLT_DATA_NO")
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

    @Comment("엑셀 데이터 적재 여부. 데이터 승인.")
    @Column(name = "DATA_YN", length = 1, nullable = false)
    @ColumnDefault("'N'")
    private String collectYn;

    @Comment("주차장 유형 구분. 1:공영노상, 2:공영노외, 3:공영부설, 4:민영노상, 5:민영노외, 6:민영부설, 7:부설, 8:부설개방, 9:사유지개방")
    @Column(name = "PKLT_TYPE", length = 2, nullable = false)
    private String lotType;

    @Comment("데이터 중복 수준 체크. 0:중복 없음, 1:부분 중복, 2:완전 중복(DB화 불가)")
    @Column(name = "DPCN_TYPE", length = 1, nullable = false)
    private String dupType;

    @Comment("중복일 경우 중복위치 안내. 중복 데이터의 주차장명을 우선 표기(확장 가능성 있음)")
    @Column(name = "DPCN_INFO", columnDefinition = "text")
    private String dupInfo;

    // PFData : FileInfo = 1 : N
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "pfData")
    private List<FileInfo> attaches = new ArrayList<>();

//    =========================================== DB화 원본파일이 삭제되면 그에 파생된 DB 데이터 일괄 삭제.
    // PFData : PFPrivate(excel data) = 1 : N
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "pfData")
    private List<PFPrivate> prvList = new ArrayList<>();

    // PFData : PFOpen (excel data) = 1 : N
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "pfData")
    private List<PFOpen> openList = new ArrayList<>();


    /*
    update
     */
    public void addAttaches(List<FileInfo> attaches) {
        this.attaches = attaches;
    }

    public void updateCollectYn(String collectYn) {
        this.collectYn = collectYn;
    }

    // 첨부파일은 변경 못함. 삭제 후 재업로드 권장. -> TODO: 파일 변경 시 기존 데이터 삭제/재업로드
    public void update(UploadDataDto.Req req) {
        this.year = req.getYear();
        this.month = req.getMonth();
        this.sggCd = req.getSggCd();
        this.dataNm = req.getDataNm();
        this.comment = req.getComment();
    }

    /*
    dto
     */
    public UploadDataDto toRes() {
        return UploadDataDto.builder()
                .id(this.id)
                .year(this.year)
                .month(this.month)
                .sggCd(this.sggCd)
                .dataNm(this.dataNm)
                .comment(this.comment)
                .collectYn(this.collectYn)
                .lotType(this.lotType)
                .dupType(this.dupType)
                .dupInfo(this.dupInfo)
                .files(this.attaches == null ?
                        null : this.attaches.stream().map(FileInfo::toFileInfoRes).collect(Collectors.toList()))
                .createDtm(this.createDtm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .createId(this.createId)
                .build();
    }
}
