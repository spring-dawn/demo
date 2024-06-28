package com.example.demo.service.survey;

import com.example.demo.atech.ExcelManager;
import com.example.demo.atech.Msg;
import com.example.demo.config.mapStruct.MyMapper;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.survey.data.RschData;
import com.example.demo.domain.survey.data.format.*;
import com.example.demo.dto.survey.format.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.atech.ExcelManager.*;
import static com.example.demo.atech.MyUtil.*;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RschFormatService {
    /*
    실태조사 정리 서식 서비스 로직
     */
    
    
    // 자동 바인딩
    private final MyMapper mapper;

    //노상
    private final FormatRdRepository rdRepo;
    //노외 off로 변경필요
    private final FormatOutRepository outRepo;
    //부설
    private final FormatSubRepository subRepo;
    //수요 노상
    private final FormatDmRdRepository dmRdRepo;
    //수요 부설 기타
    private final FormatDmEtcRepository dmEtcRepo;


    //    ======================================================================================================= 이하 서비스 로직
    public List<FormatRdDto> selectRdList() {
        return rdRepo.findAll().stream().map(FormatRd::toRes).collect(Collectors.toList());
    }

    public void insert(RschData origin) {
//        1) file validate
        FileInfo fi = origin.getAttaches().get(0);
        File file = new File(fi.getFilePath() + fi.getFileNmStored());
        if (!file.exists()) throw new NullPointerException(Msg.NO_FILES.getMsg());

//        2) get excel
        String year = origin.getYear();
        String sggCd = origin.getSggCd();

        try (XSSFWorkbook wb = readExcelFile(file)) {
            // 1~5 순서대로, 노상/노외/부설/노상 수요/기타 수요
            XSSFSheet sheet1 = wb.getSheetAt(0);
            XSSFSheet sheet2 = wb.getSheetAt(1);
            XSSFSheet sheet3 = wb.getSheetAt(2);

            // 시트별 데이터 추출
            List<FormatRd> rdList = insertRd(sheet1, year, sggCd);
            List<FormatOut> outList = insertOut(sheet2, year, sggCd);
            List<FormatSub> subList = insertSub(sheet3, year, sggCd);
            List<FormatDmRd> dmRdList = insertDmRd(wb, year, sggCd);
            List<FormatDmEtc> dmEtcList = insertDmEtc(wb, year, sggCd);

            // 데이터 적재
            List<FormatRd> saved1 = rdRepo.saveAll(rdList);
            if (rdList.size() != saved1.size()) throw new RuntimeException(getEnum(Msg.MISSING, "조사표 서식-노상주차장 데이터"));

            List<FormatOut> saved2 = outRepo.saveAll(outList);
            if (outList.size() != saved2.size()) throw new RuntimeException(getEnum(Msg.MISSING, "조사표 서식-노외주차장 데이터"));

            List<FormatSub> saved3 = subRepo.saveAll(subList);
            if (subList.size() != saved3.size()) throw new RuntimeException(getEnum(Msg.MISSING, "조사표 서식-부설주차장 데이터"));

            List<FormatDmRd> saved4 = dmRdRepo.saveAll(dmRdList);
            if (dmRdList.size() != saved4.size())
                throw new RuntimeException(getEnum(Msg.MISSING, "조사표 서식-수요조사(노상) 데이터"));

            List<FormatDmEtc> saved5 = dmEtcRepo.saveAll(dmEtcList);
            if (dmEtcList.size() != saved5.size())
                throw new RuntimeException(getEnum(Msg.MISSING, "조사표 서식-수요조사(노외, 부설, 기타) 데이터"));
        } catch (NullPointerException | IOException e) {
            logErr(e);
            throw new IllegalArgumentException(Msg.COLLECT_DATA_ERR.getMsg());
        }
//        3) check
        origin.updateCollectYn("Y");
        log.info("조사표 서식 데이터 수집 완료 " + timestamp());
    }


    /*
    실태조사 조사표 서식 각 시트별 추출 로직 분리
     */

    public List<FormatRd> insertRd(XSSFSheet sheet, String year, String sggCd) {
//        1) 특수 조건 분리
        boolean isUlju = sggCd.equals("31710"); // 울주군이면 "법정동코드" 대신 "행정리코드" 들어감.
        // 구군에 따른 마지막 셀 인덱스 지정. 북구, 울주군은 25열까지. 나머지는 19열까지. 실제 인덱스는 -1.
        int lastColIdx;
        if (sggCd.equals("31200") || sggCd.equals("31710")) {
            lastColIdx = 24;
        } else {
            lastColIdx = 18;
        }

//        2) 엑셀 유효검사
        if (!sheet.getSheetName().contains("노상"))
            throw new IllegalArgumentException("노상주차장 시트가 1번째에 위치하게 파일을 변경해주세요.");
        List<FormatRd> list = new ArrayList<>();
        XSSFRow row;

//        3) 데이터 추출
        // 3행(2)부터 데이터 시작. 1행 1dto -> mapStruct 로 엔티티에 자동 바인딩.
        for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            // 0셀 연번 값이 빈 경우, 시설형태 값이 빈 경우 건너뛰기
            if (!hasCell(row.getCell(0)) || !hasCell(row.getCell(15))) continue;
            // 일부 디폴트값 세팅
            FormatRdDto req = new FormatRdDto();
            req.setYear(year);
            req.setSggCd(sggCd);

            for (Cell cell : row) {
                String cellData = getCellData(cell, "0", false);
                mappingFmRd(req, new ExcelManager.CellInfo(cellData, cell.getColumnIndex()), isUlju);
                if (cell.getColumnIndex() == lastColIdx) break;
            }
            // 엔티티 생성
            FormatRd entity = mapper.toFormatRd(req);
            list.add(entity);
        }
        return list;
    }

    public List<FormatOut> insertOut(XSSFSheet sheet, String year, String sggCd) {
//        1)
        boolean isUlju = sggCd.equals("31710"); // 울주군이면 "법정동코드" 대신 "행정리코드" 들어감.
        boolean isNamgu = sggCd.equals("31140"); // 남구는 지번주소를 1컬럼으로 기재
        // 구군에 따른 마지막 셀 인덱스 지정. 중구/동구: 20열, 북구/울주군: 25열, 남구: 19열.
        int lastColIdx;
        if (sggCd.equals("31110") || sggCd.equals("31170")) {
            lastColIdx = 19;
        } else if (sggCd.equals("31200") || sggCd.equals("31710")) {
            lastColIdx = 24;
        } else {
            lastColIdx = 18;
        }

//        2)
        if (!sheet.getSheetName().contains("노외")) throw new IllegalArgumentException("노외주차장 시트가 2번째에 위치하게 파일을 변경해주세요.");
        List<FormatOut> list = new ArrayList<>();
        XSSFRow row;

        // 3행부터 시작.
        for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            if (!hasCell(row.getCell(0))) continue;

            FormatOutDto req = new FormatOutDto();
            req.setYear(year);
            req.setSggCd(sggCd);

            for (Cell cell : row) {
                String cellData = getCellData(cell, "0", false);
                mappingFmOut(req, new ExcelManager.CellInfo(cellData, cell.getColumnIndex()), isUlju, isNamgu);
                if (cell.getColumnIndex() == lastColIdx) break;
            }
            FormatOut entity = mapper.toFormatOut(req);
            list.add(entity);
        }
        return list;
    }

    public List<FormatSub> insertSub(XSSFSheet sheet, String year, String sggCd) {
//        1)
        boolean isUlju = sggCd.equals("31710"); // 울주군이면 "법정동코드" 대신 "행정리코드" 들어감.
        boolean isNamgu = sggCd.equals("31140"); // 남구는 지번주소를 1컬럼으로 기재
        // 구군에 따른 마지막 셀 인덱스 지정. 중구/동구: 23열, 북구/울주군: 31열, 남구: 29열(공동주택 컬럼).
        int lastColIdx;
        if (sggCd.equals("31110") || sggCd.equals("31170")) {
            lastColIdx = 22;
        } else if (sggCd.equals("31200") || sggCd.equals("31710")) {
            lastColIdx = 30;
        } else {
            lastColIdx = 28;
        }

//        2)
        if (!sheet.getSheetName().contains("부설")) throw new IllegalArgumentException("부설주차장 시트가 3번째에 위치하게 파일을 변경해주세요.");
        List<FormatSub> list = new ArrayList<>();
        XSSFRow row;

        // 3행부터 시작.
        for (int i = 2; i < sheet.getLastRowNum() + 2; i++) {
            row = sheet.getRow(i);
            // 연번, 시설형태(남구 10, 그 외 11)가 null 인 경우 건너뛰기
            if (!hasCell(row.getCell(0))) continue;
            if (isNamgu) {
                if (!hasCell(row.getCell(10))) continue;
            } else {
                if (!hasCell(row.getCell(11))) continue;
            }

            FormatSubDto req = new FormatSubDto();
            req.setYear(year);
            req.setSggCd(sggCd);

            for (Cell cell : row) {
                String cellData = getCellData(cell, "0", false);
                mappingFmSub(req, new ExcelManager.CellInfo(cellData, cell.getColumnIndex()), isUlju, isNamgu);
                if (cell.getColumnIndex() == lastColIdx) break;
            }
            FormatSub entity = mapper.toFormatSub(req);
            list.add(entity);
        }
        return list;
    }

    public List<FormatDmRd> insertDmRd(XSSFWorkbook wb, String year, String sggCd) {
//        1) 북구/울주군은 주간/야간을 별도 시트로 구성, 컬럼 개수는 모든 구군에서 동일.
        boolean is2Times = sggCd.equals("31200") || sggCd.equals("31710");
        int lastColIdx = 10;

//        2)
        List<XSSFSheet> sheets = new ArrayList<>();
        if (is2Times) {
            // 수요조사(노상) 시트는 북구, 울주군은 따로 취급.
            sheets.add(wb.getSheetAt(3));
            sheets.add(wb.getSheetAt(4));
        } else {
            // 나머지는 단일 시트.
            sheets.add(wb.getSheetAt(3));
        }

//            3) extract data
        List<FormatDmRd> list = new ArrayList<>();
        XSSFRow row;
        for (XSSFSheet sheet : sheets) {
            boolean isDay = sheet.getSheetName().contains("주간");

            for (int i = 2; i < sheet.getLastRowNum() + 2; i++) {
                row = sheet.getRow(i);
                // 건너뛰는 조건: 연번 null. 북구, 울주군이 아니면서 조사시간대가 null.
                if (!hasCell(row.getCell(0))) continue;
                if (!is2Times && !hasCell(row.getCell(5))) continue;

                FormatDmRdDto req = new FormatDmRdDto();
                req.setYear(year);
                req.setSggCd(sggCd);
//
                for (Cell cell : row) {
                    String cellData = getCellData(cell, "0", false);
                    mappingFmDmRd(req, new ExcelManager.CellInfo(cellData, cell.getColumnIndex()), is2Times, isDay);
                    if (cell.getColumnIndex() == lastColIdx) break;
                }
                FormatDmRd entity = mapper.toFormatDmRd(req);
                list.add(entity);
            }
        }
        return list;
    }

    public List<FormatDmEtc> insertDmEtc(XSSFWorkbook wb, String year, String sggCd) {
//        1) 중구, 동구는 지번이 번지/호 2개 컬럼으로 나누어짐. 나머지는 1개.
        int lastColIdx;
        if (sggCd.equals("31110") || sggCd.equals("31170")) {
            lastColIdx = 13;
        } else {
            lastColIdx = 12;
        }
        boolean isOneCol = lastColIdx == 12;

        // 시트 위치. 북구, 울주군은 수요조사(노상)시트가 주/야 2개였으니 1칸 더 밀림.
        int sheetIdx = sggCd.equals("31200") || sggCd.equals("31710") ? 5 : 4;

//        2)
        XSSFSheet sheet = wb.getSheetAt(sheetIdx);
        List<FormatDmEtc> list = new ArrayList<>();
        XSSFRow row;

        for (int i = 2; i < sheet.getLastRowNum() + 2; i++) {
            row = sheet.getRow(i);
            // 시작셀이나 연번, 조사시간대가 null 이면 건너뛰기.
            if (row == null || !hasCell(row.getCell(0)) || !hasCell(row.getCell(5))) continue;

            FormatDmEtcDto req = new FormatDmEtcDto();
            req.setYear(year);
            req.setSggCd(sggCd);

            for (Cell cell : row) {
                String cellData = getCellData(cell, "0", false);
                mappingFmDmEtc(req, new ExcelManager.CellInfo(cellData, cell.getColumnIndex()), isOneCol);
                if (cell.getColumnIndex() == lastColIdx) break;
            }
            FormatDmEtc entity = mapper.toFormatDmEtc(req);
            list.add(entity);
        }
        return list;
    }





    /**
     * 노상주차장 시트 적재
     */
    @Transactional
    public void insertRd(File file, String year, String sggCd) {
//        1) validation
        if (file == null) throw new NullPointerException(Msg.NO_FILES.getMsg());

//        2) read excel
        boolean isUlju = sggCd.equals("31710"); // 울주군이면 "법정동코드" 대신 "행정리코드" 들어감.
        // 구군에 따른 마지막 셀 인덱스 지정. 북구, 울주군은 25열까지. 나머지는 19열까지. 실제 인덱스는 -1.
        int lastColIdx;
        if (sggCd.equals("31200") || sggCd.equals("31710")) {
            lastColIdx = 24;
        } else {
            lastColIdx = 18;
        }

//        3) extract data
        List<FormatRd> list = new ArrayList<>();
        try (XSSFWorkbook wb = readExcelFile(file)) {
            // 노상주차장 시트, 셀 타입 검사기[, 데이터 포맷]
            XSSFSheet sheet = wb.getSheetAt(0);
            if (!sheet.getSheetName().contains("노상"))
                throw new IllegalArgumentException("노상주차장 시트가 1번째에 위치하게 파일을 변경해주세요.");
            XSSFRow row;
            XSSFFormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();

            // 3행(2)부터 데이터 시작. 1행 1dto -> mapStruct 로 엔티티에 자동 바인딩.
            for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
                row = sheet.getRow(i);
                // 0셀 연번 값이 빈 경우, 시설형태 값이 빈 경우 건너뛰기
                if(!hasCell(row.getCell(0)) || !hasCell(row.getCell(15))) continue;

                // 일부 디폴트값 세팅
                FormatRdDto req = new FormatRdDto();
                req.setYear(year);
                req.setSggCd(sggCd);

                for (Cell cell : row) {
                    String cellData = getCellData(cell, "0", false);
                    mappingFmRd(req, new ExcelManager.CellInfo(cellData, cell.getColumnIndex()), isUlju);
                    if (cell.getColumnIndex() == lastColIdx) break;
                }
                // 엔티티 생성
                FormatRd entity = mapper.toFormatRd(req);
                list.add(entity);
            }
            // save. todo: 자동 키 생성이 아니므로 최적화 가능.
            List<FormatRd> saved = rdRepo.saveAll(list);
            if (list.size() != saved.size()) throw new RuntimeException("조사표 정리서식-노상주차장 데이터 적재 중 누락이 발생했습니다.");

        } catch (IOException e) {
            log.error("조사표 정리서식-노상주차장 시트를 읽는 중 문제가 발생했습니다.");
        }
    }

    @Transactional
    public void insertOut(File file, String year, String sggCd) {
//        1) validation
        if (file == null) throw new NullPointerException(Msg.NO_FILES.getMsg());

//        2) read excel
        boolean isUlju = sggCd.equals("31710"); // 울주군이면 "법정동코드" 대신 "행정리코드" 들어감.
        boolean isNamgu = sggCd.equals("31140"); // 남구는 지번주소를 1컬럼으로 기재
        // 구군에 따른 마지막 셀 인덱스 지정. 중구/동구: 20열, 북구/울주군: 25열, 남구: 19열.
        int lastColIdx;
        if (sggCd.equals("31110") || sggCd.equals("31170")) {
            lastColIdx = 19;
        } else if (sggCd.equals("31200") || sggCd.equals("31710")) {
            lastColIdx = 24;
        } else {
            lastColIdx = 18;
        }

        // extract data
        try (XSSFWorkbook wb = readExcelFile(file)) {
            // 노외주차장 시트, 셀 타입 검사기[, 데이터 포맷]
            XSSFSheet sheet = wb.getSheetAt(1);
            if (!sheet.getSheetName().contains("노외"))
                throw new IllegalArgumentException("노외주차장 시트가 2번째에 위치하게 파일을 변경해주세요.");
            XSSFRow row;
            XSSFFormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();

            // 3행부터 시작.
            List<FormatOut> list = new ArrayList<>();
            for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
                row = sheet.getRow(i);
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;

                FormatOutDto req = new FormatOutDto();
                req.setYear(year);
                req.setSggCd(sggCd);

                for (Cell cell : row) {
                    String cellData = getCellData(cell, eval, "0");
                    if (cellData != null) cellData = cellData.trim();

                    mappingFmOut(req, new ExcelManager.CellInfo(cellData, cell.getColumnIndex()), isUlju, isNamgu);
                    if (cell.getColumnIndex() == lastColIdx) break;
                }
                FormatOut entity = mapper.toFormatOut(req);
                list.add(entity);
            }
            List<FormatOut> saved = outRepo.saveAll(list);
            if (saved.size() != list.size()) throw new RuntimeException("조사표 정리서식-노외주차장 데이터 적재 중 누락이 발생했습니다.");
        } catch (IOException | NullPointerException e) {
            logErr(e);
            throw new IllegalArgumentException(Msg.COLLECT_DATA_ERR.getMsg());
        }
    }

    @Transactional
    public void insertSub(File file, String year, String sggCd) {
//        1) validation
        if (file == null) throw new NullPointerException(Msg.NO_FILES.getMsg());

//        2) read excel
        boolean isUlju = sggCd.equals("31710"); // 울주군이면 "법정동코드" 대신 "행정리코드" 들어감.
        boolean isNamgu = sggCd.equals("31140"); // 남구는 지번주소를 1컬럼으로 기재
        // 구군에 따른 마지막 셀 인덱스 지정. 중구/동구: 23열, 북구/울주군: 31열, 남구: 29열(공동주택 컬럼).
        int lastColIdx;
        if (sggCd.equals("31110") || sggCd.equals("31170")) {
            lastColIdx = 22;
        } else if (sggCd.equals("31200") || sggCd.equals("31710")) {
            lastColIdx = 30;
        } else {
            lastColIdx = 28;
        }

        // extract data
        try (XSSFWorkbook wb = readExcelFile(file)) {
            // 부설주차장 시트, 셀 타입 검사기[, 데이터 포맷]
            XSSFSheet sheet = wb.getSheetAt(2);
            if (!sheet.getSheetName().contains("부설"))
                throw new IllegalArgumentException("부설주차장 시트가 3번째에 위치하게 파일을 변경해주세요.");
            XSSFRow row;
            XSSFFormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();

            // 3행부터 시작.
            List<FormatSub> list = new ArrayList<>();
            for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
                row = sheet.getRow(i);
                // 연번, 시설형태(남구 10, 그 외 11)가 null 인 경우 건너뛰기
                if (row.getCell(0) == null || row.getCell(0).getCellType() == CellType.BLANK) continue;
                if (isNamgu) {
                    if (row.getCell(10).getCellType() == CellType.BLANK) continue;
                } else {
                    if (row.getCell(11).getCellType() == CellType.BLANK) continue;
                }

                FormatSubDto req = new FormatSubDto();
                req.setYear(year);
                req.setSggCd(sggCd);

                for (Cell cell : row) {
                    String cellData = getCellData(cell, eval, "0");
                    if (cellData != null) cellData = cellData.trim();

                    mappingFmSub(req, new ExcelManager.CellInfo(cellData, cell.getColumnIndex()), isUlju, isNamgu);
                    if (cell.getColumnIndex() == lastColIdx) break;
                }
                FormatSub entity = mapper.toFormatSub(req);
                list.add(entity);
            }
            List<FormatSub> saved = subRepo.saveAll(list);
            if (saved.size() != list.size()) throw new RuntimeException("조사표 정리서식-부설주차장 데이터 적재 중 누락이 발생했습니다.");
        } catch (IOException | NullPointerException e) {
            logErr(e);
            throw new IllegalArgumentException(Msg.COLLECT_DATA_ERR.getMsg());
        }
    }

    @Transactional
    public void insertDmRd(File file, String year, String sggCd) {
        if (file == null) throw new NullPointerException(Msg.NO_FILES.getMsg());

//        1) 북구/울주군은 주간/야간을 별도 시트로 구성, 컬럼 개수는 모든 구군에서 동일.
        boolean is2Times = sggCd.equals("31200") || sggCd.equals("31710");
        int lastColIdx = 10;

//        2) read excel
        try (XSSFWorkbook wb = readExcelFile(file)) {
            XSSFFormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();

            List<XSSFSheet> sheets = new ArrayList<>();
            if (is2Times) {
                // 수요조사(노상) 시트는 북구, 울주군은 따로 취급.
                sheets.add(wb.getSheetAt(3));
                sheets.add(wb.getSheetAt(4));
            } else {
                // 나머지는 단일 시트.
                sheets.add(wb.getSheetAt(3));
            }

//            3) extract data
            List<FormatDmRd> list = new ArrayList<>();
            XSSFRow row;
            for (XSSFSheet sheet : sheets) {
                boolean isDay = sheet.getSheetName().contains("주간");

                for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
                    row = sheet.getRow(i);
                    // 건너뛰는 조건: 연번 null. 북구, 울주군이 아니면서 조사시간대가 null.
                    if (row.getCell(0).getCellType() == CellType.BLANK) continue;
                    if(!is2Times && row.getCell(5).getCellType() == CellType.BLANK) continue;

                    FormatDmRdDto req = new FormatDmRdDto();
                    req.setYear(year);
                    req.setSggCd(sggCd);
//
                    for (Cell cell : row) {
                        String cellData = getCellData(cell, eval, "0");
                        mappingFmDmRd(req, new ExcelManager.CellInfo(cellData, cell.getColumnIndex()), is2Times, isDay);
                        if (cell.getColumnIndex() == lastColIdx) break;
                    }
                    FormatDmRd entity = mapper.toFormatDmRd(req);
                    list.add(entity);
                }
            }
            List<FormatDmRd> saved = dmRdRepo.saveAll(list);
            if (saved.size() != list.size()) throw new RuntimeException("조사표 정리서식-수요조사(노상) 데이터 적재 중 누락이 발생했습니다.");
        } catch (IOException | NullPointerException e) {
            logErr(e);
            throw new IllegalArgumentException(Msg.COLLECT_DATA_ERR.getMsg());
        }
    }

    @Transactional
    public void insertDmEtc(File file, String year, String sggCd) {
        if (file == null) throw new NullPointerException(Msg.NO_FILES.getMsg());

//        1) 중구, 동구는 지번이 번지/호 2개 컬럼으로 나누어짐. 나머지는 1개.
        int lastColIdx;
        if (sggCd.equals("31110") || sggCd.equals("31170")) {
            lastColIdx = 13;
        } else {
            lastColIdx = 12;
        }
        boolean isOneCol = lastColIdx == 12 ? true : false;

        // 시트 위치. 북구, 울주군은 수요조사(노상)시트가 주/야 2개였으니 1칸 더 밀림.
        int sheetAt = sggCd.equals("31200") || sggCd.equals("31710") ? 5 : 4;

//        2)
        try (XSSFWorkbook wb = readExcelFile(file)) {
            XSSFSheet sheet = wb.getSheetAt(sheetAt);
            XSSFRow row;
            XSSFFormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();

            List<FormatDmEtc> list = new ArrayList<>();
            for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
                row = sheet.getRow(i);
                // 시작셀이나 연번, 조사시간대가 null 이면 건너뛰기.
                if (row == null || row.getCell(0) == null
                        || row.getCell(0).getCellType() == CellType.BLANK
                        || row.getCell(5).getCellType() == CellType.BLANK) continue;

                FormatDmEtcDto req = new FormatDmEtcDto();
                req.setYear(year);
                req.setSggCd(sggCd);

                for (Cell cell : row) {
                    String cellData = getCellData(cell, eval, "0");
                    mappingFmDmEtc(req, new ExcelManager.CellInfo(cellData, cell.getColumnIndex()), isOneCol);
                    if (cell.getColumnIndex() == lastColIdx) break;
                }
                FormatDmEtc entity = mapper.toFormatDmEtc(req);
                list.add(entity);
            }
            if (!list.isEmpty()) {
                List<FormatDmEtc> saved = dmEtcRepo.saveAll(list);
                if (saved.size() != list.size())
                    throw new RuntimeException("조사표 정리서식-수요조사(노외, 부설, 기타) 데이터 적재 중 누락이 발생했습니다.");
            }else {
                log.info("적재할 데이터가 없습니다.");
            }
        } catch (IOException | NullPointerException e) {
            logErr(e);
            throw new IllegalArgumentException(Msg.COLLECT_DATA_ERR.getMsg());
        }
    }



    // 매핑(바인딩) 전용
    private void mappingFmRd(FormatRdDto req, ExcelManager.CellInfo info, boolean isUlju) {
        switch (info.getColIdx()) {
            case 0:
                req.setSeq(info.cellData);
                break;
            case 1:
                req.setTypeP(info.cellData);
                break;
//            case 2:
//                req.setSggCd(info.cellData);
//                break;
            case 3:
                req.setBlockNo(info.cellData);
                break;
            case 4:
                req.setHjdCd(info.cellData);
                break;
            case 5:
                // 법정동 or 행정리(울주군)
                if (isUlju) {
                    req.setHjlCd(info.cellData);
                } else {
                    req.setBjdCd(info.cellData);
                }
                break;
            case 6:
                req.setName(info.cellData);
                break;
            case 7:
                req.setSpcsParked(info.cellData);
                break;
            case 8:
                req.setSpcsTotal(info.cellData);
                break;
            case 9:
                req.setSpcsCommon(info.cellData);
                break;
            case 10:
                req.setSpcsDis(info.cellData);
                break;
            case 11:
                req.setSpcsElec(info.cellData);
                break;
            case 12:
                req.setSpcsEtc(info.cellData);
                break;
            case 13:
                req.setIsPay(info.cellData);
                break;
            case 14:
                req.setPay(info.cellData);
                break;
            case 15:
                req.setTypeF(info.cellData);
                break;
            case 16:
                req.setIsSlope(info.cellData);
                break;
            case 17:
                req.setHasNonSlip(info.cellData);
                break;
            case 18:
                req.setHasInfoSign(info.cellData);
                break;
            case 19:
                req.setIsLegalD(info.cellData);
                break;
            case 20:
                req.setTypeCarD(info.cellData);
                break;
            case 21:
                req.setBlockSeqD(info.cellData);
                break;
            case 22:
                req.setIsLegalN(info.cellData);
                break;
            case 23:
                req.setTypeCarN(info.cellData);
                break;
            case 24:
                req.setBlockSeqN(info.cellData);
                break;
            default:
                break;
        }
    }

    private void mappingFmOut(FormatOutDto req, ExcelManager.CellInfo info, boolean isUlju, boolean isNamgu) {
        if (isNamgu) {
            switch (info.getColIdx()) {
                case 0:
                    req.setSeq(info.cellData);
                    break;
                case 1:
                    req.setTypeP(info.cellData);
                    break;
//            case 2:
//                req.setSggCd(info.cellData);
//                break;
                case 3:
                    req.setBlockNo(info.cellData);
                    break;
                case 4:
                    req.setHjdCd(info.cellData);
                    break;
                case 5:
                    req.setBjdCd(info.cellData);
                    break;
                case 6:
                    if (info.cellData != null && info.cellData.contains("-")) {
                        String[] split = info.cellData.split("-");
                        req.setLotNoAddr1(split[0]);
                        req.setLotNoAddr2(split[1]);
                    } else {
                        req.setLotNoAddr1(info.cellData);
                    }
                    break;
                case 7:
                    req.setIsPub(info.cellData);
                    break;
                case 8:
                    req.setName(info.cellData);
                    break;
                case 9:
                    req.setSpcsTotal(info.cellData);
                    break;
                case 10:
                    req.setTypeF(info.cellData);
                    break;
                case 11:
                    req.setIsPay(info.cellData);
                    break;
                case 12:
                    req.setPay(info.cellData);
                    break;
                case 13:
                    req.setIsSlope(info.cellData);
                    break;
                case 14:
                    req.setHasNonSlip(info.cellData);
                    break;
                case 15:
                    req.setHasInfoSign(info.cellData);
                    break;
                case 16:
                    req.setCctv(info.cellData);
                    break;
                case 17:
                    req.setMonitor(info.cellData);
                    break;
                case 18:
                    req.setBackup(info.cellData);
                    break;
                default:
                    break;
            }
        } else {
            switch (info.getColIdx()) {
                case 0:
                    req.setSeq(info.cellData);
                    break;
                case 1:
                    req.setTypeP(info.cellData);
                    break;
//            case 2:
//                req.setSggCd(info.cellData);
//                break;
                case 3:
                    req.setBlockNo(info.cellData);
                    break;
                case 4:
                    req.setHjdCd(info.cellData);
                    break;
                case 5:
                    // 법정동 or 행정리(울주군)
                    if (isUlju) {
                        req.setHjlCd(info.cellData);
                    } else {
                        req.setBjdCd(info.cellData);
                    }
                    break;
                case 6:
                    req.setLotNoAddr1(info.cellData);
                    break;
                case 7:
                    req.setLotNoAddr2(info.cellData);
                    break;
                case 8:
                    req.setIsPub(info.cellData);
                    break;
                case 9:
                    req.setName(info.cellData);
                    break;
                case 10:
                    req.setSpcsTotal(info.cellData);
                    break;
                case 11:
                    req.setTypeF(info.cellData);
                    break;
                case 12:
                    req.setIsPay(info.cellData);
                    break;
                case 13:
                    req.setPay(info.cellData);
                    break;
                case 14:
                    req.setIsSlope(info.cellData);
                    break;
                case 15:
                    req.setHasNonSlip(info.cellData);
                    break;
                case 16:
                    req.setHasInfoSign(info.cellData);
                    break;
                case 17:
                    req.setCctv(info.cellData);
                    break;
                case 18:
                    req.setMonitor(info.cellData);
                    break;
                case 19:
                    req.setBackup(info.cellData);
                    break;
                case 20:
                    req.setSpcsNon2wD(info.cellData);
                    break;
                case 21:
                    req.setSpcs2wD(info.cellData);
                    break;
                case 22:
                    req.setSpcsNon2wN(info.cellData);
                    break;
                case 23:
                    req.setSpcs2wN(info.cellData);
                    break;
                case 24:
                    req.setGubun(info.cellData);
                    break;
                default:
                    break;
            }
        }
    }

    private void mappingFmSub(FormatSubDto req, ExcelManager.CellInfo info, boolean isUlju, boolean isNamgu) {
        if (isNamgu) {
            switch (info.getColIdx()) {
                case 0:
                    req.setSeq(info.cellData);
                    break;
                case 1:
                    req.setTypeP(info.cellData);
                    break;
//            case 2:
//                req.setSggCd(info.cellData);
//                break;
                case 3:
                    req.setBlockNo(info.cellData);
                    break;
                case 4:
                    req.setHjdCd(info.cellData);
                    break;
                case 5:
                    req.setBjdCd(info.cellData);
                    break;
                case 6:
                    if (info.cellData != null && info.cellData.contains("-")) {
                        String[] split = info.cellData.split("-");
                        req.setLotNoAddr1(split[0]);
                        req.setLotNoAddr2(split[1]);
                    } else {
                        req.setLotNoAddr1(info.cellData);
                    }
                    break;
                case 7:
                    req.setName(info.cellData);
                    break;
                case 8:
                    req.setSpcsTotal(info.cellData);
                    break;
                case 9:
                    req.setIsResi(info.cellData);
                    break;
                case 10:
                    req.setTypeF(info.cellData);
                    break;
                case 11:
                    req.setMainUsage(info.cellData);
                    break;
                case 12:
                    req.setIsPay(info.cellData);
                    break;
                case 13:
                    req.setPay(info.cellData);
                    break;
                case 14:
                    req.setChUsage(info.cellData);
                    break;
                case 15:
                    req.setDysfunc(info.cellData);
                    break;
                case 16:
                    req.setIsSlope(info.cellData);
                    break;
                case 17:
                    req.setHasNonSlip(info.cellData);
                    break;
                case 18:
                    req.setHasInfoSign(info.cellData);
                    break;
                case 19:
                    req.setCctv(info.cellData);
                    break;
                case 20:
                    req.setMonitor(info.cellData);
                    break;
                case 21:
                    req.setBackup(info.cellData);
                    break;
                case 28:
                    req.setGubun(info.cellData);
                    break;
                default:
                    break;
            }
        } else {
            switch (info.getColIdx()) {
                case 0:
                    req.setSeq(info.cellData);
                    break;
                case 1:
                    req.setTypeP(info.cellData);
                    break;
//            case 2:
//                req.setSggCd(info.cellData);
//                break;
                case 3:
                    req.setBlockNo(info.cellData);
                    break;
                case 4:
                    req.setHjdCd(info.cellData);
                    break;
                case 5:
                    // 법정동 or 행정리(울주군)
                    if (isUlju) {
                        req.setHjlCd(info.cellData);
                    } else {
                        req.setBjdCd(info.cellData);
                    }
                    break;
                case 6:
                    req.setLotNoAddr1(info.cellData);
                    break;
                case 7:
                    req.setLotNoAddr2(info.cellData);
                    break;
                case 8:
                    req.setName(info.cellData);
                    break;
                case 9:
                    req.setSpcsTotal(info.cellData);
                    break;
                case 10:
                    req.setIsResi(info.cellData);
                    break;
                case 11:
                    req.setTypeF(info.cellData);
                    break;
                case 12:
                    req.setMainUsage(info.cellData);
                    break;
                case 13:
                    req.setIsPay(info.cellData);
                    break;
                case 14:
                    req.setPay(info.cellData);
                    break;
                case 15:
                    req.setChUsage(info.cellData);
                    break;
                case 16:
                    req.setDysfunc(info.cellData);
                    break;
                case 17:
                    req.setIsSlope(info.cellData);
                    break;
                case 18:
                    req.setHasNonSlip(info.cellData);
                    break;
                case 19:
                    req.setHasInfoSign(info.cellData);
                    break;
                case 20:
                    req.setCctv(info.cellData);
                    break;
                case 21:
                    req.setMonitor(info.cellData);
                    break;
                case 22:
                    req.setBackup(info.cellData);
                    break;
                case 23:
                    req.setSpcsNon2wD(info.cellData);
                    break;
                case 24:
                    req.setSpcs2wD(info.cellData);
                    break;
                case 25:
                    req.setSpcsNon2wN(info.cellData);
                    break;
                case 26:
                    req.setSpcs2wN(info.cellData);
                    break;
                case 27:
                    req.setRoadNmAddr(info.cellData);
                    break;
                case 28:
                    req.setRoadNmNo1(info.cellData);
                    break;
                case 29:
                    req.setRoadNmNo2(info.cellData);
                    break;
                case 30:
                    req.setGubun(info.cellData);
                    break;
                default:
                    break;
            }
        }
    }

    private void mappingFmDmRd(FormatDmRdDto req, ExcelManager.CellInfo info, boolean is2Times, boolean isDay) {
        switch (info.getColIdx()) {
            case 0:
                req.setSeq(info.cellData);
                break;
//            case 1:
//                req.setSggCd(info.cellData);
//                break;
            case 2:
                req.setBjdCd(info.cellData);
                break;
            case 3:
                req.setHjdCd(info.cellData);
                break;
            case 4:
                req.setBlockNo(info.cellData);
                break;
            case 5:
                // 주간야간 구분 필요.
                if (is2Times) {
                    req.setRschTime(isDay ? "1" : "2");
                } else {
                    req.setRschTime(info.cellData);
                }
                break;
            case 6:
                req.setCarNo(info.cellData);
                break;
            case 7:
                req.setIsLegal(info.cellData);
                break;
            case 8:
                req.setTypeC(info.cellData);
                break;
            case 9:
                req.setIsRegi(info.cellData);
                break;
            case 10:
                req.setRmYn(info.cellData);
                break;
            default:
                break;
        }
    }

    private void mappingFmDmEtc(FormatDmEtcDto req, ExcelManager.CellInfo info, boolean isOneCol) {
        if (isOneCol) {
            switch (info.getColIdx()) {
                case 0:
                    req.setSeq(info.cellData);
                    break;
//                case 1:
//                    req.setSggCd(info.cellData);
//                    break;
                case 2:
                    req.setBjdCd(info.cellData);
                    break;
                case 3:
                    req.setHjdCd(info.cellData);
                    break;
                case 4:
                    req.setBlockNo(info.cellData);
                    break;
                case 5:
                    req.setRschTime(info.cellData);
                    break;
                case 6:
                    if (info.cellData != null && info.cellData.contains("-")) {
                        String[] split = info.cellData.split("-");
                        req.setLotNoAddr1(split[0]);
                        req.setLotNoAddr2(split[1]);
                    } else {
                        req.setLotNoAddr1(info.cellData);
                    }
                    break;
                case 7:
                    req.setSpcsTotal(info.cellData);
                    break;
                case 8:
                    req.setSpcsNon2w(info.cellData);
                    break;
                case 9:
                    req.setSpcs2w(info.cellData);
                    break;
                case 10:
                    req.setUseGubun(info.cellData);
                    break;
                case 11:
                    req.setIsRegi(info.cellData);
                    break;
                case 12:
                    req.setRmYn(info.cellData);
                    break;
                default:
                    break;
            }
        } else {
            switch (info.getColIdx()) {
                case 0:
                    req.setSeq(info.cellData);
                    break;
//                case 1:
//                    req.setSggCd(info.cellData);
//                    break;
                case 2:
                    req.setBjdCd(info.cellData);
                    break;
                case 3:
                    req.setHjdCd(info.cellData);
                    break;
                case 4:
                    req.setBlockNo(info.cellData);
                    break;
                case 5:
                    req.setRschTime(info.cellData);
                    break;
                case 6:
                    req.setLotNoAddr1(info.cellData);
//                    if (info.cellData != null && info.cellData.contains("-")) {
//                        String[] split = info.cellData.split("-");
//                        req.setLotNoAddr1(split[0]);
//                        req.setLotNoAddr2(split[1]);
//                    } else {
//                        req.setLotNoAddr1(info.cellData);
//                    }
                    break;
                case 7:
                    req.setLotNoAddr2(info.cellData);
                    break;
                case 8:
                    req.setSpcsTotal(info.cellData);
                    break;
                case 9:
                    req.setSpcsNon2w(info.cellData);
                    break;
                case 10:
                    req.setSpcs2w(info.cellData);
                    break;
                case 11:
                    req.setUseGubun(info.cellData);
                    break;
                case 12:
                    req.setIsRegi(info.cellData);
                    break;
                case 13:
                    req.setRmYn(info.cellData);
                    break;
                default:
                    break;
            }
        }
    }
}
