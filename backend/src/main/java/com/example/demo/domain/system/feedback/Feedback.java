package com.example.demo.domain.system.feedback;

import com.example.demo.domain.CommonEntity;
import com.example.demo.dto.system.FeedbackDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

import static com.example.demo.atech.MyUtil.createDtm2Str;

@Getter // do not create/use `setter` on Entity class
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
@DynamicInsert // insert default value instead of null
@Table(name = "SYS_IMPRV_DMND_T", indexes = {
        @Index(name = "IMPRV_DMND_IX01", columnList = "SGG_CD"),
        @Index(name = "IMPRV_DMND_IX02", columnList = "DEPT")
})
public class Feedback extends CommonEntity {
    /*
    '시스템 개선 요청' 게시판
    sys_improvement_demand_t
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("시스템 개선 요청 번호")
    @Column(name = "IMPRV_DMND_NO")
    private Long id;

    @Comment("구군")
    @Column(name = "SGG_CD", length = 10)
    private String sggCd;

    @Comment("부서")
    @Column(name = "DEPT", length = 20)
    private String dept;

    @Comment("제목. 최대 255자까지.")
    @Column(name = "TTL", nullable = false)
    private String title;

    @Comment("내용")
    @Column(name = "CONTS", nullable = false, columnDefinition = "text")
    private String contents;

    @Comment("처리상태. 0: 요청, 1: 완료")
    @Column(name = "PRCS_STTS", length = 1, nullable = false)
    @ColumnDefault("'0'")
    private String status;

    @Comment("조회수")
    @Column(name = "INQ_CNT")
    private Long hit;


    // update
    public void updateHit() {
        hit += 1;
    }

    public void update(FeedbackDto.Req req) {
        // 그 외 사용자 정보는 사용자 세션에서 가져오므로 수정불가
        title = req.getTitle();
        contents = req.getContents();
    }

    public void updateStatus(String status) {
        this.status = status;
    }

    // dto
    public FeedbackDto toRes() {
        return FeedbackDto.builder()
                .id(id)
                .hit(hit)
                .sggCd(sggCd)
                .dept(dept)
                .title(title)
                .contents(contents)
                .status(status)
                .createId(createId)
                .createDtm(createDtm2Str(createDtm))
                .build();
    }


}
