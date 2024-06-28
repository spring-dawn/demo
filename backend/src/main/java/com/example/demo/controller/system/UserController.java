package com.example.demo.controller.system;


import com.example.demo.domain.system.user.UserRepositoryCustom;
import com.example.demo.dto.system.UserDto;
import com.example.demo.service.system.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/system/user",
        produces = "application/json"
)
public class UserController {
    private final UserService userService;
    private final UserRepositoryCustom query;


//    @GetMapping("/users")
//    ResponseEntity<?> selectUsers() {
//        List<UserDto.UserRes> res = userService.selectUsers();
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }


    @GetMapping("/users/{userId}")
    ResponseEntity<?> getUser(@PathVariable String userId) {
        UserDto.UserRes res = userService.selectUser(userId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // 회원가입 아이디 중복 조회
    @GetMapping("/isDuplicate/{userId}")
    ResponseEntity<?> isDuplicate(@PathVariable String userId) {
        boolean res = userService.isDuplicate(userId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search")
    ResponseEntity<?> searchUsers(UserDto.UserReq req) {
        List<UserDto.UserRes> res = query.searchUsers(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    // 시스템관리 - 사용자 페이지에서 계정 추가 [231107] > 사용 안함
//    @PostMapping("/users")
//    ResponseEntity<?> insert(@Validated @RequestBody UserDto.SignUpReq req) {
//        UserDto.UserRes res = userService.createUser(req);
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }

    // 회원가입
    @PostMapping("/signup")
    ResponseEntity<?> postSignUp(@Validated @RequestBody UserDto.SignUpReq req) {
        UserDto.UserRes res = userService.createUser(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    //  패스워드 찾기 전 신원 확인
    @PostMapping("/whoIam")
    ResponseEntity<?> findPwStep(@Validated @RequestBody UserDto.UserReq req) {
        UserDto.UserRes userRes = userService.sendTmpPw(req);
        return new ResponseEntity<>(userRes, HttpStatus.OK);
    }

    @PatchMapping("/users")
    ResponseEntity<?> updateUser(@Validated @RequestBody UserDto.UpdateReq req) {
        UserDto.UserRes res = userService.updateUser(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping("/users/adm")
    ResponseEntity<?> updateUserByAdmin(@Validated @RequestBody UserDto.UpdateReq req) {
        UserDto.UserRes res = userService.updateUserByAdmin(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping("/users/dtl")
    ResponseEntity<?> updatePw(@Validated @RequestBody UserDto.UpdateReq req) throws NoSuchAlgorithmException {
        UserDto.UserRes res = userService.updatePw(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @PatchMapping("/quit")
    ResponseEntity<?> quitUser(@Validated @RequestBody UserDto.UpdateReq req) {
        UserDto.UserRes res = userService.quitUser(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/users/{userId}")
    ResponseEntity<?> deleteUser(@PathVariable String userId) {
        UserDto.UserRes res = userService.deleteUser(userId);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

//    @GetMapping("/exchange/{id}")
//    ResponseEntity<?> exchangeAdmYn(@PathVariable Long id) {
//        UserDto.UserRes res = userService.exchangeAdmYn(id);
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }

    @GetMapping("/check/session")
    ResponseEntity<?> checkSessionStatus() {
        return new ResponseEntity<>(userService.checkSessionStatus(), HttpStatus.OK);
    }

    @GetMapping("/check/edit/{roleId}")
    ResponseEntity<?> checkEditAccess(@PathVariable Long roleId, @RequestParam String url) {
        return new ResponseEntity<>(userService.checkEditAccess(roleId, url), HttpStatus.OK);
    }

}
