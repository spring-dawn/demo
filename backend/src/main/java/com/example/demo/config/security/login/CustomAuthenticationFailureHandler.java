package com.example.demo.config.security.login;

import com.example.demo.atech.MyUtil;
import com.example.demo.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;
    private final ResponseDto responseLoginFail;

    public CustomAuthenticationFailureHandler() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        this.objectMapper = builder.build();
        /* 현재 프론트에서 로그인 성공, 실패 여부를 code 로 읽고 있습니다 */
        this.responseLoginFail = new ResponseDto("login_failed", "", "");
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse res, AuthenticationException exception) throws IOException {
        String errMsg = "";

        if (exception instanceof UsernameNotFoundException) {
            errMsg = "없는 사용자입니다.";
        } else if (exception instanceof BadCredentialsException) {
            errMsg = "패스워드가 일치하지 않습니다.";
        } else if (exception instanceof DisabledException) {
            errMsg = "사용할 수 없는 계정입니다. 관리자에 문의하세요.";
        } else if (exception instanceof LockedException) {
            errMsg = "5회 로그인 실패로 잠긴 계정입니다.\n새 비밀번호를 발급해주세요.";
        } else if (exception instanceof AccountExpiredException) {
            errMsg = "가입 승인 대기중입니다. 관리자에 문의하세요.";
        } else {
            errMsg = "예측되지 않은 로그인 오류가 발생했습니다.\n관리자에 문의하세요.";
        }
        res.setContentType("application/json;charset=UTF-8");
        res.setStatus(HttpServletResponse.SC_OK);

        this.responseLoginFail.setMessage(errMsg);
        this.responseLoginFail.setTimestamp(MyUtil.timestamp());
        res.getWriter().write(objectMapper.writeValueAsString(this.responseLoginFail));
    }

}