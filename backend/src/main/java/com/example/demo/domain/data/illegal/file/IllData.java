package com.example.demo.domain.data.illegal.file;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.dto.data.illegal.IllDataDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.atech.MyUtil.createDtm2Str;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIM_ILGL_PK_DATA_T", indexes = {
        @Index(name = "DIM_ILGL_PK_DATA_IX01", columnList = "YR"),
        @Index(name = "DIM_ILGL_PK_DATA_IX02", columnList = "MM"),
        @Index(name = "DIM_ILGL_PK_DATA_IX03", columnList = "SGG_CD")
})
public class IllData extends CommonEntity {
    /*
    데이터관리/불법주정차 단속/파일 업로드
    업로드 된 파일과 파생된 원천데이터는 1:N 으로 on delete cascade 적용합니다.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ILGL_PK_DATA_NO")
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

    @Comment("불법주정차 단속 업로드 파일 분류. 1: 단속실적, 2: 적발대장, 3: 징수건수(임시)...")
    @Column(name = "DATA_TYPE", length = 2)
    private String dataType;

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

    // IllData : FileInfo = 1 : N
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "illData")
    private List<FileInfo> attaches = new ArrayList<>();


    // update
    public void addAttaches(List<FileInfo> attaches) {
        this.attaches = attaches;
    }

    public void updateCollectYn(String collectYn) {
        this.collectYn = collectYn;
    }

    public void update(IllDataDto.Req req) {
        year = req.getYear();
        month = req.getMonth();
        sggCd = req.getSggCd();
        dataNm = req.getDataNm();
        comment = req.getComment();
    }

    // dto
    public IllDataDto toRes() {
        // 딸린 데이터는 표시하지 않음
        return IllDataDto.builder()
                .id(id)
                .year(year)
                .month(month)
                .sggCd(sggCd)
                .dataNm(dataNm)
                .comment(comment)
                .collectYn(collectYn)
                .files(attaches == null ?
                        null : attaches.stream().map(FileInfo::toFileInfoRes).collect(Collectors.toList()))
                .createId(createId)
                .createDtm(createDtm2Str(createDtm))
                .build();
    }
}
