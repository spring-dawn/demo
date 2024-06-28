package com.example.demo.config.security.login;

import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.domain.system.user.User;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.dto.ResponseDto;
import com.example.demo.dto.system.RoleDto;
import com.example.demo.dto.system.UserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.demo.atech.MyUtil.createDtm2Str;
import static com.example.demo.atech.MyUtil.getEnum;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final UserRepository userRepo;
    private final ObjectMapper objectMapper;
//
////    private final ResponseDto responseLoginSuccess;
//    private final LoginSuccessDto res;
//
//    public CustomAuthenticationSuccessHandler() {
//        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
//        this.objectMapper = builder.build();
////        this.responseLoginSuccess = new ResponseDto("login_success", "로그인 성공", "");
//    }

    @Override
    @Transactional(readOnly = true)
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse res, Authentication authentication) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(HttpServletResponse.SC_OK);

        // 엔티티 인스턴스(ex User)를 조회하려면 영속성 컨텍스트를 타야 하므로 @Transactional(readOnly = true) 설정이 필요합니다
        User user = userRepo.findByUserId(authentication.getPrincipal().toString())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, "사용자")));
        UserDto.UserRes dto = user.toRes();

        LoginSuccessDto successDto = LoginSuccessDto.builder()
                .code("login_success")
                .message("로그인 성공")
                .timestamp(MyUtil.timestamp())
                //
                .user(dto)
                .userId(user.getUserId())
                .userNm(user.getUserNm())
                .cellNo(user.getCellNo())
                .email(user.getEmail())
                .joinDt(createDtm2Str(user.getCreateDtm()))
                .dept(user.getDept())
                .agency(user.getAgency())
                .roleId(user.getRole().getId())
                .roleNm(user.getRole().getName())
                .roleEncodedNm(user.getRole().getEncodedNm())
                .build();
        //
        res.getWriter().write(objectMapper.writeValueAsString(successDto));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class LoginSuccessDto {
        private String code;
        private String message;
        private String timestamp;
        //
        private UserDto.UserRes user;
        private String userId;
        private String userNm;
        private String cellNo;
        private String email;
        private String joinDt;
        private String useYn;
        private String admYn;
        private String dept;
        private String agency;
        private Long roleId;
        private String roleNm;
        private String roleEncodedNm;
    }

}