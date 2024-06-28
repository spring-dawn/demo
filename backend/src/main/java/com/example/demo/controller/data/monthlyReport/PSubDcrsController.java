package com.example.demo.controller.data.monthlyReport;

import com.example.demo.domain.data.monthlyReport.repoCustom.PSubDcrsRepoCustom;
import com.example.demo.dto.data.monthlyReport.PSubDcrsDto;
import com.example.demo.service.data.monthlyReport.PSubDcrsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/mr/decrease", produces = "application/json")
public class PSubDcrsController {
    private final PSubDcrsService service;
    private final PSubDcrsRepoCustom query;

    @GetMapping
    public ResponseEntity<?> selectList() {
        List<PSubDcrsDto> res = service.selectList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(PSubDcrsDto.Keyword req) {
        List<PSubDcrsDto> res = query.search(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> insert(@Validated @RequestBody PSubDcrsDto req) {
        PSubDcrsDto res = service.insert(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<?> update(@Validated @RequestBody PSubDcrsDto req) {
        PSubDcrsDto res = service.update(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        PSubDcrsDto res = service.delete(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
