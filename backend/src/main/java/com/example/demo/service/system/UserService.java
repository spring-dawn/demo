package com.example.demo.service.system;

import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.atech.sendMail.MailManger;
import com.example.demo.config.security.SHA256Util;
import com.example.demo.domain.system.user.User;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.domain.system.user.access.Privilege;
import com.example.demo.domain.system.user.access.Role;
import com.example.demo.domain.system.user.access.RolePrivilege;
import com.example.demo.domain.system.user.access.RoleRepository;
import com.example.demo.dto.system.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.demo.atech.Msg.*;
import static com.example.demo.atech.MyUtil.getEnum;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final String THIS = "사용자";
    private final UserRepository repo;
    private final RoleRepository roleRepo;
    private final MailManger mm;


    /**
     * Spring Security login 필수 구현.
     *
     * @param userId the username identifying the user whose data is required.
     * @return 사용자 인증 정보.
     * @throws UsernameNotFoundException 식별 불가.
     */
    @Override
    public User loadUserByUsername(String userId) throws UsernameNotFoundException {
        // 계정 정보 + 권한
        User user = repo.findByUserId(userId)
                .orElseThrow(() -> new UsernameNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        user.getAuthorities();

        return user;
    }

    public UserDto.UserRes selectUser(String userId) {
        User user = repo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

        return user.toRes();
    }

    public boolean isDuplicate(String userId){
        return repo.existsByUserId(userId);
    }

    // 회원가입
    @Transactional
    public UserDto.UserRes createUser(UserDto.SignUpReq req) {
//        1) validation
        if (!hasText(req.getUserId()) || !hasText(req.getPassword()) || !hasText(req.getEmail()) || !hasText(req.getUserNm()))
            throw new NullPointerException(NPE.getMsg());
        if(!req.getPassword().equals(req.getPasswordConfirm())) throw new IllegalArgumentException(Msg.PW_DIFF.getMsg());
        // uk
        if (repo.existsByUserId(req.getUserId())) throw new DataIntegrityViolationException(getEnum(Msg.ALREADY_EXISTS, "아이디"));
        if (repo.existsByEmail(req.getEmail())) throw new DataIntegrityViolationException(getEnum(Msg.ALREADY_EXISTS, "이메일"));

//        2) pw encryption
        Pattern pattern = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{9,20}$");
        if(!pattern.matcher(req.getPassword()).matches()) throw new IllegalArgumentException("비밀번호는 영문, 숫자, 특수문자를 섞어 9~20자리여야 합니다.");
        SHA256Util.PwDto pw = SHA256Util.createPw(req.getPassword());

//        3) insert
        // role 은 디폴트로 '담당자' 부여. 원하는 내용은 roleReqMsg 에 작성해 요청하면 관리자가 처리.
        Role defaultRole = roleRepo.findByName("ROLE_SGG_MNGR").orElse(null);
        if(defaultRole == null) throw new NullPointerException("기본 권한이 존재하지 않습니다. 시스템을 점검해주세요.");

        User userNew = User.builder()
                .userId(req.getUserId())
                .salt(pw.getSalt())
                .password(pw.getSalted())
                .userNm(req.getUserNm())
                .email(req.getEmail())
                .agency(req.getAgency())
                .dept(req.getDept())
                .roleReqMsg(req.getRoleReqMsg())
                .cellNo(req.getCellNo())
                .role(defaultRole)
                .pwUpdateDt(LocalDateTime.now())
                .build();
//        4) res
        return repo.save(userNew).toRes();
    }


    /**
     * 패스워드 제외한 사용자 정보 수정.
     *
     * @param req pw, pwConfirm(본인확인용), role 등.
     * @return 변경된 정보.
     */
    @Transactional
    public UserDto.UserRes updateUser(UserDto.UpdateReq req) {
//        1) find target
        User user = repo.findByUserId(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

//        2) validation
        // pw, pwConfirm 으로 본인 확인합니다
        if (!(
                hasText(req.getPassword())
                        && hasText(req.getPasswordConfirm())
                        && hasText(req.getEmail()))
        ) {
            throw new NullPointerException(NPE.getMsg());
        } else if (!req.getPassword().equals(req.getPasswordConfirm())) {
            throw new RuntimeException(PW_DIFF.getMsg());
        } else if (repo.existsByEmail(req.getEmail())
                && repo.findByEmail(req.getEmail()).get().getId() != user.getId()) {
            throw new EntityExistsException(getEnum(Msg.ALREADY_EXISTS, "이메일"));
        } else if (!SHA256Util.validatePassword(user.getSalt(), req.getPassword(), user.getPassword())) {
            throw new RuntimeException(PW_INCORRECT.getMsg());
        }

//        3) update(dirty check)
        if (hasText(req.getRoleNm()) && !req.getRoleNm().equals(user.getRole().getName())) {
            // db에 없는 Role 을 고르는 경우는 없어야 합니다.
            Role role = roleRepo.findByName(req.getRoleNm())
                    .orElseThrow(() -> new EntityNotFoundException(getEnum(ENTITY_NOT_FOUND, "권한")));
            user.updateRole(role);
        }
        user.updateUser(req);

//        4) res
        return user.toRes();
    }

    /**
     * 관리자가 사용자 관리에서 정보 변경. 비밀번호 체크하지 않음.
     * @param req 변경 내용
     * @return res
     */
    @Transactional
    public UserDto.UserRes updateUserByAdmin(UserDto.UpdateReq req) {
//        1) find target
        User user = repo.findByUserId(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

//        2) validation: 관리자가 수정할 때는 비밀번호 확인 x, 이메일 변경 중복검사.
        if (!hasText(req.getEmail())) {
            throw new NullPointerException(NPE.getMsg());
        } else if (repo.existsByEmail(req.getEmail()) && repo.findByEmail(req.getEmail()).get().getId() != user.getId()) {
            throw new EntityExistsException(getEnum(Msg.ALREADY_EXISTS, "이메일"));
        }

//        3) update(dirty check)
        if (hasText(req.getRoleNm()) && !req.getRoleNm().equals(user.getRole().getName())) {
            // db에 없는 Role 을 고르는 경우는 없어야 합니다.
            Role role = roleRepo.findByName(req.getRoleNm())
                    .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, "권한")));
            user.updateRole(role);
        }
        user.updateUser(req);
        if(hasText(req.getUseYn())) user.updateUseYn(req.getUseYn());

//        4) res
        return user.toRes();
    }


    /**
     * 패스워드 변경
     *
     * @param req userId, 기존 패스워드, 새 패스워드, 새 패스워드 확인
     * @return 변경 확인
     * @throws NoSuchAlgorithmException ?
     */
    @Transactional
    public UserDto.UserRes updatePw(UserDto.UpdateReq req) throws NoSuchAlgorithmException {
//        1) find target
        User user = repo.findByUserId(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

//        2) pw validation
        if (!(
                hasText(req.getPassword())
                        && hasText(req.getPasswordNew())
                        && hasText(req.getPasswordNewConfirm())
        )) throw new NullPointerException(NPE.getMsg());

        if (!req.getPasswordNew().equals(req.getPasswordNewConfirm()))
            throw new RuntimeException(Msg.PW_DIFF.getMsg());
        if (!SHA256Util.validatePassword(user.getSalt(), req.getPassword(), user.getPassword()))
            throw new BadCredentialsException(PW_INCORRECT.getMsg());

//        3) update
        SHA256Util.PwDto pw = SHA256Util.createPw(req.getPasswordNew());
        user.updatePassword(pw.getSalt(), pw.getSalted());

//        4) res
        return user.toRes();
    }


    /**
     * 비밀번호 분실 시 임시 비밀번호 발급
     * @param req userId, email
     * @return 변경 후 사용자 상태
     */
    @Transactional
    public UserDto.UserRes sendTmpPw(UserDto.UserReq req){
//        1)
        if(!hasText(req.getUserId()) || !hasText(req.getEmail())) throw new NullPointerException(NPE.getMsg());

//        2)
        User user = repo.findByUserId(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        if(!user.isEnabled()) throw new DisabledException("해당 계정은 사용할 수 없습니다.");
        if(!user.getEmail().equals(req.getEmail())) throw new BadCredentialsException(getEnum(Msg.NOT_MATCH, "이메일"));

//        3) change pw
        String newTmpPw = UUID.randomUUID().toString().substring(0, 15);
        SHA256Util.PwDto pwDto = SHA256Util.createPw(newTmpPw);
        user.updatePassword(pwDto.getSalt(), pwDto.getSalted());

//        4) send mail, res
        mm.sendMail4TmpPw(req.getEmail(), newTmpPw);
        if(user.getFailCnt() != 0) user.initFailCnt();

        return user.toRes();
    }


    /**
     * 회원 탈퇴
     *
     * @param req 본인확인용 userId, 패스워드, 패스워드 확인.
     * @return 계정 비활성화
     */
    @Transactional
    public UserDto.UserRes quitUser(UserDto.UpdateReq req) {
//        1) find target
        User user = repo.findByUserId(req.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

//        2) validation
        if (!(hasText(req.getPassword()) && hasText(req.getPasswordConfirm()))) {
            throw new NullPointerException(NPE.getMsg());
        } else if (!req.getPassword().equals(req.getPasswordConfirm())) {
            throw new RuntimeException(Msg.PW_DIFF.getMsg());
        } else if (!SHA256Util.validatePassword(user.getSalt(), req.getPassword(), user.getPassword())) {
            throw new BadCredentialsException(getEnum(Msg.NOT_MATCH, "비밀번호"));
        } else {
//          3) make account disabled.
            user.quit();
        }

//        4) res
        return user.toRes();
    }


    @Transactional
    public UserDto.UserRes deleteUser(String userId) throws IllegalArgumentException {
//        1) find target
        User user = repo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

//        2) delete and res
        repo.delete(user);
        return user.toRes();
    }


    // provider 에 인증 실패 시 실패횟수 증가 서비스 로직 추가
    @Transactional
    public void updateFailCnt(String userId) {
        User user = repo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        user.updateFailCnt();
    }

    @Transactional
    public void initFailCnt(String userId) {
        User user = repo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        user.initFailCnt();
    }

    public String getSalt(String userId) {
        User user = repo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

        return user.getSalt();
    }


    // 다달이 1일마다 비밀번호 유효기간 검사. 변경 안내 메일 발송. 변경일로부터 6개월이 지났어도 로그인은 막지 않음.
    @Scheduled(cron = "0 0 3 1 * ?", zone = "Asia/Seoul")
    public void alarm4PwUpdate() {
        for (User user : repo.findAll()) {
            // 사용자들의 비밀번호 변경일시를 비교, false(6개월이 지났으면)면 메일 발송.
            if (user.isEnabled() && !user.isCredentialsNonExpired()) {
                mm.recommendPwUpdate(user.getEmail(), user.getUserId());
            }
        }

    }


    /**
     * 사용자 세션 만료 여부를 체크합니다.
     * @return T: 유효, F: 만료(로그아웃)
     */
    public boolean checkSessionStatus(){
        String sessionUserId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        return !sessionUserId.equals("anonymousUser");
    }


    /**
     * 사용자가 해당 메뉴의 수정/삭제 등 편집 권한이 있는지 확인합니다.
     *
     * @param roleId 사용자 롤 id(authrt_cont_no)
     * @return T: 편집 가능, F: 불가능
     * @url 현재 접속 중인 위치. 메뉴 url
     */
    public boolean checkEditAccess(Long roleId, String url) {
//        1) 권한 확인
        Role role = roleRepo.findById(roleId)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, "권한")));
//        2) 메뉴 데이터 확인
        return role.getPrivileges().stream()
                .map(RolePrivilege::getPrivilege)
                .filter(privilege -> privilege.getMenu().getUrl().equals(url))
                .anyMatch(privilege -> privilege.getWriteYn().equals("Y"));
    }

}