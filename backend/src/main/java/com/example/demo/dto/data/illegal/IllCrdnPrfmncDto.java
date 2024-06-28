package com.example.demo.dto.data.illegal;


import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.data.illegal.pk.IllCrdnPrfmncPk;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IllCrdnPrfmncDto{
    private String year;
    private String month;
    private String sgg;
    private String gubun;
    private Integer crdnNocs;
    private Integer levyAmt;
    private Integer clctnNocs;
    private Integer clctnAmt;
    private Double clctnRate;
    private Integer crdnNope;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Keyword{
        private String year;
        private String month;
    }
}
