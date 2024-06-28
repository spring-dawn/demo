package com.example.demo.controller.data.monthlyReport;

import com.example.demo.domain.data.monthlyReport.repoCustom.PPublicRepoCustom;
import com.example.demo.dto.data.monthlyReport.MrDataDto;
import com.example.demo.dto.data.monthlyReport.PPublicDto;
import com.example.demo.service.data.monthlyReport.PPublicService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/mr/public",
        produces = "application/json"
)
public class PPublicController {
    private final PPublicService service;
    private final PPublicRepoCustom query;

    public ResponseEntity<?> selectList() {
        List<PPublicDto> res = service.selectList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(PPublicDto.Keyword req) {
        List<PPublicDto> res = query.search(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> insert(@Validated @RequestBody PPublicDto req) {
        PPublicDto res = service.insert(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<?> update(@Validated @RequestBody PPublicDto req) {
        PPublicDto res = service.update(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        PPublicDto res = service.delete(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/excelDownload")
    public void excelDownload(HttpServletResponse response, PPublicDto.Keyword req){
        service.excelDownload(response,req);
    }

}
