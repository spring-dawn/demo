package com.example.demo.controller.data.monthlyReport;

import com.example.demo.domain.data.monthlyReport.repoCustom.PSubIncrsRepoCustom;
import com.example.demo.dto.data.monthlyReport.PSubIncrsDto;
import com.example.demo.service.data.monthlyReport.PSubIncrsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/mr/increase", produces = "application/json")
public class PSubIncrsController {
    private final PSubIncrsService service;
    private final PSubIncrsRepoCustom query;

    @GetMapping
    public ResponseEntity<?> selectList() {
        List<PSubIncrsDto> res = service.selectList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(PSubIncrsDto.Keyword req) {
        List<PSubIncrsDto> res = query.search(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> insert(@Validated @RequestBody PSubIncrsDto req) {
        PSubIncrsDto res = service.insert(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<?> update(@Validated @RequestBody PSubIncrsDto req) {
        PSubIncrsDto res = service.update(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        PSubIncrsDto res = service.delete(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
