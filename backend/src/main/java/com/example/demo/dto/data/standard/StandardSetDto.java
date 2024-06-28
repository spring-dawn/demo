package com.example.demo.dto.data.standard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StandardSetDto {
    private String mngNo;
    private String year;
    private String month;
    private String sggCd;
    private String lotNm;
    private String lotType;     // TODO: 확장 가능성 있음.
    //    private String lotType1;    // 대분류: 공영, 민영, 부설...
//    private String lotType2;    // 중분류: 노상, 노외, 부설...
    private String address;
    private String stAddress;
    private Long totalSpcs;   // 주차구획수(총주차면수)
    private String landRank;   // 급지구분? 1~3
    private String execType;    // 부제(요일제) 시행 구분
    private String workDay;     // 운영요일
    private String weekOpenTm;  // 평일 운영 시작시각
    private String weekCloseTm; // 평일 운영 종료시각
    private String satOpenTm;   // 토요일 운영 시작시각
    private String satCloseTm;  // 토요일 운영 종료시각
    private String holiOpenTm;  // 공휴일 운영 시작시각
    private String holiCloseTm; // 공휴일 운영 종료시각
    private String payInfo; // 요금정보
    private String parkingTm;   // 주차기본시간
    private String parkingPay;  // 주차기본요금
    private String plusTmUnit;  // 추가단위시간
    private String plusPayUnit; // 추가단위요금
    private String payTmByDay; // 1일주차권요금적용시간
    private String payByDay; // 1일주차권요금
    private String payByMonth; // 월정기권요금
    private String howPay;  // 결제 방법
    private String comment;     // 특기사항
    private String agency;  // 관리기관명
    private String agencyTel;   // (관리기관) 전화번호
    private String lat; // 위도
//    private String lng; // 경도
    private String lon; // 경도
    private String hasDisSpcs;  // 장애인전용주차구역보유여부
    // 옥내 자주식(대수), 옥내 기계식 대수, 옥외 자주(식 대수), 옥외 기기(기계식 대수)??
    private String indrAutoUtcnt;
    private String indrMechUtcnt;
    private String outdrAutoUtcnt;
    private String outdrMechUtcnt;
    private LocalDateTime createDtm;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Keyword {
        private String mngNo;
        private String year;
        private String month;
        private String sggCd;
        private String lotType;
        private String lotNm;
        private Long minSpcs;
        private Long maxSpcs;
    }
}
