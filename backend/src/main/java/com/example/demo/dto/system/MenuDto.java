package com.example.demo.dto.system;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
//@NoArgsConstructor
@Builder
public class MenuDto {
    private Long id;
    private String url;
    private String name;
    private Long depth;
    private int seq;
    private String ico;
    private String tabYn;

    private Long parentId;
    private List<MenuDto> children; // Menu 엔티티 그대로 받으니까 너무 길어져서, Dto 로 깔끔하게 출력.


    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class MenuReq {
//        String url, String name, String parentUrl, Long seq, List<Privilege> privileges, String ico
        private String url;
        private String name;
        private String parentUrl;
        private int seq;
        private String ico;
    }
}