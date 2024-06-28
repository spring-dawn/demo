package com.example.demo.atech.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum SggCd {
    // 지역구 코드 등 반복되는 문자열을 소스코드에서 편리하게 쓰기 위한 이넘

    MAIN_OFFICE("31000"),
    DONG("31170"),
    NAM("31140"),
    ULJU("31710"),
    BUK("31200"),
    JUNG("31110");

    private final String sggCd;

    @Override
    public String toString() {
        return sggCd;
    }


}
