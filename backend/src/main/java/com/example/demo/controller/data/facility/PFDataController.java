package com.example.demo.controller.data.facility;

import com.example.demo.domain.data.facility.file.PFDataRepoCustom;
import com.example.demo.dto.data.UploadDataDto;
import com.example.demo.service.data.facility.PFDataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/facility/file",
        produces = "application/json"
)
public class PFDataController {
    private final PFDataService service;
    private final PFDataRepoCustom query;

    @GetMapping("/{id}")
    public ResponseEntity<?> selectOne(@PathVariable Long id) {
        UploadDataDto res = service.selectOne(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(UploadDataDto.Keyword req) {
        List<UploadDataDto> res = query.search(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<?> insert(UploadDataDto.Req req) {
        UploadDataDto res = service.insert(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity<?> update(@RequestBody UploadDataDto.Req req) {
        UploadDataDto res = service.update(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/collect/{id}")
    public ResponseEntity<?> collectData(@PathVariable Long id) {
        UploadDataDto res = service.collectData(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        UploadDataDto res = service.delete(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/reject/{id}")
    public ResponseEntity<?> reject(@PathVariable Long id) {
        return new ResponseEntity<>(service.reject(id), HttpStatus.OK);
    }


}
