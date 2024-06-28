package com.example.demo.dto.data.facility;

import com.example.demo.domain.data.facility.file.PFData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
public class PFOpenDto {
    /*
    사용자 입력값과 response 사이에 차이가 없으면 내부 클래스 생성 없이 단일 dto로 사용해도 좋습니다.
    단일 dto 클래스일 경우 @NoArgsConstructor 가 필요합니다.
     */
//    private Long id;
    private String year;
    private String month;
    private String sggCd;
    // 표준관리대장
    private String mngNo;
    private String seq;
    private String lotType;
    private String lotNm;
    private String address;
    private Long spcs;
    private String area;
    private String openTm;
    private String openDay;
    private String lat;
    private String lng;
    private String createDtm;
    // 중복검사
//    private String dupChk1;
//    private String dupChk2;
//    private String dupChk3;
//    private String dupChk4;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        private String year;
        private String month;
        private String sggCd;
        // 표준관리대장
        private String mngNo;
        private String seq;
        private String lotType;
        private String lotNm;
        private String address;
        private Long spcs;
        private String area;
        private String openTm;
        private String openDay;
        private String lat;
        private String lng;
        // 중복검사
        private String dupChk1;
        private String dupChk2;
        private String dupChk3;
        private String dupChk4;
        // 원본파일 참조
        private PFData pfData;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keyword{
        private String year;
        private String month;
        private String sggCd;
        private String lotNm;
        private String address;
        private Long minSpcs;
        private Long maxSpcs;
    }
}
