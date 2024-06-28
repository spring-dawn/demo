//package com.example.demo.config.security;
//
//import com.example.demo.domain.menu.Menu;
//import com.example.demo.domain.menu.MenuRepository;
//import com.example.demo.domain.system.user.access.Privilege;
//import com.example.demo.domain.system.user.access.Role;
//import com.example.demo.domain.system.user.User;
//import com.example.demo.domain.system.user.UserRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Component;
//import org.springframework.util.AntPathMatcher;
//
//import javax.servlet.http.HttpServletRequest;
//import java.util.List;
//
//@Component
//public class AuthorizationChecker {
//    @Autowired
//    private MenuRepository menuRepository;
//
//    @Autowired
//    private UserRepository userRepository;
//
//    /**
//     * 가장 일치하는 URL Menu를 반환합니다.
//     * @param reqURI
//     * @param menuList
//     */
//    public Menu findLowestUrlMenu(String reqURI, List<Menu> menuList){
//        // 1. 매칭되는 url 있니
//        for (Menu menu : menuList) {
//            if (new AntPathMatcher().match("/api" + menu.getUrl() + "/**/*", reqURI)) {
//                // 있다면 - 하위 메뉴 중 matching 되는 url 이 있니
//                List<Menu> childrenMenu = menu.getChildren();
//
//                if(childrenMenu != null && !childrenMenu.isEmpty()){
//                    return findLowestUrlMenu(reqURI, childrenMenu);
//                }
//
//                // url 값은 고유합니다. 따라서 나머지 상위 url을 조회할 필요는 없습니다. 바로 return
//                return menu;
//            }
//        }
//
//        return null;
//    }
//
//    // TODO: menu별 권한(FCT_MENU_ROLE) 은 나중에 캐시로 가지고 있을 수 있도록 수정 필요. 현재는 요청시마다 확인하도록 만드는중
//    public boolean check(HttpServletRequest request, Authentication authentication) {
//        Object user = authentication.getPrincipal();
//
//        // 0. 권한이 필요없는 주소는 SecurityConfig에 정의되어있습니다.
//
//        // 1. AUTHENTICATION에 유저정보가 없다. == 로그인하지 않음
//        if (!(user instanceof User)) {
//            return false;
//        }
//
//        // request uri 를 확인하고 해당 url 에 접근가능한 role을 확인합니다.
//        String reqURI = request.getRequestURI();
//        Menu findMenu = this.findLowestUrlMenu(reqURI, menuRepository.findByParentIsNull());
//        if(findMenu == null){
//            return false;
//        }
//
////        for (Menu menu : menuRepository.findByParentIsNull()) {
////            if (new AntPathMatcher().match("/api" + menu.getUrl() + "/*", reqURI)) {
////                menuRoles = menu.getRoles();
////                break;
////            }
////        }
//
//        // MENU_ROLE 이 NULL 이라면 모두 허용입니다.
////        List<Role> menuRoles = findMenu.getRoles();
////        if(menuRoles == null){
////            return true;
////        }
////
////        // USER 의 role 을 확인합니다.
////        String userId = ((User) user).getUserId();
////        User signedUser = userRepository.findById(userId).orElse(null);
////        if(signedUser == null){
////            return false;
////        }
////
////        Role userRoles = signedUser.getRoles();
////
////        boolean isContain = menuRoles.stream()
////                .map(Role::getId)
////                .anyMatch(userRoles.getId()::equals);
////
////        if (!isContain) {
////            return false;
////        }
//        return true;
//    }
//
//    public boolean check(String auth){
//
//        // 로그인한 사용자 정보 가져오기
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Object user = authentication.getPrincipal();
//
//        String userId = ((User) user).getUserId();
//
//        User singedUser = userRepository.findByUserId(userId).orElse(null);
//        if(singedUser == null){
//            return false;
//        }
//
////        Role userRoles = singedUser.getRoles();
////        List<Privilege> rolePrivileges = userRoles.getPrivileges();
////
////        // role_privilege에 auth값이 존재할경우 return true
////        boolean isContain = rolePrivileges.stream()
////                .map(Privilege::getName)
////                .anyMatch(auth::contains);
//
////        return isContain;
//        return true;
//    }
//}
