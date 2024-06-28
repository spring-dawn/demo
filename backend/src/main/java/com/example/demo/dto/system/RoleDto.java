package com.example.demo.dto.system;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
    /*
    response
     */
    private Long id;
    private String name;    // uk
    private String encodedNm;
    private String comment;
    private String useYn;
    private List<RolePrivilegeDto> privileges;


    /*
    insert, update
     */
    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Req{
        private Long id;
        private String name;
        private String encodedNm;
        private String comment;
        private String useYn;

        // 다른 엔티티 값을 추가, 수정해야 할 때 직접 그 타입을 맞출 수 없는 구조이므로 Long, String 등의 매개체를 씁니다
        // 서비스 로직에서 ex) findById(Long id) 등으로 실제 엔티티를 찾은 뒤 추가, 수정하는 식입니다.
//        private List<String> privileges;
        private List<Long> privileges;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TmpReq{
        private String name;
        private String encodedNm;
        private String useYn;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class RolePrivilegeDto{
        private Long privilegeId;
        private String name;
        private String encodedNm;
    }




//    /*
//     * role 검색
//     */
//    @Getter
//    @AllArgsConstructor
//    @Builder
//    public static class RoleReq{
//        private Long id;
//        private String name;
//
//        public Role toEntity(){
//            return Role.builder()
//                    .id(this.id)
//                    .name(this.name)
//                    .build();
//        }
//    }
//
//    @Getter
//    @AllArgsConstructor
//    @Builder
//    public static class RoleRes{
//        private Long id;
//        private String name;
//        private List<PrivilegeDto> privileges;
//    }
//
//    @Getter
//    @AllArgsConstructor
//    @Builder
//    public static class InsertReq{
//        private Long id;
//        private String name;
//    }


}