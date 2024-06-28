package com.example.demo.service.system;

import com.example.demo.atech.Msg;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.domain.system.user.access.*;
import com.example.demo.dto.system.RoleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.demo.atech.MyUtil.getEnum;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoleService {
    private final UserRepository userRepo;
    private final RoleRepository roleRepo;
    private final PrivilegeRepository privilegeRepo;
    private final RolePrivilegeRepository rolePrivilegeRepo;
    private final MenuService menuService;


    public RoleDto selectRole(Long id){
        Role role = roleRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("No such role."));

        return role.toRes();
    }


    public List<RoleDto> selectList(){
        return roleRepo.findAllByUseYn("Y")
                .stream().map(Role::toRes)
                .collect(Collectors.toList());
    }


    /**
     * 권한 컨테이너 Role 생성
     * @param req ex) ROLE_ASDF, 한글명, 내용 설명, 필요한 권한들(Privileges)
     * @return RoleDto
     */
    @Transactional
    public RoleDto createRole(RoleDto.Req req){
//        1) null or dup check
        if(!StringUtils.hasText(req.getName())) throw new NullPointerException("The name cannot be empty.");
        if(roleRepo.findByName(req.getName()).isPresent())
            throw new EntityExistsException("This name already exists.");

//        2) create and save Role first.
        Role role = Role.builder()
//                TODO: 스프링의 권한명은 ROLE_ 형식이 권장되는데 앞이나 뒤에서 사용자 입력값을 제어할 필요가 있습니다.
                .name("ROLE_"+req.getName()) // ROLE_ASDF
                .encodedNm(req.getEncodedNm())
                .comment(req.getComment())
                .build();
        roleRepo.save(role);

//        3) make privilege list.
        List<Privilege> privileges = new ArrayList<>();
        for(Long pId : req.getPrivileges()){
            // 사용자가 db에 없는 권한명(혹은 권한 id)을 고르는 경우는 없어야 합니다.
//            Privilege privilege = privilegeRepo.findByName(nm).orElseThrow(EntityNotFoundException::new);
            Privilege privilege = privilegeRepo.findById(pId).orElseThrow(EntityNotFoundException::new);
            privileges.add(privilege);
        }

//        4) create medium entity and add to Role
        List<RolePrivilege> totalList = new ArrayList<>();
        for (Privilege p : privileges) {
            RolePrivilege rolePrivilege = RolePrivilege.builder()
                    .role(role)
                    .privilege(p)
                    .build();
            totalList.add(rolePrivilegeRepo.save(rolePrivilege));
        }
        role.addPrivileges(totalList);

//        5) res
        return role.toRes();
    }


    /**
     * 권한 컨테이너 내용 변경
     * @param req 이름, 비고 등 기본정보. 변경되는 권한 목록의 개별 id.
     * @return 변경 반영
     */
    @Transactional
    public RoleDto updateRole(RoleDto.Req req) {
//        1) target
        Role role = roleRepo.findByName(req.getName())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, "역할")));

//        2) uk check
        if (!StringUtils.hasText(req.getName()))
            throw new NullPointerException("The name cannot be empty.");

//       수정 시엔 이미 같은 이름이 1개 이상 있는 게 당연한데, id 가 같으면 동일 개체로 통과. 다르면 중복 방어.
//        TODO: 더 좋은 방법이 있으면 교체
        List<Role> list = roleRepo.findAll();
        for (Role r : list) {
            if (req.getName().equals(r.getName()) && req.getId() != r.getId()) {
                throw new EntityExistsException("This role name already exists. Try again.");
            }
        }

//        3) update: 있는 권한을 뺄 수도, 없는 권한을 더할 수도 있으므로 일반적인 update 개념 X. 기존 엔티티 삭제 > 추가.
        if (req.getPrivileges() != null && !req.getPrivileges().isEmpty()) {
            // 기존 권한 내역과 새 권한 입력값을 비교, 서로 같으면 변동 X
            List<Long> previous = new ArrayList<>();
            for (RolePrivilege rp : role.getPrivileges()) {
                previous.add(rp.getPrivilege().getId());
            }

            if (previous.size() != req.getPrivileges().size() || !new HashSet<>(previous).containsAll(req.getPrivileges())) {
                // 기존 연결 엔티티들 모두 삭제.
                rolePrivilegeRepo.deleteAll(role.getPrivileges());

                // 사용자 입력값을 받아 새 권한 목록 생성
                List<Privilege> privileges = new ArrayList<>();
                for (Long pId : req.getPrivileges()) {
                    Privilege privilege = privilegeRepo.findById(pId).orElseThrow(EntityNotFoundException::new);
                    privileges.add(privilege);
                }

                // 새 연결 엔티티 생성, 추가
                List<RolePrivilege> totalList = new ArrayList<>();
                for (Privilege p : privileges) {
                    RolePrivilege rolePrivilege = RolePrivilege.builder()
                            .role(role)
                            .privilege(p)
                            .build();

                    totalList.add(rolePrivilegeRepo.save(rolePrivilege));
                }
                role.addPrivileges(totalList);
                // 권한 내용이 바뀐 경우 기존 메뉴 캐시 삭제
                menuService.deleteMenuCacheByRoleNm(role.getName());
            }
        }
        role.updateRole(req);

//        4) res
        return role.toRes();
    }

    @Transactional
    public RoleDto updateRoleTmp(RoleDto.TmpReq req) {
        Role role = roleRepo.findByName(req.getName().toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, "역할")));

        role.updateRole(req);
        return role.toRes();
    }


    @Transactional
    public RoleDto deleteRole(Long id) {
//        1) target
        Role role = roleRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, "역할")));

//        2) validation: at least one user have this role, do not delete
        if (userRepo.countByRole(role) > 0) throw new DataIntegrityViolationException(getEnum(Msg.ALREADY_USE, "역할"));
//        TODO: 2) 대안 -> 사용중인 롤을 삭제할 때는 해당 롤이 적용된 사용자들의 롤을 일괄 디폴트로 변경 후 삭제
//        List<User> applied = userRepo.findAll().stream().filter(user -> user.getRole() == role).collect(Collectors.toList());
//        Role defaultRole = roleRepo.findByName("ROLE_USER").orElseThrow(EntityNotFoundException::new);
//        for(User user : applied){
//            user.updateRole(defaultRole);
//        }

//        3) delete and res
        roleRepo.delete(role);
        return role.toRes();
    }

    @Transactional
    public RoleDto deleteRole(String roleNm) {
//        1) target
        Role role = roleRepo.findByName(roleNm.toUpperCase())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, "역할")));

//        2) validation: at least one user have this role, do not delete
        if (userRepo.countByRole(role) > 0) throw new DataIntegrityViolationException(getEnum(Msg.ALREADY_USE, "역할"));
//        TODO: 2) 대안 -> 사용중인 롤을 삭제할 때는 해당 롤이 적용된 사용자들의 롤을 일괄 디폴트로 변경 후 삭제
//        List<User> applied = userRepo.findAll().stream().filter(user -> user.getRole() == role).collect(Collectors.toList());
//        Role defaultRole = roleRepo.findByName("ROLE_USER").orElseThrow(EntityNotFoundException::new);
//        for(User user : applied){
//            user.updateRole(defaultRole);
//        }

//        3) delete and res
        roleRepo.delete(role);
        return role.toRes();
    }


}
