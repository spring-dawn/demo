package com.example.demo.service.data.illegal;

import com.example.demo.atech.ExcelManager;
import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.config.mapStruct.MyMapper;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.data.illegal.*;
import com.example.demo.domain.data.illegal.file.IllData;
import com.example.demo.domain.data.illegal.file.IllDataRepository;
import com.example.demo.domain.data.illegal.repCustom.IllFixedRepoCustom;
import com.example.demo.domain.data.illegal.repo.IllCrdnNocsRepository;
import com.example.demo.domain.data.illegal.repo.IllCrdnPrfmncRepository;
import com.example.demo.domain.data.illegal.repo.IllFixedRepository;
import com.example.demo.domain.data.illegal.repo.IllMobileRepository;
import com.example.demo.domain.system.user.User;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.dto.data.illegal.IllFixedDto;
import com.example.demo.dto.data.illegal.IllMobileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.time.LocalDate;
import java.util.*;

import static com.example.demo.atech.ExcelManager.*;
import static com.example.demo.atech.Msg.NO_FILES;
import static com.example.demo.atech.MyUtil.*;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IllegalTmpService {
    /*
    [240401] 차세대 세외 행정시스템 연계가 5월 이후까지 밀리면서 임시 구현합니다.
     */
    @Value("${spring.servlet.multipart2.standardExcel.download.illTmp}")
    private String illTmpPath;

    private final MyMapper mapper;

    private final UserRepository userRepo;
    private final IllDataRepository dataRepo;

    private final IllFixedRepository fixedRepo;
    private final IllMobileRepository mobileRepo;
    private final IllCrdnNocsRepository crdnNocsRepo;
    private final IllCrdnPrfmncRepository crdnPrfmncRepo;
    private final IllFixedRepoCustom query;

    // 자주 쓰는 문자열
    private final static String SHEET_SUMMARY = "총괄";
    private final static String SHEET_FIXED = "고정형CCTV";
    private final static String SHEET_MOBILE = "이동식CCTV";
//    private final static String SHEET_BUS_MOUNTED = "버스탑재형";

    private final static String GUBUN_FIXED = "고정식";
    private final static String GUBUN_MOB = "이동식";
    private final static String GUBUN_CRDN = "인력단속";
    private final static String GUBUN_BUS = "버스탑재형";
    private final static String GUBUN_SINMUNGO = "안전신문고";
    private final static String GUBUN_NOCS_CRDN = "단속건수";
    private final static String GUBUN_NOCS_TRACTION = "견인건수";


    // 임시 데이터 수집. 2023년 11월의 불법주정차 단속실적 총괄본 파일을 분해해서 추출합니다.
    public void insert(IllData origin) {
        log.info("임시 불법주정차 단속실적 추출 시작");
//        1) 파일 내용 검사
        FileInfo fi = origin.getAttaches().get(0);
        File file = new File(fi.getFilePath() + fi.getFileNmStored());
        if (!file.exists()) throw new NullPointerException(NO_FILES.getMsg());

//        2) 총괄, 고정형, 이동식 시트 데이터 추출(나머지 제외)
        try (XSSFWorkbook wb = readExcelFile(file)) {
            readSheet0(wb.getSheetAt(0), origin);
            readSheet1(wb.getSheetAt(1), origin);
            readSheet2(wb.getSheetAt(2), origin);
        } catch (IOException | NullPointerException e) {
            logErr(e);
            throw new IllegalArgumentException(Msg.OUT_DOMAIN.getMsg());
        }

        origin.updateCollectYn("Y");
        log.info("임시 불법주정차 단속실적 데이터 추출 완료");
    }

    public Map<String, Object> tmpExcelDownload4Manager() {
//        1) 구군 담당자용 엑셀이니 사용자 정보 확인
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.MANAGER_SESSION_EXPIRED.getMsg()));

//        2) 유효검사
        String agency = user.getAgency();
        if (agency.equals("31000") || !user.getRole().getEncodedNm().contains("담당자"))
            throw new IllegalArgumentException(Msg.ONLY_MANAGER.getMsg());

//        3) 각 담당자의 구군에 알맞는 양식 파일 선택
        Map<String, Object> fileData = new HashMap<>();
        String sggNm = getSggCd2Nm(agency);
        File baseDir = new File(illTmpPath);
        if (!baseDir.exists()) throw new IllegalArgumentException(Msg.COMMON.getMsg());
        File file = Objects.requireNonNull(baseDir.listFiles((dir, name) -> name.startsWith(sggNm)))[0];

        // 2024년인 경우 참조 셀 변경해야 함. wb 접근.
        try (XSSFWorkbook wb = readExcelFile(file)) {
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row = sheet.getRow(4);
            row.getCell(1).setCellFormula("고정형CCTV!N5");
            row.getCell(6).setCellFormula("이동식CCTV!M4");

            wb.write(Files.newOutputStream(file.toPath()));
            FileSystemResource resource = new FileSystemResource(illTmpPath + file.getName());

            String thisMonth = LocalDate.now().getYear() + String.format("%02d", LocalDate.now().getMonthValue());
            String fileNm = sggNm + "_불법주정차 단속실적_" + thisMonth + format;

            fileData.put("file", resource);
            fileData.put("contentDisposition", "attachment; filename=" + URLEncoder.encode(fileNm, "UTF-8").replaceAll("\\+", "%20"));
        } catch (IOException e) {
            logErr(e);
        }

        return fileData;
    }




    // -----------------------------------------------(매핑용)
    private void mappingFixed(IllFixedDto.Req req, ExcelManager.CellInfo info) {
        String data = info.cellData;

        switch (info.getColIdx()) {
            case 0:
                req.setSeq(data);
                break;
            case 1:
                req.setCrdnBrnch(data);
                break;
            case 2:
                if (!hasText(data)) {
                    req.setInstlYmd(0);
                } else {
                    data = data.replaceAll("[^\\d]", "");
                    req.setInstlYmd(hasInteger(data));
                }
                break;
            case 3:
                req.setCrdnPrd(data);
                break;
            case 4:
                req.setCrdnCtrM(data);
                break;
            case 5:
                req.setLat(data);
                break;
            case 6:
                req.setLon(data);
                break;
            case 12:
                req.setCrdnNocs(hasText(data) ? hasInteger(data) : 0);
                break;
            default:
                break;
        }
    }

    private void mappingMobile(IllMobileDto.Req req, ExcelManager.CellInfo info) {
        String data = info.cellData;

        switch (info.getColIdx()) {
            case 0:
                req.setSeq(data);
                break;
//            case 1:
//                구군 정보. 원본 파일 따라가므로 패스.
//                break;
            case 2:
                req.setVhclNm(data);
                break;
            case 3:
                if(!hasText(data)) data = "불명";
                req.setPrchsYmd(data);
                break;
            case 4:
                req.setCrdnPrd(data);
                break;
            case 5:
                req.setCrdnCtrM(data);
                break;
            case 11:
                req.setCrdnNocs(hasText(data) ? hasInteger(data) : 0);
                break;
            case 12:
                req.setCrdnNocs(hasText(data) ? hasInteger(data) : 0);
                break;
            default:
                break;
        }
    }


    //    ------------------------------(시트별 데이터 추출, 적재 로직)
    public void readSheet0(XSSFSheet sheet, IllData origin) {
        // 단속실적(인력단속, 안전신문고앱) + 차종별 단속건수/견인건수 추출
        if (!sheet.getSheetName().equals(SHEET_SUMMARY)) throw new IllegalArgumentException(Msg.OUT_DOMAIN.getMsg());

        // 단속실적(윗줄), 단속건수(아랫줄)
        List<IllCrdnPrfmnc> list1 = new ArrayList<>();
        List<IllCrdnNocs> list2 = new ArrayList<>();
        XSSFRow row;

        // 단속실적, 단속건수 행 위치 지정
        int startRowNo1 = 4;
        int startRowNo2 = 9;

        row = sheet.getRow(startRowNo1);
        // 고정식
        list1.add(
                IllCrdnPrfmnc.builder()
                        .year(origin.getYear())
                        .month(origin.getMonth())
                        .sgg(origin.getSggCd())
                        .gubun(GUBUN_FIXED)
                        // 단속건수, 부과액, 징수건수, 징수액, (징수율 계산)
                        .crdnNocs(getInteger(row.getCell(1)))
                        .levyAmt(getInteger(row.getCell(2)))
                        .clctnNocs(getInteger(row.getCell(3)))
                        .clctnAmt(getInteger(row.getCell(4)))
                        .clctnRate(getClctnRate(getInteger(row.getCell(2)), getInteger(row.getCell(4))))
                        .build()
        );
        // 이동식
        list1.add(
                IllCrdnPrfmnc.builder()
                        .year(origin.getYear())
                        .month(origin.getMonth())
                        .sgg(origin.getSggCd())
                        .gubun(GUBUN_MOB)
                        // 단속건수, 부과액, 징수건수, 징수액, (징수율 계산)
                        .crdnNocs(getInteger(row.getCell(6)))
                        .levyAmt(getInteger(row.getCell(7)))
                        .clctnNocs(getInteger(row.getCell(8)))
                        .clctnAmt(getInteger(row.getCell(9)))
                        .clctnRate(getClctnRate(getInteger(row.getCell(7)), getInteger(row.getCell(9))))
                        .build()
        );
        // 인력단속 세팅
        list1.add(
                IllCrdnPrfmnc.builder()
                        .year(origin.getYear())
                        .month(origin.getMonth())
                        .sgg(origin.getSggCd())
                        .gubun(GUBUN_CRDN)
                        // 단속건수, 부과액, 징수건수, 징수액, (징수율 계산), 단속인원
                        .crdnNocs(getInteger(row.getCell(11)))
                        .levyAmt(getInteger(row.getCell(12)))
                        .clctnNocs(getInteger(row.getCell(13)))
                        .clctnAmt(getInteger(row.getCell(14)))
                        .clctnRate(getClctnRate(getInteger(row.getCell(12)), getInteger(row.getCell(14))))
                        .crdnNope(getInteger(row.getCell(16)))
                        .build()
        );
        // 버스탑재형
        list1.add(
                IllCrdnPrfmnc.builder()
                        .year(origin.getYear())
                        .month(origin.getMonth())
                        .sgg(origin.getSggCd())
                        .gubun(GUBUN_BUS)
                        // 단속건수, 부과액, 징수건수, 징수액, (징수율 계산)
                        .crdnNocs(getInteger(row.getCell(17)))
                        .levyAmt(getInteger(row.getCell(18)))
                        .clctnNocs(getInteger(row.getCell(19)))
                        .clctnAmt(getInteger(row.getCell(20)))
                        .clctnRate(getClctnRate(getInteger(row.getCell(18)), getInteger(row.getCell(20))))
                        .build()
        );
        // 안전신문고앱
        list1.add(
                IllCrdnPrfmnc.builder()
                        .year(origin.getYear())
                        .month(origin.getMonth())
                        .sgg(origin.getSggCd())
                        .gubun(GUBUN_SINMUNGO)
                        // 단속건수, 부과액, 징수건수, 징수액, (징수율 계산)
                        .crdnNocs(getInteger(row.getCell(22)))
                        .levyAmt(getInteger(row.getCell(23)))
                        .clctnNocs(getInteger(row.getCell(24)))
                        .clctnAmt(getInteger(row.getCell(25)))
                        .clctnRate(getClctnRate(getInteger(row.getCell(23)), getInteger(row.getCell(25))))
                        .build()
        );

        // 단속건수 행으로 내려가기
        row = sheet.getRow(startRowNo2);
        // 단속건수
        list2.add(
                IllCrdnNocs.builder()
                        .year(origin.getYear())
                        .month(origin.getMonth())
                        .sgg(origin.getSggCd())
                        .gubun(GUBUN_NOCS_CRDN)
                        .crdnCar(getInteger(row.getCell(2)))
                        .crdnVan(getInteger(row.getCell(3)))
                        .crdnTruck(getInteger(row.getCell(4)))
                        .crdnEtc(getInteger(row.getCell(5)))
                        .sum(
                                getInteger(row.getCell(2))
                                        + getInteger(row.getCell(3))
                                        + getInteger(row.getCell(4))
                                        + getInteger(row.getCell(5))
                        )
//                        .amt(0) // 컬럼이 없음
                        .build()
        );
        // 견인건수
        list2.add(
                IllCrdnNocs.builder()
                        .year(origin.getYear())
                        .month(origin.getMonth())
                        .sgg(origin.getSggCd())
                        .gubun(GUBUN_NOCS_TRACTION)
                        .crdnCar(getInteger(row.getCell(7)))
                        .crdnVan(getInteger(row.getCell(8)))
                        .crdnTruck(getInteger(row.getCell(9)))
                        .amt(getInteger(row.getCell(11))) //
                        .sum(
                                getInteger(row.getCell(7))
                                        + getInteger(row.getCell(8))
                                        + getInteger(row.getCell(9))
                        )
//                        .crdnEtc()
                        .build()
        );

        // 적재
        List<IllCrdnPrfmnc> saved1 = crdnPrfmncRepo.saveAll(list1);
        List<IllCrdnNocs> saved2 = crdnNocsRepo.saveAll(list2);
        if (saved1.size() != list1.size() || saved2.size() != list2.size())
            throw new DataIntegrityViolationException(getEnum(Msg.MISSING, SHEET_SUMMARY));
    }

    public void readSheet1(XSSFSheet sheet, IllData origin) {
        if (!sheet.getSheetName().equals(SHEET_FIXED)) throw new IllegalArgumentException(Msg.OUT_DOMAIN.getMsg());
        List<IllFixed> list = new ArrayList<>();

        // 1시트: 고정형, 2시트: 이동형
        XSSFRow row;
        // 시트별 마지막열
        int endIdx = 12;
        if (origin.getYear().equals("2024")) endIdx += 1;

        // 합계행 건너뛰고 6행부터 시작(실제 인덱스 5)
        for (int i = 5; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            if (row == null || !hasCell(row.getCell(0))) continue;
            // dto 생성, 디폴트값 원본 파일 따라 세팅
            IllFixedDto.Req req = new IllFixedDto.Req();
            req.setYear(origin.getYear());
            req.setMonth(origin.getMonth());
            req.setSgg(origin.getSggCd());
            //
            for (Cell cell : row) {
                // 단속기준(분)~2023년 데이터 사이 셀은 수집하지 않으므로 건너뛰기
                if (cell.getColumnIndex() > 6 && cell.getColumnIndex() < endIdx) continue;

                String cellData = getCellData(cell, "0", false);
                mappingFixed(req, new CellInfo(cellData, cell.getColumnIndex()));
                if (cell.getColumnIndex() == endIdx) break;
            }
            list.add(mapper.toIllFixed(req));
        }

        // 적재
        List<IllFixed> saved = fixedRepo.saveAll(list);
        if (list.size() != saved.size()) throw new DataIntegrityViolationException(getEnum(Msg.MISSING, SHEET_FIXED));
    }

    public void readSheet2(XSSFSheet sheet, IllData origin) {
        if (!sheet.getSheetName().equals(SHEET_MOBILE)) throw new IllegalArgumentException(Msg.OUT_DOMAIN.getMsg());

        List<IllMobile> list = new ArrayList<>();
        XSSFRow row;
        int endIdx = 11;
        if (origin.getYear().equals("2024")) endIdx += 1;

        // 소계행 건너뛰고 5행부터 시작
        for (int i = 4; i < sheet.getLastRowNum() + 1; i++) {
            row = sheet.getRow(i);
            if(row == null || !hasCell(row.getCell(0))) continue;

            IllMobileDto.Req req = new IllMobileDto.Req();
            req.setYear(origin.getYear());
            req.setMonth(origin.getMonth());
            req.setSgg(origin.getSggCd());

            for (Cell cell : row) {
                // 단속기준(분)~2023년 데이터 사이 셀은 수집하지 않으므로 건너뛰기
                if (cell.getColumnIndex() > 5 && cell.getColumnIndex() < endIdx) continue;

                // 병합셀 판별
                CellRangeAddress range = findMergedRegion(sheet, cell);
                if (range != null) cell = sheet.getRow(range.getFirstRow()).getCell(range.getFirstColumn());

                String cellData = getCellData(cell, "0", false);
                mappingMobile(req, new CellInfo(cellData, cell.getColumnIndex()));
                if (cell.getColumnIndex() == endIdx) break;
            }
            list.add(mapper.toIllMobile(req));
        }

        // 적재
        List<IllMobile> saved = mobileRepo.saveAll(list);
        if (list.size() != saved.size()) throw new DataIntegrityViolationException(getEnum(Msg.MISSING, SHEET_MOBILE));
    }


    // 편의상 임시 Integer 변환
    private Integer getInteger(Cell cell) {
        return hasInteger(getCellData(cell, "0", false));
    }


    /**
     * 징수율 계산
     *
     * @param clctnAmt 징수금액
     * @param levyAmt  부과금액
     * @return 소수 다섯째자리 반올림
     */
    private Double getClctnRate(Integer levyAmt, Integer clctnAmt) {
        if (levyAmt == 0 || clctnAmt == 0) return 0.0;
        BigDecimal clctn = new BigDecimal(clctnAmt);
        BigDecimal levy = new BigDecimal(levyAmt);

        return clctn.divide(levy, 5, RoundingMode.HALF_UP).doubleValue();
    }


}
