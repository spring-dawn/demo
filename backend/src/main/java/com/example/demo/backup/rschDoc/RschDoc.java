//package com.example.demo.backup.rschDoc;
//
//import com.example.demo.domain.CommonEntity;
//import lombok.*;
//import org.hibernate.annotations.Comment;
//
//import javax.persistence.*;
//
//@Getter
//@Builder
//@NoArgsConstructor
//@AllArgsConstructor(access = AccessLevel.PROTECTED)
////@Entity
////@Table(name = "DIM_RSCH_DOC_T", indexes = {
////        @Index(name = "DIM_RSCH_DOC_IX01", columnList = "YR"),
////        @Index(name = "DIM_RSCH_DOC_IX02", columnList = "MONTH"),
////        @Index(name = "DIM_RSCH_DOC_IX03", columnList = "SGG_NM")
////})
//public class RschDoc extends CommonEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "RSCH_DOC_NO")
//    private Long id;
//
//    @Comment("")
//    @Column(name = "YR", nullable = false, length = 4)
//    private String year;
//
//    @Comment("")
//    @Column(name = "MONTH", nullable = false, length = 2)
//    private String month;
//
//    @Comment("")
//    @Column(name = "SGG_NM", nullable = false, length = 10)
//    private String sggNm;
//
//    @Comment("")
//    @Column(name = "TITLE", length = 100)
//    private String title;
//
//    @Comment("")
//    @Column(name = "CN", length = 100)
//    private String content;
//
//    @Comment("")
//    @Column(name = "RMRK", columnDefinition = "text")
//    private String comment;
//
////    @JsonIgnore
////    @OneToMany(mappedBy = "rschDoc", cascade = CascadeType.REMOVE)
////    private List<FileInfo> attaches = new ArrayList<>();
//
//    // update
////    public void addAttach(List<FileInfo> attaches) {
////        this.attaches = attaches;
////    }
//
//    // dto
//    public RschDocDto toRes() {
//        return RschDocDto.builder()
//                .id(this.id)
//                .year(this.year)
//                .month(this.month)
//                .sggNm(this.sggNm)
//                .title(this.title)
//                .content(this.content)
//                .comment(this.comment)
//                .createDtm(this.createDtm.toString())
//                .createId(this.createId)
////                .attaches(this.attaches == null ?
////                        null : this.attaches.stream().map(FileInfo::toFileInfoRes).collect(Collectors.toList())
////                )
//                .build();
//    }
//
//
//}
