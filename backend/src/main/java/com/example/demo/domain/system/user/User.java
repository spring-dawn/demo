package com.example.demo.domain.system.user;

import com.example.demo.domain.CommonEntity;
import com.example.demo.domain.system.user.access.Role;
import com.example.demo.dto.system.UserDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.demo.atech.MyUtil.createDtm2Str;

@Getter // do not create/use `setter` on Entity class
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
@DynamicInsert // insert default value instead of null
@Table(name = "DIM_USER_T")
public class User extends CommonEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "USER_NO")
    private Long id;

    @Comment("사용자 ID")
    @Column(nullable = false, length = 20, unique = true, name = "USER_ID")
    private String userId;

    @Comment("비밀번호")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 200, name = "PSWD")
    private String password;

    @Comment("사용자명")
    @Column(length = 20, name = "USER_NM", nullable = false)
    private String userNm;

    @Comment("휴대폰번호")
    @Column(length = 20, name = "MBL_TELNO")
    private String cellNo;

    @Comment("이메일 주소")
    @Column(length = 50, name = "EML", unique = true, nullable = false)
    private String email;

    @Comment("소속부서")
    @Column(name = "DEPT", length = 50, nullable = false)
    private String dept;

    @Comment("소속기관. 구군청, 시청(본청)")
    @Column(name = "AGENCY", length = 20, nullable = false)
    private String agency;

    @Comment("계정 활성화 여부. Y: 사용, N: 미사용(로그인 차단)")
    @Column(length = 1, name = "ACTVTN_YN", nullable = false)
    @ColumnDefault("'Y'")
    private String useYn;

    @Comment("솔트")
    @Column(name = "SALT", nullable = false, length = 50)
    private String salt;

    @Comment("로그인 실패 횟수. 5회 실패부터 로그인 차단.")
    @Column(name = "LGN_FAIL_NMTM", nullable = false)
    @ColumnDefault("'0'")
    private int failCnt;

    @Comment("비밀번호 변경 일시. 6개월이 지나면 변경 권장 메일 발송.")
    @Column(name = "PSWD_CHG_DT", nullable = false)
    private LocalDateTime pwUpdateDt;

    @Comment("권한 요청 메시지")
    @Column(name = "AUTHRT_DMND_MSG", columnDefinition = "text")
    private String roleReqMsg;


    /*
    User : Role = N : 1 단방향 참조.
    연관 필드에는 반드시 @JsonIgnore 등 순환참조 방어 어노테이션을 붙입니다
    @ManyToOne 의 기본 fetch 전략은 EAGER 이므로 LAZY 를 명시해줍니다.
    양방향 매핑은 가능한 한 피하고, 필요한 만큼만 연관 짓습니다.
     */
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHRT_CONT_NO")
    private Role role;

    /*
    update
     */
    public void updatePassword(String salt, String newPw){
        this.salt = salt;
        this.password = newPw;
        this.pwUpdateDt = LocalDateTime.now();
    }

    public void updateUser(UserDto.UpdateReq req) {
        this.userNm = req.getUserNm();
        this.email = req.getEmail();
        this.cellNo = req.getCellNo();
        this.dept = req.getDept();
        this.agency = req.getAgency();
    }

    public void updateUseYn(String useYn){
        this.useYn = useYn;
    }

    public void updateRole(Role role) { this.role = role; }
    public void updateFailCnt() {this.failCnt += 1;}
    public void initFailCnt() {this.failCnt = 0;}
    public void quit(){ this.useYn = "N"; }
//    public void updateAdmYn(String admYn){
//        this.admYn = admYn;
//    }

    /*
    implement
     */
    @Override
    @Transactional(readOnly = true)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.getRole().getName()));

        return authorities;
    }

    @Override
    public String getUsername() {
        return this.userId;
    }

    @Override
    public String getPassword(){
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 계정 잠금 여부. 인증 시도 5회 이상 실패 시 계정 잠금.
        return failCnt < 5;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 비밀번호 유효 여부(-> 변경 알림)
//        TODO: 저장된 비밀번호 변경 일시가 오늘 날짜와 비교해 6개월이 넘었으면 메일 발송.
//         스케줄링은 1~3개월에 한 번, 으로 비밀번호 변경 권장 메일이 매일매일 발송되지 않도록 제어.
//         비밀번호 유효 기간이 지났어도 로그인은 막지 않는다.
//         단순하게 일자까지만 비교.
        LocalDate pwUpdated = pwUpdateDt.toLocalDate().plusMonths(6L);
        return pwUpdated.isAfter(LocalDate.now());
    }

    @Override
    public boolean isEnabled() {
        // 계정 활성화 여부
        return useYn.equals("Y");
    }


    /*
    dto
     */
    public UserDto.UserRes toRes() {
        return UserDto.UserRes.builder()
                .userId(this.userId)
                .userNm(this.userNm)
                .email(this.email)
                .cellNo(this.cellNo)
                .joinDt(createDtm2Str(this.createDtm))
                .dept(this.dept)
                .agency(this.agency)
                .useYn(this.useYn)
//                .admYn(this.admYn)
                .pwUpdateDt(this.pwUpdateDt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .role(this.role.toRes())
                .roleNm(this.role.getName())
                .roleReqMsg(this.roleReqMsg)
                .createDtm(this.createDtm.toString())   // order by
                .build();
    }
}
