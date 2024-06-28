package com.example.demo.domain.data.facility;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Id;
import java.io.Serializable;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor
public class FacilityPk implements Serializable {
    /*
    주차시설-민영노외, 개방 주차장 등 공통 복합키
     */
    @Id
    @Comment("실시연도 ex)2022")
    @Column(name = "YR", length = 4, nullable = false)
    private String year;

    @Id
    @Comment("실시월 01~12")
    @Column(name = "MM", length = 2, nullable = false)
    private String month;

    @Id
    @Comment("구군")
    @Column(name = "SGG_CD", length = 10, nullable = false)
    private String sggCd;

    @Id
    @Comment("1:1 연관 표준관리대장 일련번호. 논리적 연결.")
    @Column(name = "MNG_NO", length = 20, nullable = false)
    private String mngNo;

    // 생성자 명시
    public FacilityPk(String year, String month, String sggCd, String mngNo) {
        this.year = year;
        this.month = month;
        this.sggCd = sggCd;
        this.mngNo = mngNo;
    }
}
