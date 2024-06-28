package com.example.demo.dto.system;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserDto {
    /*
    builder 패턴은 매개값이 4개 이상일 때 효율적입니다.
    Dto 를 너무 많이 분리하면 복잡해질 수 있습니다.
     */

    @Getter
    @Builder
    @AllArgsConstructor
    public static class UserRes {
        private String userId;
        private String userNm;
        private String cellNo;
        private String email;
        private String joinDt;
        private String useYn;
//        private String admYn;
        private String dept;
        private String agency;
        private String pwUpdateDt;
        private String roleReqMsg;
        private RoleDto role;
        // 사용자관리 수정 모달에서 select 박스값 매핑 용도
        private String roleNm;
        private String createDtm;
    }

    // User 검색 시 사용되는 class
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserReq {
        private Long id;
        private String userId;
        private String userNm;
        private String cellNo;
        private String email;
        private String useYn;
//        private String admYn;
        private String agency;
        private String dept;
        private String roleReqMsg;
    }

    // 회원가입
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SignUpReq {
        // 필수
        @NotBlank(message = "아이디는 필수 입력입니다.")
        private String userId;
        private String password;
        private String passwordConfirm;
        @NotBlank(message = "이메일은 필수 입력입니다.")
        private String email;
        // 기타
        private String userNm;
        private String cellNo;  // 입력할 때 14자리로 제한.
        private String dept;
        private String agency;
        private String roleReqMsg;
    }

    /*
     * 로그인
     */
    @Data
    @AllArgsConstructor
    @Builder
    public static class SignInReq {
        private String userId;
        private String password;
    }

    /*
    update
     */
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdateReq {
        private Long id;
        private String userId;
        // validation
        private String password;
        private String passwordConfirm;
        private String passwordNew;
        private String passwordNewConfirm;
        // detail info
        private String userNm;
        private String email;
        private String cellNo;
        private String dept;
        private String agency;
        private String roleNm;
        private String useYn;
    }

}
