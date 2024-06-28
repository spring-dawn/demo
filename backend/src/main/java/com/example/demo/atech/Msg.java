package com.example.demo.atech;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Msg {
    /*
    시스템 전반에서 공통으로 쓰일 코드, 안내 메시지 등
    String.format(enum.getMsg(), "내용") 형식으로 반복되는 메시지를 재사용할 수 있습니다. -> [MyUtil.]getEnum() 사용.
     */
    COMMON("오류가 있습니다. 담당자에 문의하세요."),
    NOT_SUPPORT_TYPE("아직 지원하지 않는 데이터 유형입니다.\n다른 유형을 우선 선택해주세요."),

    // 엔티티 CRUD 기본 예외 메시지
    ENTITY_NOT_FOUND("해당 %s을(를) 찾을 수 없습니다."),
    ALREADY_EXISTS("이미 존재하는 %s입니다."),
    ALREADY_USE("이미 사용 중인 %s입니다."),
    OUT_DOMAIN("입력된 내용이 올바르지 않습니다. 입력 양식을 확인해주세요."),
    DATA_INTEGRITY("데이터 무결성 위반입니다. 다시 확인해주세요."),
    EMPTY_RESULT("조회 결과가 없습니다."),
    NPE("필수 입력이 비어 있습니다. 다시 확인해주세요."),
    MISSING("%s 적재 중 누락 발생하여 롤백합니다. 다시 시도해주세요."),
    NOT_MATCH("%s(이)가 일치하지 않습니다."),

    INSERT_ERR("등록 중 오류가 발생했습니다."),
    UPDATE_ERR("수정 중 오류가 발생했습니다."),
    DELETE_ERR("삭제 중 오류가 발생했습니다."),
    CANNOT_DELETE("이미 승인된 데이터의 원본은 삭제할 수 없습니다."),

    // 로그인 & 사용자관리
    NO_ACCESS("접근 권한이 없습니다."),
    PW_DIFF("비밀번호와 비밀번호 확인이 일치하지 않습니다."),
    PW_INCORRECT("비밀번호가 일치하지 않습니다."),

    // 파일 처리 유틸
    NO_FILES("파일이 발견되지 않았습니다."),
    RM_ERR("%s 삭제 중 문제가 있습니다. 다시 시도해주세요."),
    FILE_EXT_INCORRECT("올바르지 않은 파일 확장자입니다."),

    // 데이터관리
    NOT_MINE("자신이 업로드 한 데이터만 승인할 수 있습니다."),
    ABSOLUTE_DUP("완전 중복 데이터는 승인할 수 없습니다."),
    NOT_STANDARD_NM("표준 업로드 파일이 아닙니다.\n파일명이 [%s] 인지 확인해주세요."),
    DUP_CHK_ERR("중복 검사 중 오류가 발생했습니다."),
    COLLECT_DATA_ERR("데이터 승인 중 오류가 발생했습니다. 엑셀 입력이 표준 양식과 일치하는지 확인해주세요."),
    MANAGER_SESSION_EXPIRED("담당자 정보를 확인할 수 없습니다. 다시 로그인해주세요."),
    ONLY_MANAGER("구군 담당자 전용입니다."),
    CANNOT_UPDATE("변경할 수 있는 올바른 상태가 아닙니다."),
    ;

    // 공통 인자
    private final String msg;
}
