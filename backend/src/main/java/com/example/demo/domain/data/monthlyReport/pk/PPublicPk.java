package com.example.demo.domain.data.monthlyReport.pk;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class PPublicPk implements Serializable {
    private String year;
    private String month;
    private String sggCd;
    private String mngNo;

}
