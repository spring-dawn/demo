package com.example.demo.controller.data;

import com.example.demo.domain.data.research.floorPlan.FloorPlanRepositoryCustom;
import com.example.demo.domain.data.research.report.ReportRepositoryCustom;
import com.example.demo.dto.data.research.FloorPlanDto;
import com.example.demo.dto.data.research.ReportDto;
import com.example.demo.service.data.research.FloorPlanService;
import com.example.demo.service.data.research.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/data/floorPlan")
@RequiredArgsConstructor
public class FloorPlanController {

    private final FloorPlanService floorPlanService;
    private final FloorPlanRepositoryCustom floorPlanRepositoryCustom;

    // 도면 가져오기 (board 데이터)
    @GetMapping("/all")
    public ResponseEntity<?> getShp2Postgres() {
        List<FloorPlanDto.FloorPlanRes> list = floorPlanService.getList();

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // 도면 검색 (board 데이터)
    @GetMapping("/search")
    public ResponseEntity<?> search(FloorPlanDto.FloorPlanSearchReq req) {
        List<FloorPlanDto.FloorPlanRes> floorPlanRes = floorPlanRepositoryCustom.searchFloorPlan(req);

        return new ResponseEntity<>(floorPlanRes, HttpStatus.OK);
    }

    // 도면 저장 (board 데이터, file)
    @PostMapping
    public ResponseEntity<?> insertShpResult(FloorPlanDto.FloorPlanReq planReq) {
        FloorPlanDto.FloorPlanRes insert = floorPlanService.insert(planReq);

        return new ResponseEntity<>(insert, HttpStatus.OK);
    }

    // 도면 수정 (board 데이터)
    @PatchMapping(produces = "application/json")
    public ResponseEntity<?> update(@RequestBody FloorPlanDto.FloorPlanReq planReq) {

        FloorPlanDto.FloorPlanRes update = floorPlanService.update(planReq);

        return new ResponseEntity<>(update, HttpStatus.OK);
    }

    // 도면 삭제 (board 데이터, 파일)
    @DeleteMapping(produces = "application/json")
    public ResponseEntity<?> delete(@RequestParam Long fpNo) {

        floorPlanService.delete(fpNo);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
