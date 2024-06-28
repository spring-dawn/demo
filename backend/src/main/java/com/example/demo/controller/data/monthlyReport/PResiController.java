package com.example.demo.controller.data.monthlyReport;

import com.example.demo.domain.data.monthlyReport.repoCustom.PResiRepoCustom;
import com.example.demo.dto.data.monthlyReport.PResiDto;
import com.example.demo.service.data.monthlyReport.PResiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/mr/resi",
        produces = "application/json"
)
public class PResiController {
    private final PResiService service;
    private final PResiRepoCustom query;

    @GetMapping
    public ResponseEntity<?> selectList() {
        List<PResiDto> res = service.selectList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(PResiDto.Keyword req) {
        List<PResiDto> res = query.search(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> insert(@Validated @RequestBody PResiDto req) {
        PResiDto res = service.insert(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@Validated @RequestBody PResiDto.Keyword req) {
        PResiDto res = service.delete(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
}
