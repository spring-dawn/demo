package com.example.demo.config.security.login;

import com.example.demo.config.security.SHA256Util;
import com.example.demo.domain.system.user.User;
import com.example.demo.service.system.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    /*
    인증 정보 처리 작업이 실제로 수행되는 곳입니다.
    필요에 따라 여러 개 작성 가능하며, ProviderManager(Manager) 는 요청을 처리하기 적합한 프로바이더를 찾아 작업을 위임합니다.
    없는 사용자, 패스워드 불일치, 계정 만료, 계정 비활성화 등등 여섯 개의 어센티케이션 익셉션을 활용해 다양하게 검증할 수 있습니다.
     */
    private final UserService userService;

//    @Transactional(readOnly = true)
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        /*
        검증하려는 부분은 throw AuthenticationException 으로 예외를 발생시켜야 합니다.
        try/catch 로 처리하면 틀린 로그인이 제대로 잡히지 않습니다.
         */

        // 클라이언트에서 입력 받은 인증 정보
        String userId = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        // DB에 저장돼있는 실제 사용자 정보
        User user = userService.loadUserByUsername(userId);

        // 계정 유효성 검사. 가장 먼저 검증해야 하는 내용부터 위에 배치.
        if(!user.isAccountNonExpired()) throw new AccountExpiredException("This account has not been approved yet");
        if(!user.isEnabled()) throw new DisabledException("This user has lost access. Contact admin.");
        if (!user.isAccountNonLocked()) throw new LockedException("This user has failed authentication more than 5 times.");

        // 패스워드 일치여부 검사
        boolean validated = SHA256Util.validatePassword(user.getSalt(), password, user.getPassword());
        if (!validated) {
            // 인증 실패 횟수 +1
            userService.updateFailCnt(userId);
            throw new BadCredentialsException("This password is incorrect.");   // 패스워드 불일치
        }

        // 인증 성공 시 실패 횟수 초기화
        if(user.getFailCnt() != 0) userService.initFailCnt(userId);
        // id, (pw 생략), 권한 목록 리턴
        return new UsernamePasswordAuthenticationToken(user.getUserId(), null, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
//        매니저가 사용자 권한 검증 작업을 위임할 프로바이더를 찾을 때, 어떤 검증을 담당하는지 알리는 부분.
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
