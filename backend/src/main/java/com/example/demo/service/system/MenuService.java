package com.example.demo.service.system;

import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.domain.system.menu.Menu;
import com.example.demo.domain.system.menu.MenuRepository;
import com.example.demo.domain.system.user.access.Role;
import com.example.demo.domain.system.user.access.RolePrivilege;
import com.example.demo.domain.system.user.access.RoleRepository;
import com.example.demo.dto.system.MenuDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.atech.MyUtil.getEnum;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MenuService {
    private final MenuRepository menuRepo;
    private final RoleRepository roleRepo;

    // 고정값 캐싱
    @Cacheable(value = "menus")
    public List<MenuDto> getMenuList() {
        return menuRepo.findByParentIsNull()
                .stream().map(Menu::toMenuDto)
                .collect(Collectors.toList());
    }


    // Role 단위로 변동 캐싱 적용
    @Cacheable(value = "mymenu", key = "#authentication.getAuthorities().toArray()[0].toString()")
    public List<MenuDto> getMyMenuList(Authentication authentication) {
//        1) 로그인 사용자 정보 수신 (AUTHENTICATION 에 유저정보가 없다. == 로그인하지 않음)
//        2) 사용자 인증 정보에서 부여된 컨테이너 확인. 1사용자 1롤.
        String roleNm = authentication.getAuthorities().toArray()[0].toString();
        Role role = roleRepo.findByName(roleNm)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, "권한")));

//        3) 인증된 사용자에게 승인된 메뉴 목록 조회
        List<Menu> roots = new ArrayList<>();
        List<Long> allowed = new ArrayList<>();

        for (RolePrivilege rp : role.getPrivileges()) {
            if (rp.getPrivilege().getWriteYn().equals("N")) {
                Menu menu = rp.getPrivilege().getMenu();
                // root, access allowed
                if (menu.getParent() == null) roots.add(menu);
                allowed.add(menu.getId());
            }
        }
        // [231031]메뉴 필터링 중에 중복 발생 -> Set 으로 중복 제거.
        List<Menu> myMenus = filterAllowedMenu(roots, allowed);
        Set<Menu> mySet = new HashSet<>(myMenus);

//        5) res
        return mySet.stream()
                .map(Menu::toMenuDto)
                .sorted(Comparator.comparing(MenuDto::getSeq)) //seq 오름차순 정렬. 내림차순: .reversed()
                .collect(Collectors.toList());

    }

    /**
     * @param rootMenus 접근권 있는 최상위 메뉴들
     * @param allowed   접근권 있는 메뉴 전체의 id
     * @return 필터링 메뉴
     */
    private List<Menu> filterAllowedMenu(List<Menu> rootMenus, List<Long> allowed) {
//        1)
        List<Menu> allowedList = new ArrayList<>();
//        2)
        for (Menu m : rootMenus) {
            // 상위 메뉴가 허용돼야 탭도 통과되므로 이중 검사 없이 바로 추가.
            if (m.getUseYn().equals("Y") && m.getTabYn().equals("Y")) allowedList.add(m);
            if (m.getUseYn().equals("Y") && allowed.contains(m.getId())) {
                //
                if (m.getChildren() != null && !m.getChildren().isEmpty()) {
                    m.addChildren(filterAllowedMenu(m.getChildren(), allowed));
                }
                allowedList.add(m);
            }
        }
//        3)
        return allowedList;
    }

    public List<MenuDto> getTabs(String parentUrl) {
//        1) validation
        Menu menu = menuRepo.findByUrl(parentUrl).orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, "메뉴")));
        if (menu.getChildren() == null || menu.getChildren().isEmpty())
            throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());
        if (menu.getChildren().get(0).getTabYn().equals("N")) throw new IllegalArgumentException("이 메뉴에는 탭이 없습니다.");

//        2)
        return menu.getChildren().stream().map(Menu::toMenuDto).collect(Collectors.toList());
    }


    // Role 권한이 바뀌어 접근 가능 메뉴가 달라지는 경우 캐시 삭제.
    @CacheEvict(value = "mymenu", key = "#roleNm")
    public void deleteMenuCacheByRoleNm(String roleNm) {
        log.info(roleNm + " 메뉴에 @CacheEvict 가 실행됩니다. " + MyUtil.timestamp());
    }


}