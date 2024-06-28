package com.example.demo.dto.data.illegal;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.Comment;

import javax.persistence.Column;
import javax.persistence.Id;

@Data
@Builder
@AllArgsConstructor
public class IllCrdnNocsDto {
    private String year;
    private String month;
    private String sgg;
    private String gubun;
    private Integer crdnCar;
    private Integer crdnVan;
    private Integer crdnTruck;
    private Integer sum;
    private Integer crdnEtc;
    private Integer amt;
}
