package com.example.demo.config.security;

import com.example.demo.atech.MyUtil;
import com.example.demo.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

//    private final ResponseDto.ResponseRes response403;
    private final ResponseDto response401;

    public CustomAuthenticationEntryPoint() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        this.objectMapper = builder.build();
//        this.response403 = new ResponseDto("now()","unauthorized", "UnAuthorized");
        this.response401 = new ResponseDto("401 UnAuthorized", "미인증 사용자의 요청입니다.", "");
    }


    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, AuthenticationException authException) throws IOException, ServletException {
        res.setContentType("application/json;charset=UTF-8");
//        res.setStatus(401);
        res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response401.setTimestamp(MyUtil.timestamp());
        res.getWriter().write(objectMapper.writeValueAsString(this.response401));
    }
}