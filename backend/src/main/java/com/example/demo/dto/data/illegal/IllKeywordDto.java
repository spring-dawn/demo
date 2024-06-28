package com.example.demo.dto.data.illegal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IllKeywordDto {
    private String year;
    private String month;
    private String sggCd;
}
