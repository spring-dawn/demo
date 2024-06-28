package com.example.demo.controller.data.illegal;

import com.example.demo.dto.data.illegal.IllCrdnPrfmncDto;
import com.example.demo.service.data.illegal.IllCrdnPrfmncService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/illegal/prfmnc",
        produces = "application/json"
)
public class IllCrdnPrfmncController {
    private final IllCrdnPrfmncService service;

    @GetMapping
    public ResponseEntity<?> selectList() {
        List<IllCrdnPrfmncDto> res = service.selectList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
