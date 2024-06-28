package com.example.demo.config.security;

import com.example.demo.config.security.login.CustomAuthenticationFailureHandler;
import com.example.demo.config.security.login.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import javax.servlet.http.HttpSessionEvent;
import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
public class SecurityConfig {
    /*
    주차행정: Java 8, gradle, jpa, SpringBoot, SpringSecurity, react 사용
    리액트가 독립적으로 프론트를 구성하므로 정적 리소스에 대한 접근 제어는 시큐리티에서 설정하지 않습니다.
     */

    /*
    의존 주입
     */
    private final CustomAccessDeniedHandler customAccessDeniedHandler;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    // 성공, 실패 핸들러. 로그인 결과를 커스텀, 클라이언트에 응답할 수 있습니다.
    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;


    /*
    Security Filter Chain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // session 관리하지 않는 단순 api 서버(restful)일 경우 disable
                .httpBasic().disable()

                /*
                요청별 접근 제어
                디테일한 요청일수록 위에, /** 등 포괄적인 요청일수록 아래에 두어 찾는 속도를 높입니다.
                분리된 구조이므로 일부 접근 제어는 프론트에서 담당합니다.
                 */
                .authorizeRequests(config -> config
                                .antMatchers(
                                        "/api/signin",  // 로그인 요청
                                        "/api/system/user/signup",  // 회원가입 요청
                                        "/api/system/user/isDuplicate/**", // 아이디 중복 확인
                                        "/api/system/user/users/whoIam", // 비밀번호 분실 시 신원 확인
                                        "/api/system/code/codes/search/**",
                                        "/api/menus",
                                        "/api/mymenu",

                                        // 개발 중 임시 허용
//                                        "/api/gis/**",
//                                        "/api/hi",
                                        "/api/system/**",
//                                        "/api/file/**",
//                                        "/api/data/**",
//                                        "/api/analy/**",
//                                        "/api/main/**",

                                        // 스웨거 오픈
                                        "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/webjars/**",
//                                "/v2/**",
                                        "/v3/**",
                                        "/swagger-resources/**",
                                        "/api/data/**"
                                ).permitAll()
                                .anyRequest().authenticated()
                        // 뷰(페이지) 접근 제어는 프론트에서 별도 설정.
                )
//                기본 로그인(form-data)
                .formLogin(config -> config
                        .loginProcessingUrl("/api/signin")
                        .usernameParameter("userId")
                        .passwordParameter("password")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                )
//                로그아웃
                .logout(config -> config
                        .logoutUrl("/api/signout")
                        .deleteCookies("JSESSIONID", "remember-me")
                        .invalidateHttpSession(true)
                )
//                세션 관리
                .sessionManagement(config -> config
                        .sessionFixation().changeSessionId()
                        .maximumSessions(1) // 최대 허용 가능 세션 수 (-1 : 무제한)
                )
//                자동 로그인: 유지시간 디폴트 14일.
                .rememberMe(config -> config
                                .rememberMeParameter("autoLogin")
                                .key(UUID.randomUUID().toString())
//                        .tokenValiditySeconds(90 * 24 * 60 * 60) // 3달 유효 기간 설정
                )
//                예외 처리
                .exceptionHandling(config -> config
                        .authenticationEntryPoint(customAuthenticationEntryPoint) // 인증이 되지않은 유저가 요청을 했을때 동작
                        .accessDeniedHandler(customAccessDeniedHandler) // 서버에 요청을 할 때 액세스가 가능한지 권한을 체크, 액세스 할 수 없는 요청을 했을시 동작
                );

        return http.build();
    }


    // 세션 디버깅. 완료 단계에서 지워야 하는 부분.
//    @Bean
//    public ServletListenerRegistrationBean<HttpSessionEventPublisher> httpSessionEventPublisher() {
//        return new ServletListenerRegistrationBean<>(new HttpSessionEventPublisher() {
//            @Override
//            public void sessionCreated(HttpSessionEvent event) {
//                super.sessionCreated(event);
//                System.out.printf("===> [%s] asdf 세션 생성됨 %s \n", LocalDateTime.now(), event.getSession().getId());
//            }
//
//            @Override
//            public void sessionDestroyed(HttpSessionEvent event) {
//                super.sessionDestroyed(event);
//                System.out.printf("===> [%s] asdf 세션 만료됨 %s \n", LocalDateTime.now(), event.getSession().getId());
//            }
//
//            @Override
//            public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
//                super.sessionIdChanged(event, oldSessionId);
//                System.out.printf("===> [%s] asdf 세션 아이디 변경 %s:%s \n", LocalDateTime.now(), oldSessionId, event.getSession().getId());
//            }
//        });
//    }

}