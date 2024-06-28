package com.example.demo.domain.data.standardSet;

import com.example.demo.domain.CommonEntity;
import com.example.demo.dto.data.standard.StandardMngDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;

import static com.example.demo.atech.MyUtil.createDtm2Str;
import static org.springframework.util.StringUtils.hasText;

@Getter
@Builder
@DynamicInsert
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "FCT_STD_MNG_LDGR_T", indexes = {
        @Index(name = "FCT_STD_MNG_LDGR_IX01", columnList = "DPCN_CHCK1"),
        @Index(name = "FCT_STD_MNG_LDGR_IX02", columnList = "DPCN_CHCK2"),
        @Index(name = "FCT_STD_MNG_LDGR_IX03", columnList = "DPCN_CHCK3"),
        @Index(name = "FCT_STD_MNG_LDGR_IX04", columnList = "DPCN_CHCK4"),
})
public class StandardMng extends CommonEntity {
    /*
    표준 데이터셋의 ID 관리대장.

    1. 구군 담당자(사용자)가 엑셀 파일 업로드
    2. 일련번호가 없는 행에 대해 중복검사(주차장명, 지번주소, 총주차면수)
    3. 완전중복: 데이터 승인 불가, 부분중복: 관리자 권한으로 승인 가능 -> 파일 엔티티에 표시
    4. 데이터 승인 시 각 원천 데이터와 비교하여 신규 데이터 일련번호 채번(구군 3자리-주차장 유형 1자리+유형별 인덱스 6자리 패딩 000001)
    5. 이후 raw data + 관리대장 데이터(일련번호) 를 표준 데이터셋에 적재
     */

    @Id
    @Comment("일련번호: 구군 3자리-주차장 유형 1자리-유형별 인덱스 6자리 ex)195-1-000001")
    @Column(name = "MNG_NO", length = 20)
    private String id;

    @Comment("구군코드 3자리. 192:중구, 193:남구, 194:동구, 195:북구, 196:울주군")
    @Column(name = "SGG_CD", length = 3)
    private String sggCd;

    @Comment("주차장 유형 1자리. 1:공영노상, 2:공영노외, 3:공영부설, 4:민영노상, 5:민영노외, 6:민영부설, 7:부설, 8:부설개방, 9:사유지개방")
    @Column(name = "PKLT_TYPE", length = 2, nullable = false)
    private String lotType;

    @Comment("주차장 유형별 인덱스 6자리")
    @Column(name = "IDX_PKLT_TYPE", length = 10)
    private String idxByType;   // 자동증가 옵션 쓰면 롤백됐을 때도 1 올라간 걸로 치겠지... 안 되겠네.

    @Comment("연도")
    @Column(name = "YR", length = 4)
    private String year;

    @Comment("월")
    @Column(name = "MM", length = 2)
    private String month;

    // 중복 검사용 컬럼 TODO: 검사 기준은 확정인가?
    @Comment("완전중복 검사용 컬럼. 주차장명+지번주소+총주차면수")
    @Column(name = "DPCN_CHCK1", columnDefinition = "text", nullable = false)
    private String dupChk1;

    @Comment("부분중복 검사용 컬럼. 주차장명+지번주소")
    @Column(name = "DPCN_CHCK2", columnDefinition = "text", nullable = false)
    private String dupChk2;

    @Comment("부분중복 검사용 컬럼. 주차장명+총주차면수")
    @Column(name = "DPCN_CHCK3", columnDefinition = "text", nullable = false)
    private String dupChk3;

    @Comment("부분중복 검사용 컬럼. 지번주소+총주차면수")
    @Column(name = "DPCN_CHCK4", columnDefinition = "text", nullable = false)
    private String dupChk4;

//    @Comment("관리대장에 연결된 원천 데이터 삭제여부. Y:삭제되었음, N:존재")
//    @Column(name = "DELETE_YN", nullable = false, length = 1)
//    @ColumnDefault("'N'")
//    private String deleteYn;

//    update
//    public void updateDeleteYn(String deleteYn){
//        this.deleteYn = deleteYn;
//    }

    //    dto
    public StandardMngDto toRes() {
        return StandardMngDto.builder()
                .id(this.id)
                .sggCd(this.sggCd)
                .lotType(this.lotType)
                .idxByType(this.idxByType)
                .year(this.year)
                .month(this.month)
                .dupChk1(this.dupChk1)
                .dupChk2(this.dupChk2)
                .dupChk3(this.dupChk3)
                .dupChk4(this.dupChk4)
                .createDtm(createDtm2Str(this.createDtm))
                .build();
    }


}
