package com.example.demo.domain.data.illegal.pk;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class IllCrdnNocsPk implements Serializable{
    private String year;
    private String month;
    private String sgg;
    private String gubun;
}
