package com.example.demo.controller.survey;

import com.example.demo.domain.survey.data.mngCard.RschMngCardRepoCustom;
import com.example.demo.dto.survey.mngCard.RschSummaryDto;
import com.example.demo.service.survey.RschMngCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "/api/data/rsch/mngCard")
@RequiredArgsConstructor
public class RschController {
    /*
    실태조사 관리카드(총괄표) 데이터 리스트
     */
    private final RschMngCardService service;
    private final RschMngCardRepoCustom query;


    @GetMapping("/search")
    public ResponseEntity<?> selectSummaryList(RschSummaryDto.Keyword req) {
        return new ResponseEntity<>(query.search(req), HttpStatus.OK);
    }


}
