package com.example.demo.scheduler;

import com.example.demo.dto.system.CodeDto;
import com.example.demo.service.api.bm.BuildingManagementApiService;
import com.example.demo.service.api.fh.FireHydrantApiService;
import com.example.demo.service.api.pa.ProtectedAreaApiService;
import com.example.demo.service.api.pubPk.PubPkApiService;
import com.example.demo.service.api.residnt.ResidntApiService;
import com.example.demo.service.system.CodeService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommonScheduler {

    private final ProtectedAreaApiService protectedAreaApiService;
    private final FireHydrantApiService fireHydrantApiService;
    private final PubPkApiService prkplceApiService;
    private final ResidntApiService residntApiService;
    private final CodeService codeService;
    private final BuildingManagementApiService buildingManagementApiService;

    // TODO: 건축물관리대장 DB 업데이트
    @Scheduled(cron = "0 0 20 1 * *")
    public void subPkScheduler() throws UnsupportedEncodingException, URISyntaxException, InterruptedException {
        log.info("부설주차장 json 저장 스케줄러 실행...");

        Map<String, String[]> paramMap = new HashMap<>();

        paramMap.put("31140", new String[]{
                "10100",
                "10200",
                "10300",
                "10400",
                "10500",
                "10600",
                "10700",
                "10800",
                "10900",
                "11000",
                "11100",
                "11200",
                "11300",
                "11400",
                "11500",
                "11600",
                "11700",
                "11800",
                "11900"
        });


        paramMap.put("31170", new String[]{
                "10100",
                "10200",
                "10300",
                "10400",
                "10500",
                "10600",
                "10700",
                "10800"
        });

        paramMap.put("31200", new String[]{
                "10100",
                "10200",
                "10300",
                "10400",
                "10500",
                "10600",
                "10700",
                "10800",
                "10900",
                "11000",
                "11100",
                "11200",
                "11300",
                "11400",
                "11500",
                "11600",
                "11700",
                "11800",
                "11900",
                "12000",
                "12100",
                "12200",
                "12300",
                "12400",
                "12500",
                "12600",
                "12700"
        });

        paramMap.put("31710", new String[]{
                "25000",
                "25021",
                "25022",
                "25023",
                "25024",
                "25025",
                "25026",
                "25027",
                "25028",
                "25029",
                "25030",
                "25031",
                "25032",
                "25033",
                "25300",
                "25321",
                "25322",
                "25323",
                "25324",
                "25325",
                "25326",
                "25327",
                "25328",
                "25329",
                "25330",
                "25331",
                "25332",
                "25333",
                "25334",
                "25335",
                "25600",
                "25621",
                "25622",
                "25623",
                "25624",
                "25625",
                "25626",
                "25627",
                "25628",
                "25629",
                "25630",
                "25900",
                "25921",
                "25922",
                "25923",
                "25924",
                "25925",
                "25926",
                "25927",
                "25928",
                "25929",
                "25930",
                "26200",
                "26221",
                "26222",
                "26223",
                "26224",
                "26225",
                "26226",
                "26227",
                "26228",
                "26229",
                "26500",
                "26521",
                "26522",
                "26523",
                "26524",
                "26525",
                "31000",
                "31021",
                "31022",
                "31023",
                "31024",
                "31025",
                "31026",
                "31027",
                "31028",
                "31029",
                "31030",
                "34000",
                "34021",
                "34022",
                "34023",
                "34024",
                "34025",
                "34026",
                "34027",
                "34028",
                "34029",
                "36000",
                "36021",
                "36022",
                "36023",
                "36024",
                "36025",
                "36026",
                "36027",
                "36028",
                "37000",
                "37021",
                "37022",
                "37023",
                "37024",
                "37025",
                "37026",
                "37027",
                "37028",
                "37029",
                "38000",
                "38021",
                "38022",
                "38023",
                "38024",
                "38025",
                "38026",
                "38027",
                "38028",
                "38029",
                "38030",
                "38032",
                "38033",
                "38034",
                "40000",
                "40021",
                "40022",
                "40023",
                "40024",
                "40025",
                "40026",
                "40027"
        });

        paramMap.put("31110", new String[]{
                "10100",
                "10200",
                "10300",
                "10400",
                "10500",
                "10600",
                "10700",
                "10800",
                "10900",
                "11000",
                "11100",
                "11200",
                "11300",
                "11400",
                "11500",
                "11600",
                "11700",
                "11800"
        });

        for (String sggCd : paramMap.keySet()) {
            buildingManagementApiService.saveSubPk(sggCd, paramMap.get(sggCd));
        }

        log.info("부설주차장 저장완료...");
    }

    // 보호구역 json 파일 업데이트
    @Scheduled(cron = "0 0 22 1 * *")
    public void protectedAreaScheduler() throws UnsupportedEncodingException, URISyntaxException {
        log.info("보호구역 json 저장 스케줄러 실행...");

        String response = protectedAreaApiService.request("31");
        boolean isSave = protectedAreaApiService.saveFile(response);

        if (isSave) {
            log.info("보호구역 json 저장 성공");
        } else {
            log.info("보호구역 json 저장 실패");
        }
    }

    // 소방전 json 파일 업데이트
    @Scheduled(cron = "0 20 22 1 * *")
    public void fireHydrantScheduler() throws UnsupportedEncodingException, URISyntaxException, JsonProcessingException {
        log.info("소방전 json 저장 스케줄러 실행...");

        String response = fireHydrantApiService.request();
        boolean isSave = fireHydrantApiService.saveFile(response);

        if (isSave) {
            log.info("소방전 json 저장 성공");
        } else {
            log.info("소방전 json 저장 실패");
        }
    }

    // 주차장 공영 현황 json 파일 업데이트
//    @Scheduled(cron = "0 40 22 1 * *")
//    public void prkplceScheduler() throws UnsupportedEncodingException, URISyntaxException, JsonProcessingException, InterruptedException {
//        log.info("주차장 공영 현황 json 저장 스케줄러 실행...");
//
//        String response = prkplceApiService.request();
//        boolean isSave = prkplceApiService.saveFile(response);
//
//        if (isSave) {
//            log.info("주차장 공영 현황 json 저장 성공");
//        } else {
//            log.info("주차장 공영 현황 json 저장 실패");
//        }
//
//        log.info("주차장 공영 현황 json 저장 스케줄러 종료");
//    }

    // 거주자 우선 주차장 현황 json 파일 업데이트
//    @Scheduled(cron = "0 0 23 1 * *")
//    public void residntScheduler() throws UnsupportedEncodingException, JsonProcessingException, InterruptedException {
//        log.info("거주자 우선 주차장 현황 json 저장 스케줄러 실행...");
//
//        String response = residntApiService.request();
//        boolean isSave = residntApiService.requestAndSaveJson(response);
//
//        if (isSave) {
//            log.info("거주자 우선 주차장 현황 json 저장 성공");
//        } else {
//            log.info("거주자 우선 주차장 현황 json 저장 실패");
//        }
//
//        log.info("거주자 우선 주차장 현황 json 저장 스케줄러 종료");
//    }
}
