package com.example.demo.domain.system.user.access;

import com.example.demo.domain.system.menu.Menu;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SYS_AUTHRT_T")
public class Privilege {
    /*
    상수값이라 서비스로직이 필요하지 않습니다.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTHRT_NO")
    private Long id;

    @Comment("권한명 영문")
    @Column(name = "AUTHRT_NM", nullable = false)
    private String name;

    @Comment("권한명 한글")
    @Column(name = "AUTHRT_KORN_NM")
    private String encodedNm;

    @Comment("비고")
    @Column(name = "RMRK")
    private String comment;

    @Comment("조회/편집 여부. Y: 편집, N: 조회")
    @Column(name = "INQ_EDT_YN", length = 1, nullable = false)
    private String writeYn;

    /*
  Role : Privilege = N : M
   */
    @JsonIgnore
    @OneToMany(mappedBy = "privilege")
    private List<RolePrivilege> roles = new ArrayList<>();

    /*
  Privilege : Menu = N : 1
  어떤 메뉴에 대한 어떤 권한인지 명시
   */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MENU_NO")
    private Menu menu;

}