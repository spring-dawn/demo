package com.example.demo.controller.analy;

import com.example.demo.dto.survey.mngCard.RschSummaryDto;
import com.example.demo.service.survey.RschMngCardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "/api/analy")
@RequiredArgsConstructor
public class AnalyController {
    private final RschMngCardService service;


    @GetMapping("/summary/quadrant")
    public ResponseEntity<?> selectSummaryList(@RequestParam("sgg") String sgg) {
//        PK1 = dataStore.pfTotal / dataStore.pdTotal; // 주차장 확보율
//        PK3 = (dataStore.pdRDIn + dataStore.pdOutSum + dataStore.pdSubSum) / dataStore.pfTotal; // 주차장 이용률
//        PK9 = pfSubSum - pdSubSum; // 유휴 주차규모
//        PK7 = (dataStore.pdRDOut + dataStore.pdRDIll) / dataStore.pdTotal; // 불법 주차율


        List<RschSummaryDto> res = service.selectSummaryList(sgg);

//        HashMap<String, ResearchSDto> blockMap = new HashMap<>();
//
//        res.stream().forEach(researchSDto -> {
//            researchSDto.setIdle(0.0);
//            researchSDto.setUse(0.0);
//            researchSDto.setSecure(0.0);
//            researchSDto.setIlegal(0.0);
//            blockMap.put(researchSDto.getBlock(), researchSDto);
//        });
//
//        List<ResearchSDto> collect = res.stream().map(researchSDto -> {
//            Double pfTotal = Double.parseDouble(researchSDto.getPfTotal());
//            Double pdTotal = Double.parseDouble(researchSDto.getPdTotal());
//            Double pdRDIn = Double.parseDouble(researchSDto.getPdRDIn());
//            Double pdOutSum = Double.parseDouble(researchSDto.getPdOutSum());
//            Double pdSubSum = Double.parseDouble(researchSDto.getPdSubSum());
//            Double pfSubSum = Double.parseDouble(researchSDto.getPfSubSum());
//            Double pdRDOut = Double.parseDouble(researchSDto.getPdRDOut());
//            Double pdRDIll = Double.parseDouble(researchSDto.getPdRDIll());
//
//            Double use = pfTotal / pdTotal;
//            Double secure = (pdRDIn + pdOutSum + pdSubSum) / pfTotal;
//            Double ilegal = pfSubSum - pdSubSum;
//            Double idle = (pdRDOut + pdRDIll) / pdTotal;
//
//            ResearchSDto blockData = blockMap.get(researchSDto.getBlock());
//
//            blockData.setUse(blockData.getUse() + use);
//            blockData.setSecure(blockData.getSecure() + secure);
//            blockData.setIlegal(blockData.getIlegal() + ilegal);
//            blockData.setIdle(blockData.getIdle() + idle);
//
//            return researchSDto;
//        }).collect(Collectors.toList());

        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
