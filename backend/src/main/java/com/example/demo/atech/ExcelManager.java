package com.example.demo.atech;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static com.example.demo.atech.Msg.FILE_EXT_INCORRECT;
import static com.example.demo.atech.MyUtil.*;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
public class ExcelManager {
    /*
    엑셀 관련 유틸리티
     */
    private ExcelManager() {
        // 인스턴스화를 방지하기 위한 private 생성자
    }

    /*
    자주 쓰는 엑셀 데이터 포맷
    ex) CellStyle.setDataFormat(wb.createDataFormat().getFormat("#,##0"));
     */
    public static final String format = ".xlsx";
    public static final String formatXls = ".xls";
    public static final String FORMAT_DECIMAL_1PLACES = "#,##0.0";  // 소수 첫째자리까지
    public static final String FORMAT_DECIMAL_2PLACES = "#,##0.00";  // 소수 둘째자리까지
    public static final String FORMAT_COMMA = "#,##0";  // 천 단위로 콤마

    //    public static final String ZERO = "0.0";
    public static final String ZERO = "0";
    public static final DateTimeFormatter DATE_FM = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATE_TIME_FM = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

//    ============================================================================================= 이하 유틸리티 메서드

    /**
     * 엑셀 양식 읽기 (틀 만들기)
     *
     * @param path   양식파일 경로. yml/properties 권장.
     * @param fileNm 확장자 있는 양식파일명.
     * @return 양식을 읽어들인 새 엑셀 객체
     */
    public static XSSFWorkbook readExcelFile(String path, String fileNm) {
//        1) 파일 유효 검사: xlsx only
        if (!getFileNmOrExt(fileNm, false).equals(format))
            throw new IllegalArgumentException(FILE_EXT_INCORRECT.getMsg());
//
//        2) 새 엑셀 객체 생성
        XSSFWorkbook wb = null;
        try (FileInputStream fis = new FileInputStream(path + fileNm)) {

//            3) 읽어 들이려는 파일이 있는 경로 + 파일명 + .xlsx > workbook 객체로 생성
            wb = new XSSFWorkbook(fis);
        } catch (IOException | NullPointerException e) {
            logErr(e);
        }
//        4) res
        return wb;
    }

    /**
     * 엑셀 양식 읽기 오버로딩
     *
     * @param file 멀티파트 객체.
     * @return 엑셀 워크북
     */
    public static XSSFWorkbook readExcelFile(MultipartFile file) {
        String originNm = file.getOriginalFilename();
        if (originNm != null && !originNm.endsWith(format))
            throw new IllegalArgumentException(FILE_EXT_INCORRECT.getMsg());

        XSSFWorkbook wb = null;
        try (InputStream is = file.getInputStream()) {
            wb = new XSSFWorkbook(is);
        } catch (IOException | NullPointerException e) {
            logErr(e);
        }
        return wb;
    }

    public static XSSFWorkbook readExcelFile(File file) {
        if (file != null && !file.getName().endsWith(format))
            throw new IllegalArgumentException(FILE_EXT_INCORRECT.getMsg());

        XSSFWorkbook wb = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            wb = new XSSFWorkbook(fis);
        } catch (IOException | NullPointerException e) {
            logErr(e);
        }
        return wb;
    }

    public static HSSFWorkbook readExcelFile(File file, boolean xls) {
        if (file != null && !file.getName().endsWith(formatXls))
            throw new IllegalArgumentException(FILE_EXT_INCORRECT.getMsg());

        HSSFWorkbook wb = null;
        try (FileInputStream fis = new FileInputStream(file)) {
            wb = new HSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wb;
    }

    /**
     * 엑셀 파일 출력(브라우저에서 다운로드) + Workbook 자원 해제. 지정 제목_다운로드 일자
     * 이미 블록 안에 파일 출력이 있으므로 컨트롤러 api 타입도 void 여야 합니다.
     * 클라이언트 요청 시엔 비동기가 아닌 url 쿼리스트링을 사용합니다.
     *
     * @param response 응답 헤더, 컨텐트 타입 필요.
     * @param workbook 출력할 엑셀(.xlsx) 객체
     * @param fileNm   확장자 없는 출력 파일명
     */
    public static void writeExcelFile(HttpServletResponse response, XSSFWorkbook workbook, String fileNm) {
        try (XSSFWorkbook wb = workbook) {
//            1) 파일명:
            fileNm += format;
            String encFileNm = URLEncoder.encode(fileNm, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

//            2) 응답 헤더 작성
            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encFileNm + "\";");

//            3) 완성된 엑셀 출력, 스트림 닫기
            wb.write(response.getOutputStream());
        } catch (IOException | NullPointerException e) {
            logErr(e);
        }
    }


    // 셀의 병합 여부 파악
    public static CellRangeAddress findMergedRegion(XSSFSheet sheet, Cell cell) {
        for (CellRangeAddress range : sheet.getMergedRegions()) {
            int rowNo = cell.getRowIndex();
            int colNo = cell.getColumnIndex();

            if (rowNo >= range.getFirstRow() && rowNo <= range.getLastRow()
                    && colNo >= range.getFirstColumn() && colNo <= range.getLastColumn()) return range;
        }
        return null;
    }


    /**
     * 셀 타입 검사 후 값 추출. null 은 0으로 대체.
     *
     * @param cell 셀
     * @param eval 수식 셀 검사도구
     * @param cs   수식 셀 포맷 ex) 소수 첫째자리까지 표현
     * @return String 값
     */
    public static String getCellData(Cell cell, XSSFFormulaEvaluator eval, CellStyle cs) {
        String result = null;

        if (cell != null) {
            switch (cell.getCellType()) {
                case ERROR:
                    break;
                case BLANK:
                    break;
                case STRING:
//                    if (!cell.getStringCellValue().equals("-"))
                    result = cell.getStringCellValue();
                    break;
                case NUMERIC:   // double 반환
                    cell.setCellStyle(cs);
                    result = String.valueOf(cell.getNumericCellValue());
                    break;
                case FORMULA:
                    if (eval.evaluate(cell).getCellType() == CellType.STRING) {
//                        if (!cell.getStringCellValue().equals("-"))
                        result = cell.getStringCellValue();
                    } else if (eval.evaluate(cell).getCellType() == CellType.NUMERIC) {
                        cell.setCellStyle(cs);
                        result = String.valueOf(cell.getNumericCellValue());
                    } else {
                        log.info("!예측되지 않은 셀 데이터 타입!");
                    }
                    break;
                default:
                    log.info("!예측되지 않은 셀 데이터 타입!");
                    break;
            }
        } else {
            return null;
        }

        // 개행, 공백 등 정리
        if (result != null) {
            result = result.replaceAll("\n", "");
            result = result.trim();
        }
        return result;
    }

    public static String getCellData(Cell cell, XSSFFormulaEvaluator eval, String formatter) {
        String result = null;

        if (cell != null) {
            switch (cell.getCellType()) {
                case ERROR:
                    result = null;
                    break;
                case BLANK:
                    result = null;
                    break;
                case STRING:
//                    if (!cell.getStringCellValue().equals("-"))
                    result = cell.getStringCellValue();
                    break;
                case NUMERIC:   // double, Long, Integer 구분

                    result = new DecimalFormat(formatter).format(cell.getNumericCellValue());
                    break;
                case FORMULA:
                    if (eval.evaluate(cell).getCellType() == CellType.STRING) {
//                        if (!cell.getStringCellValue().equals("-"))
                        result = cell.getStringCellValue();
                    } else if (eval.evaluate(cell).getCellType() == CellType.NUMERIC) {
                        result = new DecimalFormat(formatter).format(cell.getNumericCellValue());
                    } else {
                        log.info("!예측되지 않은 셀 데이터 타입!");
                    }
                    break;
                default:
                    log.info("!예측되지 않은 셀 데이터 타입!");
                    break;
            }
        } else {
            return null;
        }

        // 개행, 공백 등 정리
        if (result != null) {
            result = result.replaceAll("\n", "");
            result = result.trim();
        }
        return result;
    }


    /**
     * xlsx 사양, 셀값을 원형 그대로 문자열로 변환하여 추출
     *
     * @param cell XSSFCell
     * @return trim 적용된 문자열. 값이 없어도 null 이 나오지 않음.
     */
    public static String getCellData(Cell cell) {
//        1) 유효 검사
        if (cell == null) return null;

//        2) 셀 타입 관계 없이 문자열로 취급
        DataFormatter df = new DataFormatter();
        String result = df.formatCellValue(cell);

        // 앞뒤 공백 등 정리
        if (hasText(result)) result = result.trim();
        return result;
    }


    public static String getCellData(Cell cell, HSSFFormulaEvaluator eval, String formatter) {
        String result = ZERO;

        if (cell != null) {
            switch (cell.getCellType()) {
                case ERROR:
                    result = null;
                    break;
                case BLANK:
                    result = null;
                    break;
                case STRING:
                    if (!cell.getStringCellValue().equals("-")) result = cell.getStringCellValue();
                    break;
                case NUMERIC:   // double 반환
                    result = new DecimalFormat(formatter).format(cell.getNumericCellValue());
                    break;
                case FORMULA:
                    if (eval.evaluate(cell).getCellType() == CellType.STRING) {
                        if (!cell.getStringCellValue().equals("-")) result = cell.getStringCellValue();
                    } else if (eval.evaluate(cell).getCellType() == CellType.NUMERIC) {
                        result = new DecimalFormat(formatter).format(cell.getNumericCellValue());
                    } else {
                        log.info("!예측되지 않은 셀 데이터 타입!");
                    }
                    break;
                default:
                    log.info("!예측되지 않은 셀 데이터 타입!");
                    break;
            }
        } else {
            return null;
        }

        // 개행, 공백 등 정리
        if (result != null) {
            result = result.replaceAll("\n", "");
            result = result.trim();
        }
        return result;
    }



    /**
     * 셀 데이터 일괄 문자열로 추출
     * @param cell  XSSFCell
     * @param formatter 수식 셀에 적용할 데이터 포맷 ex) 천의 자리 콤마, 소수 자릿수 등.
     * @param isDtm 날짜 셀 반환 모양. T: LocalDateTime, F: LocalDate
     * @return trim 적용된 문자열. 값이 없는 경우 null 발생.
     */
    public static String getCellData(Cell cell, String formatter, boolean isDtm) {
//        1) 유효검사
        CellType type = cell.getCellType();
        if (type == CellType.BLANK || type == CellType.ERROR) return null;

        String result = null;

//        2) 셀 데이터를 셀 타입에 상관없이 일괄 문자열로 반환하되, 날짜/수식 셀인 경우 따로 처리
        if (type == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            // 날짜 서식 지정셀은 반드시 숫자 타입으로 분류.
            Instant instant = cell.getDateCellValue().toInstant();

            if (isDtm) {
                // LocalDateTime 으로 변환
                LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                result = DATE_TIME_FM.format(localDateTime);
            } else {
                // LocalDate 로 변환
                LocalDate localDate = LocalDateTime.ofInstant(instant, ZoneId.systemDefault()).toLocalDate();
                result = DATE_FM.format(localDate);
            }
        } else if (type == CellType.FORMULA) {
            // 지정 포맷 적용하여 수식 결과값 반환
            result = new DecimalFormat(formatter).format(cell.getNumericCellValue());
        } else if (type == CellType.NUMERIC) {
            // 숫자인 경우 문자열에서 쉼표 제거
            result = new DataFormatter().formatCellValue(cell).replaceAll(",", "");
        } else {
            result = new DataFormatter().formatCellValue(cell);
        }

        // 앞뒤 공백 등 정리
        if (hasText(result)) result = result.trim();
        return result;
    }


    /**
     * 유효셀 여부
     *
     * @param cell XSSFCell
     * @return T: 유효한 셀, F: null/빈 셀
     */
    public static boolean hasCell(XSSFCell cell) {
        // 보통 cell == null 로는 잡히지 않고, CellType.BLANK 로 잡힘.
        return cell != null && cell.getCellType() != CellType.BLANK && cell.getCellType() != CellType.ERROR;
    }

    /*
    ------------------------------------------
    .xlsx 용 엑셀 서식. 잦은 스타일 생성은 성능 저하 원인이므로 한 번 만들어두고 재사용하는 방식 권장.
    색상 등 까다로운 변수는 필요하다면 따로 설정.
    */

    /**
     * 소수 첫째자리까지 표현
     *
     * @param wb 워크북
     * @return 셀스타일
     */
    public static CellStyle dataFormatCs_1p(XSSFWorkbook wb) {
        XSSFCellStyle cs = wb.createCellStyle();
        cs.setDataFormat(wb.createDataFormat().getFormat(FORMAT_DECIMAL_1PLACES));
        return cs;
    }

    public static CellStyle dataFormat(XSSFWorkbook wb, String formatter) {
        XSSFCellStyle cs = wb.createCellStyle();
        cs.setDataFormat(wb.createDataFormat().getFormat(formatter));
        return cs;
    }

    /**
     * .xlsx 새 셀스타일 커스텀
     *
     * @param wb            XSSFWorkbook
     * @param hasBorder     윤곽선 유무(디폴트: 얇은, 검정색, 셀 전체)
     * @param isAlignCenter 가운데정렬 여부
     * @param isWrapText    자동 개행 여부
     * @param isShrinkToFit 셀에 맞춤 여부
     * @return XSSFCellStyle
     */
    public static CellStyle setCustomStyle(XSSFWorkbook wb, boolean hasBorder, boolean isAlignCenter, boolean isWrapText, boolean isShrinkToFit) {
//        새 스타일 생성. 새로 만들지 않으면 객체 중복으로 결과가 바르지 않을 수 있습니다.
        XSSFCellStyle cs = wb.createCellStyle();
        // 테두리:
        if (hasBorder) {
            cs.setBorderLeft(BorderStyle.THIN);
            cs.setBorderRight(BorderStyle.THIN);
            cs.setBorderTop(BorderStyle.THIN);
            cs.setBorderBottom(BorderStyle.THIN);
        }
        // 정렬: 수직 정렬은 디폴트, 수평은 왼쪽 정렬 or 가운데 정렬 선택.
        if (isAlignCenter) cs.setAlignment(HorizontalAlignment.CENTER);
        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        // 자동 개행 or 셀에 맞춤
        cs.setWrapText(isWrapText);
        cs.setShrinkToFit(isShrinkToFit);

        // 셀 배경색 예제. 디폴트 설정 곤란.
//        CellStyle greenBackground = setCustomStyle(wb, false, true, false);
//        greenBackground.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
//        greenBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cs;
    }


    /**
     * .xlsx 새 폰트 커스텀.
     *
     * @param wb     XSSFWorkbook
     * @param isBold 볼드 처리 여부
     * @param fontNm 글꼴명 ex) "맑은 고딕", "Arial"
     * @param pt     글꼴 크기
     * @return XSSFFont
     */
    public static XSSFFont setCustomFont(XSSFWorkbook wb, boolean isBold, String fontNm, int pt) {
//        새 폰트 생성
        XSSFFont font = wb.createFont();
        // 글꼴
        font.setFontName(fontNm);
        // 굵기
        font.setBold(isBold);
        // 크기
        font.setFontHeightInPoints((short) pt);
        // 색상..?
//        if(isRedLetter) font.setColor(new XSSFColor(new java.awt.Color(255, 0, 0)));
        return font;
    }

    /**
     * @return style + font 세트
     */
    public static XSSFCellStyle setDefaultStyle(XSSFWorkbook wb, boolean hasBorder, boolean isAlignCenter, boolean isWrapText, boolean isShrinkToFit, boolean isBold, String fontNm, int pt) {
        XSSFCellStyle cs = wb.createCellStyle();
        XSSFFont font = wb.createFont();

        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        if (isAlignCenter) cs.setAlignment(HorizontalAlignment.CENTER);
        if (hasBorder) {
            cs.setBorderLeft(BorderStyle.THIN);
            cs.setBorderRight(BorderStyle.THIN);
            cs.setBorderTop(BorderStyle.THIN);
            cs.setBorderBottom(BorderStyle.THIN);
        }

        cs.setWrapText(isWrapText);
        cs.setShrinkToFit(isShrinkToFit);

        font.setFontName(fontNm);
        font.setBold(isBold);
        font.setFontHeightInPoints((short) pt);
        cs.setFont(font);

        return cs;
    }

    /**
     * setDefaultStyle 오버로딩. 디폴트 폰트 적용 버전(현재: 맑은 고딕)
     */
    public static XSSFCellStyle setDefaultStyle(XSSFWorkbook wb, boolean hasBorder, boolean isAlignCenter, boolean isWrapText, boolean isShrinkToFit, boolean isBold, int pt) {
        XSSFCellStyle cs = wb.createCellStyle();
        XSSFFont font = wb.createFont();

        cs.setVerticalAlignment(VerticalAlignment.CENTER);
        if (isAlignCenter) cs.setAlignment(HorizontalAlignment.CENTER);
        if (hasBorder) {
            cs.setBorderLeft(BorderStyle.THIN);
            cs.setBorderRight(BorderStyle.THIN);
            cs.setBorderTop(BorderStyle.THIN);
            cs.setBorderBottom(BorderStyle.THIN);
        }

        cs.setWrapText(isWrapText);
        cs.setShrinkToFit(isShrinkToFit);

        font.setFontName("맑은 고딕");
        font.setBold(isBold);
        font.setFontHeightInPoints((short) pt);
        cs.setFont(font);

        return cs;
    }


    // 셀 정보 클래스 (dto)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CellInfo {
        public String cellData;
        public int colIdx;
    }

    // 중복 검사용 셀 정보 클래스(dto)
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DupChkInfo {
        // 주차장명, 지번주소, 총주차면수.
        public int lotNm;
        public int address;
        public int totalSpcs;
    }


}
