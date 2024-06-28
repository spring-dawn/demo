//package com.example.demo.backup.rschDoc;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//@Slf4j
//@RestController
//@RequiredArgsConstructor
//@RequestMapping(value = "/api/data/rsch/doc", produces = "application/json")
//public class RschDocController {
//    private final RschDocService service;
//
//    @GetMapping
//    public ResponseEntity<?> selectList() {
//        List<RschDocDto> res = service.selectList();
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }
//
//    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<?> createOne(RschDocDto.Req req
//            , @RequestParam(value = "files", required = false) List<MultipartFile> files
//    ) {
//        RschDocDto res = service.createOne(req, files);
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteOne(@PathVariable Long id) {
//        RschDocDto res = service.deleteOne(id);
//        return new ResponseEntity<>(res, HttpStatus.OK);
//    }
//
//}
