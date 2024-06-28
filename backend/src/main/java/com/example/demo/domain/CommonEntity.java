package com.example.demo.domain;

import lombok.Getter;
import org.hibernate.annotations.Comment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter // do not create/use `setter` on Entity class
@MappedSuperclass
public class CommonEntity {
    /*
    공통 상속 엔티티.
    생성일자, 생성자, 수정일자, 수정자 정보 등 공통으로 쓰일만한 내용을 필요한 엔티티 클래스에 상속하여 자동으로 적용시킵니다.
     */

    @Basic
    @Comment("생성자")
    @Column(updatable = false, length = 20, name = "CRT_ID")
    protected String createId;

    @Basic
    @Comment("생성일시")
    @Column(updatable = false, name = "CRT_YMD")
    protected LocalDateTime createDtm;

    @Basic
    @Comment("수정자")
    @Column(length = 20, name = "MDFCN_ID")
    protected String updateId;

    @Basic
    @Comment("수정일시")
    @Column(name = "MDFCN_YMD")
    protected LocalDateTime updateDtm;

    /*
     */
    @PrePersist
    protected void onPersist() {
        this.createDtm = this.updateDtm = LocalDateTime.now();
        this.createId = this.updateId = getSessionId();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updateDtm = LocalDateTime.now();
        this.updateId = getSessionId();
    }


    /**
     * 데이터 생성/수정 시 세션 아이디 기입
     * @return 세션이 없거나 익명 사용자인 경우 null
     */
    protected String getSessionId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return null;
        if (authentication.getPrincipal().toString().equals("anonymousUser")) return null;
        return authentication.getPrincipal().toString();
    }

}
