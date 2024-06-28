package com.example.demo.domain.system.loginLog;

import com.example.demo.domain.CommonEntity;
import com.example.demo.dto.system.LoginLogDto;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;

import static com.example.demo.atech.MyUtil.createDtm2Str;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
@Table(name = "FCT_LGN_LOG_T")
public class LoginLog extends CommonEntity {
    /*
    사용자별 접속 정보를 기록합니다. 공기관 특성상 ip 기록은 필요없다고 합니다.
    [24. 03. 07] 구현 미정.
     */
    @Id
    @Comment("로그 번호")
    @Column(name = "LOG_NO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("사용자 아이디")
    @Column(name = "USER_ID", length = 20, nullable = false)
    private String userId;

    @Comment("사용자명")
    @Column(name = "USER_NM", length = 20)
    private String userNm;

    @Comment("소속부서")
    @Column(name = "DEPT", length = 50)
    private String dept;

    @Comment("소속기관. 구군청, 시청(본청) 코드")
    @Column(name = "AGENCY", length = 5)
    private String agency;

    // dto
    public LoginLogDto toRes() {
        return LoginLogDto.builder()
                .id(id)
                .userId(userId)
                .userNm(userNm)
                .dept(dept)
                .agency(agency)
                .createDtm(createDtm2Str(createDtm))
                .build();
    }


}
