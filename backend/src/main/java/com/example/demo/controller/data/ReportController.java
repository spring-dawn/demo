package com.example.demo.controller.data;

import com.example.demo.domain.data.research.report.ReportRepositoryCustom;
import com.example.demo.dto.data.research.ReportDto;
import com.example.demo.service.data.research.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/data/report")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ReportRepositoryCustom reportRepositoryCustom;

    // 보고서 가져오기 (board 데이터)
    @GetMapping("/all")
    public ResponseEntity<?> getShp2Postgres() {
        List<ReportDto.ReportRes> report = reportService.getReportAll();

        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    // 보고서 검색 (board 데이터)
    @GetMapping("/search")
    public ResponseEntity<?> search(ReportDto.ReportSearchReq req) {
        List<ReportDto.ReportRes> report = reportRepositoryCustom.searchReport(req);

        return new ResponseEntity<>(report, HttpStatus.OK);
    }

    // 보고서 저장 (board 데이터)
    @PostMapping
    public ResponseEntity<?> insertShpResult(ReportDto.ReportReq reportReq) {
        ReportDto.ReportRes reportRes = reportService.insertReport(reportReq);

        return new ResponseEntity<>(reportRes, HttpStatus.OK);
    }

    // 보고서 수정 (board 데이터)
    @PatchMapping(produces = "application/json")
    public ResponseEntity<?> update(@RequestBody ReportDto.ReportReq reportReq) {

        ReportDto.ReportRes reportRes = reportService.update(reportReq);

        return new ResponseEntity<>(reportRes, HttpStatus.OK);
    }

    // 보고서 삭제 (board 데이터, 파일)
    @DeleteMapping(produces = "application/json")
    public ResponseEntity<?> delete(@RequestParam Long reportNo) {

        reportService.delete(reportNo);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
