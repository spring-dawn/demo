package com.example.demo.controller.data.facility;

import com.example.demo.domain.data.facility.read.PFOpenRepoCustom;
import com.example.demo.dto.data.facility.PFOpenDto;
import com.example.demo.dto.data.facility.PFPrivateDto;
import com.example.demo.service.data.facility.PFOpenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/facility/read/open",
        produces = "application/json"
)
public class PFOpenController {
    private final PFOpenService service;
    private final PFOpenRepoCustom openQuery;

    private static String subType = "8";
    private static String prvType = "9";


    // 부설개방
    @GetMapping("/sub/search")
    public ResponseEntity<?> searchSubOpen(PFOpenDto.Keyword req) {
        return new ResponseEntity<>(openQuery.search(req, subType), HttpStatus.OK);
    }

    // 부설개방 조회, 다운로드
    @GetMapping("/sub/standard/download")
    public void standardDownloadSub(HttpServletResponse response, PFOpenDto.Keyword req) {
        service.excelDownload(response, req, subType);
    }
    // 부설개방 구군담당자 업로드용 다운로드
    @GetMapping("/sub/standard/manager/keyword")
    public ResponseEntity<?> getKeyword4managerSub() {
        return new ResponseEntity<>(service.getKeyword4manager(subType), HttpStatus.OK);
    }

    // 사유지개방
    @GetMapping("/prv/search")
    public ResponseEntity<?> searchOwnOpen(PFOpenDto.Keyword req) {
        return new ResponseEntity<>(openQuery.search(req, prvType), HttpStatus.OK);
    }
    // 사유지개방 조회, 다운로드
    @GetMapping("/prv/standard/download")
    public void standardDownloadPrv(HttpServletResponse response, PFOpenDto.Keyword req) {
        service.excelDownload(response, req, prvType);
    }
    // 사유지개방 구군담당자 업로드용 다운로드
    @GetMapping("/prv/standard/manager/keyword")
    public ResponseEntity<?> getKeyword4managerPrv() {
        return new ResponseEntity<>(service.getKeyword4manager(prvType), HttpStatus.OK);
    }

    // -----------------
    @PatchMapping
    public ResponseEntity<?> update(@RequestBody PFOpenDto.Req req){
        return new ResponseEntity<>(service.update(req), HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@RequestBody PFOpenDto.Req req){
        return new ResponseEntity<>(service.delete(req), HttpStatus.OK);
    }



}
