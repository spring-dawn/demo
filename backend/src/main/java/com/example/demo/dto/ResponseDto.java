package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
//@NoArgsConstructor
public class ResponseDto {
    /*
    전역 응답 dto
     */

//    private int status;    // 상태
    private String code;        // ?
    private String message;     // 결과 메시지
    private String timestamp;   // 로그 시간

}