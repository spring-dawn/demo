package com.example.demo.domain.system.menu;

import com.example.demo.domain.CommonEntity;
import com.example.demo.dto.system.MenuDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@ToString
@Getter // do not create/use `setter` on Entity class
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@DynamicInsert
@Entity
@Table(name = "SYS_MENU_T")
public class Menu extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MENU_NO")
    private Long id;

    @Comment("메뉴 주소")
    @Column(unique = true, name = "MENU_URL")
    private String url;

    @Comment("메뉴명")
    @Column(name = "MENU_NM", length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UP_MENU_NO")
    private Menu parent;

    @Comment("메뉴 나열 순서(오름차순). 같은 계층(depth) 안에서 숫자가 작을수록 왼쪽/상위에 배치.")
    @Column(name = "SORT_SEQ", nullable = false)
    private int seq;    // 메뉴 나열 순서. 숫자가 작은 순서대로 왼쪽.

    @Comment("사용 여부. Y:사용, N: 미사용.")
    @Column(name = "ACTVTN_YN", length = 1, nullable = false)
    @ColumnDefault("'Y'")   // N이면 사용하지 않는 메뉴, 출력X.
    private String useYn;

    @Comment("탭 여부. 탭은 사용자의 권한을 검사하지 않음. Y: 탭, N: 일반 메뉴.")
    @Column(name = "TAB_YN", length = 1, nullable = false)
    @ColumnDefault("'N'")
    private String tabYn;

    @Comment("메뉴 아이콘 정보")
    @Column(name = "MENU_ICON")
    private String ico;

    @OneToMany(mappedBy = "parent")
    private List<Menu> children = new ArrayList<>();


    /*
    update
     */
    public void addChildren(List<Menu> children){
        this.children = children;
    }


    /*
    dto
     */
    public MenuDto toMenuDto(){
        return MenuDto.builder()
                .id(this.id)
                .name(this.name)
                .url(this.url)
                .seq(this.seq)
                .ico(this.ico)
                .tabYn(this.tabYn)
                .parentId(this.parent != null ? this.parent.getId() : null)
                .children(this.children == null ? null : this.children.stream().map(Menu::toMenuDto).collect(Collectors.toList()))
                .build();
    }
}
