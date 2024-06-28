package com.example.demo.dto.data.monthlyReport;

import com.example.demo.domain.data.monthlyReport.MrData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PPublicDto {
    private Long id;
    private Long seq;
    private String year;
    private String month;
    private String sggCd;
    private String name;
    private String installDt;
    private String location;
    private String wh;
    private String whSaturday;
    private String whHoliday;
    private String dayOff;
    private String payYn;
    private String pay4Hour;
    private String pay4Day;
    private Long totalSpaces;
    private Long spaces;
    private Long forDisabled;
    private Long forLight;
    private Long forPregnant;
    private Long forBus;
    private Long forElectric;
    private String roadYn;
    private String resiYn;
    private String lotType;
    private String owner;
    private String agency;
    private String comment;
    private String createDtm;
    //추가 컬럼 -sks
    private String serial_number;
    private String point_out;
    private double area;
    private Long forEcho;
    private Long forElderly;

    // 중복 검사 컬럼
    private String dupChk1;
    private String dupChk2;
    private String dupChk3;
    private String dupChk4;

    // 표준관리대장
    private String mngNo;
    private String lon;
    private String lat;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Keyword {
        private String year;
        private String month;
        private String sggCd;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        private Long id;
        private Long seq;
        @NotBlank
        @NotNull
        private String year;
        @NotBlank
        @NotNull
        private String month;
        @NotBlank
        @NotNull
        private String sggCd;
        private String name;
        private String installDt;
        private String location;
        private String wh;
        private String whSaturday;
        private String whHoliday;
        private String dayOff;
        private String payYn;
        private String pay4Hour;
        private String pay4Day;
        private Long totalSpaces;
        private Long spaces;
        private Long forDisabled;
        private Long forLight;
        private Long forPregnant;
        private Long forBus;
        private Long forElectric;
        private String roadYn;
        private String resiYn;
        private String lotType;
        private String owner;
        private String agency;
        private String comment;
        private String createDtm;
        //추가 컬럼 -sks
        private String serial_number;
        private String point_out;
        private double area;
        private Long forEcho;
        private Long forElderly;
        // 중복 검사 컬럼
        private String dupChk1;
        private String dupChk2;
        private String dupChk3;
        private String dupChk4;

        private MrData mrData;
        // 표준관리대장
        private String mngNo;
        private String lon;
        private String lat;
    }
}
