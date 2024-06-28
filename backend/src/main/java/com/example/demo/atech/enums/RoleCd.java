package com.example.demo.atech.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleCd {
    /*
    시스템 권한 관리가 없는 프로젝트라서 헷갈려서 만든 롤 이넘
    시 관리자가 최고 관리자.
    담당자(실무자)는 데이터 업로드 주체, 열람자는 오직 조회만 가능합니다.
    https://docs.google.com/spreadsheets/d/1s4GdzxTkfNQlRltZjzHYJcwzP4CdxQkTXD6Q4VbON-I/edit#gid=0
     */
    ROLE_1ST("시 관리자"), ROLE_2ND("구군 관리자"), ROLE_3RD("담당자"), ROLE_ELSE("열람자");

    private final String role;
}
