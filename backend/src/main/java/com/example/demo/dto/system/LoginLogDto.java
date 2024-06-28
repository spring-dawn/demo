package com.example.demo.dto.system;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;

@Data
@Builder
@AllArgsConstructor
public class LoginLogDto {
    // response
    private Long id;
    private String userId;
    private String userNm;
    private String dept;
    private String agency;
    private String createDtm;

    // insert, update
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Req {
        private Long id;
        private String userId;
        private String userNm;
        private String dept;
        private String agency;
    }

    public static class Keyword {
        private String userId;
        private String userNm;
        private String dept;
        private String agency;
    }

}
