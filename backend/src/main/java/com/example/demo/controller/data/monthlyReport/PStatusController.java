package com.example.demo.controller.data.monthlyReport;

import com.example.demo.domain.data.monthlyReport.repoCustom.PStatusRepoCustom;
import com.example.demo.dto.data.monthlyReport.PStatusDto;
import com.example.demo.service.data.monthlyReport.PStatusService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/mr/status",
        produces = "application/json"
)
public class PStatusController {
    private final PStatusService service;
    private final PStatusRepoCustom query;
    @GetMapping
    public ResponseEntity<?> selectList() {
        List<PStatusDto> res = service.selectList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(PStatusDto.Keyword req) {
        List<PStatusDto> res = query.search(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
/*
    @GetMapping("/total")
    public ResponseEntity<?> selectMonthlyTotal(PStatusDto.Keyword req) {
        Map<String, PStatusDto> res = query.selectMonthlyTotal(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
*/
    //total 경우 월 데이터 미입력시 각 구군별 가장최근 월의 데이터를 불러옴
    @GetMapping("/total")
    public ResponseEntity<?> selectMonthlyTotal(PStatusDto.Keyword req) {
        Map<String, Object> res;
        if (req.getSggCd()== null || req.getSggCd().equals("")) {
            res = service.selectSggTotal(req);
        }
        else
            res = query.selectMonthlyTotal(req);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    //total_main 경우 전체 데이터중 가장 최근 데이터의 월을 체크후 해당 월로 전체 시군구 조회(해당월에 해당하는 데이터가 없을시 0
    @GetMapping("/total-main")
    public ResponseEntity<?> selectMonthlyMainTotal(PStatusDto.Keyword req) {
        Map<String, Object> res;
        PStatusDto lastData = query.findLastOne(req);
        req.setYear(lastData.getYear());
        req.setMonth(lastData.getMonth());
        res = service.selectSggMainTotal(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    //total_main 경우 전체 데이터중 가장 최근 데이터의 월을 체크후 해당 월로 전체 시군구 조회(해당월에 해당하는 데이터가 없을시 0
    @GetMapping("/total-range")
    public ResponseEntity<?> selectMonthlyMainTotal(@RequestParam("range") int range)
    {
        PStatusDto.Keyword keyword = new PStatusDto.Keyword();
        PStatusDto lastData = query.findLastOne(keyword);
        keyword.setYear(lastData.getYear());
        keyword.setMonth(lastData.getMonth());
        ArrayList<Map<String, Object>> res = service.selectSggRangeTotal(keyword, range);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    //월간보고중 가장 최신데이터확인
    @GetMapping("/last-mr")
    public ResponseEntity<?> selectLastMonthlyReport(PStatusDto.Keyword req) {
        PStatusDto res = query.findLastOne(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    //월간보고 연간데이터
    /*
    @GetMapping("/pkLotYear")
    public ResponseEntity<?> selectYearPkLot(PStatusDto.Keyword req) {
        Map<String, Object> res;
        PStatusDto lastData = query.findLastOne(req);
        req.setYear(lastData.getYear());
        req.setSggCd(lastData.getSggCd());
        req.setMonth(lastData.getMonth());
        res = service.selectPkLotYear(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }
*/
    // 전월 누계.
    @GetMapping("/prevTotal")
    public ResponseEntity<?> selectPrevMonthlyTotal(PStatusDto.Keyword req) {
        PStatusDto.Total res = query.prevTotal(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> insert(@Validated @RequestBody PStatusDto req) {
        PStatusDto res = service.insert(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
