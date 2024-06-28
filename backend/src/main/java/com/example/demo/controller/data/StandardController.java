package com.example.demo.controller.data;

import com.example.demo.domain.data.standardSet.StandardMngRepoCustom;
import com.example.demo.dto.data.standard.StandardSetDto;
import com.example.demo.service.data.standard.StandardMngService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping(value = "/api/data/facility/standard")
@RequiredArgsConstructor
public class StandardController {
    private final StandardMngService service;
    private final StandardMngRepoCustom query;

    @GetMapping("/search")
    public ResponseEntity<?> search(StandardSetDto.Keyword req) {
        return new ResponseEntity<>(service.selectStandardSet(req), HttpStatus.OK);
    }

    @GetMapping("/download")
    public void excelDownload(HttpServletResponse response, StandardSetDto.Keyword req) {
        service.excelDownload(response, req);
    }

}
