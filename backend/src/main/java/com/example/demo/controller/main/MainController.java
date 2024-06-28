package com.example.demo.controller.main;

import com.example.demo.domain.data.illegal.IllCrdnNocs;
import com.example.demo.domain.data.illegal.repCustom.IllCrdnPrfmncRepoCustom;
import com.example.demo.domain.data.illegal.repCustom.IllFirePlugRepoCustom;
import com.example.demo.domain.data.illegal.repCustom.IllNocsRepoCustom;
import com.example.demo.domain.data.illegal.repCustom.IllProtectedAreaRepoCustom;
import com.example.demo.domain.data.monthlyReport.repoCustom.PStatusRepoCustom;
import com.example.demo.dto.data.illegal.*;
import com.example.demo.dto.data.monthlyReport.PStatusDto;
import com.example.demo.service.api.kosis.KosisService;
import com.example.demo.service.data.monthlyReport.PStatusService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RestController
@RequestMapping(value = "/api/main")
@RequiredArgsConstructor
public class MainController {

    private final KosisService kosisService;
    private final PStatusRepoCustom pStatusRepoCustom;
    private final IllProtectedAreaRepoCustom illProtectedAreaRepoCustom;
    private final IllFirePlugRepoCustom illFirePlugRepoCustom;
    private final IllCrdnPrfmncRepoCustom illCrdnPrfmncRepoCustom;

    private final IllNocsRepoCustom illNocsRepoCustom;

    @GetMapping("/kosis/pop")
    public ResponseEntity<?> getKosisPop() throws UnsupportedEncodingException, JsonProcessingException {
        PStatusDto lastOne = pStatusRepoCustom.findLastOne(new PStatusDto.Keyword());
        String baseDate = lastOne.getYear() + lastOne.getMonth() + "01";;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate endDate = LocalDate.parse(baseDate, formatter);

        String endYear = String.valueOf(endDate.getYear());
        String endMonth = String.format("%02d", endDate.getMonth().getValue());
        String paramEnd = endYear + endMonth;

        List<Map<String, String>> requestPop = kosisService.requestPop(paramEnd,paramEnd);

        return new ResponseEntity<>(requestPop, HttpStatus.OK);
    }

    @GetMapping("/kosis/car")
    public ResponseEntity<?> getKosisCar() throws UnsupportedEncodingException, JsonProcessingException {
        PStatusDto lastOne = pStatusRepoCustom.findLastOne(new PStatusDto.Keyword());
        String baseDate = lastOne.getYear() + lastOne.getMonth() + "01";;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate endDate = LocalDate.parse(baseDate, formatter);
        LocalDate startDate = endDate.minusMonths(11L);

        String startYear = String.valueOf(startDate.getYear());
        String startMonth = String.format("%02d", startDate.getMonth().getValue());

        String endYear = String.valueOf(endDate.getYear());
        String endMonth = String.format("%02d", endDate.getMonth().getValue());

        List<Map<String, String>> requestCar = kosisService.requestCar(startYear + startMonth, endYear + endMonth);

        return new ResponseEntity<>(requestCar, HttpStatus.OK);
    }

    @GetMapping("/kosis/month/car")
    public ResponseEntity<?> getKosisMonthCar() throws UnsupportedEncodingException, JsonProcessingException {
        List<Map<String, String>> requestCar = kosisService.requestMonthCar();

        return new ResponseEntity<>(requestCar, HttpStatus.OK);
    }

    @GetMapping("/ill")
    public ResponseEntity<?> getChartData() {
        PStatusDto lastOne = pStatusRepoCustom.findLastOne(new PStatusDto.Keyword());
        String baseDate = lastOne.getYear() + lastOne.getMonth() + "01";;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate endDate = LocalDate.parse(baseDate, formatter);

        String endYear = String.valueOf(endDate.getYear());
        String endMonth = String.format("%02d", endDate.getMonth().getValue());

        IllKeywordDto req = new IllKeywordDto();
        req.setYear(endYear);
        req.setMonth(endMonth);

        // 버스탑재형
        List<IllCrdnPrfmncDto> illCrdnPrfmncDtos = illCrdnPrfmncRepoCustom.search(req);
        List<IllCrdnPrfmncDto> busData = illCrdnPrfmncDtos.stream().filter(illCrdnPrfmncDto -> illCrdnPrfmncDto.getGubun().equals("버스탑재형")).collect(Collectors.toList());

        // 소화전
        List<IllFireplugDto> fireplugData = illFirePlugRepoCustom.search(req);

        // 어린이보호구역
        List<IllProtectedAreaDto> protectedAreaData = illProtectedAreaRepoCustom.search(req);

        // 단속건수 현황
        List<IllCrdnNocsDto> illCrdnNocsData = illNocsRepoCustom.search(req);

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("bus", busData);
        resultMap.put("fireplug", fireplugData);
        resultMap.put("protectedArea", protectedAreaData);
        resultMap.put("nocs", illCrdnNocsData);

        return new ResponseEntity<>(resultMap, HttpStatus.OK);
    }
}
