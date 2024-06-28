package com.example.demo.domain.survey.data;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.dto.data.UploadDataDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.atech.MyUtil.createDtm2Str;

@Getter // do not create/use `setter` on Entity class
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@DynamicInsert
@Entity
@Table(name = "DIM_SRVY_DATA_T", indexes = {
        @Index(name = "DIM_SRVY_DATA_IX01", columnList = "YR"),
        @Index(name = "DIM_SRVY_DATA_IX02", columnList = "SGG_CD")
})
public class RschData extends CommonEntity {
    /*
    실태조사 > 조사자료 > 관리카드 엑셀 파일 업로드 및 DB화
    [24.02.19] 표준단어사전에 따라 '실태조사'를 RESEARCH -> SURVEY(설문) 으로 변경합니다.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SRVY_DATA_NO")
    private Long id;

    @Comment("실시연도 ex)2022")
    @Column(name = "YR", length = 4, nullable = false)
    private String year;

    @Comment("구군")
    @Column(name = "SGG_CD", length = 10, nullable = false)
    private String sggCd;

    @Comment("데이터명")
    @Column(name = "DATA_NM", length = 100)
    private String dataNm;

    @Comment("비고")
    @Column(name = "RMRK", columnDefinition = "text")
    private String comment;

    @Comment("실태조사 관리카드, 조사표 정리 서식 파일 구분. 0: 관리카드, 1: 정리 서식")
    @Column(name = "TYPE", length = 1, nullable = false)
    @ColumnDefault("'0'")
    private String rschType;

    @Comment("엑셀 데이터 적재 여부")
    @Column(name = "DATA_YN", length = 1, nullable = false)
    @ColumnDefault("'N'")
    private String collectYn;


    // RschData : FileInfo = 1 : N
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "rschData")
    private List<FileInfo> attaches = new ArrayList<>();


    // update
    public void addAttaches(List<FileInfo> attaches) {
        this.attaches = attaches;
    }

    public void updateCollectYn(String collectYn) {
        this.collectYn = collectYn;
    }

    public void update(UploadDataDto.Req req){
        this.year = req.getYear();
        this.sggCd = req.getSggCd();
        this.dataNm = req.getDataNm();
        this.comment = req.getComment();
    }

    // dto res
    public UploadDataDto toRes() {
        return UploadDataDto.builder()
                .id(this.id)
                .year(this.year)
                .sggCd(this.sggCd)
                .rschType(this.rschType)
                .dataNm(this.dataNm)
                .comment(this.comment)
                .collectYn(this.collectYn)
                .files(this.attaches == null ?
                        null : this.attaches.stream().map(FileInfo::toFileInfoRes).collect(Collectors.toList()))
                .createDtm(createDtm2Str(this.createDtm))
                .createId(this.createId)
                .build();
    }


}
