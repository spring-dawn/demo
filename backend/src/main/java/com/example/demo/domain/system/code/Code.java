package com.example.demo.domain.system.code;

import com.example.demo.domain.CommonEntity;
import com.example.demo.dto.system.CodeDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Comment;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

//@ToString
@Getter // do not create/use `setter` on Entity class
@Builder
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Entity
@Table(name = "SYS_CD_T")
public class Code extends CommonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CD_NO")
    private Long id;

    @Comment("코드")
    @Column(unique = true, name = "CD_NM", length = 50)
    private String name;

    @Comment("코드값. 의미하는 내용.")
    @Column(name = "CD_CN", length = 50)
    private String value;

    @Comment("비고")
    @Column(name = "RMRK", columnDefinition = "text")
    private String comment;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "UP_CD_NO")
    private Code parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Code> children = new ArrayList<>();


    /*
    update
     */
    public void update(CodeDto.CodeReq req) {
        this.name = req.getName();
        this.value = req.getValue();
        this.comment = req.getComment();
    }

    public void updateParent(Code parent) {
        this.parent = parent;
    }


    /*
    dto.
     */
    public CodeDto toCodeRes() {
        // depth 는 따로 컬럼을 두지 않고 수동 계산
        int depth = 1;
        if(this.parent != null && this.parent.parent == null) depth = 2;
        if(this.parent != null && this.parent.parent != null) depth = 3;

        return CodeDto.builder()
                .id(this.id)
                .name(this.name)
                .value(this.value)
                .depth(depth)
                .comment(this.comment)
                .parentId(this.parent == null ? null : this.parent.getId())
                .children(this.children == null ?
                        null : this.children.stream().map(Code::toCodeRes).collect(Collectors.toList()))
                .build();
    }

}
