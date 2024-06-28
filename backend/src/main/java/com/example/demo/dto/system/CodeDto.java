package com.example.demo.dto.system;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class CodeDto {
    /*
    response
     */
    private Long id;
    private String name;
    private String value;
    private int depth;
    private String comment;
    private Long parentId;
    private List<CodeDto> children;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class childrenDto{
        private Long id;
        private String name;
        private String value;
        private String comment;
        private Long parentId;
    }


    /*
    insert, update req
     */
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CodeReq{
        private Long id;
        private String name;
        private String value;
        private String comment;
        private Long parentId;
    }


}