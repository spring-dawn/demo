package com.example.demo.service.survey;

import com.example.demo.atech.ExcelManager;
import com.example.demo.atech.Msg;
import com.example.demo.config.mapStruct.MyMapper;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.survey.data.RschData;
import com.example.demo.domain.survey.data.mngCard.RschSummary;
import com.example.demo.domain.survey.data.mngCard.RschMngCardRepository;
import com.example.demo.dto.survey.mngCard.RschSummaryDto;
import com.example.demo.dto.system.CodeDto;
import com.example.demo.service.system.CodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
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
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RschMngCardService {
    private final MyMapper mapper;
    private final RschMngCardRepository repo;

    private final CodeService codeService;


    public List<RschSummaryDto> selectSummaryList(String sgg) {
        CodeDto sggCode = codeService.selectCodeByName(sgg);

        return repo.findAll().stream()
                .filter(rschSummary -> rschSummary.getSggNm().equals(sggCode.getValue()))
                .map(RschSummary::toRes)
                .collect(Collectors.toList());
    }


    /**
     * 3년마다 실시하는 실태조사 관리카드 중 총괄표(1시트) 데이터 추출.
     * 취합본에서는 주차수요-빈터 소계(pdEmptySum) 컬럼이 없음.
     *
     * @param origin 데이터 승인한 파일
     */
    public void insert(RschData origin) {
//        1) file validate
        FileInfo fi = origin.getAttaches().get(0);
        File file = new File(fi.getFilePath() + fi.getFileNmStored());
        if (!file.exists()) throw new NullPointerException(Msg.NO_FILES.getMsg());

//        2) get excel
        List<RschSummaryDto.Req> reqList = new ArrayList<>();
        try (XSSFWorkbook wb = ExcelManager.readExcelFile(file)) {
            XSSFSheet sheet = wb.getSheetAt(0);
            if (!sheet.getSheetName().equals("총괄표")) throw new IllegalArgumentException("첫 번째 시트가 실태조사 총괄표인지 확인해주세요.");
            // 마지막 수집열 위치 지정. AR열까지.
            int endIdx = 43;
            XSSFRow row;

//            3) extract data
            for (int i = 5; i < sheet.getLastRowNum(); i++) {
                row = sheet.getRow(i);
                if (row == null) continue;
                // dto 생성(행 데이터), 원본 파일따라 디폴트 세팅.
                RschSummaryDto.Req req = new RschSummaryDto.Req();
                req.setYear(origin.getYear());
                req.setSggNm(getSggCd2Nm(origin.getSggCd()));

                for (Cell cell : row) {
                    // 주차장 확보율~유휴 부설주차규모(개방여력) 까지는 건너뛰기. AF~AP
                    if (cell.getColumnIndex() >= 31 && cell.getColumnIndex() <= 41) continue;

                    // 병합셀인 경우 해당 셀의 첫 행, 첫 열에서 데이터 추출.
                    // 행은 병합이지만 열은 단일. 열 기준으로 데이터 매핑하므로 병합 열이 있는 경우 cell 변수를 재할당하지 않도록 주의합니다.
                    CellRangeAddress range = findMergedRegion(sheet, cell);
                    if (range != null) cell = sheet.getRow(range.getFirstRow()).getCell(range.getFirstColumn());
                    String cellData = getCellData(cell, "0", false);
                    // 셀데이터가 null, "-" 인 경우 "0"으로 변환
                    if (cellData == null || cellData.equals("-")) cellData = ZERO;

                    mappingSummary(req, new CellInfo(cellData, cell.getColumnIndex()));
                    if (cell.getColumnIndex() == endIdx) break;
                }
                reqList.add(req);
            }
        } catch (NullPointerException | IOException e) {
            logErr(e);
            throw new IllegalArgumentException(Msg.COLLECT_DATA_ERR.getMsg());
        }
//        4) entity mapping, save
        if (reqList.isEmpty()) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());

        List<RschSummary> result = new ArrayList<>();
        for (RschSummaryDto.Req req : reqList) {
            result.add(mapper.toRschSummary(req));
        }
        List<RschSummary> saved = repo.saveAll(result);
        if (saved.size() != result.size()) throw new RuntimeException("실태조사 관리카드 총괄표 시트 적재 중 누락이 발생하여 롤백합니다.");

//        5) check
        origin.updateCollectYn("Y");
        log.info("총괄표 데이터 수집 완료 " + timestamp());
    }


    //    ====================================================================================(데이터 매핑
    private void mappingSummary(RschSummaryDto.Req req, CellInfo info) {
        switch (info.getColIdx()) {
//            case 0:
//                // 구군 정보는 파일 디폴트 세팅
//                break;
            case 1:
                req.setHjDong(info.cellData);
                break;
            case 2:
                req.setBlock(info.cellData);
                break;
            case 3:
                req.setDayNight(info.cellData);
                break;
            case 4:
                req.setPop(info.cellData);
                break;
            case 5:
                req.setHouseholds(info.cellData);
                break;
            case 6:
                req.setVehicleCnt(info.cellData);
                break;
            case 7:
                req.setLandUsage(info.cellData);
                break;
            case 8:
                req.setEmptyLands(info.cellData);
                break;
            case 9:
                req.setEmptyArea(info.cellData);
                break;
            case 10:
                req.setPfTotal(info.cellData);
                break;
            case 11:
                req.setPfRDSum(info.cellData);
                break;
            case 12:
                req.setPfRDResi(info.cellData);
                break;
            case 13:
                req.setPfRDEtc(info.cellData);
                break;
            case 14:
                req.setPfOutSum(info.cellData);
                break;
            case 15:
                req.setPfOutPub(info.cellData);
                break;
            case 16:
                req.setPfOutPri(info.cellData);
                break;
            case 17:
                req.setPfSubSum(info.cellData);
                break;
            case 18:
                req.setPfSubResi(info.cellData);
                break;
            case 19:
                req.setPfSubNonRegi(info.cellData);
                break;
            case 20:
                req.setPdTotal(info.cellData);
                break;
            case 21:
                req.setPdRDSum(info.cellData);
                break;
            case 22:
                req.setPdRDIn(info.cellData);
                break;
            case 23:
                req.setPdRDOut(info.cellData);
                break;
            case 24:
                req.setPdRDIll(info.cellData);
                break;
            case 25:
                req.setPdOutSum(info.cellData);
                break;
            case 26:
                req.setPdOutPub(info.cellData);
                break;
            case 27:
                req.setPdOutPri(info.cellData);
                break;
            case 28:
                req.setPdSubSum(info.cellData);
                break;
            case 29:
                req.setPdSubResi(info.cellData);
                break;
            case 30:
                req.setPdSubNonRegi(info.cellData);
                break;
            case 42:
                req.setRegionAnalysis(info.cellData);
                break;
            case 43:
                req.setSolution(info.cellData);
                break;
            default:
                break;
        }
    }

}
