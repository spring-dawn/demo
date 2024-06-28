package com.example.demo.service.system;

import com.example.demo.atech.Msg;
import com.example.demo.domain.system.loginLog.LoginLog;
import com.example.demo.domain.system.loginLog.LoginLogRepository;
import com.example.demo.domain.system.user.User;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.dto.system.LoginLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.atech.MyUtil.getEnum;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoginLogService {
    private final String THIS = "접속 기록";
    private final LoginLogRepository repo;
    private final UserRepository userRepo;

    public LoginLogDto selectOne(Long id) {
        LoginLog loginLog = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        return loginLog.toRes();
    }

    @Transactional
    public LoginLogDto insert() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepo.findByUserId(authentication.getPrincipal().toString()).orElse(null);
        if (user == null) return null;

        return repo
                .save(LoginLog.builder()
                        .userId(user.getUserId())
                        .userNm(user.getUserNm())
                        .agency(user.getAgency())
                        .dept(user.getDept())
                        .build())
                .toRes();
    }

}
