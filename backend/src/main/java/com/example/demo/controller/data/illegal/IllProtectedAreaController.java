package com.example.demo.controller.data.illegal;

import com.example.demo.domain.data.illegal.repCustom.IllProtectedAreaRepoCustom;
import com.example.demo.dto.data.illegal.IllKeywordDto;
import com.example.demo.dto.data.illegal.IllProtectedAreaDto;
import com.example.demo.service.data.illegal.IllProtectedAreaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/illegal/protected",
        produces = "application/json"
)
public class IllProtectedAreaController {
    private final IllProtectedAreaService service;

    private final IllProtectedAreaRepoCustom custom;

    @GetMapping("/search")
    public ResponseEntity<?> search(IllProtectedAreaDto.Keyword req) {
        List<HashMap> search = service.search(req);
        return new ResponseEntity<>(search, HttpStatus.OK);
    }

    @GetMapping("/db/search")
    public ResponseEntity<?> searchDb(IllKeywordDto req) {
        List<IllProtectedAreaDto> search = custom.search(req);
        return new ResponseEntity<>(search, HttpStatus.OK);
    }
}
