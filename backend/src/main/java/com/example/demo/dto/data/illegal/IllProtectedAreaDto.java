package com.example.demo.dto.data.illegal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
public class IllProtectedAreaDto {

    private String year;
    private String month;
    private String sgg;
    private Integer levyYmd;
    private Integer nocs;
    private Integer amt;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Keyword {
        private String year;
        private String month;
        private String sggCd;
    }
}
