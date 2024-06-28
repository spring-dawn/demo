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
public class IllFireplugDto {

    private String year;
    private String month;
    private String sgg;
    private Integer nop;
    private Integer crdnYmd;
    private Integer beforeCrdnNocs;
    private Integer crdnNocs;
    private Integer afterCrdnNocs;

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
