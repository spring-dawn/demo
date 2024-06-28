package com.example.demo.service.data.illegal;

import com.example.demo.atech.Msg;
import com.example.demo.config.mapStruct.MyMapper;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.data.illegal.file.IllData;
import com.example.demo.domain.data.illegal.read.Illegal;
import com.example.demo.domain.data.illegal.read.IllegalRepoCustom;
import com.example.demo.domain.data.illegal.read.IllegalRepository;
import com.example.demo.dto.data.illegal.IllegalDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.atech.ExcelManager.*;
import static com.example.demo.atech.Msg.NO_FILES;
import static com.example.demo.atech.MyUtil.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IllService {
    /*
    불법주정차 단속 서비스 로직. 주로 엑셀 파일 데이터 적재.
     */

    @Value("${spring.servlet.multipart2.standardExcel.download.ill}")
    private String illPath;
    private final String EXCEL_NM = "ILLEGAL.xlsx";
    private final MyMapper mapper;

    private final IllegalRepository repo;
    private final IllegalRepoCustom query;

    /**
     * @param origin
     */
    public void insert(IllData origin) {
//        1) 파일 내용 검사
        FileInfo fi = origin.getAttaches().get(0);
        File file = new File(fi.getFilePath() + fi.getFileNmStored());
        if (!file.exists()) throw new NullPointerException(NO_FILES.getMsg());

//        2) 데이터 읽어들이기
        List<IllegalDto.Req> reqList = new ArrayList<>();
        try (XSSFWorkbook wb = readExcelFile(file)) {
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;
            int endIdx = 31;    // ~위반자주소 열까지 수집.

            for (int i = 2; i < sheet.getLastRowNum() + 1; i++) {
                row = sheet.getRow(i);
                if(row == null || !hasCell(row.getCell(0))) continue;
                // dto 생성, 디폴트값 세팅.
                IllegalDto.Req req = new IllegalDto.Req();
                req.setYear(origin.getYear());
                req.setMonth(origin.getMonth());

                for (Cell cell : row) {
                    String cellData = getCellData(cell, "0", false);
                    mappingIll(req, new CellInfo(cellData, cell.getColumnIndex()));
                    if(cell.getColumnIndex() == endIdx) break;
                }
                reqList.add(req);
            }
        } catch (IOException | NullPointerException e) {
            logErr(e);
        }
        if(reqList.isEmpty()) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());

//        3) 적재
        List<Illegal> list = new ArrayList<>();
        for (IllegalDto.Req req : reqList) {
            list.add(mapper.toIllegal(req));
        }
        List<Illegal> saved = repo.saveAll(list);
        if(saved.size() != list.size()) throw new RuntimeException(getEnum(Msg.MISSING, "주정차과태료 적발대장"));

        origin.updateCollectYn("Y");
    }

    public void excelDownload(HttpServletResponse response, IllegalDto.Keyword req) {
//        1)
        List<IllegalDto> dataList = query.search(req);
        if (dataList.isEmpty()) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());

        XSSFWorkbook wb = readExcelFile(illPath, EXCEL_NM);
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row;

        int rowNo = 2;
        for (IllegalDto dto : dataList) {
            row = sheet.createRow(rowNo++);
            int cellNo = 0;

            row.createCell(cellNo++).setCellValue(dto.getProcessStat());
            row.createCell(cellNo++).setCellValue(dto.getViolationDt());
            row.createCell(cellNo++).setCellValue(dto.getEvidenceNo());
            row.createCell(cellNo++).setCellValue(dto.getCarNo());
            row.createCell(cellNo++).setCellValue(dto.getViolationDtm());
            row.createCell(cellNo++).setCellValue(dto.getViolationPlace());
            row.createCell(cellNo++).setCellValue(dto.getViolatorNm());
            row.createCell(cellNo++).setCellValue(dto.getResidentRegiNo());
            row.createCell(cellNo++).setCellValue(dto.getSchoolZone());
            row.createCell(cellNo++).setCellValue(dto.getFflng());
            row.createCell(cellNo++).setCellValue(dto.getPic());
            row.createCell(cellNo++).setCellValue(dto.getRequestData());
            row.createCell(cellNo++).setCellValue(dto.getIsRll());
            row.createCell(cellNo++).setCellValue(dto.getCode1());
            row.createCell(cellNo++).setCellValue(dto.getViolatedLaw());
            row.createCell(cellNo++).setCellValue(dto.getCarType());
            row.createCell(cellNo++).setCellValue(dto.getCarNm());
            row.createCell(cellNo++).setCellValue(dto.getCapacity());
            row.createCell(cellNo++).setCellValue(dto.getInspectorNm());
            row.createCell(cellNo++).setCellValue(dto.getCameraNm());
            row.createCell(cellNo++).setCellValue(dto.getComment());
            row.createCell(cellNo++).setCellValue(dto.getCode2());
            row.createCell(cellNo++).setCellValue(dto.getFflngNm());
            row.createCell(cellNo++).setCellValue(dto.getLegalStat());
            row.createCell(cellNo++).setCellValue(dto.getCrdnPtn());
            row.createCell(cellNo++).setCellValue(dto.getPreNoticeDay());
            row.createCell(cellNo++).setCellValue(dto.getPreNoticePayTerm());
            row.createCell(cellNo++).setCellValue(dto.getNonPayReason());
            row.createCell(cellNo++).setCellValue(dto.getIsUnrlCar());
            row.createCell(cellNo++).setCellValue(dto.getSpecialVioPlace());
            row.createCell(cellNo++).setCellValue(dto.getViolatorAddr());
        }

        String fileNm = "sample 불법 주정차 단속";
        writeExcelFile(response, wb, fileNm);
    }

    /*
    매핑용
     */
    private void mappingIll(IllegalDto.Req req, CellInfo info) {
        String data = info.cellData;
        
        switch (info.getColIdx()) {
            case 0:
                req.setProcessStat(data);
                break;
            case 1:
                req.setViolationDt(data);
                break;
            case 2:
                req.setEvidenceNo(data);
                break;
            case 3:
                req.setCarNo(data);
                break;
            case 4:
                req.setViolationDtm(data);
                break;
            case 5:
                req.setViolationPlace(data);
                break;
            case 6:
                req.setViolatorNm(data);
                break;
            case 7:
                req.setResidentRegiNo(data);
                break;
            case 8:
                req.setSchoolZone(data);
                break;
            case 9:
                req.setFflng(hasInteger(data));
                break;
            case 10:
                req.setPic(data);
                break;
            case 11:
                req.setRequestData(data);
                break;
            case 12:
                req.setIsRll(data);
                break;
            case 13:
                req.setCode1(data);
                break;
            case 14:
                req.setViolatedLaw(data);
                break;
            case 15:
                req.setCarType(data);
                break;
            case 16:
                req.setCarNm(data);
                break;
            case 17:
                req.setCapacity(hasInteger(data));
                break;
            case 18:
                req.setInspectorNm(data);
                break;
            case 19:
                req.setCameraNm(data);
                break;
            case 20:
                req.setEtc(data);
                break;
            case 21:
                req.setComment(data);
                break;
            case 22:
                req.setCode2(data);
                break;
            case 23:
                req.setFflngNm(data);
                break;
            case 24:
                req.setLegalStat(data);
                break;
            case 25:
                req.setCrdnPtn(data);
                break;
            case 26:
                req.setPreNoticeDay(data);
                break;
            case 27:
                req.setPreNoticePayTerm(data);
                break;
            case 28:
                req.setNonPayReason(data);
                break;
            case 29:
                req.setIsUnrlCar(data);
                break;
            case 30:
                req.setSpecialVioPlace(data);
                break;
            case 31:
                req.setViolatorAddr(data);
                break;
            default:
                break;
        }

    }

}
