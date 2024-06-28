//package com.example.demo.domain.system.user;
//
//import org.springframework.data.jpa.domain.Specification;
//
//public class UserSpecification {
//    /*
//    jpa 검색 유틸입니다
//     */
//
////    equal, like, between
////    keyword: userId, userNm, email, admYn, useYn... 사용자가 직접 입력하면 like, 셀렉트 옵션은 equal 사용.
//
//    public static Specification<User> likeUserId(String userId) {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
//                root.get("userId")
//                , "%" + userId + "%"
//        );
//
//    }
//
//    public static Specification<User> likeUserNm(String userNm) {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
//                root.get("userNm")
//                , "%" + userNm + "%"
//        );
//    }
//
//    public static Specification<User> likeEmail(String email) {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.like(
//                root.get("email")
//                , "%" + email + "%"
//        );
//    }
//
//    public static Specification<User> equalUseYn(String useYn) {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("useYn"), useYn);
//    }
//
//    public static Specification<User> equalAdmYn(String admYn) {
//        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("admYn"), admYn);
//    }
//
//}
