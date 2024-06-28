package com.example.demo.domain.system.user.access;

import com.example.demo.domain.CommonEntity;
import com.example.demo.dto.system.RoleDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter // do not create/use `setter` on Entity class
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "DIM_AUTHRT_CONT_T")
public class Role extends CommonEntity {
    /*
    사용자 권한, 혹은 권한 컨테이너.
    사용자가 권한 관리를 할 때는 Role 만 제어할 수 있습니다. Role 관련 서비스 로직만 작성하면 됩니다.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTHRT_CONT_NO")
    private Long id;

    @Comment("사용자권한명 영문")
    @Column(unique = true, name = "AUTHRT_CONT_NM", length = 50)
    private String name;

    @Comment("사용자권한명 한글")
    @Column(name = "AUTHRT_CONT_KORN_NM", length = 50)
    private String encodedNm;

//    [240411] 고도화하면 권한관리 생길 거라 예상.
    @Comment("사용여부")
    @Column(length = 1, name = "ACTVTN_YN", nullable = false)
    @ColumnDefault("'Y'")
    private String useYn;

    @Comment("비고: 사용자권한에 대한 설명")
    @Column(name = "RMRK", columnDefinition = "text")
    private String comment;

    /*
    Role : Privilege = N : M
    다대다 매핑이 필요하면 독립적인 연결 엔티티를 만들어 1:N, N:1 로 관계를 해소시킵니다.

    양방향 연관 엔티티가 있는 경우 트랜잭션 시점의 오차 + 필드 초기화 이슈로 dto에서 NullPointerException 이 발생할 수 있습니다.
    https://velog.io/@titu/Spring-Transaction-%EC%A4%91%EC%B2%A9-%EC%82%AC%EC%9A%A9Transactional
     */
    @JsonIgnore
    @OneToMany(mappedBy = "role", cascade = CascadeType.REMOVE)
    private List<RolePrivilege> privileges = new ArrayList<>();

    //연관 엔티티 필드 수동 초기화
    public void addPrivileges (List<RolePrivilege> privileges){
        this.privileges = privileges;
    }


    /*
    update
     */
    public void updateRole(RoleDto.Req req){
        // [240411] 아직 실제 권한 내역 변경은 하지 않음.
        name = req.getName();
        encodedNm = req.getEncodedNm();
        comment = req.getComment();
        useYn = req.getUseYn();
    }
    public void updateRole(RoleDto.TmpReq req){
        // [240411] 아직 실제 권한 내역 변경은 하지 않음.
        name = req.getName();
        encodedNm = req.getEncodedNm();
        useYn = req.getUseYn();
    }


    /*
    dto
     */
    public RoleDto toRes(){
        return RoleDto.builder()
                .id(id)
                .name(name)
                .encodedNm(encodedNm)
                .useYn(useYn)
                .privileges(privileges.stream().map(RolePrivilege::toRolePrivilegeDto).collect(Collectors.toList()))
                .comment(comment)
                .build();
    }


}