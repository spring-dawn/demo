package com.example.demo.controller.data.illegal;

import com.example.demo.dto.data.illegal.IllCrdnNocsDto;
import com.example.demo.service.data.illegal.IllCrdnNocsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/illegal/nocs",
        produces = "application/json"
)
public class IllCrdnNocsController {
    private final IllCrdnNocsService service;

    @GetMapping
    public ResponseEntity<?> selectList() {
        List<IllCrdnNocsDto> res = service.selectList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
