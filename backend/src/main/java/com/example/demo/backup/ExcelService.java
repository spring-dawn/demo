package com.example.demo.backup;

import com.example.demo.atech.ExcelManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.atech.ExcelManager.getCellData;
import static com.example.demo.atech.ExcelManager.readExcelFile;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExcelService {
    /*
    엑셀 제작은 코드가 길어지므로 FileInfo 와 클래스를 분리합니다. 별도 엔티티가 필요하지 않습니다.
    DB 내용을 읽어올 때는 readOnly.
     */

    @Value("${spring.servlet.multipart2.standardExcel.download.pf}")
    private String pfPath;


    /**
     * 엑셀 생성 예시
     *
     * @param response 응답
     */
    public void writeExcel_test(HttpServletResponse response) {
//        1) 엑셀 생성
        XSSFWorkbook wb = new XSSFWorkbook();
        XSSFSheet sheet = wb.createSheet();

//        2) 내용
        XSSFRow row;
        int rowNo = 0;
        int cellNo = 0;

        // 첫 줄에 헤더 쓰고 꾸미기
        row = sheet.createRow(rowNo);
        row.setHeightInPoints((float) 30);

        CellStyle basic = ExcelManager.setCustomStyle(wb, true, true, false, true);
        XSSFFont bold20 = ExcelManager.setCustomFont(wb, true, "굴림체", 20);
        basic.setFont(bold20);
//
        String[] header = {"가", "나", "다", "라"};
        for (int i = 0; i < header.length; i++) {
            row.createCell(i).setCellValue(header[i]);
            row.getCell(i).setCellStyle(basic);
        }

        // 둘째줄
        row = sheet.createRow(++rowNo);
        row.createCell(cellNo++).setCellValue("A");
        row.createCell(cellNo++).setCellValue("B");
        row.createCell(cellNo++).setCellValue("C");
        row.createCell(cellNo).setCellValue("D");

        //셋째줄. 병합
        row = sheet.createRow(++rowNo);

        // 빈 엑셀에 처음 만들 때는 createCell() 로 셀 객체를 만들어야만 병합도 할 수 있다
        cellNo = 0;
        row.createCell(cellNo++);
        row.createCell(cellNo++);
        row.createCell(cellNo++);
        row.createCell(cellNo);
        sheet.addMergedRegion(new CellRangeAddress(rowNo, rowNo, row.getFirstCellNum(), row.getLastCellNum() - 1));

        CellStyle mergingStyle = ExcelManager.setCustomStyle(wb, false, true, true, false);
        XSSFFont pt14 = ExcelManager.setCustomFont(wb, false, "맑은 고딕", 14);
        mergingStyle.setFont(pt14);

        row.getCell(0).setCellValue("(주)에이테크");
        row.getCell(0).setCellStyle(mergingStyle);

//        넷째줄. 데이터 포맷 적용.  병합
        row = sheet.createRow(++rowNo);

        row.createCell(0);
        row.createCell(1);
        row.createCell(2);
        row.createCell(3);
        sheet.addMergedRegion(new CellRangeAddress(rowNo, rowNo, row.getFirstCellNum(), row.getLastCellNum() - 1));

        row.getCell(0).setCellValue(Double.parseDouble("123456789"));
//        row.createCell(0).setCellValue(Double.parseDouble("35.23452432240003"));

        // 서식.
        basic.setDataFormat(wb.createDataFormat().getFormat(ExcelManager.FORMAT_COMMA));
//        basic.setDataFormat(wb.createDataFormat().getFormat(ExcelManager.FORMAT_DECIMAL2PLACES));
        for (Cell cell : row) {
            cell.setCellStyle(basic);
        }
//        3) 엑셀 생성, 출력
        String fileNm = "월간보고현황";
        ExcelManager.writeExcelFile(response, wb, fileNm);
    }

    // 아마 연, 월 정보가 들어가야 하긴 할 것. 전월치 데이터를 받아봐야 하니까.
    public void standardPrvDownload(HttpServletResponse response) {
        File excel = new File(pfPath + "PRV.xlsx");
        XSSFWorkbook wb = readExcelFile(excel);
        ExcelManager.writeExcelFile(response, wb, "민영노외 표준양식 다운로드 TEST");
    }

}
