package com.example.demo.controller.data.facility;

import com.example.demo.domain.data.facility.read.PFOpenRepoCustom;
import com.example.demo.domain.data.facility.read.PFPrivateRepoCustom;
import com.example.demo.dto.data.facility.PFOpenDto;
import com.example.demo.dto.data.facility.PFPrivateDto;
import com.example.demo.service.data.facility.PFPrivateService;
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
@RequestMapping(value = "/api/data/facility/read/prv",
        produces = "application/json"
)
public class PFPrivateController {
    private final PFPrivateService service;

    private final PFPrivateRepoCustom query;

    @GetMapping("/search")
    public ResponseEntity<?> searchPrv(PFPrivateDto.Keyword req) {
        List<PFPrivateDto> res = query.search(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/standard/download")
    public void standardDownloadPrv(HttpServletResponse response, PFPrivateDto.Keyword req) {
        service.excelDownload(response, req);
    }

    @GetMapping("/standard/manager/keyword")
    public ResponseEntity<?> getKeyword4manager() {
        return new ResponseEntity<>(service.getKeyword4manager(), HttpStatus.OK);
    }

    // update
    @PatchMapping
    public ResponseEntity<?> update(@RequestBody PFPrivateDto.Req req){
        return new ResponseEntity<>(service.update(req), HttpStatus.OK);
    }

    // delete
    @DeleteMapping
    public ResponseEntity<?> delete(@RequestBody PFPrivateDto.Req req){
        return new ResponseEntity<>(service.delete(req), HttpStatus.OK);
    }


}
