package com.example.demo.domain.system.user.access;

import com.example.demo.dto.system.RoleDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "FCT_AUTHRT_LNKG_T")
public class RolePrivilege {
    /*
    Role, Privilege 다대다 매핑의 연결 엔티티입니다.
    이 연결 엔티티를 추가/수정/삭제해도 Role, Privilege, User 등에 영향을 끼치지 않습니다.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTHRT_LNKG_NO")
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHRT_CONT_NO")
    private Role role;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "AUTHRT_NO")
    private Privilege privilege;


    /*
    dto
     */
    public RoleDto.RolePrivilegeDto toRolePrivilegeDto() {
        // Role 안의 권한 정보를 반환합니다.
        return RoleDto.RolePrivilegeDto.builder()
                .privilegeId(privilege.getId())
                .name(privilege.getName())
                .encodedNm(privilege.getEncodedNm())
                .build();
    }


}