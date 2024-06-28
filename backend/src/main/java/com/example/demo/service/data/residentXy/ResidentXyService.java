package com.example.demo.service.data.residentXy;

import com.example.demo.atech.Msg;
import com.example.demo.domain.data.residentXy.ResidentXy;
import com.example.demo.domain.data.residentXy.ResidentXyRepository;
import com.example.demo.domain.survey.data.format.FormatRd;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;
import org.apache.poi.ss.usermodel.DataFormatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.demo.atech.ExcelManager.getCellData;
import static com.example.demo.atech.ExcelManager.readExcelFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResidentXyService {

    private final ResidentXyRepository repo;

    public void insert(File file, String sggCd) {
//        1) validation
        if (file == null) throw new NullPointerException(Msg.NO_FILES.getMsg());

//        3) extract data
        List<FormatRd> list = new ArrayList<>();
        try (XSSFWorkbook wb = readExcelFile(file)) {
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow headerRow = sheet.getRow(3);;
            XSSFRow row;
            XSSFFormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();
            ArrayList<String> headerList = new ArrayList<>();

            // headers
            for (Cell cell : headerRow) {
                String cellData = getCellData(cell, eval, "0");
                if (cellData != null) cellData = cellData.trim();

                headerList.add(cellData);
            }

            ArrayList<ResidentXy> entityList = new ArrayList<ResidentXy>();
            for (int i = 4; i < sheet.getLastRowNum() + 1; i++) {
                row = sheet.getRow(i);
                HashMap<String, String> dataMap = new HashMap<>();

                int cellIdx = 0;
                for (Cell cell : row) {
                    String cellData;

                    if (cellIdx == 8 || cellIdx == 9 || cellIdx == 10 || cellIdx == 11 || cellIdx == 12 || cellIdx == 13 || cellIdx == 14) {
                        DataFormatter dataFormatter = new DataFormatter();
                        cellData = dataFormatter.formatCellValue(cell);
                    } else {
                        cellData = getCellData(cell, eval, "0.######");
                        if (cellData != null) cellData = cellData.trim();
                    }

                    dataMap.put(headerList.get(cellIdx), cellData);

                    cellIdx++;
                }

                // 엔티티 생성
                ResidentXy residentXy = ResidentXy.builder()
                        .sgg(sggCd)
                        .build();

                residentXy.initExcelRow(dataMap);

                entityList.add(residentXy);
            }

            // save. todo: 자동 키 생성이 아니므로 최적화 가능.
            List<ResidentXy> saved = repo.saveAll(entityList);
            log.info("데이터 저장 완료...{}", file.getName());

        } catch (IOException e) {
            log.error("조사표 정리서식-노상주차장 시트를 읽는 중 문제가 발생했습니다.");
        } catch (Exception e) {
            log.error("조사표 정리서식-노상주차장 데이터 적재 중에 이슈가 있습니다. 스택트레이스 확인해주세요.");
            e.printStackTrace();
        }
    }

    public List<ResidentXy> getAll() {
        List<ResidentXy> all = repo.findAll();

        return all;
    }
}
