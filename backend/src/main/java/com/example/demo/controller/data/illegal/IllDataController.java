package com.example.demo.controller.data.illegal;

import com.example.demo.domain.data.illegal.file.IllDataRepoCustom;
import com.example.demo.dto.data.illegal.IllDataDto;
import com.example.demo.service.data.illegal.IllDataService;
import com.example.demo.service.data.illegal.IllegalTmpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/illegal/data",
        produces = "application/json"
)
public class IllDataController {
    private final IllDataService service;
    private final IllDataRepoCustom query;
    private final IllegalTmpService tmpService;


    @GetMapping("/search")
    public ResponseEntity<?> search(IllDataDto.Keyword req) {
        List<IllDataDto> res = query.searchIllData(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> createOne(IllDataDto.Req req) {
        IllDataDto res = service.createOne(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<?> updateOne(@RequestBody IllDataDto.Req req) {
        IllDataDto res = service.updateOne(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/collect/{id}")
    public ResponseEntity<?> collectData(@PathVariable Long id) {
        IllDataDto res = service.collectData(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteOne(@PathVariable Long id) {
        IllDataDto res = service.deleteOne(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/tmp/download")
    public ResponseEntity<?> tmpFileDownload() {
        Map<String, Object> res = tmpService.tmpExcelDownload4Manager();
        Resource file = (Resource) res.get("file");
        String contentDisposition = (String) res.get("contentDisposition");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(file);
    }

    @GetMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        return new ResponseEntity<>(service.reject(id), HttpStatus.OK);
    }
}

