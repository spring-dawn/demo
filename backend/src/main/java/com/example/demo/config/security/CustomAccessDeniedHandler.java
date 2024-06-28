package com.example.demo.config.security;

import com.example.demo.atech.MyUtil;
import com.example.demo.dto.ResponseDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

//    private final ResponseDto.ResponseRes response403;
    private final ResponseDto response403;

//    private static final Logger LOG = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    public CustomAccessDeniedHandler() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        this.objectMapper = builder.build();

//        this.response403 = new ResponseDto.ResponseRes("now()", "access_denied", "access Denied.", "");
        this.response403 = new ResponseDto("403 Forbidden", "접근 거부되었습니다.", "");
    }

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        res.setContentType("application/json;charset=UTF-8");
//        res.setStatus(403);
        res.setStatus(HttpServletResponse.SC_FORBIDDEN);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {

//            System.out.println("User '" + authentication.getName() +
//                    "' attempted to access the URL: " +
//                    request.getRequestURI());
        }

        response403.setTimestamp(MyUtil.timestamp());
        res.getWriter().write(objectMapper.writeValueAsString(this.response403));
    }
}