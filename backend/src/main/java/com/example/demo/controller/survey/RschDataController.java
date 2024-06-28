package com.example.demo.controller.survey;

import com.example.demo.domain.survey.data.RschDataRepositoryCustom;
import com.example.demo.dto.data.UploadDataDto;
import com.example.demo.service.survey.RschDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/data/rsch/data")
@RequiredArgsConstructor
public class RschDataController {
    /*
    실태조사 파일 업로드 관리 컨트롤러
     */
    private final RschDataService service;
    private final RschDataRepositoryCustom query;

    @GetMapping("/{id}")
    public ResponseEntity<?> selectOne(@PathVariable Long id) {
        UploadDataDto res = service.selectOne(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(UploadDataDto.Keyword req) {
        List<UploadDataDto> res = query.search(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createOne(UploadDataDto.Req req) {
        UploadDataDto res = service.createOne(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/collect/{id}")
    public ResponseEntity<?> collectData(@PathVariable Long id) {
        return new ResponseEntity<>(service.collectData(id), HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<?> updateOne(@RequestBody UploadDataDto.Req req) {
        return new ResponseEntity<>(service.update(req), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable Long id) {
        UploadDataDto res = service.deleteOne(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


}
