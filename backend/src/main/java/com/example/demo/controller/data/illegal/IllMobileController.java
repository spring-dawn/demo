package com.example.demo.controller.data.illegal;

import com.example.demo.domain.data.illegal.repCustom.IllMobileRepoCustom;
import com.example.demo.dto.data.illegal.IllMobileDto;
import com.example.demo.service.data.illegal.IllMobileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/illegal/mobile",
        produces = "application/json"
)
public class IllMobileController {
    private final IllMobileService service;
    private final IllMobileRepoCustom query;

    @GetMapping
    public ResponseEntity<?> selectList() {
        List<IllMobileDto> res = service.selectList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @GetMapping("/search")
    public ResponseEntity<?> search(IllMobileDto.Keyword req) {
        List<IllMobileDto> res = query.search(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

/*
    @PostMapping
    public ResponseEntity<?> insert(@Validated @RequestBody PResiDto req) {
        PResiDto res = service.insert(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@Validated @RequestBody PResiDto.Keyword req) {
        PResiDto res = service.delete(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }*/
}
