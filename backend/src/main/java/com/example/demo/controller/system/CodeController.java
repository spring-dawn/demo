package com.example.demo.controller.system;

import com.example.demo.dto.system.CodeDto;
import com.example.demo.service.system.CodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//@RequiredArgsConstructor
@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/system/code",
        produces = "application/json"
)
public class CodeController {
    private final CodeService codeService;


    @GetMapping("/codes")
    ResponseEntity<?> selectList1Lv() {
        List<CodeDto> res = codeService.selectList();

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/codes/search/{name}")
    ResponseEntity<?> selectCodeByName(@PathVariable("name") String name) {
        CodeDto res = codeService.selectCodeByName(name);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/codes/{id}")
    ResponseEntity<?> selectCode(@PathVariable Long id) {
//        CodeDto.CodeRes res = codeService.selectCode(id);
        CodeDto res = codeService.selectCode(id);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PostMapping("/codes")
    ResponseEntity<?> insert(@Validated @RequestBody CodeDto.CodeReq req) {
        CodeDto res = codeService.createCode(req);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @PatchMapping("/codes")
    ResponseEntity<?> update(@Validated @RequestBody CodeDto.CodeReq req){
        CodeDto res = codeService.updateCode(req);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @DeleteMapping("/codes/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        CodeDto res = codeService.deleteCode(id);

        return new ResponseEntity<>(res, HttpStatus.OK);
    }


}