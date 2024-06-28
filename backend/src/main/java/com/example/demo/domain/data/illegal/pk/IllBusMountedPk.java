package com.example.demo.domain.data.illegal.pk;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class IllBusMountedPk implements Serializable{
    private String year;
    private String month;
    private String sgg;
}
