package com.example.demo.service.data.monthlyReport.mrMng;

import com.example.demo.atech.ExcelManager;
import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.config.mapStruct.MyMapper;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.data.monthlyReport.*;
import com.example.demo.domain.data.monthlyReport.repo.*;
import com.example.demo.domain.data.monthlyReport.repoCustom.*;
import com.example.demo.domain.system.user.User;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.dto.data.facility.PFPrivateDto;
import com.example.demo.dto.data.monthlyReport.*;
import com.example.demo.dto.data.standard.StandardMngDto;
import com.example.demo.service.api.geoCoder.GeoCoderService;
import com.example.demo.service.data.standard.StandardMngService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.demo.atech.ExcelManager.getCellData;
import static com.example.demo.atech.ExcelManager.hasCell;
import static com.example.demo.atech.Msg.COLLECT_DATA_ERR;
import static com.example.demo.atech.Msg.NO_FILES;
import static com.example.demo.atech.MyUtil.*;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MrService {
    /*
    실제 엑셀 데이터 처리 로직
     */
    public static final String mrStdExcelName = "monthReportStandard.xlsx";
    public static final String mrStdExcelNameSggTotal = "monthReportStandard_sggTotal.xlsx";
    private final MyMapper mapper;
    private final PStatusRepository repoPStatus;
    private final PStatusRepoCustom queryPStatus;
    private final PSubIncrsRepository repoPSubIncrs;
    private final PSubIncrsRepoCustom queryPSubIncrs;
    private final PSubDcrsRepository repoPSubDcrs;

    private final PSubDcrsRepoCustom queryPSubDcrs;
    private final PPublicRepository repoPPublic;
    private final PPublicRepoCustom queryPPublic;
    private final PResiRepository repoPResi;
    private final PResiRepoCustom queryPResi;
    private final StandardMngService mngService;
    private final GeoCoderService geoService;
    private final UserRepository userRepo;
    private final PPublicRepoCustom pblQuery;


    @Value("${spring.servlet.multipart.location}")
    private String filePath;

    @Value("${spring.servlet.multipart2.standardExcel.download.mr}")
    private String mrStdExcelPath;

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public void collectMrData(MrData origin) {
        FileInfo fi = origin.getAttaches().get(0);
        File file = new File(fi.getFilePath() + fi.getFileNmStored());
        if (!file.exists()) throw new NullPointerException(NO_FILES.getMsg());
        // [240123] 남구는 파일이 2개 발견되었으나 공통 컬럼에 어긋나는 양식 파일은 일단 보류.

//        1)
        PStatus pStatus = new PStatus();
        List<PSubIncrs> pSubIncrsList = new ArrayList<>();
        List<PSubDcrs> pSubDcrsList = new ArrayList<>();
        List<PPublic> pPublicList = new ArrayList<>();
        List<PResi> pResiList = new ArrayList<>();


//        3) 분류, 데이터 수집 작업
        boolean isPStatus = false;
        boolean isPSubIncrs = false;
        boolean isPSubDcrs = false;
        boolean isPPublic = false;
        boolean isPResi = false;
        try (XSSFWorkbook wb = ExcelManager.readExcelFile(file)) {
            // wb 단위 공통 도구: 실수 데이터 포맷, 셀 타입 검사 도구
            CellStyle df = ExcelManager.dataFormatCs_1p(wb);
            XSSFFormulaEvaluator eval = wb.getCreationHelper().createFormulaEvaluator();

            // 주차장확보현황
            if (wb.getSheet("주차장확보현황_표준") != null ) isPStatus = true;
            if (isPStatus) {
                pStatus = getPStatusData(wb, eval, df, origin);
                //pStatusTotal = getPStatusTotalData(wb, eval, df, origin.getYear(), origin.getMonth(), origin.getSggCd());
            }
            if (wb.getSheet("주차장증가현황_표준양식") != null ) isPSubIncrs = true;
            if (isPSubIncrs) {
                pSubIncrsList = getPSubIncrs(wb, eval, df, origin);
            }
            if (wb.getSheet("주차장감소현황_표준양식") != null ) isPSubDcrs = true;
            if (isPSubDcrs) {
                pSubDcrsList = getPSubDcrs(wb, eval, df, origin);
            }
            if (wb.getSheet("공영주차장 현황_표준양식") != null && (wb.getSheet("공영주차장 현황_표준양식").getLastRowNum()>5) ) isPPublic = true;
            if (isPPublic) {
                pPublicList = getPPublic(wb, eval, df, origin);
            }
            if (wb.getSheet("거주자우선주차제현황_표준양식") != null ) isPResi = true;
            if (isPResi) {
                pResiList = getPResi(wb, eval, df, origin);
            }

        } catch (IOException e) {
            logErr(e);
            throw new IllegalArgumentException(COLLECT_DATA_ERR.getMsg());
        }
        // 전체, 기초 데이터 적재(있으면)
        /*
        if (pStatus != null && pStatusTotal != null && !pSubIncrsList.isEmpty() && !pSubDcrsList.isEmpty() && !pPublicList.isEmpty()) {
            PStatus savedPStatus = repoPStatus.save(pStatus);
            PStatusTotal savedPStatusTotal = repoPStatusTotal.save(pStatusTotal);
            List<PSubIncrs> savedPSubIncrs = repoPSubIncrs.saveAll(pSubIncrsList);
            List<PSubDcrs> savedPSubDcrs = repoPSubDcrs.saveAll(pSubDcrsList);
            List<PPublic> savedPPublic = repoPPublic.saveAll(pPublicList);


            if (savedPStatus == null || savedPStatusTotal == null || savedPSubIncrs.size() != pSubIncrsList.size() || savedPSubDcrs.size() != pSubDcrsList.size() || savedPPublic.size() != pPublicList.size())
                throw new RuntimeException("주차장확보현황 적재 중 문제가 발생했습니다.");
        }
*/
        if (pStatus != null ) {
            PStatus savedPStatus = repoPStatus.save(pStatus);
            if (savedPStatus == null )
                throw new RuntimeException("주차장확보현황 적재 중 문제가 발생했습니다.");
        }

        if (!pSubIncrsList.isEmpty()) {
            List<PSubIncrs> savedPSubIncrs = repoPSubIncrs.saveAll(pSubIncrsList);

            if (savedPSubIncrs.size() != pSubIncrsList.size())
                throw new RuntimeException("주차장증가현황 적재 중 문제가 발생했습니다.");
        }

        if (!pSubDcrsList.isEmpty()) {
            List<PSubDcrs> savedPSubDcrs = repoPSubDcrs.saveAll(pSubDcrsList);

            if (savedPSubDcrs.size() != pSubDcrsList.size())
                throw new RuntimeException("주차장감소현황 적재 중 문제가 발생했습니다.");
        }

        if (!pPublicList.isEmpty()) {
            List<PPublic> savedPPublic = repoPPublic.saveAll(pPublicList);


            if (savedPPublic.size() != pPublicList.size())
                throw new RuntimeException("공영주차장현황 적재 중 문제가 발생했습니다.");
        }


        if (!pResiList.isEmpty()){
            List<PResi> savedPResi = repoPResi.saveAll(pResiList);
            if (savedPResi.size() != pResiList.size())
                throw new RuntimeException("거주자우선주차제현황 적재 중 문제가 발생했습니다.");
        }
//        4) 최종 완료되면 문서 승인 여부 체크
        origin.updateCollectYn("Y");
    }

    public MrDataDto.Keyword getKeyword4manager() {
//        1) 사용자 정보 확인
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.MANAGER_SESSION_EXPIRED.getMsg()));

//        2) 유효검사
        String agency = user.getAgency();
        if (agency.equals("31000") || !user.getRole().getEncodedNm().contains("담당자"))
            throw new IllegalArgumentException(Msg.ONLY_MANAGER.getMsg());

//        3) [240409] '공영주차장 데이터' 중 해당하는 구군의 최신 year, month 값 검색.
        PPublicDto.Keyword latestDt = pblQuery.getLatestDt(agency);
        String year = latestDt.getYear();
        String month = latestDt.getMonth();
        if (!hasText(year) || !hasText(month)) throw new EntityNotFoundException(Msg.EMPTY_RESULT.getMsg());

//        4) return keyword
        MrDataDto.Keyword req = new MrDataDto.Keyword();
        req.setYear(year);
        req.setMonth(month);
        req.setSggCd(agency);

        return req;
    }

    private PStatus getPStatusData(XSSFWorkbook wb, XSSFFormulaEvaluator eval, CellStyle df, MrData origin) {
        XSSFSheet sheet = wb.getSheet("주차장확보현황_표준");
        if (sheet == null) throw new IllegalArgumentException("'주차장확보현황_표준' 시트가 발견되지 않았습니다.");
        log.info("주차장확보현황_표준 데이터 수집 시작.");
        PStatus pStatus =  new PStatus();
        Pattern pattern = Pattern.compile("^[a-zA-Z]+");
        PStatusDto.Req req = new PStatusDto.Req();
        req.setYear(origin.getYear());
        req.setMonth(origin.getMonth());
        req.setSggCd(origin.getSggCd());
        req.setMrData(origin);
        //        날짜비교용 날짜 컬럼 추가. yyyyMMdd 형태
        LocalDate localDt = LocalDate.parse(req.getYear() + String.format("%02d", Integer.parseInt(req.getMonth())) + "01", formatter);
        req.setLocalDt(localDt);
        req.setPBLRD_PAY_L_I(sheet.getRow(8).getCell(7).getNumericCellValue());
        req.setPBLRD_PAY_L_D(sheet.getRow(8).getCell(8).getNumericCellValue());
        req.setPBLRD_PAY_S_I(sheet.getRow(8).getCell(10).getNumericCellValue());
        req.setPBLRD_PAY_S_D(sheet.getRow(8).getCell(11).getNumericCellValue());
        req.setPBLRD_PAY_A_I(sheet.getRow(8).getCell(13).getNumericCellValue());
        req.setPBLRD_PAY_A_D(sheet.getRow(8).getCell(14).getNumericCellValue());

        req.setPBLRD_FREE_L_I(sheet.getRow(9).getCell(7).getNumericCellValue());
        req.setPBLRD_FREE_L_D(sheet.getRow(9).getCell(8).getNumericCellValue());
        req.setPBLRD_FREE_S_I(sheet.getRow(9).getCell(10).getNumericCellValue());
        req.setPBLRD_FREE_S_D(sheet.getRow(9).getCell(11).getNumericCellValue());
        req.setPBLRD_FREE_A_I(sheet.getRow(9).getCell(13).getNumericCellValue());
        req.setPBLRD_FREE_A_D(sheet.getRow(9).getCell(14).getNumericCellValue());

        req.setPBLRD_RESI_L_I(sheet.getRow(10).getCell(7).getNumericCellValue());
        req.setPBLRD_RESI_L_D(sheet.getRow(10).getCell(8).getNumericCellValue());
        req.setPBLRD_RESI_S_I(sheet.getRow(10).getCell(10).getNumericCellValue());
        req.setPBLRD_RESI_S_D(sheet.getRow(10).getCell(11).getNumericCellValue());
        req.setPBLRD_RESI_A_I(sheet.getRow(10).getCell(13).getNumericCellValue());
        req.setPBLRD_RESI_A_D(sheet.getRow(10).getCell(14).getNumericCellValue());

        req.setPBLOUT_PAY_L_I(sheet.getRow(11).getCell(7).getNumericCellValue());
        req.setPBLOUT_PAY_L_D(sheet.getRow(11).getCell(8).getNumericCellValue());
        req.setPBLOUT_PAY_S_I(sheet.getRow(11).getCell(10).getNumericCellValue());
        req.setPBLOUT_PAY_S_D(sheet.getRow(11).getCell(11).getNumericCellValue());
        req.setPBLOUT_PAY_A_I(sheet.getRow(11).getCell(13).getNumericCellValue());
        req.setPBLOUT_PAY_A_D(sheet.getRow(11).getCell(14).getNumericCellValue());

        req.setPBLOUT_FREE_L_I(sheet.getRow(12).getCell(7).getNumericCellValue());
        req.setPBLOUT_FREE_L_D(sheet.getRow(12).getCell(8).getNumericCellValue());
        req.setPBLOUT_FREE_S_I(sheet.getRow(12).getCell(10).getNumericCellValue());
        req.setPBLOUT_FREE_S_D(sheet.getRow(12).getCell(11).getNumericCellValue());
        req.setPBLOUT_FREE_A_I(sheet.getRow(12).getCell(13).getNumericCellValue());
        req.setPBLOUT_FREE_A_D(sheet.getRow(12).getCell(14).getNumericCellValue());

        req.setPRV_L_I(sheet.getRow(13).getCell(7).getNumericCellValue());
        req.setPRV_L_D(sheet.getRow(13).getCell(8).getNumericCellValue());
        req.setPRV_S_I(sheet.getRow(13).getCell(10).getNumericCellValue());
        req.setPRV_S_D(sheet.getRow(13).getCell(11).getNumericCellValue());
        req.setPRV_A_I(sheet.getRow(13).getCell(13).getNumericCellValue());
        req.setPRV_A_D(sheet.getRow(13).getCell(14).getNumericCellValue());

        req.setSUBSE_SUR_L_I(sheet.getRow(15).getCell(7).getNumericCellValue());
        req.setSUBSE_SUR_L_D(sheet.getRow(15).getCell(8).getNumericCellValue());
        req.setSUBSE_SUR_S_I(sheet.getRow(15).getCell(10).getNumericCellValue());
        req.setSUBSE_SUR_S_D(sheet.getRow(15).getCell(11).getNumericCellValue());
        req.setSUBSE_SUR_A_I(sheet.getRow(15).getCell(13).getNumericCellValue());
        req.setSUBSE_SUR_A_D(sheet.getRow(15).getCell(14).getNumericCellValue());

        req.setSUBSE_MOD_L_I(sheet.getRow(16).getCell(7).getNumericCellValue());
        req.setSUBSE_MOD_L_D(sheet.getRow(16).getCell(8).getNumericCellValue());
        req.setSUBSE_MOD_S_I(sheet.getRow(16).getCell(10).getNumericCellValue());
        req.setSUBSE_MOD_S_D(sheet.getRow(16).getCell(11).getNumericCellValue());
        req.setSUBSE_MOD_A_I(sheet.getRow(16).getCell(13).getNumericCellValue());
        req.setSUBSE_MOD_A_D(sheet.getRow(16).getCell(14).getNumericCellValue());

        req.setSUBAU_ATT_L_I(sheet.getRow(17).getCell(7).getNumericCellValue());
        req.setSUBAU_ATT_L_D(sheet.getRow(17).getCell(8).getNumericCellValue());
        req.setSUBAU_ATT_S_I(sheet.getRow(17).getCell(10).getNumericCellValue());
        req.setSUBAU_ATT_S_D(sheet.getRow(17).getCell(11).getNumericCellValue());
        req.setSUBAU_ATT_A_I(sheet.getRow(17).getCell(13).getNumericCellValue());
        req.setSUBAU_ATT_A_D(sheet.getRow(17).getCell(14).getNumericCellValue());

        req.setSUBAU_PRV_L_I(sheet.getRow(18).getCell(7).getNumericCellValue());
        req.setSUBAU_PRV_L_D(sheet.getRow(18).getCell(8).getNumericCellValue());
        req.setSUBAU_PRV_S_I(sheet.getRow(18).getCell(10).getNumericCellValue());
        req.setSUBAU_PRV_S_D(sheet.getRow(18).getCell(11).getNumericCellValue());
        req.setSUBAU_PRV_A_I(sheet.getRow(18).getCell(13).getNumericCellValue());
        req.setSUBAU_PRV_A_D(sheet.getRow(18).getCell(14).getNumericCellValue());

        req.setOWN_HOME_L_I(sheet.getRow(20).getCell(7).getNumericCellValue());
        req.setOWN_HOME_L_D(sheet.getRow(20).getCell(8).getNumericCellValue());
        req.setOWN_HOME_S_I(sheet.getRow(20).getCell(10).getNumericCellValue());
        req.setOWN_HOME_S_D(sheet.getRow(20).getCell(11).getNumericCellValue());
        req.setOWN_HOME_A_I(sheet.getRow(20).getCell(13).getNumericCellValue());
        req.setOWN_HOME_A_D(sheet.getRow(20).getCell(14).getNumericCellValue());

        req.setOWN_APT_L_I(sheet.getRow(21).getCell(7).getNumericCellValue());
        req.setOWN_APT_L_D(sheet.getRow(21).getCell(8).getNumericCellValue());
        req.setOWN_APT_S_I(sheet.getRow(21).getCell(10).getNumericCellValue());
        req.setOWN_APT_S_D(sheet.getRow(21).getCell(11).getNumericCellValue());
        req.setOWN_APT_A_I(sheet.getRow(21).getCell(13).getNumericCellValue());
        req.setOWN_APT_A_D(sheet.getRow(21).getCell(14).getNumericCellValue());
        pStatus = mapper.toPStatus(req);
        log.info("기초 데이터 수집 완료.");
        return pStatus;
    }

    public void excelDownload(HttpServletResponse response, MrDataDto.Keyword req) {
        String excelFileName ="";
        String sggNm = "";
        if (req.getSggCd().equals("31110")) {
            sggNm = "중구";
        }
        else if (req.getSggCd().equals("31140")){
            sggNm ="남구";
        }
        else if (req.getSggCd().equals("31170")){
            sggNm ="동구";
        }
        else if (req.getSggCd().equals("31200")){
            sggNm ="북구";
        }
        else if (req.getSggCd().equals("31710")) {
            sggNm = "울주군";
        }


//        1) 엑셀 생성
        try (XSSFWorkbook wb = ExcelManager.readExcelFile(mrStdExcelPath, mrStdExcelName)) {
            PStatusDto.Keyword pStatusDto =  new PStatusDto.Keyword();
            pStatusDto.setYear(req.getYear());
            pStatusDto.setMonth(req.getMonth());
            pStatusDto.setSggCd(req.getSggCd());

            PStatusDto resPStatus = queryPStatus.searchOne(pStatusDto);
            PStatusDto.Total resPStatusThisTotal = queryPStatus.thisTotal(pStatusDto);
            PStatusDto.Total resPStatusPrevTotal = queryPStatus.prevTotal(pStatusDto);

            XSSFSheet sheetStatus = wb.getSheet("주차장확보현황_표준");
            if (sheetStatus != null) {
                String prevYear=req.getYear();
                String prevMonth=req.getMonth();
                if (req.getMonth().equals("1")){
                    prevYear=(Integer.toString(Integer.parseInt(req.getYear())- 1));
                    prevMonth = "12";
                }
                else if (req.getMonth() != null) {
                    prevMonth = (Integer.toString(Integer.parseInt(req.getMonth()) - 1));
                }

                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetStatus.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 주차장 확보현황");

                //주차장확보현황 구군
                row = sheetStatus.getRow(2);
                row.getCell(17).setCellValue("[울산광역시 " + sggNm + "]");

                //전월 누계
                row = sheetStatus.getRow(3);
                row.getCell(3).setCellValue(prevYear + "년 " + prevMonth + "월 누계");

                //금월 누계
                row = sheetStatus.getRow(3);
                row.getCell(15).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 누계");

                pStatusThisMonthDbToExcel(sheetStatus,resPStatusThisTotal);
                pStatusPrevMonthDbToExcel(sheetStatus,resPStatusPrevTotal);
                pStatusDbToExcel(sheetStatus,resPStatus);

            }
            else  throw new IllegalArgumentException("'주차장확보현황_표준' 시트가 발견되지 않았습니다.");


            PSubIncrsDto.Keyword pSubIncrsDto =  new PSubIncrsDto.Keyword();
            pSubIncrsDto.setYear(req.getYear());
            pSubIncrsDto.setMonth(req.getMonth());
            pSubIncrsDto.setSggCd(req.getSggCd());

            List<PSubIncrsDto> resPSubIncrsList = queryPSubIncrs.search(pSubIncrsDto);

            XSSFSheet sheetPSubIncrs = wb.getSheet("주차장증가현황_표준양식");
            if (sheetPSubIncrs != null) {
                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetPSubIncrs.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 부설주차장 증가 현황");

                row = sheetPSubIncrs.getRow(2);
                row.getCell(2).setCellValue("[울산광역시 " + sggNm + "]");

                for (int i =0; i<resPSubIncrsList.size(); i++){
                    Long spaces = resPSubIncrsList.get(i).getSpaces();
                    if(spaces == null) spaces = 0L;

                    row = sheetPSubIncrs.createRow(i+4);
                    row.createCell(0).setCellValue(i+1);
                    row.createCell(1).setCellValue(resPSubIncrsList.get(i).getBuildType());
                    row.createCell(2).setCellValue(resPSubIncrsList.get(i).getBuildOwner());
                    row.createCell(3).setCellValue(resPSubIncrsList.get(i).getBuildNm());
                    row.createCell(4).setCellValue(resPSubIncrsList.get(i).getPermitNo());
                    row.createCell(5).setCellValue(resPSubIncrsList.get(i).getPrmsnYmd());
                    row.createCell(6).setCellValue(resPSubIncrsList.get(i).getApprovalDt());
                    row.createCell(7).setCellValue(resPSubIncrsList.get(i).getLocation());
                    row.createCell(8).setCellValue(resPSubIncrsList.get(i).getTtlFlarea());
                    row.createCell(9).setCellValue(resPSubIncrsList.get(i).getMainUse());
                    row.createCell(10).setCellValue(resPSubIncrsList.get(i).getSubUse());
                    row.createCell(11).setCellValue(alterNullToZero(resPSubIncrsList.get(i).getGeneration()));
                    row.createCell(12).setCellValue(resPSubIncrsList.get(i).getBldHo());
                    row.createCell(13).setCellValue(alterNullToZero(resPSubIncrsList.get(i).getHouseholds()));
                    row.createCell(14).setCellValue(resPSubIncrsList.get(i).getTotalArea());
                    row.createCell(15).setCellValue(spaces);
                    row.createCell(16).setCellValue(alterNullToZero(resPSubIncrsList.get(i).getAddPkspaceCnt()));
                    row.createCell(17).setCellValue(resPSubIncrsList.get(i).getSubau());
                    row.createCell(18).setCellValue(resPSubIncrsList.get(i).getSubse());
                    row.createCell(19).setCellValue(resPSubIncrsList.get(i).getRmrk());
                }
            }
            else  throw new IllegalArgumentException("'주차장증가현황_표준양식' 시트가 발견되지 않았습니다.");

            PSubDcrsDto.Keyword pSubDcrsDto =  new PSubDcrsDto.Keyword();
            pSubDcrsDto.setYear(req.getYear());
            pSubDcrsDto.setMonth(req.getMonth());
            pSubDcrsDto.setSggCd(req.getSggCd());

            List<PSubDcrsDto> resPSubDcrsList = queryPSubDcrs.search(pSubDcrsDto);

            XSSFSheet sheetPSubDcrs = wb.getSheet("주차장감소현황_표준양식");
            if (sheetPSubDcrs != null) {
                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetPSubDcrs.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 부설주차장 감소 현황");

                row = sheetPSubDcrs.getRow(2);
                row.getCell(2).setCellValue("[울산광역시 " + sggNm + "]");

                for (int i =0; i<resPSubDcrsList.size(); i++){
                    row = sheetPSubDcrs.createRow(i+4);
                    row.createCell(0).setCellValue(resPSubDcrsList.get(i).getReportNo());
                    row.createCell(1).setCellValue(resPSubDcrsList.get(i).getLocation());
                    row.createCell(2).setCellValue(resPSubDcrsList.get(i).getOwner());
                    row.createCell(3).setCellValue(resPSubDcrsList.get(i).getType());
                    row.createCell(4).setCellValue(resPSubDcrsList.get(i).getSpaces());
                    row.createCell(5).setCellValue(resPSubDcrsList.get(i).getTotalArea());
                    row.createCell(6).setCellValue(resPSubDcrsList.get(i).getDemolitionDt());
                    row.createCell(7).setCellValue(resPSubDcrsList.get(i).getDemolitionReason());
                    row.createCell(8).setCellValue(resPSubDcrsList.get(i).getStructure());
                    row.createCell(9).setCellValue(resPSubDcrsList.get(i).getBuildUsage());
                    row.createCell(10).setCellValue(resPSubDcrsList.get(i).getAbtCelYn());
                    row.createCell(11).setCellValue(resPSubDcrsList.get(i).getAbtIstYn());
                    row.createCell(12).setCellValue(resPSubDcrsList.get(i).getAbtRoofYn());
                    row.createCell(13).setCellValue(resPSubDcrsList.get(i).getAbtLagYn());
                    row.createCell(14).setCellValue(resPSubDcrsList.get(i).getAbtEtcYn());
                    row.createCell(15).setCellValue(resPSubDcrsList.get(i).getAbtNonYn());
                    row.createCell(16).setCellValue(resPSubDcrsList.get(i).getPrmsnYmd());
                }
            }
            else  throw new IllegalArgumentException("'주차장감소현황_표준양식' 시트가 발견되지 않았습니다.");



            PPublicDto.Keyword pPublicDto =  new PPublicDto.Keyword();
            pPublicDto.setYear(req.getYear());
            pPublicDto.setMonth(req.getMonth());
            pPublicDto.setSggCd(req.getSggCd());

            List<PPublicDto> resPPublicList = queryPPublic.search(pPublicDto);


            XSSFSheet sheetPPublic = wb.getSheet("공영주차장 현황_표준양식");
            if (sheetPPublic != null) {
                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetPPublic.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 공영주차장 현황");

                row = sheetPPublic.getRow(2);
                row.getCell(3).setCellValue("[울산광역시 " + sggNm + "]");

                for (int i =0; i<resPPublicList.size(); i++){
                    row = sheetPPublic.createRow(i+5);
                    row.createCell(0).setCellValue(i+1);
                    row.createCell(1).setCellValue(resPPublicList.get(i).getMngNo());
                    row.createCell(2).setCellValue(resPPublicList.get(i).getName());
                    row.createCell(3).setCellValue(resPPublicList.get(i).getInstallDt());
                    row.createCell(4).setCellValue(resPPublicList.get(i).getLocation());
                    row.createCell(5).setCellValue(resPPublicList.get(i).getPoint_out());
                    row.createCell(6).setCellValue(resPPublicList.get(i).getArea());
                    row.createCell(7).setCellValue(resPPublicList.get(i).getWh());
                    row.createCell(8).setCellValue(resPPublicList.get(i).getWhSaturday());
                    row.createCell(9).setCellValue(resPPublicList.get(i).getWhHoliday());
                    row.createCell(10).setCellValue(resPPublicList.get(i).getDayOff());
                    row.createCell(11).setCellValue(resPPublicList.get(i).getPayYn().equals("Y") ? "유료" : "무료");
                    row.createCell(12).setCellValue(resPPublicList.get(i).getPay4Hour());
                    row.createCell(13).setCellValue(resPPublicList.get(i).getPay4Day());
                    row.createCell(14).setCellValue(resPPublicList.get(i).getTotalSpaces());
                    row.createCell(15).setCellValue(resPPublicList.get(i).getSpaces());

                    //소계

                    row.createCell(16).setCellValue(alterNullToZero(resPPublicList.get(i).getForDisabled())
                            +alterNullToZero(resPPublicList.get(i).getForLight())
                            +alterNullToZero(resPPublicList.get(i).getForPregnant())
                            +alterNullToZero(resPPublicList.get(i).getForBus())
                            +alterNullToZero(resPPublicList.get(i).getForEcho())
                            +alterNullToZero(resPPublicList.get(i).getForElderly())
                            +alterNullToZero(resPPublicList.get(i).getForElectric()));

                    row.createCell(17).setCellValue(alterNullToZero(resPPublicList.get(i).getForDisabled()));
                    row.createCell(18).setCellValue(alterNullToZero(resPPublicList.get(i).getForLight()));
                    row.createCell(19).setCellValue(alterNullToZero(resPPublicList.get(i).getForPregnant()));
                    row.createCell(20).setCellValue(alterNullToZero(resPPublicList.get(i).getForBus()));
                    row.createCell(21).setCellValue(alterNullToZero(resPPublicList.get(i).getForEcho()));
                    row.createCell(22).setCellValue(alterNullToZero(resPPublicList.get(i).getForElderly()));
                    row.createCell(23).setCellValue(alterNullToZero(resPPublicList.get(i).getForElectric()));
                    row.createCell(24).setCellValue(resPPublicList.get(i).getRoadYn());
                    row.createCell(25).setCellValue(resPPublicList.get(i).getOwner());
                    row.createCell(26).setCellValue(resPPublicList.get(i).getAgency());
                    row.createCell(27).setCellValue(resPPublicList.get(i).getComment());

                }
            }
            else  throw new IllegalArgumentException("'공영주차장 현황_표준양식' 시트가 발견되지 않았습니다.");


            PResiDto.Keyword pResiDto =  new PResiDto.Keyword();
            //pResiDto.setYear(req.getYear());
            //pResiDto.setMonth(req.getMonth());
            pResiDto.setSggCd(req.getSggCd());

            List<PResiDto> resPResiList = queryPResi.search(pResiDto);
            if (resPResiList.size() > 0) {
                XSSFSheet sheetPResi = wb.getSheet("거주자우선주차제현황_표준양식");
                if (sheetPResi != null) {
                    XSSFRow row;
                    int rowIndex = 0;
                    for (rowIndex = 0; rowIndex < resPResiList.size(); rowIndex++) {
                        row = sheetPResi.createRow(rowIndex + 4);

                        row.createCell(0).setCellValue(resPResiList.get(rowIndex).getYear());
                        row.createCell(1).setCellValue(resPResiList.get(rowIndex).getMonth());
                        row.createCell(2).setCellValue(resPResiList.get(rowIndex).getPrevSpaces());
                        row.createCell(3).setCellValue(resPResiList.get(rowIndex).getNewSpaces());
                        row.createCell(4).setCellValue(resPResiList.get(rowIndex).getLostSpaces());
                        row.createCell(5).setCellValue(resPResiList.get(rowIndex).getReSpaces());
                        row.createCell(6).setCellValue(resPResiList.get(rowIndex).getVariance());
                        row.createCell(7).setCellValue(resPResiList.get(rowIndex).getThisSpaces());
                        row.createCell(8).setCellValue(resPResiList.get(rowIndex).getThisArea());
                        row.createCell(9).setCellValue(resPResiList.get(rowIndex).getVarianceReason());
                        row.createCell(10).setCellValue(resPResiList.get(rowIndex).getNonUse());
                        row.createCell(11).setCellValue(resPResiList.get(rowIndex).getInUse());
/*
                    if((i!=0) && (i%12 == 0)){
                        sheetPResi.addMergedRegion(new CellRangeAddress( (i-12)+4, (i-1)+4, 0, 0 ));
                        sheetPResi.getRow((i-11)+4).getCell(0).setCellValue(resPResiList.get(i).getYear());
                    }
 */
                    }
                    int startYear = Integer.parseInt(resPResiList.get(0).getYear());
                    int yearCount = rowIndex / 12;
                    int remMonth = rowIndex % 12;
                    for (int i = 0; i < yearCount; i++) {
                        sheetPResi.addMergedRegion(new CellRangeAddress((i * 12) + 4, (i * 12) + 11 + 4, 0, 0));
                    }
                    if (remMonth != 0) {
                        sheetPResi.addMergedRegion(new CellRangeAddress((yearCount * 12) + 4, (yearCount * 12) + remMonth + 4, 0, 0));
                    }
                } else throw new IllegalArgumentException("'거주자우선주차제현황_표준양식' 시트가 발견되지 않았습니다.");
            }
            //3) 엑셀 생성, 출력
//            String fileNm = req.getYear()+"년 "+req.getMonth()+"월 "+ "월간 보고 현황";
            String fileNm = makeStandardFileNm4Mr(req.getYear(), req.getMonth(), req.getSggCd());
            ExcelManager.writeExcelFile(response, wb, fileNm);

        } catch (IOException e) {
            logErr(e);
            throw new IllegalArgumentException("다운로드 중 오류가 발생했습니다.");
        }
    }

    public void excelDownload_sggTotal(HttpServletResponse response, MrDataDto.Keyword req) {


//        1) 엑셀 생성
        try (XSSFWorkbook wb = ExcelManager.readExcelFile(mrStdExcelPath, mrStdExcelNameSggTotal)) {
            PStatusDto.Keyword pStatusDto =  new PStatusDto.Keyword();
            pStatusDto.setYear(req.getYear());
            pStatusDto.setMonth(req.getMonth());
            pStatusDto.setSggCd(req.getSggCd());

            //PStatusDto resPStatus = queryPStatus.searchOne(pStatusDto);
            pStatusDto.setSggCd("31110");
            PStatusDto.Total resPStatusJungguTotal = queryPStatus.thisTotal(pStatusDto);
            pStatusDto.setSggCd("31140");
            PStatusDto.Total resPStatusNamguTotal = queryPStatus.thisTotal(pStatusDto);
            pStatusDto.setSggCd("31170");
            PStatusDto.Total resPStatusDongguTotal = queryPStatus.thisTotal(pStatusDto);
            pStatusDto.setSggCd("31200");
            PStatusDto.Total resPStatusBukguTotal = queryPStatus.thisTotal(pStatusDto);
            pStatusDto.setSggCd("31710");
            PStatusDto.Total resPStatusUljugunTotal = queryPStatus.thisTotal(pStatusDto);
            //PStatusDto.Total resPStatusPrevTotal = queryPStatus.prevTotal(pStatusDto);

            XSSFSheet sheetStatus = wb.getSheet("주차장확보현황_표준(전체)");
            if (sheetStatus != null) {

                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetStatus.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 주차장 확보현황");

                //주차장확보현황 구군
                row = sheetStatus.getRow(2);
                row.getCell(14).setCellValue("[울산광역시]");

                //총계
                row = sheetStatus.getRow(6);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getTOTAL_L_SUM()
                                                  + resPStatusNamguTotal.getTOTAL_L_SUM()
                                                  + resPStatusDongguTotal.getTOTAL_L_SUM()
                                                  + resPStatusBukguTotal.getTOTAL_L_SUM()
                                                  + resPStatusUljugunTotal.getTOTAL_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getTOTAL_S_SUM()
                                                  + resPStatusNamguTotal.getTOTAL_S_SUM()
                                                  + resPStatusDongguTotal.getTOTAL_S_SUM()
                                                  + resPStatusBukguTotal.getTOTAL_S_SUM()
                                                  + resPStatusUljugunTotal.getTOTAL_S_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getTOTAL_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getTOTAL_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getTOTAL_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getTOTAL_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getTOTAL_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getTOTAL_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getTOTAL_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getTOTAL_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getTOTAL_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getTOTAL_S_SUM());
;

                //공영소계
                row = sheetStatus.getRow(7);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getPBL_L_SUBTOTAL()
                                                  + resPStatusNamguTotal.getPBL_L_SUBTOTAL()
                                                  + resPStatusDongguTotal.getPBL_L_SUBTOTAL()
                                                  + resPStatusBukguTotal.getPBL_L_SUBTOTAL()
                                                  + resPStatusUljugunTotal.getPBL_L_SUBTOTAL());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getPBL_S_SUBTOTAL()
                                                  + resPStatusNamguTotal.getPBL_S_SUBTOTAL()
                                                  + resPStatusDongguTotal.getPBL_S_SUBTOTAL()
                                                  + resPStatusBukguTotal.getPBL_S_SUBTOTAL()
                                                  + resPStatusUljugunTotal.getPBL_S_SUBTOTAL());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getPBL_L_SUBTOTAL());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getPBL_S_SUBTOTAL());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getPBL_L_SUBTOTAL());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getPBL_S_SUBTOTAL());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getPBL_L_SUBTOTAL());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getPBL_S_SUBTOTAL());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getPBL_L_SUBTOTAL());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getPBL_S_SUBTOTAL());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getPBL_L_SUBTOTAL());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getPBL_S_SUBTOTAL());

                //공영 노상 유료
                row = sheetStatus.getRow(8);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getPBLRD_PAY_L_SUM()
                        + resPStatusNamguTotal.getPBLRD_PAY_L_SUM()
                        + resPStatusDongguTotal.getPBLRD_PAY_L_SUM()
                        + resPStatusBukguTotal.getPBLRD_PAY_L_SUM()
                        + resPStatusUljugunTotal.getPBLRD_PAY_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getPBLRD_PAY_S_SUM()
                        + resPStatusNamguTotal.getPBLRD_PAY_S_SUM()
                        + resPStatusDongguTotal.getPBLRD_PAY_S_SUM()
                        + resPStatusBukguTotal.getPBLRD_PAY_S_SUM()
                        + resPStatusUljugunTotal.getPBLRD_PAY_S_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getPBLRD_PAY_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getPBLRD_PAY_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getPBLRD_PAY_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getPBLRD_PAY_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getPBLRD_PAY_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getPBLRD_PAY_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getPBLRD_PAY_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getPBLRD_PAY_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getPBLRD_PAY_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getPBLRD_PAY_S_SUM());

                //공영 노상 무료
                row = sheetStatus.getRow(9);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getPBLRD_FREE_L_SUM()
                        + resPStatusNamguTotal.getPBLRD_FREE_L_SUM()
                        + resPStatusDongguTotal.getPBLRD_FREE_L_SUM()
                        + resPStatusBukguTotal.getPBLRD_FREE_L_SUM()
                        + resPStatusUljugunTotal.getPBLRD_FREE_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getPBLRD_FREE_S_SUM()
                        + resPStatusNamguTotal.getPBLRD_FREE_S_SUM()
                        + resPStatusDongguTotal.getPBLRD_FREE_S_SUM()
                        + resPStatusBukguTotal.getPBLRD_FREE_S_SUM()
                        + resPStatusUljugunTotal.getPBLRD_FREE_S_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getPBLRD_FREE_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getPBLRD_FREE_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getPBLRD_FREE_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getPBLRD_FREE_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getPBLRD_FREE_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getPBLRD_FREE_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getPBLRD_FREE_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getPBLRD_FREE_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getPBLRD_FREE_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getPBLRD_FREE_S_SUM());

                //공영 노상 거주자
                row = sheetStatus.getRow(10);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getPBLRD_RESI_L_SUM()
                        + resPStatusNamguTotal.getPBLRD_RESI_L_SUM()
                        + resPStatusDongguTotal.getPBLRD_RESI_L_SUM()
                        + resPStatusBukguTotal.getPBLRD_RESI_L_SUM()
                        + resPStatusUljugunTotal.getPBLRD_RESI_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getPBLRD_RESI_L_SUM()
                        + resPStatusNamguTotal.getPBLRD_RESI_L_SUM()
                        + resPStatusDongguTotal.getPBLRD_RESI_L_SUM()
                        + resPStatusBukguTotal.getPBLRD_RESI_L_SUM()
                        + resPStatusUljugunTotal.getPBLRD_RESI_L_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getPBLRD_RESI_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getPBLRD_RESI_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getPBLRD_RESI_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getPBLRD_RESI_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getPBLRD_RESI_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getPBLRD_RESI_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getPBLRD_RESI_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getPBLRD_RESI_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getPBLRD_RESI_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getPBLRD_RESI_S_SUM());

                //공영 노외 유료
                row = sheetStatus.getRow(11);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getPBLOUT_PAY_L_SUM()
                        + resPStatusNamguTotal.getPBLOUT_PAY_L_SUM()
                        + resPStatusDongguTotal.getPBLOUT_PAY_L_SUM()
                        + resPStatusBukguTotal.getPBLOUT_PAY_L_SUM()
                        + resPStatusUljugunTotal.getPBLOUT_PAY_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getPBLOUT_PAY_S_SUM()
                        + resPStatusNamguTotal.getPBLOUT_PAY_S_SUM()
                        + resPStatusDongguTotal.getPBLOUT_PAY_S_SUM()
                        + resPStatusBukguTotal.getPBLOUT_PAY_S_SUM()
                        + resPStatusUljugunTotal.getPBLOUT_PAY_S_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getPBLOUT_PAY_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getPBLOUT_PAY_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getPBLOUT_PAY_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getPBLOUT_PAY_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getPBLOUT_PAY_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getPBLOUT_PAY_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getPBLOUT_PAY_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getPBLOUT_PAY_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getPBLOUT_PAY_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getPBLOUT_PAY_S_SUM());

                //공영 노외 무료
                row = sheetStatus.getRow(12);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getPBLOUT_FREE_L_SUM()
                        + resPStatusNamguTotal.getPBLOUT_FREE_L_SUM()
                        + resPStatusDongguTotal.getPBLOUT_FREE_L_SUM()
                        + resPStatusBukguTotal.getPBLOUT_FREE_L_SUM()
                        + resPStatusUljugunTotal.getPBLOUT_FREE_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getPBLOUT_FREE_S_SUM()
                        + resPStatusNamguTotal.getPBLOUT_FREE_S_SUM()
                        + resPStatusDongguTotal.getPBLOUT_FREE_S_SUM()
                        + resPStatusBukguTotal.getPBLOUT_FREE_S_SUM()
                        + resPStatusUljugunTotal.getPBLOUT_FREE_S_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getPBLOUT_FREE_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getPBLOUT_FREE_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getPBLOUT_FREE_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getPBLOUT_FREE_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getPBLOUT_FREE_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getPBLOUT_FREE_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getPBLOUT_FREE_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getPBLOUT_FREE_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getPBLOUT_FREE_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getPBLOUT_FREE_S_SUM());

                //민영
                row = sheetStatus.getRow(13);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getPRV_L_SUM()
                        + resPStatusNamguTotal.getPRV_L_SUM()
                        + resPStatusDongguTotal.getPRV_L_SUM()
                        + resPStatusBukguTotal.getPRV_L_SUM()
                        + resPStatusUljugunTotal.getPRV_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getPRV_S_SUM()
                        + resPStatusNamguTotal.getPRV_S_SUM()
                        + resPStatusDongguTotal.getPRV_S_SUM()
                        + resPStatusBukguTotal.getPRV_S_SUM()
                        + resPStatusUljugunTotal.getPRV_S_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getPRV_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getPRV_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getPRV_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getPRV_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getPRV_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getPRV_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getPRV_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getPRV_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getPRV_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getPRV_S_SUM());

                //부설소계
                row = sheetStatus.getRow(14);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getSUB_L_SUBTOTAL()
                        + resPStatusNamguTotal.getSUB_L_SUBTOTAL()
                        + resPStatusDongguTotal.getSUB_L_SUBTOTAL()
                        + resPStatusBukguTotal.getSUB_L_SUBTOTAL()
                        + resPStatusUljugunTotal.getSUB_L_SUBTOTAL());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getSUB_S_SUBTOTAL()
                        + resPStatusNamguTotal.getSUB_S_SUBTOTAL()
                        + resPStatusDongguTotal.getSUB_S_SUBTOTAL()
                        + resPStatusBukguTotal.getSUB_S_SUBTOTAL()
                        + resPStatusUljugunTotal.getSUB_S_SUBTOTAL());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getSUB_L_SUBTOTAL());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getSUB_S_SUBTOTAL());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getSUB_L_SUBTOTAL());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getSUB_S_SUBTOTAL());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getSUB_L_SUBTOTAL());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getSUB_S_SUBTOTAL());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getSUB_L_SUBTOTAL());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getSUB_S_SUBTOTAL());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getSUB_L_SUBTOTAL());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getSUB_S_SUBTOTAL());

                ///////////////

                //부설 자주식 노면
                row = sheetStatus.getRow(15);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getSUBSE_SUR_L_SUM()
                        + resPStatusNamguTotal.getSUBSE_SUR_L_SUM()
                        + resPStatusDongguTotal.getSUBSE_SUR_L_SUM()
                        + resPStatusBukguTotal.getSUBSE_SUR_L_SUM()
                        + resPStatusUljugunTotal.getSUBSE_SUR_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getSUBSE_SUR_S_SUM()
                        + resPStatusNamguTotal.getSUBSE_SUR_S_SUM()
                        + resPStatusDongguTotal.getSUBSE_SUR_S_SUM()
                        + resPStatusBukguTotal.getSUBSE_SUR_S_SUM()
                        + resPStatusUljugunTotal.getSUBSE_SUR_S_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getSUBSE_SUR_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getSUBSE_SUR_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getSUBSE_SUR_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getSUBSE_SUR_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getSUBSE_SUR_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getSUBSE_SUR_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getSUBSE_SUR_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getSUBSE_SUR_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getSUBSE_SUR_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getSUBSE_SUR_S_SUM());

                //부설 자주식 조립식
                row = sheetStatus.getRow(16);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getSUBSE_MOD_L_SUM()
                        + resPStatusNamguTotal.getSUBSE_MOD_L_SUM()
                        + resPStatusDongguTotal.getSUBSE_MOD_L_SUM()
                        + resPStatusBukguTotal.getSUBSE_MOD_L_SUM()
                        + resPStatusUljugunTotal.getSUBSE_MOD_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getSUBSE_MOD_S_SUM()
                        + resPStatusNamguTotal.getSUBSE_MOD_S_SUM()
                        + resPStatusDongguTotal.getSUBSE_MOD_S_SUM()
                        + resPStatusBukguTotal.getSUBSE_MOD_S_SUM()
                        + resPStatusUljugunTotal.getSUBSE_MOD_S_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getSUBSE_MOD_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getSUBSE_MOD_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getSUBSE_MOD_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getSUBSE_MOD_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getSUBSE_MOD_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getSUBSE_MOD_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getSUBSE_MOD_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getSUBSE_MOD_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getSUBSE_MOD_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getSUBSE_MOD_S_SUM());

                //부설 기계식 부속
                row = sheetStatus.getRow(17);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getSUBAU_ATT_L_SUM()
                        + resPStatusNamguTotal.getSUBAU_ATT_L_SUM()
                        + resPStatusDongguTotal.getSUBAU_ATT_L_SUM()
                        + resPStatusBukguTotal.getSUBAU_ATT_L_SUM()
                        + resPStatusUljugunTotal.getSUBAU_ATT_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getSUBAU_ATT_S_SUM()
                        + resPStatusNamguTotal.getSUBAU_ATT_S_SUM()
                        + resPStatusDongguTotal.getSUBAU_ATT_S_SUM()
                        + resPStatusBukguTotal.getSUBAU_ATT_S_SUM()
                        + resPStatusUljugunTotal.getSUBAU_ATT_S_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getSUBAU_ATT_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getSUBAU_ATT_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getSUBAU_ATT_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getSUBAU_ATT_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getSUBAU_ATT_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getSUBAU_ATT_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getSUBAU_ATT_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getSUBAU_ATT_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getSUBAU_ATT_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getSUBAU_ATT_S_SUM());

                //부설 기계식 전용
                row = sheetStatus.getRow(18);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getSUBAU_PRV_L_SUM()
                        + resPStatusNamguTotal.getSUBAU_PRV_L_SUM()
                        + resPStatusDongguTotal.getSUBAU_PRV_L_SUM()
                        + resPStatusBukguTotal.getSUBAU_PRV_L_SUM()
                        + resPStatusUljugunTotal.getSUBAU_PRV_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getSUBAU_PRV_S_SUM()
                        + resPStatusNamguTotal.getSUBAU_PRV_S_SUM()
                        + resPStatusDongguTotal.getSUBAU_PRV_S_SUM()
                        + resPStatusBukguTotal.getSUBAU_PRV_S_SUM()
                        + resPStatusUljugunTotal.getSUBAU_PRV_S_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getSUBAU_PRV_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getSUBAU_PRV_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getSUBAU_PRV_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getSUBAU_PRV_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getSUBAU_PRV_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getSUBAU_PRV_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getSUBAU_PRV_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getSUBAU_PRV_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getSUBAU_PRV_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getSUBAU_PRV_S_SUM());

                //자가주차장 소계
                row = sheetStatus.getRow(19);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getOWN_L_SUBTOTAL()
                        + resPStatusNamguTotal.getOWN_L_SUBTOTAL()
                        + resPStatusDongguTotal.getOWN_L_SUBTOTAL()
                        + resPStatusBukguTotal.getOWN_L_SUBTOTAL()
                        + resPStatusUljugunTotal.getOWN_L_SUBTOTAL());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getOWN_S_SUBTOTAL()
                        + resPStatusNamguTotal.getOWN_S_SUBTOTAL()
                        + resPStatusDongguTotal.getOWN_S_SUBTOTAL()
                        + resPStatusBukguTotal.getOWN_S_SUBTOTAL()
                        + resPStatusUljugunTotal.getOWN_S_SUBTOTAL());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getOWN_L_SUBTOTAL());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getOWN_S_SUBTOTAL());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getOWN_L_SUBTOTAL());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getOWN_S_SUBTOTAL());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getOWN_L_SUBTOTAL());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getOWN_S_SUBTOTAL());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getOWN_L_SUBTOTAL());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getOWN_S_SUBTOTAL());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getOWN_L_SUBTOTAL());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getOWN_S_SUBTOTAL());

                //자가주차장 단독
                row = sheetStatus.getRow(20);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getOWN_HOME_L_SUM()
                        + resPStatusNamguTotal.getOWN_HOME_L_SUM()
                        + resPStatusDongguTotal.getOWN_HOME_L_SUM()
                        + resPStatusBukguTotal.getOWN_HOME_L_SUM()
                        + resPStatusUljugunTotal.getOWN_HOME_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getOWN_HOME_S_SUM()
                        + resPStatusNamguTotal.getOWN_HOME_S_SUM()
                        + resPStatusDongguTotal.getOWN_HOME_S_SUM()
                        + resPStatusBukguTotal.getOWN_HOME_S_SUM()
                        + resPStatusUljugunTotal.getOWN_HOME_S_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getOWN_HOME_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getOWN_HOME_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getOWN_HOME_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getOWN_HOME_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getOWN_HOME_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getOWN_HOME_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getOWN_HOME_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getOWN_HOME_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getOWN_HOME_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getOWN_HOME_S_SUM());


                //자가주차장 공동
                row = sheetStatus.getRow(21);
                row.getCell(3).setCellValue(resPStatusJungguTotal.getOWN_APT_L_SUM()
                        + resPStatusNamguTotal.getOWN_APT_L_SUM()
                        + resPStatusDongguTotal.getOWN_APT_L_SUM()
                        + resPStatusBukguTotal.getOWN_APT_L_SUM()
                        + resPStatusUljugunTotal.getOWN_APT_L_SUM());

                row.getCell(4).setCellValue(resPStatusJungguTotal.getOWN_APT_S_SUM()
                        + resPStatusNamguTotal.getOWN_APT_S_SUM()
                        + resPStatusDongguTotal.getOWN_APT_S_SUM()
                        + resPStatusBukguTotal.getOWN_APT_S_SUM()
                        + resPStatusUljugunTotal.getOWN_APT_S_SUM());

                row.getCell(5).setCellValue(resPStatusJungguTotal.getOWN_APT_L_SUM());
                row.getCell(6).setCellValue(resPStatusJungguTotal.getOWN_APT_S_SUM());

                row.getCell(7).setCellValue(resPStatusNamguTotal.getOWN_APT_L_SUM());
                row.getCell(8).setCellValue(resPStatusNamguTotal.getOWN_APT_S_SUM());

                row.getCell(9).setCellValue(resPStatusDongguTotal.getOWN_APT_L_SUM());
                row.getCell(10).setCellValue(resPStatusDongguTotal.getOWN_APT_S_SUM());

                row.getCell(11).setCellValue(resPStatusBukguTotal.getOWN_APT_L_SUM());
                row.getCell(12).setCellValue(resPStatusBukguTotal.getOWN_APT_S_SUM());

                row.getCell(13).setCellValue(resPStatusUljugunTotal.getOWN_APT_L_SUM());
                row.getCell(14).setCellValue(resPStatusUljugunTotal.getOWN_APT_S_SUM());

            }
            else  throw new IllegalArgumentException("'주차장확보현황_표준' 시트가 발견되지 않았습니다.");


            PSubIncrsDto.Keyword pSubIncrsDto =  new PSubIncrsDto.Keyword();
            pSubIncrsDto.setYear(req.getYear());
            pSubIncrsDto.setMonth(req.getMonth());
            //pSubIncrsDto.setSggCd(req.getSggCd());

            List<PSubIncrsDto> resPSubIncrsList = queryPSubIncrs.search(pSubIncrsDto);

            XSSFSheet sheetPSubIncrs = wb.getSheet("주차장증가현황_표준양식");
            if (sheetPSubIncrs != null) {
                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetPSubIncrs.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 부설주차장 증가 현황");

                row = sheetPSubIncrs.getRow(2);
                row.getCell(20).setCellValue("[울산광역시]");

                for (int i =0; i<resPSubIncrsList.size(); i++){
                    row = sheetPSubIncrs.createRow(i+4);
                    row.createCell(0).setCellValue(alterSggCdToSggName(resPSubIncrsList.get(i).getSggCd()));
                    row.createCell(1).setCellValue(i+1);
                    row.createCell(2).setCellValue(resPSubIncrsList.get(i).getBuildType());
                    row.createCell(3).setCellValue(resPSubIncrsList.get(i).getBuildOwner());
                    row.createCell(4).setCellValue(resPSubIncrsList.get(i).getBuildNm());
                    row.createCell(5).setCellValue(resPSubIncrsList.get(i).getPermitNo());
                    row.createCell(6).setCellValue(resPSubIncrsList.get(i).getPrmsnYmd());
                    row.createCell(7).setCellValue(resPSubIncrsList.get(i).getApprovalDt());
                    row.createCell(8).setCellValue(resPSubIncrsList.get(i).getLocation());
                    row.createCell(9).setCellValue(resPSubIncrsList.get(i).getTtlFlarea());
                    row.createCell(10).setCellValue(resPSubIncrsList.get(i).getMainUse());
                    row.createCell(11).setCellValue(resPSubIncrsList.get(i).getSubUse());
                    row.createCell(12).setCellValue(alterNullToZero(resPSubIncrsList.get(i).getGeneration()));
                    row.createCell(13).setCellValue(resPSubIncrsList.get(i).getBldHo());
                    row.createCell(14).setCellValue(alterNullToZero(resPSubIncrsList.get(i).getHouseholds()));
                    row.createCell(15).setCellValue(resPSubIncrsList.get(i).getTotalArea());
                    row.createCell(16).setCellValue(alterNullToZero(resPSubIncrsList.get(i).getSpaces()));
                    row.createCell(17).setCellValue(alterNullToZero(resPSubIncrsList.get(i).getAddPkspaceCnt()));
                    row.createCell(18).setCellValue(resPSubIncrsList.get(i).getSubau());
                    row.createCell(19).setCellValue(resPSubIncrsList.get(i).getSubse());
                    row.createCell(20).setCellValue(resPSubIncrsList.get(i).getRmrk());
                }


            }
            else  throw new IllegalArgumentException("'주차장증가현황_표준양식' 시트가 발견되지 않았습니다.");


            PSubDcrsDto.Keyword pSubDcrsDto =  new PSubDcrsDto.Keyword();
            pSubDcrsDto.setYear(req.getYear());
            pSubDcrsDto.setMonth(req.getMonth());

            List<PSubDcrsDto> resPSubDcrsList = queryPSubDcrs.search(pSubDcrsDto);

            XSSFSheet sheetPSubDcrs = wb.getSheet("주차장감소현황_표준양식");
            if (sheetPSubDcrs != null) {
                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetPSubDcrs.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 부설주차장 감소 현황");

                row = sheetPSubDcrs.getRow(2);
                row.getCell(17).setCellValue("[울산광역시]");

                for (int i =0; i<resPSubDcrsList.size(); i++){
                    row = sheetPSubDcrs.createRow(i+4);
                    row.createCell(0).setCellValue(alterSggCdToSggName(resPSubDcrsList.get(i).getSggCd()));
                    row.createCell(1).setCellValue(resPSubDcrsList.get(i).getReportNo());
                    row.createCell(2).setCellValue(resPSubDcrsList.get(i).getLocation());
                    row.createCell(3).setCellValue(resPSubDcrsList.get(i).getOwner());
                    row.createCell(4).setCellValue(resPSubDcrsList.get(i).getType());
                    row.createCell(5).setCellValue(resPSubDcrsList.get(i).getSpaces());
                    row.createCell(6).setCellValue(resPSubDcrsList.get(i).getTotalArea());
                    row.createCell(7).setCellValue(resPSubDcrsList.get(i).getDemolitionDt());
                    row.createCell(8).setCellValue(resPSubDcrsList.get(i).getDemolitionReason());
                    row.createCell(9).setCellValue(resPSubDcrsList.get(i).getStructure());
                    row.createCell(10).setCellValue(resPSubDcrsList.get(i).getBuildUsage());
                    row.createCell(11).setCellValue(resPSubDcrsList.get(i).getAbtCelYn());
                    row.createCell(12).setCellValue(resPSubDcrsList.get(i).getAbtIstYn());
                    row.createCell(13).setCellValue(resPSubDcrsList.get(i).getAbtRoofYn());
                    row.createCell(14).setCellValue(resPSubDcrsList.get(i).getAbtLagYn());
                    row.createCell(15).setCellValue(resPSubDcrsList.get(i).getAbtEtcYn());
                    row.createCell(16).setCellValue(resPSubDcrsList.get(i).getAbtNonYn());
                    row.createCell(17).setCellValue(resPSubDcrsList.get(i).getPrmsnYmd());
                }
            }
            else  throw new IllegalArgumentException("'주차장감소현황_표준양식' 시트가 발견되지 않았습니다.");



            PPublicDto.Keyword pPublicDto =  new PPublicDto.Keyword();
            pPublicDto.setYear(req.getYear());
            pPublicDto.setMonth(req.getMonth());

            List<PPublicDto> resPPublicList = queryPPublic.search(pPublicDto);


            XSSFSheet sheetPPublic = wb.getSheet("공영주차장 현황_표준양식");
            if (sheetPPublic != null) {
                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetPPublic.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 공영주차장 현황");

                row = sheetPPublic.getRow(2);
                row.getCell(28).setCellValue("[울산광역시]");

                for (int i =0; i<resPPublicList.size(); i++){
                    row = sheetPPublic.createRow(i+5);
                    row.createCell(0).setCellValue(alterSggCdToSggName(resPPublicList.get(i).getSggCd()));
                    row.createCell(1).setCellValue(i+1);
                    row.createCell(2).setCellValue(resPPublicList.get(i).getMngNo());
                    row.createCell(3).setCellValue(resPPublicList.get(i).getName());
                    row.createCell(4).setCellValue(resPPublicList.get(i).getInstallDt());
                    row.createCell(5).setCellValue(resPPublicList.get(i).getLocation());
                    row.createCell(6).setCellValue(resPPublicList.get(i).getPoint_out());
                    row.createCell(7).setCellValue(resPPublicList.get(i).getArea());
                    row.createCell(8).setCellValue(resPPublicList.get(i).getWh());
                    row.createCell(9).setCellValue(resPPublicList.get(i).getWhSaturday());
                    row.createCell(10).setCellValue(resPPublicList.get(i).getWhHoliday());
                    row.createCell(11).setCellValue(resPPublicList.get(i).getDayOff());
                    row.createCell(12).setCellValue(resPPublicList.get(i).getPayYn().equals("Y") ? "유료" : "무료");
                    row.createCell(13).setCellValue(resPPublicList.get(i).getPay4Hour());
                    row.createCell(14).setCellValue(resPPublicList.get(i).getPay4Day());
                    row.createCell(15).setCellValue(resPPublicList.get(i).getTotalSpaces());
                    row.createCell(16).setCellValue(resPPublicList.get(i).getSpaces());

                    //소계
                    row.createCell(17).setCellValue(alterNullToZero(resPPublicList.get(i).getForDisabled())
                    +alterNullToZero(resPPublicList.get(i).getForLight())
                    +alterNullToZero(resPPublicList.get(i).getForPregnant())
                    +alterNullToZero(resPPublicList.get(i).getForBus())
                    +alterNullToZero(resPPublicList.get(i).getForEcho())
                    +alterNullToZero(resPPublicList.get(i).getForElderly())
                    +alterNullToZero(resPPublicList.get(i).getForElectric()));

                    row.createCell(18).setCellValue(alterNullToZero(resPPublicList.get(i).getForDisabled()));
                    row.createCell(19).setCellValue(alterNullToZero(resPPublicList.get(i).getForLight()));
                    row.createCell(20).setCellValue(alterNullToZero(resPPublicList.get(i).getForPregnant()));
                    row.createCell(21).setCellValue(alterNullToZero(resPPublicList.get(i).getForBus()));
                    row.createCell(22).setCellValue(alterNullToZero(resPPublicList.get(i).getForEcho()));
                    row.createCell(23).setCellValue(alterNullToZero(resPPublicList.get(i).getForElderly()));
                    row.createCell(24).setCellValue(alterNullToZero(resPPublicList.get(i).getForElectric()));

                    row.createCell(25).setCellValue(resPPublicList.get(i).getRoadYn());
                    row.createCell(26).setCellValue(resPPublicList.get(i).getOwner());
                    row.createCell(27).setCellValue(resPPublicList.get(i).getAgency());
                    row.createCell(28).setCellValue(resPPublicList.get(i).getComment());


                }
            }
            else  throw new IllegalArgumentException("'공영주차장 현황_표준양식' 시트가 발견되지 않았습니다.");

            /*

            PResiDto.Keyword pResiDto =  new PResiDto.Keyword();
            pResiDto.setYear(req.getYear());
            pResiDto.setMonth(req.getMonth());

            //거주자 양식은 중구만 존재
            pResiDto.setSggCd("31110");

            List<PResiDto> resPResiList = queryPResi.searchPrev(pResiDto);
            if (resPResiList.size() > 0) {
                XSSFSheet sheetPResi = wb.getSheet("거주자우선주차제현황_표준양식");
                if (sheetPResi != null) {
                    XSSFRow row;
                    int rowIndex = 0;
                    for (rowIndex = 0; rowIndex < resPResiList.size(); rowIndex++) {
                        row = sheetPResi.createRow(rowIndex + 4);

                        row.createCell(0).setCellValue(resPResiList.get(rowIndex).getYear());
                        row.createCell(1).setCellValue(resPResiList.get(rowIndex).getMonth());
                        row.createCell(2).setCellValue(resPResiList.get(rowIndex).getPrevSpaces());
                        row.createCell(3).setCellValue(resPResiList.get(rowIndex).getNewSpaces());
                        row.createCell(4).setCellValue(resPResiList.get(rowIndex).getLostSpaces());
                        row.createCell(5).setCellValue(resPResiList.get(rowIndex).getReSpaces());
                        row.createCell(6).setCellValue(resPResiList.get(rowIndex).getVariance());
                        row.createCell(7).setCellValue(resPResiList.get(rowIndex).getThisSpaces());
                        row.createCell(8).setCellValue(resPResiList.get(rowIndex).getThisArea());
                        row.createCell(9).setCellValue(resPResiList.get(rowIndex).getVarianceReason());
                        row.createCell(10).setCellValue(resPResiList.get(rowIndex).getNonUse());
                        row.createCell(11).setCellValue(resPResiList.get(rowIndex).getInUse());

                    }
                    int startYear = Integer.parseInt(resPResiList.get(0).getYear());
                    int yearCount = rowIndex / 12;
                    int remMonth = rowIndex % 12;
                    for (int i = 0; i < yearCount; i++) {
                        sheetPResi.addMergedRegion(new CellRangeAddress((i * 12) + 4, (i * 12) + 11 + 4, 0, 0));
                    }
                    if (remMonth != 0) {
                        sheetPResi.addMergedRegion(new CellRangeAddress((yearCount * 12) + 4, (yearCount * 12) + remMonth + 4, 0, 0));
                    }
                } else throw new IllegalArgumentException("'거주자우선주차제현황_표준양식' 시트가 발견되지 않았습니다.");
            }
            */
            //3) 엑셀 생성, 출력
            //String fileNm = "Swagger 에서는 한글명 제대로 출력중";
            String fileNm = req.getYear()+"년 "+req.getMonth()+"월 "+ "월간 보고 현황";
            ExcelManager.writeExcelFile(response, wb, fileNm);

        } catch (IOException e) {
            logErr(e);
        }
    }


    public void excelFormDownload(HttpServletResponse response, MrDataDto.Keyword req) {
        String sggNm = "";
        if (req.getSggCd().equals("31110")) {
            sggNm = "중구";
        } else if (req.getSggCd().equals("31140")) {
            sggNm = "남구";
        } else if (req.getSggCd().equals("31170")) {
            sggNm = "동구";
        } else if (req.getSggCd().equals("31200")) {
            sggNm = "북구";
        } else if (req.getSggCd().equals("31710")) {
            sggNm = "울주군";
        }

        PStatusDto.Keyword pStatusDto = new PStatusDto.Keyword();
        pStatusDto.setYear(req.getYear());
        pStatusDto.setMonth(req.getMonth());
        pStatusDto.setSggCd(req.getSggCd());

        PStatusDto.Total resPStatusTotal = queryPStatus.prevTotal(pStatusDto);
//        1) 엑셀 생성
        try (XSSFWorkbook wb = ExcelManager.readExcelFile(mrStdExcelPath, mrStdExcelName)) {
            XSSFSheet sheetStatus = wb.getSheet("주차장확보현황_표준");
            if (sheetStatus != null) {
                String prevYear = req.getYear();
                String prevMonth = req.getMonth();
                if (req.getMonth().equals("1")) {
                    prevYear = (Integer.toString(Integer.parseInt(req.getYear()) - 1));
                    prevMonth = "12";
                } else if (req.getMonth() != null) {
                    prevMonth = (Integer.toString(Integer.parseInt(req.getMonth()) - 1));
                }

                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetStatus.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 주차장 확보현황");

                //주차장확보현황 구군
                row = sheetStatus.getRow(2);
                row.getCell(17).setCellValue("[울산광역시 " + sggNm + "]");

                //전월 누계
                row = sheetStatus.getRow(3);
                row.getCell(3).setCellValue(prevYear + "년 " + prevMonth + "월 누계");

                //금월 누계
                row = sheetStatus.getRow(3);
                row.getCell(15).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 누계");

                pStatusPrevMonthDbToExcel(sheetStatus, resPStatusTotal);
            } else throw new IllegalArgumentException("'주차장확보현황_표준' 시트가 발견되지 않았습니다.");


            XSSFSheet sheetPSubIncrs = wb.getSheet("주차장증가현황_표준양식");
            if (sheetPSubIncrs != null) {
                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetPSubIncrs.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 부설주차장 증가 현황");

                row = sheetPSubIncrs.getRow(2);
                row.getCell(2).setCellValue("[울산광역시 " + sggNm + "]");


            } else throw new IllegalArgumentException("'주차장증가현황_표준양식' 시트가 발견되지 않았습니다.");

            XSSFSheet sheetPSubDcrs = wb.getSheet("주차장감소현황_표준양식");
            if (sheetPSubDcrs != null) {
                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetPSubDcrs.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 부설주차장 감소 현황");

                row = sheetPSubDcrs.getRow(2);
                row.getCell(2).setCellValue("[울산광역시 " + sggNm + "]");


            } else throw new IllegalArgumentException("'주차장감소현황_표준양식' 시트가 발견되지 않았습니다.");


            PPublicDto.Keyword pPublicDto = new PPublicDto.Keyword();
            pPublicDto.setYear(req.getYear());
            pPublicDto.setMonth(req.getMonth());
            pPublicDto.setSggCd(req.getSggCd());

            List<PPublicDto> resPPublicList = queryPPublic.searchPrev(pPublicDto);


            XSSFSheet sheetPPublic = wb.getSheet("공영주차장 현황_표준양식");
            if (sheetPPublic != null) {
                XSSFRow row;
                //주차장확보현황 타이틀
                row = sheetPPublic.getRow(0);
                row.getCell(0).setCellValue(req.getYear() + "년 " + req.getMonth() + "월 공영주차장 현황");

                row = sheetPPublic.getRow(2);
                row.getCell(3).setCellValue("[울산광역시 " + sggNm + "]");

                for (int i = 0; i < resPPublicList.size(); i++) {
                    row = sheetPPublic.createRow(i + 5);
                    row.createCell(0).setCellValue(i + 1);
                    row.createCell(1).setCellValue(resPPublicList.get(i).getMngNo());
                    row.createCell(2).setCellValue(resPPublicList.get(i).getName());
                    row.createCell(3).setCellValue(resPPublicList.get(i).getInstallDt());
                    row.createCell(4).setCellValue(resPPublicList.get(i).getLocation());
                    row.createCell(5).setCellValue(resPPublicList.get(i).getPoint_out());
                    row.createCell(6).setCellValue(resPPublicList.get(i).getArea());
                    row.createCell(7).setCellValue(resPPublicList.get(i).getWh());
                    row.createCell(8).setCellValue(resPPublicList.get(i).getWhSaturday());
                    row.createCell(9).setCellValue(resPPublicList.get(i).getWhHoliday());
                    row.createCell(10).setCellValue(resPPublicList.get(i).getDayOff());
                    row.createCell(11).setCellValue(resPPublicList.get(i).getPayYn().equals("Y") ? "유료" : "무료");
                    row.createCell(12).setCellValue(resPPublicList.get(i).getPay4Hour());
                    row.createCell(13).setCellValue(resPPublicList.get(i).getPay4Day());
                    row.createCell(14).setCellValue(resPPublicList.get(i).getTotalSpaces());
                    row.createCell(15).setCellValue(resPPublicList.get(i).getSpaces());

                    row.createCell(16).setCellValue(resPPublicList.get(i).getForDisabled()
                            + resPPublicList.get(i).getForLight()
                            + resPPublicList.get(i).getForPregnant()
                            + resPPublicList.get(i).getForBus()
                            + resPPublicList.get(i).getForEcho()
                            + resPPublicList.get(i).getForElderly()
                            + resPPublicList.get(i).getForElectric());

                    row.createCell(17).setCellValue(resPPublicList.get(i).getForDisabled());
                    row.createCell(18).setCellValue(resPPublicList.get(i).getForLight());
                    row.createCell(19).setCellValue(resPPublicList.get(i).getForPregnant());
                    row.createCell(20).setCellValue(resPPublicList.get(i).getForBus());
                    row.createCell(21).setCellValue(resPPublicList.get(i).getForEcho());
                    row.createCell(22).setCellValue(resPPublicList.get(i).getForElderly());
                    row.createCell(23).setCellValue(resPPublicList.get(i).getForElectric());
                    row.createCell(24).setCellValue(resPPublicList.get(i).getRoadYn());
                    row.createCell(25).setCellValue(resPPublicList.get(i).getOwner());
                    row.createCell(26).setCellValue(resPPublicList.get(i).getAgency());
                    row.createCell(27).setCellValue(resPPublicList.get(i).getComment());


                }
            } else throw new IllegalArgumentException("'공영주차장 현황_표준양식' 시트가 발견되지 않았습니다.");


            PResiDto.Keyword pResiDto = new PResiDto.Keyword();
            pResiDto.setYear(req.getYear());
            pResiDto.setMonth(req.getMonth());
            pResiDto.setSggCd(req.getSggCd());

            List<PResiDto> resPResiList = queryPResi.searchPrev(pResiDto);
            if (resPResiList.size() > 0) {
                XSSFSheet sheetPResi = wb.getSheet("거주자우선주차제현황_표준양식");
                if (sheetPResi != null) {
                    XSSFRow row;
                    int rowIndex = 0;
                    for (rowIndex = 0; rowIndex < resPResiList.size(); rowIndex++) {
                        row = sheetPResi.createRow(rowIndex + 4);

                        row.createCell(0).setCellValue(resPResiList.get(rowIndex).getYear());
                        row.createCell(1).setCellValue(resPResiList.get(rowIndex).getMonth());
                        row.createCell(2).setCellValue(resPResiList.get(rowIndex).getPrevSpaces());
                        row.createCell(3).setCellValue(resPResiList.get(rowIndex).getNewSpaces());
                        row.createCell(4).setCellValue(resPResiList.get(rowIndex).getLostSpaces());
                        row.createCell(5).setCellValue(resPResiList.get(rowIndex).getReSpaces());
                        row.createCell(6).setCellValue(resPResiList.get(rowIndex).getVariance());
                        row.createCell(7).setCellValue(resPResiList.get(rowIndex).getThisSpaces());
                        row.createCell(8).setCellValue(resPResiList.get(rowIndex).getThisArea());
                        row.createCell(9).setCellValue(resPResiList.get(rowIndex).getVarianceReason());
                        row.createCell(10).setCellValue(resPResiList.get(rowIndex).getNonUse());
                        row.createCell(11).setCellValue(resPResiList.get(rowIndex).getInUse());
/*
                    if((i!=0) && (i%12 == 0)){
                        sheetPResi.addMergedRegion(new CellRangeAddress( (i-12)+4, (i-1)+4, 0, 0 ));
                        sheetPResi.getRow((i-11)+4).getCell(0).setCellValue(resPResiList.get(i).getYear());
                    }
 */
                    }
                    int startYear = Integer.parseInt(resPResiList.get(0).getYear());
                    int yearCount = rowIndex / 12;
                    int remMonth = rowIndex % 12;
                    for (int i = 0; i < yearCount; i++) {
                        sheetPResi.addMergedRegion(new CellRangeAddress((i * 12) + 4, (i * 12) + 11 + 4, 0, 0));
                    }
                    if (remMonth != 0) {
                        sheetPResi.addMergedRegion(new CellRangeAddress((yearCount * 12) + 4, (yearCount * 12) + remMonth + 4, 0, 0));
                    }
                } else throw new IllegalArgumentException("'거주자우선주차제현황_표준양식' 시트가 발견되지 않았습니다.");
            }
//            3) 엑셀 생성, 출력
//            String fileNm = req.getYear() + "년 " + req.getMonth() + "월 " + "월간 보고 현황";
            String fileNm = makeStandardFileNm4Mr(req.getYear(), req.getMonth(), req.getSggCd());
            ExcelManager.writeExcelFile(response, wb, fileNm);
        } catch (IOException e) {
            logErr(e);
            throw new IllegalArgumentException("다운로드 중 오류가 있습니다.");
        }
    }



    private List<PSubIncrs> getPSubIncrs(XSSFWorkbook wb, XSSFFormulaEvaluator eval, CellStyle df, MrData origin) {
        XSSFSheet sheet = wb.getSheet("주차장증가현황_표준양식");
        if (sheet == null) throw new IllegalArgumentException("'주차장증가현황' 시트가 발견되지 않았습니다.");
        log.info("주차장증가현황 데이터 수집 시작.");

        List<PSubIncrs> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("^[a-zA-Z]+");

//            시작행은 5행
        for (int i = 4; i <= sheet.getLastRowNum(); i++) {
            //if (sheet.getRow(i).getCell(0) == null) break;

            PSubIncrsDto.Req req = new PSubIncrsDto.Req();
            req.setYear(origin.getYear());
            req.setMonth(origin.getMonth());
            req.setSggCd(origin.getSggCd());// 고정
            req.setMrData(origin);
            for (Cell cell : sheet.getRow(i)) {
                String cellData = ExcelManager.getCellData(cell);  // cellData
                String addr = cell.getAddress().toString();
                Matcher matcher = pattern.matcher(addr);

                mappingPSubIncrs(req, matcher, cellData);
                if (addr.startsWith("AO")) break;
            }
            // 엔티티로 합치기
            PSubIncrs pSubIncrs = mapper.toPSubIncrs(req);
            list.add(pSubIncrs);
        }
        log.info("기초 데이터 수집 완료.");
        return list;
    }

    private List<PSubDcrs> getPSubDcrs(XSSFWorkbook wb, XSSFFormulaEvaluator eval, CellStyle df, MrData origin) {
        XSSFSheet sheet = wb.getSheet("주차장감소현황_표준양식");
        if (sheet == null) throw new IllegalArgumentException("'주차장감소현황' 시트가 발견되지 않았습니다.");
        log.info("주차장감소현황 데이터 수집 시작.");

        List<PSubDcrs> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("^[a-zA-Z]+");

//            시작행은 5행
        for (int i = 4; i <= sheet.getLastRowNum(); i++) {
            //if (sheet.getRow(i).getCell(0) == null) break;

            PSubDcrsDto.Req req = new PSubDcrsDto.Req();
            req.setYear(origin.getYear());
            req.setMonth(origin.getMonth());
            req.setSggCd(origin.getSggCd());// 고정
            req.setMrData(origin);
            for (Cell cell : sheet.getRow(i)) {
                String cellData = ExcelManager.getCellData(cell);  // cellData
                String addr = cell.getAddress().toString();
                Matcher matcher = pattern.matcher(addr);

                mappingPSubDcrs(req, matcher, cellData);
                if (addr.startsWith("AO")) break;
            }
            // 엔티티로 합치기
            PSubDcrs pSubDcrs = mapper.toPSubDcrs(req);
            list.add(pSubDcrs);
        }
        log.info("기초 데이터 수집 완료.");
        return list;
    }

    private List<PPublic> getPPublic(XSSFWorkbook wb, XSSFFormulaEvaluator eval, CellStyle df, MrData origin) {
        XSSFSheet sheet = wb.getSheet("공영주차장 현황_표준양식");
        if (sheet == null) throw new IllegalArgumentException("'공영주차장 현황_표준양식' 시트가 발견되지 않았습니다.");
        log.info("공영주차장 현황_표준양식 데이터 수집 시작.");

        List<PPublic> list = new ArrayList<>();
        //노상
        List<PPublicDto.Req> reqRdList = new ArrayList<>();
        //노외
        List<PPublicDto.Req> reqOutList = new ArrayList<>();
        //부설
        List<PPublicDto.Req> reqSubList = new ArrayList<>();

        //관리대장 노상
        List<StandardMngDto.Req> mngRdReq = new ArrayList<>();
        //관리대장 노외
        List<StandardMngDto.Req> mngOutReq = new ArrayList<>();
        //관리대장 부설
        List<StandardMngDto.Req> mngSubReq = new ArrayList<>();

        Pattern pattern = Pattern.compile("^[a-zA-Z]+");

        XSSFRow row;
        // 데이터 범위 지정. 2행부터 시작, 1~21열까지 수집.

        for (int i = 5; i <= sheet.getLastRowNum(); i++) {
            row = sheet.getRow(i);
            if (!hasCell(row.getCell(0))) continue;



            // 행당 dto 생성, 디폴트값 세팅.
            PPublicDto.Req req = new PPublicDto.Req();
            req.setYear(origin.getYear());
            req.setMonth(origin.getMonth());
            req.setSggCd(origin.getSggCd());
            req.setMrData(origin);

            for (Cell cell : sheet.getRow(i)) {
                String cellData = ExcelManager.getCellData(cell, eval, df);  // cellData
                String addr = cell.getAddress().toString();
                Matcher matcher = pattern.matcher(addr);

                mappingPPublic(req, matcher, cellData);
                if (addr.startsWith("AO")) break;
            }

            req.setDupChk1(req.getName() + req.getLocation() + Long.toString((long) req.getTotalSpaces()));
            req.setDupChk2(req.getName() + req.getLocation());
            req.setDupChk3(req.getName() + Long.toString((long) req.getTotalSpaces()));
            req.setDupChk4(req.getLocation() + Long.toString((long) req.getTotalSpaces()));



            //geocord
            if (req.getLocation()!=null && !req.getLocation().equals("")) {
                try {
                    HashMap<String, String> lonLat = geoService.request(req.getLocation().replace("\n", " "), "PARCEL");
                    req.setLon(lonLat.get("lon"));
                    req.setLat(lonLat.get("lat"));

                } catch (JsonProcessingException | UnsupportedEncodingException e) {
                    logErr(e);
                    throw new RuntimeException(COLLECT_DATA_ERR.getMsg());
                }
            }
            //이미 일련번호가 있는 row들을 list add
            if (hasCell(row.getCell(1))) {
                list.add(mapper.toPPublic(req));
                continue;
            }
            //노상 일련번호용 list 생성
            String lotType = "";
            if(req.getRoadYn().equals("노상")){
                lotType = "1";
                req.setLotType(lotType);
                reqRdList.add(req);
                mngRdReq.add(StandardMngDto.Req
                        .builder()
                        .year(req.getYear())
                        .month(req.getMonth())
                        .sggCd(req.getSggCd())
                        .lotType(req.getLotType())
                        .dupChk1(req.getName() + req.getLocation() + req.getTotalSpaces())
                        .dupChk2(req.getName() + req.getLocation())
                        .dupChk3(req.getName() + req.getTotalSpaces())
                        .dupChk4(req.getLocation() + req.getTotalSpaces())
                        .build()
                );
            }
            //노외 일련번호용 list 생성
            else if(req.getRoadYn().equals("노외")){
                lotType = "2";
                req.setLotType(lotType);
                reqOutList.add(req);
                mngOutReq.add(StandardMngDto.Req
                        .builder()
                        .year(req.getYear())
                        .month(req.getMonth())
                        .sggCd(req.getSggCd())
                        .lotType(req.getLotType())  // 민영노외 5
                        .dupChk1(req.getName() + req.getLocation() + req.getTotalSpaces())
                        .dupChk2(req.getName() + req.getLocation())
                        .dupChk3(req.getName() + req.getTotalSpaces())
                        .dupChk4(req.getLocation() + req.getTotalSpaces())
                        .build()
                );
            }
            else if(req.getRoadYn().equals("부설")){
                lotType = "3";
                req.setLotType(lotType);
                reqSubList.add(req);
                mngSubReq.add(StandardMngDto.Req
                        .builder()
                        .year(req.getYear())
                        .month(req.getMonth())
                        .sggCd(req.getSggCd())
                        .lotType(req.getLotType())
                        .dupChk1(req.getName() + req.getLocation() + req.getTotalSpaces())
                        .dupChk2(req.getName() + req.getLocation())
                        .dupChk3(req.getName() + req.getTotalSpaces())
                        .dupChk4(req.getLocation() + req.getTotalSpaces())
                        .build()
                );
            }

        }
//        if (reqRdList.isEmpty() && reqOutList.isEmpty()) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());
        if (!reqRdList.isEmpty()) {
//            3) 관리대장 우선 적재
            List<StandardMngDto> mngRdList = mngService.insert(mngRdReq);
            for (int i = 0; i < mngRdList.size(); i++) {
                reqRdList.get(i).setMngNo(mngRdList.get(i).getId());
            }
            //            4) 적재
            //Todo 연번 바껴도 되는지 바뀌면 안뒬시 로직변경 필요
            for (PPublicDto.Req req : reqRdList) {
                list.add(mapper.toPPublic(req));
            }
        }

        if (!reqOutList.isEmpty()) {
//            3) 관리대장 우선 적재
            List<StandardMngDto> mngOutList = mngService.insert(mngOutReq);
            for (int i = 0; i < mngOutList.size(); i++) {
                reqOutList.get(i).setMngNo(mngOutList.get(i).getId());
            }
            //            4) 적재
            //Todo 연번 바껴도 되는지 바뀌면 안뒬시 로직변경 필요
            for (PPublicDto.Req req : reqOutList) {
                list.add(mapper.toPPublic(req));
            }
        }

        if (!reqSubList.isEmpty()) {
//            3) 관리대장 우선 적재
            List<StandardMngDto> mngSubList = mngService.insert(mngSubReq);
            for (int i = 0; i < mngSubList.size(); i++) {
                reqSubList.get(i).setMngNo(mngSubList.get(i).getId());
            }
            //            4) 적재
            //Todo 연번 바껴도 되는지 바뀌면 안뒬시 로직변경 필요
            for (PPublicDto.Req req : reqSubList) {
                list.add(mapper.toPPublic(req));
            }
        }
/*

//            시작행은 5행
        for (int i = 5; i <= sheet.getLastRowNum(); i++) {
            //if (sheet.getRow(i).getCell(0) == null) break;

            PPublicDto.Req req = new PPublicDto.Req();
            req.setYear(year);
            req.setMonth(month);
            req.setSggCd(sggCd);// 고정
            for (Cell cell : sheet.getRow(i)) {
                String cellData = ExcelManager.getCellData(cell);  // cellData
                String addr = cell.getAddress().toString();
                Matcher matcher = pattern.matcher(addr);
                mappingPPublic(req, matcher, cellData);
                if (addr.startsWith("AO")) break;
            }
            // 엔티티로 합치기
            PPublic pPublic = mapper.toPPublic(req);
            list.add(pPublic);
        }
 */

        log.info("기초 데이터 수집 완료.");
        return list;
    }

    private List<PResi> getPResi(XSSFWorkbook wb, XSSFFormulaEvaluator eval, CellStyle df, MrData origin) {
        XSSFSheet sheet = wb.getSheet("거주자우선주차제현황_표준양식");
        if (sheet == null) throw new IllegalArgumentException("'거주자우선주차제현황_표준양식' 시트가 발견되지 않았습니다.");
        log.info("거주자우선주차제현황_표준양식 데이터 수집 시작.");

        List<PResi> list = new ArrayList<>();
        Pattern pattern = Pattern.compile("^[a-zA-Z]+");
        String tempYear="";
//            시작행은 5행
        for (int i = 4; i <= sheet.getLastRowNum(); i++) {
            //if (sheet.getRow(i).getCell(0) == null) break;

            PResiDto.Req req = new PResiDto.Req();
            //req.setYear(year);
            //req.setMonth(month);
            req.setSggCd(origin.getSggCd());// 고정
            req.setMrData(origin);

            if (!(sheet.getRow(i).getCell(0).getCellType() == CellType.BLANK)){
                tempYear = String.valueOf((int)Double.parseDouble(sheet.getRow(i).getCell(0).toString()));
            }
            req.setYear(tempYear);
            for (Cell cell : sheet.getRow(i)) {
                String cellData = ExcelManager.getCellData(cell, eval, df);  // cellData
                String addr = cell.getAddress().toString();
                Matcher matcher = pattern.matcher(addr);

                mappingPResi(req, matcher, cellData);
                if (addr.startsWith("AO")) break;
            }
            // 엔티티로 합치기
            PResi pResi = mapper.toPResi(req);
            list.add(pResi);
        }
        log.info("기초 데이터 수집 완료.");
        return list;
    }

    private String zero2blank(String cellResult){
        return cellResult.equals(ExcelManager.ZERO) ? null : cellResult ;
    }
    private Double str2Double(String cellData){
        if (cellData == null) cellData = "";
        return Double.parseDouble(cellData.equals("") ? "0" : cellData);
    }

    private void mappingPSubIncrs(PSubIncrsDto.Req req, Matcher matcher, String cd) {
        if (!matcher.find()) throw new IllegalArgumentException("올바른 파일 양식이 아닙니다.");

        switch (matcher.group()) {
            case "B":
                req.setBuildType(cd);
                break;
            case "C":
                req.setBuildOwner(cd);
                break;
            case "D":
                req.setBuildNm(cd);
                break;
            case "E":
                req.setPermitNo(cd);
                break;
            case "F":
                req.setPrmsnYmd(cd);
                break;
            case "G":
                req.setApprovalDt(cd);
                break;
            case "H":
                req.setLocation(cd);
                break;
            case "I":
                req.setTtlFlarea(str2Double(cd));
                break;
            case "J":
                req.setMainUse(cd);
                break;
            case "K":
                req.setSubUse(cd);
                break;
            case "L":
                req.setGeneration(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "M":
                req.setBldHo(cd);
                break;
            case "N":
                req.setHouseholds(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "O":
                req.setTotalArea(str2Double(cd));
                break;
            case "P":
                req.setSpaces(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "Q":
                req.setAddPkspaceCnt(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "R":
                req.setSubau(str2Double(cd));
                break;
            case "S":
                req.setSubse(str2Double(cd));
                break;
            case "T":
                req.setRmrk(cd);
                break;
            default:
                break;
        }
    }

    private void mappingPSubDcrs(PSubDcrsDto.Req req, Matcher matcher, String cd) {
        if (!matcher.find()) throw new IllegalArgumentException("올바른 파일 양식이 아닙니다.");
        switch (matcher.group()) {
            case "A":
                req.setReportNo(cd);
                break;
            case "B":
                req.setLocation(cd);
                break;
            case "C":
                req.setOwner(cd);
                break;
            case "D":
                req.setType(cd);
                break;
            case "E":
                req.setSpaces(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "F":
                req.setTotalArea(str2Double(cd));
                break;
            case "G":
                req.setDemolitionDt(cd);
                break;
            case "H":
                req.setDemolitionReason(cd);
                break;
            case "I":
                req.setStructure(cd);
                break;
            case "J":
                req.setBuildUsage(cd);
                break;
            case "K":
                req.setAbtCelYn(cd);
                break;
            case "L":
                req.setAbtIstYn(cd);
                break;
            case "M":
                req.setAbtRoofYn(cd);
                break;
            case "N":
                req.setAbtLagYn(cd);
                break;
            case "O":
                req.setAbtEtcYn(cd);
                break;
            case "P":
                req.setAbtNonYn(cd);
                break;
            case "Q":
                req.setPrmsnYmd(cd);
                break;

            default:
                break;
        }
    }

    private void mappingPPublic(PPublicDto.Req req, Matcher matcher, String cd) {
        if (!matcher.find()) throw new IllegalArgumentException("올바른 파일 양식이 아닙니다.");
        switch (matcher.group()) {

            case "A":
                req.setSeq((long)Double.parseDouble(cd.equals("") ? "0" : cd));
                break;
            case "B":
                req.setMngNo(cd);
                break;

            case "C":
                if(cd.equals("거주자우선")){
                    req.setResiYn("Y");
                }else{
                    req.setResiYn("N");
                }
                req.setName(cd);
                break;
            case "D":
                req.setInstallDt(cd);
                if(req.getResiYn().equals("Y")){
                    req.setInstallDt("");
                    req.setLocation(cd);
                }
                break;
            case "E":
                if(req.getResiYn().equals("Y")){
                    break;
                }
                req.setLocation(cd);
                break;
            case "F":
                req.setPoint_out(cd);
                break;
            case "G":
                req.setArea(str2Double(cd));
                break;
            case "H":
                req.setWh(cd);
                break;
            case "I":
                req.setWhSaturday(cd);
                break;
            case "J":
                req.setWhHoliday(cd);
                break;
            case "K":
                req.setDayOff(cd);
                break;
            case "L":
                req.setPayYn(cd.equals("유료") ? "Y" : "N");
                break;
            case "M":
                req.setPay4Day(cd);
                break;
            case "N":
                req.setPay4Hour(cd);
                break;
            case "O":
                req.setTotalSpaces(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "P":
                req.setSpaces(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "R":
                req.setForDisabled(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "S":
                req.setForLight(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "T":
                req.setForPregnant(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "U":
                req.setForBus(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "V":
                req.setForEcho(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "W":
                req.setForElderly(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "X":
                req.setForElectric(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "Y":
                req.setRoadYn(cd);
                break;
            case "Z":
                req.setOwner(cd);
                break;
            case "AA":
                req.setAgency(cd);
                break;
            case "AB":
                req.setComment(cd);
                break;
            default:
                break;
        }
    }

    private void mappingPResi(PResiDto.Req req, Matcher matcher, String cd) {
        if (!matcher.find()) throw new IllegalArgumentException("올바른 파일 양식이 아닙니다.");
        switch (matcher.group()) {
            /*
            case "A":
                req.setReportNo(cd);
                break;

             */
            case "B":
                req.setMonth(String.valueOf((int)Double.parseDouble(cd)));
                break;
            case "C":
                req.setPrevSpaces(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "D":
                req.setNewSpaces(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "E":
                req.setLostSpaces(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "F":
                req.setReSpaces(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "G":
                req.setVariance(str2Double(cd));
                break;
            case "H":
                req.setThisSpaces(Double.valueOf(str2Double(cd)).longValue());
                break;
            case "I":
                req.setThisArea(str2Double(cd));
                break;
            case "J":
                req.setVarianceReason(cd);
                break;
            case "K":
                req.setNonUse(str2Double(cd));
                break;
            case "L":
                req.setInUse(str2Double(cd));
                break;
            default:
                break;
        }
    }

    //금월 누계 db to excel
    private void pStatusThisMonthDbToExcel(XSSFSheet sheetStatus, PStatusDto.Total resPStatusTotal){
        XSSFRow row;

        //총계
        row = sheetStatus.getRow(6);
        row.getCell(15).setCellValue(resPStatusTotal.getTOTAL_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getTOTAL_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getTOTAL_A_SUM());

        //공영소계
        row = sheetStatus.getRow(7);
        row.getCell(15).setCellValue(resPStatusTotal.getPBL_L_SUBTOTAL());
        row.getCell(16).setCellValue(resPStatusTotal.getPBL_S_SUBTOTAL());
        row.getCell(17).setCellValue(resPStatusTotal.getPBL_A_SUBTOTAL());

        //공영 노상 유료
        row = sheetStatus.getRow(8);
        row.getCell(15).setCellValue(resPStatusTotal.getPBLRD_PAY_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getPBLRD_PAY_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getPBLRD_PAY_A_SUM());

        //공영 노상 무료
        row = sheetStatus.getRow(9);
        row.getCell(15).setCellValue(resPStatusTotal.getPBLRD_FREE_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getPBLRD_FREE_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getPBLRD_FREE_A_SUM());

        //공영 거주자 무료
        row = sheetStatus.getRow(10);
        row.getCell(15).setCellValue(resPStatusTotal.getPBLRD_RESI_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getPBLRD_RESI_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getPBLRD_RESI_A_SUM());

        //공영 거주자 무료
        row = sheetStatus.getRow(11);
        row.getCell(15).setCellValue(resPStatusTotal.getPBLOUT_PAY_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getPBLOUT_PAY_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getPBLOUT_PAY_A_SUM());

        //공영 거주자 무료
        row = sheetStatus.getRow(12);
        row.getCell(15).setCellValue(resPStatusTotal.getPBLOUT_FREE_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getPBLOUT_FREE_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getPBLOUT_FREE_A_SUM());

        //민영
        row = sheetStatus.getRow(13);
        row.getCell(15).setCellValue(resPStatusTotal.getPRV_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getPRV_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getPRV_A_SUM());

        //부설소계
        row = sheetStatus.getRow(14);
        row.getCell(15).setCellValue(resPStatusTotal.getSUB_L_SUBTOTAL());
        row.getCell(16).setCellValue(resPStatusTotal.getSUB_S_SUBTOTAL());
        row.getCell(17).setCellValue(resPStatusTotal.getSUB_A_SUBTOTAL());

        ///////////////

        //부설 자주식 노면
        row = sheetStatus.getRow(15);
        row.getCell(15).setCellValue(resPStatusTotal.getSUBSE_SUR_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getSUBSE_SUR_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getSUBSE_SUR_A_SUM());

        //부설 자주식 조립식
        row = sheetStatus.getRow(16);
        row.getCell(15).setCellValue(resPStatusTotal.getSUBSE_MOD_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getSUBSE_MOD_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getSUBSE_MOD_A_SUM());

        //부설 기계식 부속
        row = sheetStatus.getRow(17);
        row.getCell(15).setCellValue(resPStatusTotal.getSUBAU_ATT_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getSUBAU_ATT_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getSUBAU_ATT_A_SUM());

        //부설 기계식 전용
        row = sheetStatus.getRow(18);
        row.getCell(15).setCellValue(resPStatusTotal.getSUBAU_PRV_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getSUBAU_PRV_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getSUBAU_PRV_A_SUM());

        //자가주차장 소계
        row = sheetStatus.getRow(19);
        row.getCell(15).setCellValue(resPStatusTotal.getOWN_L_SUBTOTAL());
        row.getCell(16).setCellValue(resPStatusTotal.getOWN_S_SUBTOTAL());
        row.getCell(17).setCellValue(resPStatusTotal.getOWN_A_SUBTOTAL());

        //자가주차장 단독
        row = sheetStatus.getRow(20);
        row.getCell(15).setCellValue(resPStatusTotal.getOWN_HOME_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getOWN_HOME_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getOWN_HOME_A_SUM());

        //자가주차장 공동
        row = sheetStatus.getRow(21);
        row.getCell(15).setCellValue(resPStatusTotal.getOWN_APT_L_SUM());
        row.getCell(16).setCellValue(resPStatusTotal.getOWN_APT_S_SUM());
        row.getCell(17).setCellValue(resPStatusTotal.getOWN_APT_A_SUM());

    }


    //전월 누계 db to excel
    private void pStatusPrevMonthDbToExcel(XSSFSheet sheetStatus, PStatusDto.Total resPStatusTotal){
        XSSFRow row;

        //총계
        row = sheetStatus.getRow(6);
        row.getCell(3).setCellValue(resPStatusTotal.getTOTAL_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getTOTAL_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getTOTAL_A_SUM());

        //공영소계
        row = sheetStatus.getRow(7);
        row.getCell(3).setCellValue(resPStatusTotal.getPBL_L_SUBTOTAL());
        row.getCell(4).setCellValue(resPStatusTotal.getPBL_S_SUBTOTAL());
        row.getCell(5).setCellValue(resPStatusTotal.getPBL_A_SUBTOTAL());

        //공영 노상 유료
        row = sheetStatus.getRow(8);
        row.getCell(3).setCellValue(resPStatusTotal.getPBLRD_PAY_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getPBLRD_PAY_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getPBLRD_PAY_A_SUM());

        //공영 노상 무료
        row = sheetStatus.getRow(9);
        row.getCell(3).setCellValue(resPStatusTotal.getPBLRD_FREE_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getPBLRD_FREE_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getPBLRD_FREE_A_SUM());

        //공영 거주자 무료
        row = sheetStatus.getRow(10);
        row.getCell(3).setCellValue(resPStatusTotal.getPBLRD_RESI_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getPBLRD_RESI_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getPBLRD_RESI_A_SUM());

        //공영 거주자 무료
        row = sheetStatus.getRow(11);
        row.getCell(3).setCellValue(resPStatusTotal.getPBLOUT_PAY_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getPBLOUT_PAY_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getPBLOUT_PAY_A_SUM());

        //공영 거주자 무료
        row = sheetStatus.getRow(12);
        row.getCell(3).setCellValue(resPStatusTotal.getPBLOUT_FREE_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getPBLOUT_FREE_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getPBLOUT_FREE_A_SUM());

        //민영
        row = sheetStatus.getRow(13);
        row.getCell(3).setCellValue(resPStatusTotal.getPRV_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getPRV_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getPRV_A_SUM());

        //부설소계
        row = sheetStatus.getRow(14);
        row.getCell(3).setCellValue(resPStatusTotal.getSUB_L_SUBTOTAL());
        row.getCell(4).setCellValue(resPStatusTotal.getSUB_S_SUBTOTAL());
        row.getCell(5).setCellValue(resPStatusTotal.getSUB_A_SUBTOTAL());

        ///////////////

        //부설 자주식 노면
        row = sheetStatus.getRow(15);
        row.getCell(3).setCellValue(resPStatusTotal.getSUBSE_SUR_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getSUBSE_SUR_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getSUBSE_SUR_A_SUM());

        //부설 자주식 조립식
        row = sheetStatus.getRow(16);
        row.getCell(3).setCellValue(resPStatusTotal.getSUBSE_MOD_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getSUBSE_MOD_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getSUBSE_MOD_A_SUM());

        //부설 기계식 부속
        row = sheetStatus.getRow(17);
        row.getCell(3).setCellValue(resPStatusTotal.getSUBAU_ATT_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getSUBAU_ATT_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getSUBAU_ATT_A_SUM());

        //부설 기계식 전용
        row = sheetStatus.getRow(18);
        row.getCell(3).setCellValue(resPStatusTotal.getSUBAU_PRV_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getSUBAU_PRV_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getSUBAU_PRV_A_SUM());

        //자가주차장 소계
        row = sheetStatus.getRow(19);
        row.getCell(3).setCellValue(resPStatusTotal.getOWN_L_SUBTOTAL());
        row.getCell(4).setCellValue(resPStatusTotal.getOWN_S_SUBTOTAL());
        row.getCell(5).setCellValue(resPStatusTotal.getOWN_A_SUBTOTAL());

        //자가주차장 단독
        row = sheetStatus.getRow(20);
        row.getCell(3).setCellValue(resPStatusTotal.getOWN_HOME_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getOWN_HOME_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getOWN_HOME_A_SUM());

        //자가주차장 공동
        row = sheetStatus.getRow(21);
        row.getCell(3).setCellValue(resPStatusTotal.getOWN_APT_L_SUM());
        row.getCell(4).setCellValue(resPStatusTotal.getOWN_APT_S_SUM());
        row.getCell(5).setCellValue(resPStatusTotal.getOWN_APT_A_SUM());

    }

    //금월 실적 db to excel
    private void pStatusDbToExcel(XSSFSheet sheetStatus, PStatusDto resPStatus){
        XSSFRow row;
        //공영 소계 개소수
        double pblLISum = resPStatus.getPBLRD_PAY_L_I() +
                             resPStatus.getPBLRD_FREE_L_I() +
                             resPStatus.getPBLRD_RESI_L_I() +
                             resPStatus.getPBLOUT_PAY_L_I() +
                             resPStatus.getPBLOUT_FREE_L_I();
        double pblLDSum = resPStatus.getPBLRD_PAY_L_D() +
                             resPStatus.getPBLRD_FREE_L_D() +
                             resPStatus.getPBLRD_RESI_L_D() +
                             resPStatus.getPBLOUT_PAY_L_D() +
                             resPStatus.getPBLOUT_FREE_L_D();
        double pblLSubtotal = pblLISum - pblLDSum;

        //공영 소계 주차대수
        double pblSISum = resPStatus.getPBLRD_PAY_S_I() +
                          resPStatus.getPBLRD_FREE_S_I() +
                          resPStatus.getPBLRD_RESI_S_I() +
                          resPStatus.getPBLOUT_PAY_S_I() +
                          resPStatus.getPBLOUT_FREE_S_I();
        double pblSDSum = resPStatus.getPBLRD_PAY_S_D() +
                          resPStatus.getPBLRD_FREE_S_D() +
                          resPStatus.getPBLRD_RESI_S_D() +
                          resPStatus.getPBLOUT_PAY_S_D() +
                          resPStatus.getPBLOUT_FREE_S_D();
        double pblSSubtotal = pblSISum - pblSDSum;

        //공영 소계 면적
        double pblAISum = resPStatus.getPBLRD_PAY_A_I() +
                          resPStatus.getPBLRD_FREE_A_I() +
                          resPStatus.getPBLRD_RESI_A_I() +
                          resPStatus.getPBLOUT_PAY_A_I() +
                          resPStatus.getPBLOUT_FREE_A_I();
        double pblADSum = resPStatus.getPBLRD_PAY_A_D() +
                          resPStatus.getPBLRD_FREE_A_D() +
                          resPStatus.getPBLRD_RESI_A_D() +
                          resPStatus.getPBLOUT_PAY_A_D() +
                          resPStatus.getPBLOUT_FREE_A_D();
        double pblASubtotal = pblAISum - pblADSum;

        //부설 소계 개소수
        double subLISum = resPStatus.getSUBSE_SUR_L_I() +
                          resPStatus.getSUBSE_MOD_L_I() +
                          resPStatus.getSUBAU_ATT_L_I() +
                          resPStatus.getSUBAU_PRV_L_I();
        double subLDSum = resPStatus.getSUBSE_SUR_L_D() +
                          resPStatus.getSUBSE_MOD_L_D() +
                          resPStatus.getSUBAU_ATT_L_D() +
                          resPStatus.getSUBAU_PRV_L_D();
        double subLSubtotal = subLISum - subLDSum;

        //부설 소계 주차대수
        double subSISum = resPStatus.getSUBSE_SUR_S_I() +
                          resPStatus.getSUBSE_MOD_S_I() +
                          resPStatus.getSUBAU_ATT_S_I() +
                          resPStatus.getSUBAU_PRV_S_I();
        double subSDSum = resPStatus.getSUBSE_SUR_S_D() +
                          resPStatus.getSUBSE_MOD_S_D() +
                          resPStatus.getSUBAU_ATT_S_D() +
                          resPStatus.getSUBAU_PRV_S_D();
        double subSSubtotal = subSISum - subSDSum;

        //부설 소계 면적
        double subAISum = resPStatus.getSUBSE_SUR_A_I() +
                          resPStatus.getSUBSE_MOD_A_I() +
                          resPStatus.getSUBAU_ATT_A_I() +
                          resPStatus.getSUBAU_PRV_A_I();
        double subADSum = resPStatus.getSUBSE_SUR_A_D() +
                          resPStatus.getSUBSE_MOD_A_D() +
                          resPStatus.getSUBAU_ATT_A_D() +
                          resPStatus.getSUBAU_PRV_A_D();
        double subASubtotal = subAISum - subADSum;

        //자가 소계 개소수
        double ownLISum = resPStatus.getOWN_HOME_L_I() +
                          resPStatus.getOWN_APT_L_I();
        double ownLDSum = resPStatus.getOWN_HOME_L_D() +
                          resPStatus.getOWN_APT_L_D();
        double ownLSubtotal = ownLISum - ownLDSum;

        //자가 소계 주차대수
        double ownSISum = resPStatus.getOWN_HOME_S_I() +
                          resPStatus.getOWN_APT_S_I();
        double ownSDSum = resPStatus.getOWN_HOME_S_D() +
                          resPStatus.getOWN_APT_S_D();
        double ownSSubtotal = ownSISum - ownSDSum;

        //자가 소계 면적
        double ownAISum = resPStatus.getOWN_HOME_A_I() +
                resPStatus.getOWN_APT_A_I();
        double ownADSum = resPStatus.getOWN_HOME_A_D() +
                resPStatus.getOWN_APT_A_D();
        double ownASubtotal = ownAISum - ownADSum;

        //총계 개소수
        double totalLISum = pblLISum + subLISum + ownLISum +
                            resPStatus.getPRV_L_I();
        double totalLDSum = pblLDSum + subLDSum + ownLDSum +
                resPStatus.getPRV_L_D();
        double totalLSubtotal = totalLISum - totalLDSum;

        //총계 주차대수
        double totalSISum = pblSISum + subSISum + ownSISum +
                resPStatus.getPRV_S_I();
        double totalSDSum = pblSDSum + subSDSum + ownSDSum +
                resPStatus.getPRV_S_D();
        double totalSSubtotal = totalSISum - totalSDSum;

        //총계 면적
        double totalAISum = pblAISum + subAISum + ownAISum +
                resPStatus.getPRV_A_I();
        double totalADSum = pblADSum + subADSum + ownADSum +
                resPStatus.getPRV_A_D();
        double totalASubtotal = totalAISum - totalADSum;

        //총계
        row = sheetStatus.getRow(6);
        row.getCell(6).setCellValue(totalLSubtotal);
        row.getCell(7).setCellValue(totalLISum);
        row.getCell(8).setCellValue(totalLDSum);
        row.getCell(9).setCellValue(totalSSubtotal);
        row.getCell(10).setCellValue(totalSISum);
        row.getCell(11).setCellValue(totalSDSum);
        row.getCell(12).setCellValue(totalASubtotal);
        row.getCell(13).setCellValue(totalAISum);
        row.getCell(14).setCellValue(totalADSum);

        //공영소계
        row = sheetStatus.getRow(7);
        row.getCell(6).setCellValue(pblLSubtotal);
        row.getCell(7).setCellValue(pblLISum);
        row.getCell(8).setCellValue(pblLDSum);
        row.getCell(9).setCellValue(pblSSubtotal);
        row.getCell(10).setCellValue(pblSISum);
        row.getCell(11).setCellValue(pblSDSum);
        row.getCell(12).setCellValue(pblASubtotal);
        row.getCell(13).setCellValue(pblAISum);
        row.getCell(14).setCellValue(pblADSum);

        //공영 노상 유료
        row = sheetStatus.getRow(8);
        row.getCell(6).setCellValue(resPStatus.getPBLRD_PAY_L_I()-resPStatus.getPBLRD_PAY_L_D());
        row.getCell(7).setCellValue(resPStatus.getPBLRD_PAY_L_I());
        row.getCell(8).setCellValue(resPStatus.getPBLRD_PAY_L_D());
        row.getCell(9).setCellValue(resPStatus.getPBLRD_PAY_S_I()-resPStatus.getPBLRD_PAY_S_D());
        row.getCell(10).setCellValue(resPStatus.getPBLRD_PAY_S_I());
        row.getCell(11).setCellValue(resPStatus.getPBLRD_PAY_S_D());
        row.getCell(12).setCellValue(resPStatus.getPBLRD_PAY_A_I()-resPStatus.getPBLRD_PAY_A_D());
        row.getCell(13).setCellValue(resPStatus.getPBLRD_PAY_A_I());
        row.getCell(14).setCellValue(resPStatus.getPBLRD_PAY_A_D());

        //공영 노상 무료
        row = sheetStatus.getRow(9);
        row.getCell(6).setCellValue(resPStatus.getPBLRD_FREE_L_I()-resPStatus.getPBLRD_FREE_L_D());
        row.getCell(7).setCellValue(resPStatus.getPBLRD_FREE_L_I());
        row.getCell(8).setCellValue(resPStatus.getPBLRD_FREE_L_D());
        row.getCell(9).setCellValue(resPStatus.getPBLRD_FREE_S_I()-resPStatus.getPBLRD_FREE_S_D());
        row.getCell(10).setCellValue(resPStatus.getPBLRD_FREE_S_I());
        row.getCell(11).setCellValue(resPStatus.getPBLRD_FREE_S_D());
        row.getCell(12).setCellValue(resPStatus.getPBLRD_FREE_A_I()-resPStatus.getPBLRD_FREE_A_D());
        row.getCell(13).setCellValue(resPStatus.getPBLRD_FREE_A_I());
        row.getCell(14).setCellValue(resPStatus.getPBLRD_FREE_A_D());

        //공영 거주자 무료
        row = sheetStatus.getRow(10);
        row.getCell(6).setCellValue(resPStatus.getPBLRD_RESI_L_I()-resPStatus.getPBLRD_RESI_L_D());
        row.getCell(7).setCellValue(resPStatus.getPBLRD_RESI_L_I());
        row.getCell(8).setCellValue(resPStatus.getPBLRD_RESI_L_D());
        row.getCell(9).setCellValue(resPStatus.getPBLRD_RESI_S_I()-resPStatus.getPBLRD_RESI_S_D());
        row.getCell(10).setCellValue(resPStatus.getPBLRD_RESI_S_I());
        row.getCell(11).setCellValue(resPStatus.getPBLRD_RESI_S_D());
        row.getCell(12).setCellValue(resPStatus.getPBLRD_RESI_A_I()-resPStatus.getPBLRD_RESI_A_D());
        row.getCell(13).setCellValue(resPStatus.getPBLRD_RESI_A_I());
        row.getCell(14).setCellValue(resPStatus.getPBLRD_RESI_A_D());

        //공영 거주자 무료
        row = sheetStatus.getRow(11);
        row.getCell(6).setCellValue(resPStatus.getPBLOUT_PAY_L_I()-resPStatus.getPBLOUT_PAY_L_D());
        row.getCell(7).setCellValue(resPStatus.getPBLOUT_PAY_L_I());
        row.getCell(8).setCellValue(resPStatus.getPBLOUT_PAY_L_D());
        row.getCell(9).setCellValue(resPStatus.getPBLOUT_PAY_S_I()-resPStatus.getPBLOUT_PAY_S_D());
        row.getCell(10).setCellValue(resPStatus.getPBLOUT_PAY_S_I());
        row.getCell(11).setCellValue(resPStatus.getPBLOUT_PAY_S_D());
        row.getCell(12).setCellValue(resPStatus.getPBLOUT_PAY_A_I()-resPStatus.getPBLOUT_PAY_A_D());
        row.getCell(13).setCellValue(resPStatus.getPBLOUT_PAY_A_I());
        row.getCell(14).setCellValue(resPStatus.getPBLOUT_PAY_A_D());

        //공영 거주자 무료
        row = sheetStatus.getRow(12);
        row.getCell(6).setCellValue(resPStatus.getPBLOUT_FREE_L_I()-resPStatus.getPBLOUT_FREE_L_D());
        row.getCell(7).setCellValue(resPStatus.getPBLOUT_FREE_L_I());
        row.getCell(8).setCellValue(resPStatus.getPBLOUT_FREE_L_D());
        row.getCell(9).setCellValue(resPStatus.getPBLOUT_FREE_S_I()-resPStatus.getPBLOUT_FREE_S_D());
        row.getCell(10).setCellValue(resPStatus.getPBLOUT_FREE_S_I());
        row.getCell(11).setCellValue(resPStatus.getPBLOUT_FREE_S_D());
        row.getCell(12).setCellValue(resPStatus.getPBLOUT_FREE_A_I()-resPStatus.getPBLOUT_FREE_A_D());
        row.getCell(13).setCellValue(resPStatus.getPBLOUT_FREE_A_I());
        row.getCell(14).setCellValue(resPStatus.getPBLOUT_FREE_A_D());

        //민영
        row = sheetStatus.getRow(13);
        row.getCell(6).setCellValue(resPStatus.getPRV_L_I()-resPStatus.getPRV_L_D());
        row.getCell(7).setCellValue(resPStatus.getPRV_L_I());
        row.getCell(8).setCellValue(resPStatus.getPRV_L_D());
        row.getCell(9).setCellValue(resPStatus.getPRV_S_I()-resPStatus.getPRV_S_D());
        row.getCell(10).setCellValue(resPStatus.getPRV_S_I());
        row.getCell(11).setCellValue(resPStatus.getPRV_S_D());
        row.getCell(12).setCellValue(resPStatus.getPRV_A_I()-resPStatus.getPRV_A_D());
        row.getCell(13).setCellValue(resPStatus.getPRV_A_I());
        row.getCell(14).setCellValue(resPStatus.getPRV_A_D());

        //부설소계
        row = sheetStatus.getRow(14);
        row.getCell(6).setCellValue(subLSubtotal);
        row.getCell(7).setCellValue(subLISum);
        row.getCell(8).setCellValue(subLDSum);
        row.getCell(9).setCellValue(subSSubtotal);
        row.getCell(10).setCellValue(subSISum);
        row.getCell(11).setCellValue(subSDSum);
        row.getCell(12).setCellValue(subASubtotal);
        row.getCell(13).setCellValue(subAISum);
        row.getCell(14).setCellValue(subADSum);

        ///////////////

        //부설 자주식 노면
        row = sheetStatus.getRow(15);
        row.getCell(6).setCellValue(resPStatus.getSUBSE_SUR_L_I()-resPStatus.getSUBSE_SUR_L_D());
        row.getCell(7).setCellValue(resPStatus.getSUBSE_SUR_L_I());
        row.getCell(8).setCellValue(resPStatus.getSUBSE_SUR_L_D());
        row.getCell(9).setCellValue(resPStatus.getSUBSE_SUR_S_I()-resPStatus.getSUBSE_SUR_S_D());
        row.getCell(10).setCellValue(resPStatus.getSUBSE_SUR_S_I());
        row.getCell(11).setCellValue(resPStatus.getSUBSE_SUR_S_D());
        row.getCell(12).setCellValue(resPStatus.getSUBSE_SUR_A_I()-resPStatus.getSUBSE_SUR_A_D());
        row.getCell(13).setCellValue(resPStatus.getSUBSE_SUR_A_I());
        row.getCell(14).setCellValue(resPStatus.getSUBSE_SUR_A_D());

        //부설 자주식 조립식
        row = sheetStatus.getRow(16);
        row.getCell(6).setCellValue(resPStatus.getSUBSE_MOD_L_I()-resPStatus.getSUBSE_MOD_L_D());
        row.getCell(7).setCellValue(resPStatus.getSUBSE_MOD_L_I());
        row.getCell(8).setCellValue(resPStatus.getSUBSE_MOD_L_D());
        row.getCell(9).setCellValue(resPStatus.getSUBSE_MOD_S_I()-resPStatus.getSUBSE_MOD_S_D());
        row.getCell(10).setCellValue(resPStatus.getSUBSE_MOD_S_I());
        row.getCell(11).setCellValue(resPStatus.getSUBSE_MOD_S_D());
        row.getCell(12).setCellValue(resPStatus.getSUBSE_MOD_A_I()-resPStatus.getSUBSE_MOD_A_D());
        row.getCell(13).setCellValue(resPStatus.getSUBSE_MOD_A_I());
        row.getCell(14).setCellValue(resPStatus.getSUBSE_MOD_A_D());


        //부설 기계식 부속
        row = sheetStatus.getRow(17);
        row.getCell(6).setCellValue(resPStatus.getSUBAU_ATT_L_I()-resPStatus.getSUBAU_ATT_L_D());
        row.getCell(7).setCellValue(resPStatus.getSUBAU_ATT_L_I());
        row.getCell(8).setCellValue(resPStatus.getSUBAU_ATT_L_D());
        row.getCell(9).setCellValue(resPStatus.getSUBAU_ATT_S_I()-resPStatus.getSUBAU_ATT_S_D());
        row.getCell(10).setCellValue(resPStatus.getSUBAU_ATT_S_I());
        row.getCell(11).setCellValue(resPStatus.getSUBAU_ATT_S_D());
        row.getCell(12).setCellValue(resPStatus.getSUBAU_ATT_A_I()-resPStatus.getSUBAU_ATT_A_D());
        row.getCell(13).setCellValue(resPStatus.getSUBAU_ATT_A_I());
        row.getCell(14).setCellValue(resPStatus.getSUBAU_ATT_A_D());

        //부설 기계식 전용
        row = sheetStatus.getRow(18);
        row.getCell(6).setCellValue(resPStatus.getSUBAU_PRV_L_I()-resPStatus.getSUBAU_PRV_L_D());
        row.getCell(7).setCellValue(resPStatus.getSUBAU_PRV_L_I());
        row.getCell(8).setCellValue(resPStatus.getSUBAU_PRV_L_D());
        row.getCell(9).setCellValue(resPStatus.getSUBAU_PRV_S_I()-resPStatus.getSUBAU_PRV_S_D());
        row.getCell(10).setCellValue(resPStatus.getSUBAU_PRV_S_I());
        row.getCell(11).setCellValue(resPStatus.getSUBAU_PRV_S_D());
        row.getCell(12).setCellValue(resPStatus.getSUBAU_PRV_A_I()-resPStatus.getSUBAU_PRV_A_D());
        row.getCell(13).setCellValue(resPStatus.getSUBAU_PRV_A_I());
        row.getCell(14).setCellValue(resPStatus.getSUBAU_PRV_A_D());

        //자가주차장 소계
        row = sheetStatus.getRow(19);
        row.getCell(6).setCellValue(ownLSubtotal);
        row.getCell(7).setCellValue(ownLISum);
        row.getCell(8).setCellValue(ownLDSum);
        row.getCell(9).setCellValue(ownSSubtotal);
        row.getCell(10).setCellValue(ownSISum);
        row.getCell(11).setCellValue(ownSDSum);
        row.getCell(12).setCellValue(ownASubtotal);
        row.getCell(13).setCellValue(ownAISum);
        row.getCell(14).setCellValue(ownADSum);

        //자가주차장 단독
        row = sheetStatus.getRow(20);
        row.getCell(6).setCellValue(resPStatus.getOWN_HOME_L_I()-resPStatus.getOWN_HOME_L_D());
        row.getCell(7).setCellValue(resPStatus.getOWN_HOME_L_I());
        row.getCell(8).setCellValue(resPStatus.getOWN_HOME_L_D());
        row.getCell(9).setCellValue(resPStatus.getOWN_HOME_S_I()-resPStatus.getOWN_HOME_S_D());
        row.getCell(10).setCellValue(resPStatus.getOWN_HOME_S_I());
        row.getCell(11).setCellValue(resPStatus.getOWN_HOME_S_D());
        row.getCell(12).setCellValue(resPStatus.getOWN_HOME_A_I()-resPStatus.getOWN_HOME_A_D());
        row.getCell(13).setCellValue(resPStatus.getOWN_HOME_A_I());
        row.getCell(14).setCellValue(resPStatus.getOWN_HOME_A_D());

        //자가주차장 공동
        row = sheetStatus.getRow(21);
        row.getCell(6).setCellValue(resPStatus.getOWN_APT_L_I()-resPStatus.getOWN_APT_L_D());
        row.getCell(7).setCellValue(resPStatus.getOWN_APT_L_I());
        row.getCell(8).setCellValue(resPStatus.getOWN_APT_L_D());
        row.getCell(9).setCellValue(resPStatus.getOWN_APT_S_I()-resPStatus.getOWN_APT_S_D());
        row.getCell(10).setCellValue(resPStatus.getOWN_APT_S_I());
        row.getCell(11).setCellValue(resPStatus.getOWN_APT_S_D());
        row.getCell(12).setCellValue(resPStatus.getOWN_APT_A_I()-resPStatus.getOWN_APT_A_D());
        row.getCell(13).setCellValue(resPStatus.getOWN_APT_A_I());
        row.getCell(14).setCellValue(resPStatus.getOWN_APT_A_D());

    }

    String alterSggCdToSggName(String code) {
        String sggNm = "";
        if (code.equals("31110")) {
            sggNm = "중구";
        } else if (code.equals("31140")) {
            sggNm = "남구";
        } else if (code.equals("31170")) {
            sggNm = "동구";
        } else if (code.equals("31200")) {
            sggNm = "북구";
        } else if (code.equals("31710")) {
            sggNm = "울주군";
        }
        return sggNm;
    }
    Long alterNullToZero(Long inputValue){
        Long result= Long.valueOf(0);
        if (inputValue!=null){
            result = inputValue;
        }
        return result;
    }
}
