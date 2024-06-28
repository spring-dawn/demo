package com.example.demo.controller.system;

import com.example.demo.service.system.LoginLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/system/log/login",
        produces = "application/json"
)
public class LoginLogController {
    private final LoginLogService service;

    @GetMapping
    public ResponseEntity<?> insert() {
        return new ResponseEntity<>(service.insert(), HttpStatus.OK);
    }
}
