package com.example.demo.controller.data.illegal;

import com.example.demo.domain.data.illegal.repCustom.IllFixedRepoCustom;
import com.example.demo.dto.data.illegal.IllFixedDto;
import com.example.demo.dto.data.illegal.IllegalDto;
import com.example.demo.service.data.illegal.IllFixedService;
import com.example.demo.service.data.illegal.IllService;
import com.example.demo.service.data.illegal.IllegalTmpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/data/illegal/fixed",
        produces = "application/json"
)
public class IllFixedController {
    private final IllFixedService service;
    private final IllService newIllService;
    private final IllegalTmpService tmpService;
    private final IllFixedRepoCustom query;

    @GetMapping
    public ResponseEntity<?> selectList() {
        List<IllFixedDto> res = service.selectList();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(IllFixedDto.Keyword req) {
        List<IllFixedDto> res = query.search(req);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


}
