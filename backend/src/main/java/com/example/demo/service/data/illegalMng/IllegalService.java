package com.example.demo.service.data.illegalMng;

import com.example.demo.atech.ExcelManager;
import com.example.demo.config.mapStruct.MyMapper;
import com.example.demo.domain.data.illegal.*;
import com.example.demo.domain.data.illegal.repo.*;
import com.example.demo.domain.survey.data.RschDataRepository;
import com.example.demo.dto.data.illegal.IllFixedDto;
import com.example.demo.dto.system.CodeDto;
import com.example.demo.service.system.CodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import static com.example.demo.atech.ExcelManager.getCellData;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IllegalService {
    private final RschDataRepository rschDataRepository;
    private final MyMapper mapper;
    private final IllFixedRepository repoIllFixed;
    private final IllMobileRepository repoIllMobile;
    private final IllBusMountedRepository repoIllBusMounted;
    private final IllFireplugRepository repoIllFireplug;
    private final IllProtectedAreaRepository repoIllProtected;
    private final IllCrdnPrfmncRepository repoIllCrdnPrfmnc;
    private final IllCrdnNocsRepository repoIllCrdnNocs;
    private final CodeService codeService;

    @Value("${spring.servlet.multipart.location}")
    private String filePath;

    // select summary, all, basic
    public List<IllFixedDto> selectIllFixedList() {
        return repoIllFixed.findAll().stream().map(IllFixed::toRes).collect(Collectors.toList());
    }

    /*
    데이터 수집용
     */

    private String zero2blank(String cellResult){
        return cellResult.equals(ExcelManager.ZERO) ? null : cellResult ;
    }

    // jjlee
    @Transactional
    public void insertExcel(String filePath, String fileNm, String sgg, String year, String month) {
        CodeDto codeDto = codeService.selectCodeByName(sgg);
        String sggCode = codeDto.getName();
        String sggName = codeDto.getValue();

        try (XSSFWorkbook wb = ExcelManager.readExcelFile(filePath, fileNm)) {

            fixedExcelHandler(year, month, sggCode, sggName, wb);
//
//            mobileExcelHandler(year, month, sggCode, sggName, wb);
//
//            busMountedExcelHandler(year, month, sggCode, sggName, wb);
//
//            fireplugExcelHandler(year, month, sggCode, sggName, wb);
//
//            protectedExcelHandler(year, month, sggCode, sggName, wb);

//            generalExcelHandler(year, month, sggCode, sggName, wb);

        } catch (IOException e) {
            log.error("불법주정차 시트를 읽는 중 문제가 발생했습니다.");
        } catch (Exception e) {
            log.error("불법주정차 데이터 적재 중에 이슈가 있습니다. 스택트레이스 확인해주세요.");
            e.printStackTrace();
        }
    }

    private void protectedExcelHandler(String year, String month, String sggCode, String sggName, XSSFWorkbook wb) {
        XSSFFormulaEvaluator eval;
        XSSFSheet sheet;
        XSSFRow row;
        ArrayList<String> headerList;
        /* 안전구역 ========================================================== */
        IllProtectedArea illProtectedArea = IllProtectedArea.builder()
                .sgg(sggCode)
                .year(year)
                .month(month)
                .build();

        sheet = wb.getSheet("어린이보호구역 과태료 부과현황");

        eval = wb.getCreationHelper().createFormulaEvaluator();
        headerList = new ArrayList<>();

        // headers
        headerList.add("건수");
        headerList.add("금액");
        int keyIdx1 = 0;
        int keyIdx2 = 0;

        if (sggName.equals("중구")) keyIdx1 = 1;
        keyIdx2 = 2;
        if (sggName.equals("남구")) keyIdx1 = 3;
        keyIdx2 = 4;
        if (sggName.equals("동구")) keyIdx1 = 5;
        keyIdx2 = 6;
        if (sggName.equals("북구")) keyIdx1 = 7;
        keyIdx2 = 8;
        if (sggName.equals("울주군")) keyIdx1 = 9;
        keyIdx2 = 10;

        for (int i = 5; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            XSSFCell monthCell = row.getCell(0);
            HashMap<String, String> dataMap = new HashMap<>();

            if (monthCell.getStringCellValue().equals(month + "월")) {
                int cellIdx = 0;
                for (Cell cell : row) {
                    String cellData;

                    cellData = getCellData(cell, eval, "");
                    if (cellData != null) {
                        cellData = cellData.trim();
                        cellData = cellData.replaceAll(" ", "");
                    }

                    if (keyIdx1 == cellIdx) {
                        dataMap.put(headerList.get(0), cellData);
                    }

                    if (keyIdx2 == cellIdx) {
                        dataMap.put(headerList.get(1), cellData);
                    }

                    cellIdx++;
                }

                // 엔티티 생성
                illProtectedArea.initExcelRow(dataMap, year);
                break;
            }
        }

        repoIllProtected.save(illProtectedArea);
    }

    private void fireplugExcelHandler(String year, String month, String sggCode, String sggName, XSSFWorkbook wb) {
        ArrayList<String> headerList;
        XSSFSheet sheet;
        XSSFFormulaEvaluator eval;
        XSSFRow row;
        /* 소화전 ========================================================== */
        IllFireplug illFireplug = IllFireplug.builder()
                .sgg(sggCode)
                .year(year)
                .month(month)
                .build();

        sheet = wb.getSheet("소화전주변 적색표시구역 단속실적");

        headerList = new ArrayList<>();

        // headers
        headerList.add("구분");
        headerList.add("개소수");
        headerList.add("설치전");
        headerList.add("설치후");
        headerList.add("전월");
        headerList.add("금월");
        headerList.add("누계");

        for (int i = 7; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            XSSFCell sggCell = row.getCell(0);
            HashMap<String, String> dataMap = new HashMap<>();
            eval = wb.getCreationHelper().createFormulaEvaluator();

            if (sggCell.getStringCellValue().equals(sggName)) {
                int cellIdx = 0;
                for (Cell cell : row) {
                    String cellData;

                    cellData = getCellData(cell, eval, "0");
                    if (cellData != null) {
                        cellData = cellData.trim();
                    }

                    dataMap.put(headerList.get(cellIdx), cellData);

                    cellIdx++;
                }

                // 엔티티 생성
                illFireplug.initExcelRow(dataMap, year);
                break;
            }
        }

        repoIllFireplug.save(illFireplug);
    }

    private void busMountedExcelHandler(String year, String month, String sggCode, String sggName, XSSFWorkbook wb) {
        ArrayList<String> headerList;
        XSSFFormulaEvaluator eval;
        XSSFSheet sheet;
        XSSFRow row;
        XSSFRow headerRow;
        /* 버스탑재형 ======================================================= */
        IllBusMounted illBusMounted = IllBusMounted.builder()
                .sgg(sggCode)
                .year(year)
                .month(month)
                .build();
        sheet = wb.getSheet("버스탑재형");

        /* step 1) ======================================================= */
        headerRow = sheet.getRow(2);
        headerList = new ArrayList<>();

        // headers
        for (Cell cell : headerRow) {
            String cellData = getCellData(cell);
            if (cellData != null) {
                cellData = cellData.replaceAll(" ", "");
            }

            headerList.add(cellData);
        }

        for (int i = 5; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            XSSFCell sggCell = row.getCell(0);
            HashMap<String, String> dataMap = new HashMap<>();

            if (sggCell.getStringCellValue().equals(sggName)) {
                int cellIdx = 0;
                for (Cell cell : row) {
                    String cellData;

                    cellData = getCellData(cell);

                    dataMap.put(headerList.get(cellIdx), cellData);

                    cellIdx++;
                }

                // 엔티티 생성
                illBusMounted.initExcelRow1(dataMap, year);

                break;
            }
        }

        /* step 2) ======================================================= */
        eval = wb.getCreationHelper().createFormulaEvaluator();
        headerList = new ArrayList<>();

        // headers
        headerList.add("구분");
        headerList.add("계");
        headerList.add("승용");
        headerList.add("승합");
        headerList.add("화물");
        headerList.add("기타");
        headerList.add("택시");

        for (int i = 15; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            XSSFCell sggCell = row.getCell(0);
            HashMap<String, String> dataMap = new HashMap<>();

            if (sggCell.getStringCellValue().equals(sggName)) {
                int cellIdx = 0;
                for (Cell cell : row) {
                    String cellData;

                    cellData = getCellData(cell, eval, "");

                    dataMap.put(headerList.get(cellIdx), cellData);

                    cellIdx++;
                }

                // 엔티티 생성
                illBusMounted.initExcelRow2(dataMap, year);
                break;
            }
        }

        repoIllBusMounted.save(illBusMounted);
    }

    private void mobileExcelHandler(String year, String month, String sggCode, String sggName, XSSFWorkbook wb) {
        ArrayList<String> headerList;
        XSSFRow headerRow;
        XSSFRow row;
        XSSFFormulaEvaluator eval;
        XSSFSheet sheet;
        /* 이동형 ========================================================== */
        sheet = wb.getSheet("이동식CCTV");
        headerRow = sheet.getRow(2);
        eval = wb.getCreationHelper().createFormulaEvaluator();
        headerList = new ArrayList<>();

        // headers
        for (Cell cell : headerRow) {
            String cellData = getCellData(cell, eval, "");

            if (cellData != null) {
                cellData = cellData.replaceAll(" ", "");
            }

            headerList.add(cellData);
        }

        ArrayList<IllMobile> illMobileArrayList = new ArrayList<IllMobile>();
        boolean start = false; // 시군구 row에 도착했는가?
        int rowIdx = 0; // 시군구 row에 도착한 이후 idx 값
        int rowSeq = 0; // 현재 row의 연번 값
        for (int i = 4; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            XSSFCell sggCell = row.getCell(1);

            // 시군구 셀이 없으면 브레이크
            if (sggCell == null) break;

            // 시군가 값이 일치하는 row에 도착했는가?
            if (sggCell.getStringCellValue().contains(sggName)) start = true;

            // 다음 시군구 row에 도착한 경우 브레이크
            if (rowIdx > 0 && !sggCell.getStringCellValue().isEmpty()) {
                break;
            }

            if (start) {
                rowIdx++;

                // 소계 row 스킵
                if (rowIdx == 1) continue;

                HashMap<String, String> dataMap = new HashMap<>();

                int cellIdx = 0;
                for (Cell cell : row) {
                    String cellData;

                    cellData = getCellData(cell);

                    dataMap.put(headerList.get(cellIdx), cellData);

                    cellIdx++;
                }

                if (dataMap.size() != headerList.size()) {
                    break;
                }

                String rowSeqVal = dataMap.get("연번");

                if (!rowSeqVal.isEmpty()) {
                    rowSeq = Integer.parseInt(rowSeqVal);
                } else {
                    dataMap.put("연번", String.valueOf(rowSeq));
                }

                if (rowSeq == 12) {
                    System.out.println(rowSeq);
                }

                // 엔티티 생성
                IllMobile illMobile = IllMobile.builder()
                        .sgg(sggCode)
                        .year(year)
                        .month(month)
                        .build();

                illMobile.initExcelRow(dataMap, year);

                illMobileArrayList.add(illMobile);
            }
        }

        repoIllMobile.saveAll(illMobileArrayList);
    }

    private void fixedExcelHandler(String year, String month, String sggCode, String sggName, XSSFWorkbook wb) {
        ArrayList<String> headerList;
        XSSFSheet sheet;
        XSSFRow row;
        XSSFRow headerRow;
        XSSFFormulaEvaluator eval;
        /* 고정형 ========================================================== */
        sheet = wb.getSheet("고정형CCTV-" + sggName);
        headerRow = sheet.getRow(2);
        eval = wb.getCreationHelper().createFormulaEvaluator();
        headerList = new ArrayList<>();

        // headers
        for (Cell cell : headerRow) {
            String cellData = getCellData(cell,eval,"0.######");
            if (cellData != null) {
                cellData = cellData.replaceAll(" ", "");
            }

            headerList.add(cellData);
        }

        ArrayList<IllFixed> illFixedArrayList = new ArrayList<IllFixed>();
        for (int i = 5; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            HashMap<String, String> dataMap = new HashMap<>();

            int cellIdx = 0;
            for (Cell cell : row) {
                String cellData;

                cellData = getCellData(cell);

                dataMap.put(headerList.get(cellIdx), cellData);

                cellIdx++;
            }

            if (dataMap.size() != headerList.size()) {
                break;
            }

            // 엔티티 생성
            IllFixed illFixed = IllFixed.builder()
                    .sgg(sggCode)
                    .year(year)
                    .month(month)
                    .build();

            illFixed.initExcelRow(dataMap, year);

            illFixedArrayList.add(illFixed);
        }

        repoIllFixed.saveAll(illFixedArrayList);
    }

    private void generalExcelHandler(String year, String month, String sggCode, String sggName, XSSFWorkbook wb) {
        ArrayList<String> headerList;
        XSSFSheet sheet;
        XSSFRow row;
        XSSFRow headerRow;
        XSSFFormulaEvaluator eval;
        sheet = wb.getSheet("총괄");
        eval = wb.getCreationHelper().createFormulaEvaluator();

        /* 총괄 단속 실적 1) ========================================================== */
        headerList = new ArrayList<>();

        // headers
        String[] gubun = {"고정식", "이동식", "인력단속", "버스탑재형", "안전신문고"};

        headerList.add("구분");
        for (String gb : gubun) {
            headerList.add(gb + " 단속건수");
            headerList.add(gb + " 부과액");
            headerList.add(gb + " 징수건수");
            headerList.add(gb + " 징수액");
            headerList.add(gb + " 징수율");

            if (gb.equals("인력단속")) {
                headerList.add("단속인원");
            }
        }

        for (int i = 3; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            XSSFCell sggCell = row.getCell(0);

            if (sggCell.getStringCellValue().equals(sggName)) {
                HashMap<String, String> dataMap = new HashMap<>();

                int cellIdx = 0;
                for (Cell cell : row) {
                    String cellData;

                    cellData = getCellData(cell, eval, "0.#####");

                    dataMap.put(headerList.get(cellIdx), cellData);

                    cellIdx++;
                }

                for (String gb : gubun) {
                    String crdnNocs = dataMap.get(gb + " 단속건수");
                    String levyAmt = dataMap.get(gb + " 부과액");
                    String clctnNocs = dataMap.get(gb + " 징수건수");
                    String clctnAmt = dataMap.get(gb + " 징수액");
                    String clctnRate = dataMap.get(gb + " 징수율");

                    String crdnNope = "";
                    if (gb.equals("인력단속")) {
                        crdnNope = dataMap.get("단속인원");
                    }

                    // 엔티티 생성
                    IllCrdnPrfmnc illCrdnPrfmnc = IllCrdnPrfmnc.builder()
                            .sgg(sggCode)
                            .year(year)
                            .month(month)
                            .gubun(gb)
                            .crdnNocs(crdnNocs == null || crdnNocs.isEmpty() ? 0 : Integer.parseInt(crdnNocs))
                            .levyAmt(levyAmt == null || levyAmt.isEmpty() ? 0 : Integer.parseInt(levyAmt))
                            .clctnNocs(clctnNocs == null || clctnNocs.isEmpty() ? 0 : Integer.parseInt(clctnNocs))
                            .clctnAmt(clctnAmt == null || clctnAmt.isEmpty() ? 0 : Integer.parseInt(clctnAmt))
                            .clctnRate(clctnRate == null || clctnRate.isEmpty() ? 0 : Double.parseDouble(clctnRate))
                            .crdnNope(crdnNope == null || crdnNope.isEmpty() ? 0 : Integer.parseInt(crdnNope))
                            .build();

                    repoIllCrdnPrfmnc.save(illCrdnPrfmnc);
                }

                break;
            }
        }

        /* 총괄 단속 건수 2) ========================================================== */
        headerList = new ArrayList<>();

        // headers
        String[] gubun2 = {"단속건수", "견인건수"};

        headerList.add("구분");
        for (String gb : gubun2) {
            headerList.add(gb + " 소계");
            headerList.add(gb + " 승용");
            headerList.add(gb + " 승합");
            headerList.add(gb + " 화물");
            headerList.add(gb + " 기타");
        }
        headerList.add("금액");

        for (int i = 24; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            XSSFCell sggCell = row.getCell(0);

            if (sggCell.getStringCellValue().equals(sggName)) {
                HashMap<String, String> dataMap = new HashMap<>();

                int cellIdx = 0;
                for (Cell cell : row) {
                    String cellData;

                    cellData = getCellData(cell, eval, "0");

                    dataMap.put(headerList.get(cellIdx), cellData);

                    cellIdx++;
                }

                for (String gb : gubun2) {
                    String sum = dataMap.get(gb + " 소계");
                    String crdnCar = dataMap.get(gb + " 승용");
                    String crdnVan = dataMap.get(gb + " 승합");
                    String crdnTruck = dataMap.get(gb + " 화물");
                    String crdnEtc = dataMap.get(gb + " 기타");
                    String amt = dataMap.get("금액");

                    // 엔티티 생성
                    IllCrdnNocs illCrdnNocs = IllCrdnNocs.builder()
                            .sgg(sggCode)
                            .year(year)
                            .month(month)
                            .gubun(gb)
                            .crdnCar( crdnCar == null || crdnCar.isEmpty() ? 0 : Integer.parseInt(crdnCar))
                            .crdnVan( crdnVan == null || crdnVan.isEmpty() ? 0 : Integer.parseInt(crdnVan))
                            .crdnTruck( crdnTruck == null || crdnTruck.isEmpty() ? 0 : Integer.parseInt(crdnTruck))
                            .crdnEtc( crdnEtc == null || crdnEtc.isEmpty() ? 0 : Integer.parseInt(crdnEtc))
                            .sum( sum == null || sum.isEmpty() ? 0 : Integer.parseInt(sum))
                            .amt( amt == null || amt.isEmpty() ? 0 : Integer.parseInt(amt))
                            .build();

                    repoIllCrdnNocs.save(illCrdnNocs);
                }

                break;
            }
        }
    }
}
