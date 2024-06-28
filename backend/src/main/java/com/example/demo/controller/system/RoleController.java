package com.example.demo.controller.system;

import com.example.demo.dto.system.RoleDto;
import com.example.demo.service.system.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/system/role", produces = "application/json")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService service;

    @GetMapping("/roles/{id}")
    public ResponseEntity<?> selectRole(@PathVariable Long id){
        RoleDto res = service.selectRole(id);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/roles")
    public ResponseEntity<List<?>> selectList(){
        List<RoleDto> res = service.selectList();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

//    @PostMapping("/roles")
//    public ResponseEntity<RoleDto> createRole(@Validated @RequestBody RoleDto.RoleReq req){
//        RoleDto res = service.createRole(req);
//
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }
//
    @PatchMapping("/roles")
    public ResponseEntity<?> updateRole(@Validated @RequestBody RoleDto.Req req){
        return new ResponseEntity<>(service.updateRole(req), HttpStatus.OK);
    }
    @PatchMapping("/roles/tmp")
    public ResponseEntity<?> updateRoleTmp(@Validated @RequestBody RoleDto.TmpReq req){
        return new ResponseEntity<>(service.updateRoleTmp(req), HttpStatus.OK);
    }

    @DeleteMapping("/roles/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable Long id){
        RoleDto res = service.deleteRole(id);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/roles/toUpperCase/{roleNm}")
    public ResponseEntity<?> deleteRoleByNm(@PathVariable String roleNm) {
        return new ResponseEntity<>(service.deleteRole(roleNm), HttpStatus.OK);
    }


}
