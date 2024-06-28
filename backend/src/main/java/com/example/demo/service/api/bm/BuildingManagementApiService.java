package com.example.demo.service.api.bm;

import com.example.demo.atech.ExcelManager;
import com.example.demo.atech.Msg;
import com.example.demo.domain.api.building_management.BuildingManagement;
import com.example.demo.domain.api.building_management.BuildingManagementRepoCustom;
import com.example.demo.domain.api.building_management.BuildingManagementRepository;
import com.example.demo.domain.data.research.shp.ShpResult;
import com.example.demo.domain.data.research.shp.ShpResultRepository;
import com.example.demo.dto.GeoJSONResponse;
import com.example.demo.dto.api.BuildingManagementDto;
import com.example.demo.service.GisService;
import com.example.demo.service.api.geoCoder.GeoCoderService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.demo.atech.ExcelManager.setDefaultStyle;
import static com.example.demo.atech.MyUtil.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class BuildingManagementApiService {

    private final BuildingManagementApiProperties properties;
    private final BuildingManagementRepository buildingManagementRepository;
    private final ShpResultRepository shpResultRepository;
    private final GisService gisService;
    private final BuildingManagementRepoCustom query;

    private final GeoCoderService geoCoderService;

    // 총괄 표제부
    public List<BuildingManagementApiResponse> request1(String sigunguCd, String bjdongCd) throws UnsupportedEncodingException, InterruptedException {
        String BASE_URL = "http://apis.data.go.kr/1613000/BldRgstService_v2/getBrRecapTitleInfo";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        int totalCount = request1TotalCount(sigunguCd, bjdongCd);
        int resultsPerPage = 100; // Number of results to retrieve per page

        List<BuildingManagementApiResponse> allResults = new ArrayList<>();
        int failCount = 0;

        for (int page = 1; page <= Math.ceil((double) totalCount / resultsPerPage); page++) {
            if (failCount > 5) throw new RuntimeException("건축물관리대장 API 호출에 5번 이상 실패했습니다.");

            log.info("BuildingManagemen_1 PAGE {}/{}",page * resultsPerPage, totalCount);
            int finalPage = page;
            Thread.sleep(3000);

            try {
                String response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("serviceKey", properties.serviceKey)
                                .queryParam("sigunguCd", sigunguCd)
                                .queryParam("bjdongCd", bjdongCd)
                                .queryParam("numOfRows", resultsPerPage)
                                .queryParam("pageNo", finalPage)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                JAXBContext context = JAXBContext.newInstance(BuildingManagementApiResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();

                BuildingManagementApiResponse responseObject = (BuildingManagementApiResponse) unmarshaller.unmarshal(new StringReader(response));
                allResults.add(responseObject);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                failCount++;
                page--;
            }
        }

        return allResults;
    }

    public Integer request1TotalCount(String sigunguCd, String bjdongCd) throws UnsupportedEncodingException {
        String BASE_URL = "http://apis.data.go.kr/1613000/BldRgstService_v2/getBrRecapTitleInfo";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        GeoJSONResponse geoJSONResponse = null;

        while (true) {
            try {
                String response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("serviceKey", properties.serviceKey)
                                .queryParam("sigunguCd", sigunguCd)
                                .queryParam("bjdongCd", bjdongCd)
                                .queryParam("numOfRows", 1) // Request only one result to get total count
                                .queryParam("pageNo", 1)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                JAXBContext context = JAXBContext.newInstance(BuildingManagementApiResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();

                BuildingManagementApiResponse responseObject = (BuildingManagementApiResponse) unmarshaller.unmarshal(new StringReader(response));

                return Integer.valueOf(responseObject.getBody().getTotalCount());

            } catch (JAXBException e) {
                log.error("총괄표제부 토탈CNT 가져오는 중 문제발생...");
            }
        }
    }

    // 표제부
    public List<BuildingManagementApiResponse> request2(String sigunguCd, String bjdongCd) throws UnsupportedEncodingException, InterruptedException {
        String BASE_URL = "http://apis.data.go.kr/1613000/BldRgstService_v2/getBrTitleInfo";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        int totalCount = request2TotalCount(sigunguCd, bjdongCd);
        int resultsPerPage = 100;

        List<BuildingManagementApiResponse> allResults = new ArrayList<>();
        int failCount = 0;

        for (int page = 1; page <= Math.ceil((double) totalCount / resultsPerPage); page++) {
            if (failCount > 5) throw new RuntimeException("건축물관리대장 API 호출에 5번 이상 실패했습니다.");

            Thread.sleep(1000);
            log.info("BuildingManagemen_2 PAGE {}/{}",page * resultsPerPage, totalCount);
            try {
                int finalPage = page;

                String response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("serviceKey", properties.serviceKey)
                                .queryParam("sigunguCd", sigunguCd)
                                .queryParam("bjdongCd", bjdongCd)
                                .queryParam("numOfRows", resultsPerPage)
                                .queryParam("pageNo", finalPage)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();


                JAXBContext context = JAXBContext.newInstance(BuildingManagementApiResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();

                BuildingManagementApiResponse responseObject = (BuildingManagementApiResponse) unmarshaller.unmarshal(new StringReader(response));
                allResults.add(responseObject);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                failCount++;
                page--;
            }
        }

        return allResults;
    }

    public Integer request2TotalCount(String sigunguCd, String bjdongCd) throws UnsupportedEncodingException {
        String BASE_URL = "http://apis.data.go.kr/1613000/BldRgstService_v2/getBrTitleInfo";
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(BASE_URL);
        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);

        WebClient webClient = WebClient.builder()
                .uriBuilderFactory(factory)
                .baseUrl(BASE_URL)
                .build();

        GeoJSONResponse geoJSONResponse = null;

        while (true) {
            try {
                String response = webClient.get()
                        .uri(uriBuilder -> uriBuilder
                                .queryParam("serviceKey", properties.serviceKey)
                                .queryParam("sigunguCd", sigunguCd)
                                .queryParam("bjdongCd", bjdongCd)
                                .queryParam("numOfRows", 1) // Request only one result to get total count
                                .queryParam("pageNo", 1)
                                .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .block();

                JAXBContext context = JAXBContext.newInstance(BuildingManagementApiResponse.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();

                BuildingManagementApiResponse responseObject = (BuildingManagementApiResponse) unmarshaller.unmarshal(new StringReader(response));

                return Integer.valueOf(responseObject.getBody().getTotalCount());

            } catch (JAXBException e) {
                log.error("표제부 토탈CNT 가져오는 중 문제발생...");
            }
        }
    }

    // 표제부 (일괄 저장)
    @Transactional
    public List<BuildingManagement> insert2(BuildingManagementApiResponse res) {
        List<BuildingManagementApiResponse.Item> itemList = res.getBody().getItems().getItem();
        List<BuildingManagement> entityList = new ArrayList<>();

        if (itemList != null) {
            for (BuildingManagementApiResponse.Item item : itemList) {
                BuildingManagement entity = BuildingManagement
                        .builder()
                        .bjdongCd(Integer.parseInt(item.getBjdongCd()))
                        .bldNm(item.getBldNm())
                        .bun(Integer.parseInt(item.getBun()))
                        .crtnDay(item.getCrtnDay())
                        .indrAutoArea(item.getIndrAutoArea())
                        .indrAutoUtcnt(item.getIndrAutoUtcnt())
                        .indrMechArea(item.getIndrMechArea())
                        .indrMechUtcnt(item.getIndrMechUtcnt())
                        .ji(Integer.parseInt(item.getJi()))
                        .mainPurpsCdNm(item.getMainPurpsCdNm())
                        .mgmBldrgstPk(item.getMgmBldrgstPk())
                        .naBjdongCd(item.getNaBjdongCd())
                        .naMainBun(item.getNaMainBun())
                        .naRoadCd(item.getNaRoadCd())
                        .naSubBun(item.getNaSubBun())
                        .newPlatPlc(item.getNewPlatPlc())
                        .oudrAutoArea(item.getOudrAutoArea())
                        .oudrAutoUtcnt(item.getOudrAutoUtcnt())
                        .oudrMechArea(item.getOudrMechArea())
                        .oudrMechUtcnt(item.getOudrMechUtcnt())
                        .platPlc(item.getPlatPlc())
                        .sigunguCd(Integer.parseInt(item.getSigunguCd()))
                        .strctCdNm(item.getStrctCdNm())
                        .etcPurps(item.getEtcPurps())
                        .rnum(Integer.parseInt(item.getRnum()))
                        .dongNm(item.getDongNm())
                        .build();

                entityList.add(entity);
            }

            List<BuildingManagement> buildingManagementAlls = buildingManagementRepository.saveAll(entityList);

            return buildingManagementAlls;
        }

        return null;
    }

    // shp 연계 테이블 업데이트
    @Transactional
    public void connectTableUpdate() throws JsonProcessingException {
        List<ShpResult> all = shpResultRepository.findAll();
        List<ShpResult> filter = all.stream().filter(shpResult -> shpResult.getSubType().equals("부설")).collect(Collectors.toList());
        List<Map<String, String>> reqTree = new ArrayList<>();

        for (ShpResult shpResult : filter) {
            HashMap<String, String> reqMap = new HashMap<>();
            reqMap.put("tree0", shpResult.getTableName());
            reqTree.add(reqMap);
        }

        ArrayList<HashMap<String, Object>> searchObject = gisService.searchObject(reqTree, "");

        System.out.println(searchObject);


    }

    @Transactional
    @CacheEvict(value = "pfSubDefault") // 데이터가 변경될 때마다 주차시설-부설주차장 조회 캐시를 초기화합니다.
    public void saveSubPk(String sggCd, String[] dongList) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "5");

        // 기존 부설주차장 lon, lat 값 저장 Map
        HashMap<String, HashMap<String, String>> lonLatStore = new HashMap<>();
        List<BuildingManagement> allBySigunguCd = buildingManagementRepository.findAllBySigunguCd(Integer.parseInt(sggCd));
        allBySigunguCd.stream().forEach(management -> {
            String platPlc = management.getPlatPlc();
            String lon = management.getLon();
            String lat = management.getLat();
            HashMap<String, String> lonLatMap = new HashMap<>();
            lonLatMap.put("lon", lon);
            lonLatMap.put("lat", lat);

            lonLatStore.put(platPlc, lonLatMap);
        });

        // 기존 시군구 데이터 제거
        buildingManagementRepository.deleteAll(allBySigunguCd);

        // 총괄표제부 모든 데이터 가져오기
        List<BuildingManagementApiResponse> allResponseList = new ArrayList<>();

        Arrays.stream(dongList).parallel().forEach(dong -> {
            try {
                List<BuildingManagementApiResponse> responseList = request1(sggCd, dong);

                for (BuildingManagementApiResponse response : responseList) {
                    allResponseList.add(response);
                }

                log.info("--완료 {}/{}", sggCd, dong);

            } catch (UnsupportedEncodingException e) {
                log.error("총괄표제부 데이터 가져오는 중 문제 발생...{}/{}",sggCd,dong);
            } catch (InterruptedException e) {
                log.error("총괄표제부 데이터 가져오는 중 문제 발생...{}/{}",sggCd,dong);
            }
        });

        // 표제부 모든 데이터 가져오기
        List<BuildingManagementApiResponse> allResponseList2 = new ArrayList<>();

        Arrays.stream(dongList).parallel().forEach(dong -> {
            try {
                List<BuildingManagementApiResponse> responseList = request2(sggCd, dong);

                for (BuildingManagementApiResponse response : responseList) {
                    allResponseList2.add(response);
                }

                log.info("--완료 {}/{}", sggCd, dong);

            } catch (UnsupportedEncodingException e) {
                log.error("표제부 데이터 가져오는 중 문제 발생...{}/{}",sggCd,dong);
            } catch (InterruptedException e) {
                log.error("표제부 데이터 가져오는 중 문제 발생...{}/{}",sggCd,dong);
            }
        });

        // 중복제거 후 최종 데이터 모음
        HashMap<String, BuildingManagementApiResponse.Item> objectObjectHashMap = new HashMap<>();

        // 1) 총괄표제부의 데이터를 대지번호 기준으로 중복제거하여 넣는다
        allResponseList.stream().forEach(buildingManagementApiResponse -> {
            buildingManagementApiResponse.getBody().getItems().getItem().stream().forEach(item -> {
                if (!objectObjectHashMap.containsKey(item.getPlatPlc())) {
                    objectObjectHashMap.put(item.getPlatPlc(), item);
                }
            });
        });

        // 2) 표제부의 대지번호, 옥내주차대수, 옥외주차대수 기준으로 중복일 경우 생성일 기준으로 가장 최신것 만 남긴다
        allResponseList2.stream().forEach(buildingManagementApiResponse -> {
            buildingManagementApiResponse.getBody().getItems().getItem().stream().forEach(item -> {

                // 이미 대지번호로 데이터가 있는가?
                if (objectObjectHashMap.containsKey(item.getPlatPlc())) {
                    BuildingManagementApiResponse.Item before = objectObjectHashMap.get(item.getPlatPlc());
                    String beforeKey = before.getIndrAutoUtcnt() + before.getIndrMechUtcnt() + before.getOudrAutoUtcnt() + before.getOudrMechUtcnt();
                    String afterKey = item.getIndrAutoUtcnt() + item.getIndrMechUtcnt() + item.getOudrAutoUtcnt() + item.getOudrMechUtcnt();

                    // 주차면수 key 값이 기존이랑 일치한가?
                    if (beforeKey.equals(afterKey)) {

                        // 생성일이 기존 보다 더 최신인가?
                        if (Integer.valueOf(before.getCrtnDay()) < Integer.valueOf(item.getCrtnDay())) {
                            objectObjectHashMap.put(item.getPlatPlc(), item);
                        }

                        // 일치하지 않음
                    } else {
                        objectObjectHashMap.put(item.getPlatPlc(), item);
                    }

                    // 새로운 대지번호 데이터
                } else {
                    objectObjectHashMap.put(item.getPlatPlc(), item);
                }
            });

        });

        // 모든 데이터 주소 기반으로 지오코딩 진행
        List<BuildingManagement> buildingManagements = objectObjectHashMap.values().parallelStream().map(item -> {
            HashMap<String, String> lonLat = null;

            try {
                if (lonLatStore.containsKey(item.getPlatPlc())) {
                    lonLat = lonLatStore.get(item.getPlatPlc());
                } else {
                    lonLat = geoCoderService.request(item.getPlatPlc(), "PARCEL");
                    System.out.println("geoCoder 실행");
                }

                System.out.println(lonLat);
            } catch (UnsupportedEncodingException e) {
                System.out.println(e.getMessage());
            } catch (JsonProcessingException e) {
                System.out.println(e.getMessage());
            }

            // 만약 지오코딩 결과가 없으면 일단은 null로 등록
            BuildingManagement entity = BuildingManagement
                    .builder()
                    .bjdongCd(Integer.parseInt(item.getBjdongCd()))
                    .bldNm(item.getBldNm())
                    .bun(Integer.parseInt(item.getBun()))
                    .crtnDay(item.getCrtnDay())
                    .indrAutoArea(item.getIndrAutoArea())
                    .indrAutoUtcnt(item.getIndrAutoUtcnt())
                    .indrMechArea(item.getIndrMechArea())
                    .indrMechUtcnt(item.getIndrMechUtcnt())
                    .ji(Integer.parseInt(item.getJi()))
                    .mainPurpsCdNm(item.getMainPurpsCdNm())
                    .mgmBldrgstPk(item.getMgmBldrgstPk())
                    .naBjdongCd(item.getNaBjdongCd())
                    .naMainBun(item.getNaMainBun())
                    .naRoadCd(item.getNaRoadCd())
                    .naSubBun(item.getNaSubBun())
                    .newPlatPlc(item.getNewPlatPlc())
                    .oudrAutoArea(item.getOudrAutoArea())
                    .oudrAutoUtcnt(item.getOudrAutoUtcnt())
                    .oudrMechArea(item.getOudrMechArea())
                    .oudrMechUtcnt(item.getOudrMechUtcnt())
                    .platPlc(item.getPlatPlc())
                    .sigunguCd(Integer.parseInt(item.getSigunguCd()))
                    .strctCdNm(item.getStrctCdNm())
                    .etcPurps(item.getEtcPurps())
                    .rnum(Integer.parseInt(item.getRnum()))
                    .dongNm(item.getDongNm())
                    .lon(lonLat != null ? lonLat.get("lon") : "")
                    .lat(lonLat != null ? lonLat.get("lat") : "")
                    .build();

            return entity;
        }).collect(Collectors.toList());

        buildingManagementRepository.saveAll(buildingManagements);
    }

    public void excelDownload(HttpServletResponse response, BuildingManagementDto.BuildingManagementReq req) {
        List<BuildingManagementDto.BuildingManagementRes> dataList = query.search(req);
        if (dataList.isEmpty()) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());

        try (XSSFWorkbook wb = new XSSFWorkbook()) {
            XSSFSheet sheet = wb.createSheet();
            XSSFRow row;
            // 열 너비 지정
            sheet.setDefaultColumnWidth(12);
            sheet.setColumnWidth(4, 8000);
            sheet.setColumnWidth(5,  15000);
            //
            sheet.setColumnWidth(16, 8000);
            sheet.setColumnWidth(17, 8000);

            String[] header = {
                    "건축물대장번호", "일련번호", "구군", "법정동코드", "건물명", "대지위치", "본번", "부번",
                    "옥내자주식면적(㎡)", "옥내자주식대수(대)", "옥내기계식면적(㎡)", "옥내기계식대수(대)",
                    "옥외자주식면적(㎡)", "옥외자주식대수(대)", "옥외기계식면적(㎡)", "옥외기계식대수(대)",
                    "주용도", "기타용도", "구조", "위도", "경도", "생성일자",
                    "신규주소법정동코드", "신규주소도로코드", "신규주소본번", "신규주소부번"
            };

            XSSFCellStyle headerStyle = setDefaultStyle(wb, true, true, false, true, true, 12);
            row = sheet.createRow(0);
            for (int i = 0; i < header.length; i++) {
                row.createCell(i).setCellValue(header[i]);
                row.getCell(i).setCellStyle(headerStyle);
            }
            sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 25));

            // 데이터. 임시.
            int rowNo = 1;
            for (BuildingManagementDto.BuildingManagementRes dto : dataList) {
                row = sheet.createRow(rowNo++);
                int cellNo = 0;
                String sggNm = getSggCd2Nm(String.valueOf(dto.getSigunguCd()));

                row.createCell(cellNo++).setCellValue(dto.getId());
                row.createCell(cellNo++).setCellValue(dto.getSeq());    // 일련번호
                row.createCell(cellNo++).setCellValue(sggNm);
                row.createCell(cellNo++).setCellValue(dto.getBjdongCd());
                row.createCell(cellNo++).setCellValue(dto.getBldNm());
                row.createCell(cellNo++).setCellValue(dto.getPlatPlc());
                row.createCell(cellNo++).setCellValue(dto.getBun());
                row.createCell(cellNo++).setCellValue(dto.getJi());
                //
                row.createCell(cellNo++).setCellValue(hasDouble(dto.getIndrAutoArea()));
                row.createCell(cellNo++).setCellValue(hasInteger(dto.getIndrAutoUtcnt()));
                row.createCell(cellNo++).setCellValue(hasDouble(dto.getIndrMechArea()));
                row.createCell(cellNo++).setCellValue(hasInteger(dto.getIndrMechUtcnt()));
                //
                row.createCell(cellNo++).setCellValue(hasDouble(dto.getOudrAutoArea()));
                row.createCell(cellNo++).setCellValue(hasInteger(dto.getOudrAutoUtcnt()));
                row.createCell(cellNo++).setCellValue(hasDouble(dto.getOudrMechArea()));
                row.createCell(cellNo++).setCellValue(hasInteger(dto.getOudrMechUtcnt()));
                //
                row.createCell(cellNo++).setCellValue(dto.getMainPurpsCdNm());
                row.createCell(cellNo++).setCellValue(dto.getEtcPurps());
                row.createCell(cellNo++).setCellValue(dto.getStrctCdNm());
                row.createCell(cellNo++).setCellValue(dto.getLat());
                row.createCell(cellNo++).setCellValue(dto.getLot());
                row.createCell(cellNo++).setCellValue(hasInteger(dto.getCrtnDay()));
                //
                row.createCell(cellNo++).setCellValue(dto.getNaBjdongCd());
                row.createCell(cellNo++).setCellValue(dto.getNaRoadCd());
                row.createCell(cellNo++).setCellValue(dto.getNaMainBun());
                row.createCell(cellNo++).setCellValue(dto.getNaSubBun());
            }

            // 서식 적용
            XSSFCellStyle left = setDefaultStyle(wb, false, false, true, false, false, 10);
            XSSFCellStyle center = setDefaultStyle(wb, false, true, false, true, false, 10);

            for (int i = 1; i < dataList.size() + 1; i++) {
                row = sheet.getRow(i);

                for (Cell cell : row) {
                    switch (cell.getColumnIndex()) {
                        case 4:
                        case 5:
                        case 16:
                        case 17:
                            cell.setCellStyle(left);
                            break;
                        default:
                            cell.setCellStyle(center);
                            break;
                    }
                }
            }

            String fileNm = "부설주차장(건축물관리대장)_" + timestamp();
            ExcelManager.writeExcelFile(response, wb, fileNm);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
