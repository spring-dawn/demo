//package com.example.demo.controller;
//
//import com.example.demo.config.security.JwtTokenProvider;
//import com.example.demo.dto.system.UserDto;
//import com.example.demo.service.system.UserService;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.validation.annotation.Validated;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpSession;
//import java.security.NoSuchAlgorithmException;
//import java.security.spec.InvalidKeySpecException;
//
////@RequiredArgsConstructor
//@RestController
//@RequestMapping(
//        value = "/api/",
//        produces = "application/json"
//)
//public class RootController {
////    @Autowired
//    private final UserService userService;
////    @Autowired
//    private final JwtTokenProvider jwtProvider;
//
//    public RootController(UserService userService, JwtTokenProvider jwtProvider){
//        this.userService = userService;
//        this.jwtProvider = jwtProvider;
//    }
//
//    @GetMapping("/hi")
//    ResponseEntity<?> getHi(HttpSession session, HttpServletRequest req){
//        System.out.println(session);
//        return new ResponseEntity<>("hi", HttpStatus.OK);
//    }
//
//    @GetMapping("/hi2")
//    ResponseEntity<?> getHi2(HttpSession session, HttpServletRequest req){
//        System.out.println(session.getId());
//        return new ResponseEntity<>("hi2", HttpStatus.OK);
//    }
//
//    @GetMapping("/hi3")
//    ResponseEntity<?> getHi3(HttpSession session, HttpServletRequest req){
//        return new ResponseEntity<>("로그인한사람만볼수있습니다.", HttpStatus.OK);
//    }
//}