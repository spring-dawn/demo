package com.example.demo.dto.data.standard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
public class StandardMngDto {
    private String id;
    private String year;
    private String month;
    private String sggCd;
    private String lotType;
    private String idxByType;
    //
    private String dupChk1;
    private String dupChk2;
    private String dupChk3;
    private String dupChk4;
    private String deleteYn;
    //
    private String createDtm;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        private String id;
        private String year;
        private String month;
        private String sggCd;
        //
        private String lotType;
        //
        private String dupChk1;
        private String dupChk2;
        private String dupChk3;
        private String dupChk4;
        private String deleteYn;
    }





}
