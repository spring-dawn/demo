package com.example.demo.controller.data.monthlyReport;

import com.example.demo.domain.data.monthlyReport.repoCustom.MrDataRepoCustom;
import com.example.demo.domain.data.monthlyReport.repoCustom.PStatusRepoCustom;
import com.example.demo.dto.data.monthlyReport.MrDataDto;
import com.example.demo.service.data.monthlyReport.MrDataService;
import com.example.demo.service.data.monthlyReport.mrMng.MrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/mr/data",
        produces = "application/json"
)
public class MrDataController {
    private final MrDataService service;
    private final MrService mrService;
    private final MrDataRepoCustom query;
    private final PStatusRepoCustom queryPstatus;

    @GetMapping("/{id}")
    public ResponseEntity<?> selectOne(@PathVariable Long id) {
        MrDataDto res = service.selectOne(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(MrDataDto.Keyword req) {
        List<MrDataDto> res = query.searchMrData(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/standard/manager/keyword")
    public ResponseEntity<?> getKeyword4manager(){
        return new ResponseEntity<>(mrService.getKeyword4manager(), HttpStatus.OK);
    }

    @GetMapping("/excelDownload")
    public void excelDownload(HttpServletResponse response, MrDataDto.Keyword req) {
        //mrService.excelDownload(response,req);
        if (req.getSggCd() == null || req.getSggCd().equals("")) {
            mrService.excelDownload_sggTotal(response, req);

        } else {
            mrService.excelDownload(response, req);
        }
    }

    @GetMapping("/excelFormDownload")
    public void excelFormDownload(HttpServletResponse response, MrDataDto.Keyword req) {
        mrService.excelFormDownload(response, req);

    }

    /* excel download에 통합
    @GetMapping("/excelDownload_sggTotal")
    public void excelDownload_sggTotal(HttpServletResponse response, MrDataDto.Keyword req){
        mrService.excelDownload_sggTotal(response,req);
    }
*/
    @PostMapping
    public ResponseEntity<?> createOne(MrDataDto.Req req) {
        MrDataDto res = service.createOne(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<?> updateOne(@RequestBody MrDataDto.Req req) {
        MrDataDto res = service.updateOne(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/collect/{id}")
    public ResponseEntity<?> collectData(@PathVariable Long id) {
        MrDataDto res = service.collectData(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable Long id) {
        MrDataDto res = service.deleteOne(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        return new ResponseEntity<>(service.reject(id), HttpStatus.OK);
    }


}
