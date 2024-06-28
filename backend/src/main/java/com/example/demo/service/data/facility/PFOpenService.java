package com.example.demo.service.data.facility;

import com.example.demo.atech.ExcelManager;
import com.example.demo.atech.Msg;
import com.example.demo.config.mapStruct.MyMapper;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.data.facility.FacilityPk;
import com.example.demo.domain.data.facility.file.PFData;
import com.example.demo.domain.data.facility.file.PFDataRepoCustom;
import com.example.demo.domain.data.facility.file.PFDataRepository;
import com.example.demo.domain.data.facility.read.PFOpen;
import com.example.demo.domain.data.facility.read.PFOpenRepo;
import com.example.demo.domain.data.facility.read.PFOpenRepoCustom;
import com.example.demo.domain.data.facility.read.PFPrivate;
import com.example.demo.domain.system.user.User;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.dto.data.UploadDataDto;
import com.example.demo.dto.data.facility.PFOpenDto;
import com.example.demo.dto.data.facility.PFPrivateDto;
import com.example.demo.dto.data.standard.StandardMngDto;
import com.example.demo.service.api.geoCoder.GeoCoderService;
import com.example.demo.service.data.standard.StandardMngService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.demo.atech.ExcelManager.*;
import static com.example.demo.atech.Msg.*;
import static com.example.demo.atech.MyUtil.*;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PFOpenService {
    /*
    사유지/부설 개방
     */
    @Value("${spring.servlet.multipart2.standardExcel.download.pf}")
    private String pfPath;
    private final String EXCEL_NM = "OPEN.xlsx";
    private final String THIS = "주차시설-개방 주차장 데이터";
    private final MyMapper mapper;
    private final PFOpenRepo repo;
    private final UserRepository userRepo;
    private final StandardMngService mngService;
    private final PFOpenRepoCustom query;
    // 지오코딩
    private final GeoCoderService geoCoder;

    public PFOpenDto selectOne(String year, String month, String sggCd, String mngNo) {
        return repo.findById(new FacilityPk(year, month, sggCd, mngNo))
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)))
                .toRes();
    }


    /**
     * 엑셀 파일 -> DB 데이터
     *
     * @param origin 원본파일이 첨부된 문서
     */
    public void insert(PFData origin) {
//        1) get file
        FileInfo fi = origin.getAttaches().get(0);
        File file = new File(fi.getFilePath() + fi.getFileNmStored());
        if (!file.exists()) throw new NullPointerException(NO_FILES.getMsg());

        List<PFOpenDto.Req> reqList = new ArrayList<>();
        // 부설개방, 사유지개방 테이블 중 가장 최신 인덱스 구하기: 일련번호 생성하기 위한 정보
        Integer latestMngIdx = mngService.getLatestIdx(origin.getLotType());

//        2) get workbook
        try (XSSFWorkbook wb = readExcelFile(file)) {
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;

            // 데이터 범위 지정. 2~10(1~11)열까지.
            int endIdx = 12;
            for (int i = 1; i < sheet.getLastRowNum() + 2; i++) {
                // 종류(부설/사유지 구분), 주소가 빈 경우 수집하지 않고 건너뛰기
                row = sheet.getRow(i);
                if (row == null || !hasCell(row.getCell(6))) continue;

                // 행당 dto 생성, 디폴트값 세팅.
                PFOpenDto.Req req = new PFOpenDto.Req();
                req.setYear(origin.getYear());
                req.setMonth(origin.getMonth());
                req.setSggCd(origin.getSggCd());
                req.setPfData(origin);

                for (Cell cell : row) {
                    String cellData = getCellData(cell);
                    mappingPFOpen(req, new ExcelManager.CellInfo(cellData, cell.getColumnIndex()));
                    if (cell.getColumnIndex() == endIdx) break;
                }
                // 위경도 지오코딩
                if (!hasText(req.getLat())) {
                    HashMap<String, String> geoCodeMap = geoCoder.request(req.getAddress(), "PARCEL");
                    req.setLat(geoCodeMap.get("lat"));
                    req.setLng(geoCodeMap.get("lon"));
                }
                // 일련번호가 없는 데이터의 경우 관리대장 신규 등록
                if (!hasText(req.getMngNo())) {
                    StandardMngDto.Req mngReq = StandardMngDto.Req.builder()
                            .year(req.getYear())
                            .month(req.getMonth())
                            .sggCd(req.getSggCd())
                            .lotType(req.getLotType())  // 부설개방8, 사유지개방9
                            .dupChk1(req.getLotNm() + req.getAddress() + req.getSpcs())
                            .dupChk2(req.getLotNm() + req.getAddress())
                            .dupChk3(req.getLotNm() + req.getSpcs())
                            .dupChk4(req.getAddress() + req.getSpcs())
                            .build();
                    StandardMngDto newMngDto = mngService.insertOne(mngReq, latestMngIdx++);
                    req.setMngNo(newMngDto.getId());
                }
                // 중복검사용 컬럼 세팅
                req.setDupChk1(req.getLotNm() + req.getAddress() + req.getSpcs());
                req.setDupChk2(req.getLotNm() + req.getAddress());
                req.setDupChk3(req.getLotNm() + req.getSpcs());
                req.setDupChk4(req.getAddress() + req.getSpcs());
                reqList.add(req);
            }
        } catch (IOException e) {
            logErr(e);
            throw new IllegalArgumentException("주차시설-개방 주차장 엑셀 파일을 읽는 중에 문제가 있습니다.");
        }
        if (reqList.isEmpty()) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());

//            3) 적재
        List<PFOpen> list = new ArrayList<>();
        for (PFOpenDto.Req req : reqList) {
            list.add(mapper.toPFOpen(req));
        }
        List<PFOpen> saved = repo.saveAll(list);
        if (list.size() != saved.size()) throw new RuntimeException(getEnum(MISSING, THIS));

//            4) 원본에 데이터 승인 여부 체크
        origin.updateCollectYn("Y");

    }

    // update
    @Transactional
    public PFOpenDto update(PFOpenDto.Req req) {
        // 복합키 타겟
        FacilityPk pk = new FacilityPk(req.getYear(), req.getMonth(), req.getSggCd(), req.getMngNo());
        PFOpen target = repo.findById(pk)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

        try {
            target.update(req);
        } catch (Exception e) {
            logErr(e);
            throw new RuntimeException(UPDATE_ERR.getMsg());
        }
        return target.toRes();
    }

    // delete
    @Transactional
    public PFOpenDto delete(PFOpenDto.Req req){
        // 복합키 타겟
        FacilityPk pk = new FacilityPk(req.getYear(), req.getMonth(), req.getSggCd(), req.getMngNo());
        PFOpen target = repo.findById(pk)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

        try {
            repo.delete(target);
        } catch (Exception e) {
            logErr(e);
            throw new RuntimeException(DELETE_ERR.getMsg());
        }
        return target.toRes();
    }


    public void dupChkPfOpen(UploadDataDto.Req req, XSSFSheet sheet) {
        // 주차장 유형별 데이터베이스가 0인 경우 중복검사 하지 않음.
        if (repo.countByLotType(req.getLotType()) == 0) {
            req.setDupType("0");
            return;
        }

        try {
            for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
                XSSFRow row = sheet.getRow(i);
                // 일련번호가 있는 경우에는 중복검사 하지 않음
                if (row == null || hasCell(row.getCell(0))) continue;

                String lotNm = getCellData(row.getCell(5));
                String address = getCellData(row.getCell(6));
                String totalSpcs = getCellData(row.getCell(7));

                // 부분 중복을 허용하는 정책에선 중복 검사 결과가 2개 이상 걸릴 수 있음 -> countBy 로 교체
                if (repo.existsByDupChk1(lotNm + address + totalSpcs)) {
                    lotNm = lotNm == null ? "" : lotNm;
                    String dupRowInfo = row.getRowNum() + 1 + "행, " + lotNm;
                    req.setDupType("2");
                    req.setDupInfo(dupRowInfo);
                    break;
                } else if (
                        repo.countByDupChk2(lotNm + address) > 0
                                || repo.countByDupChk3(lotNm + totalSpcs) > 0
                                || repo.countByDupChk4(address + totalSpcs) > 0
                ) {
                    lotNm = lotNm == null ? "" : lotNm;
                    String dupRowInfo = row.getRowNum() + 1 + "행, " + lotNm;
                    req.setDupType("1");
                    req.setDupInfo(dupRowInfo);
                    break;
                }
            }
        } catch (Exception e) {
            logErr(e);
            throw new RuntimeException(DUP_CHK_ERR.getMsg());
        }
        // 루프를 다 돌고도 매겨진 값이 없으면 중복없음으로 판단
        if (!hasText(req.getDupType())) req.setDupType("0");
    }

    /**
     * 개방주차장 엑셀 다운로드
     * @param req 필터링 검색어
     * @param lotType 부설개방(8)/사유지개방(9) 구분
     */
    public void excelDownload(HttpServletResponse response, PFOpenDto.Keyword req, String lotType) {
//        1) 엑셀 데이터 확보
        List<PFOpenDto> dataList = query.search(req, lotType);
//        if (dataList.isEmpty()) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());

//        2) 엑셀 객체 생성
        XSSFWorkbook wb = readExcelFile(new File(pfPath + EXCEL_NM));
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row;

        // 변환 필요한 경우
        String lotTypeNm = lotType.equals("8") ? "부설개방" : "사유지개방";

        // 2행부터(idx:1) 데이터 세팅 시작
        int rowNo = 1;
        for (PFOpenDto dto : dataList) {
            row = sheet.createRow(rowNo++);
            int cellNo = 0;
            String sggNm = getSggCd2Nm(dto.getSggCd());

            row.createCell(cellNo++).setCellValue(dto.getMngNo()); // 일련번호
            row.createCell(cellNo++).setCellValue(lotTypeNm);
            row.createCell(cellNo++).setCellValue(dto.getYear());
            row.createCell(cellNo++).setCellValue(dto.getSeq());
            row.createCell(cellNo++).setCellValue(sggNm);
            row.createCell(cellNo++).setCellValue(dto.getLotNm());
            row.createCell(cellNo++).setCellValue(dto.getAddress());
            row.createCell(cellNo++).setCellValue(dto.getSpcs());
            row.createCell(cellNo++).setCellValue(dto.getArea());
            row.createCell(cellNo++).setCellValue(dto.getOpenTm());
            row.createCell(cellNo++).setCellValue(dto.getOpenDay());
            row.createCell(cellNo++).setCellValue(dto.getLat());
            row.createCell(cellNo).setCellValue(dto.getLng());
        }

        // 서식 적용
        XSSFCellStyle left = setDefaultStyle(wb, false, false, true, false, false, 12);
        XSSFCellStyle center = setDefaultStyle(wb, false, true, false, true, false, 12);
        for (int i = 1; i < dataList.size() + 1; i++) {
            row = sheet.getRow(i);

            for (Cell cell : row) {
                switch (cell.getColumnIndex()) {
                    case 5:
                    case 6:
                    case 9:
                        cell.setCellStyle(left);
                        break;
                    default:
                        cell.setCellStyle(center);
                        break;
                }
            }
        }

//        3) 다운로드. 제목은 표준으로 사용.
        // 연, 월, 구군 3개 검색어가 전부 있는 경우에만 업로드용 양식으로 파일명 생성
        String year = req.getYear();
        String month = req.getMonth();
        String sggCd = req.getSggCd();

        String fileNm;
        if (hasText(year) && hasText(month) && hasText(sggCd)) {
            fileNm = makeStandardFileNm4Pf(year, month, sggCd, lotType);
        } else {
            // 검색어 3개가 갖춰지지 않은 경우 일반조회용으로 생성
            fileNm = lotTypeNm + "_주차시설 표준_조회" + timestamp().substring(0, 10);
        }

        ExcelManager.writeExcelFile(response, wb, fileNm);
    }


    /**
     * 구군 담당자의 월간보고용 표준 양식 다운로드 키워드 제공
     * @return 최신 엑셀 다운로드에 필요한 키워드 year, month, sggCd
     */
    public PFOpenDto.Keyword getKeyword4manager(String lotType) {
//        1) 사용자 정보 확인
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.MANAGER_SESSION_EXPIRED.getMsg()));

//        2) 유효검사
        String agency = user.getAgency();
        if (agency.equals("31000") || !user.getRole().getEncodedNm().contains("담당자"))
            throw new IllegalArgumentException(Msg.ONLY_MANAGER.getMsg());

//        3) 존재하는 가장 최신 행의 year, month 로 검색하여 결과 가져옴.
        PFOpenDto.Keyword latestDt = query.getLatestDt(agency, lotType);
        String year = latestDt.getYear();
        String month = latestDt.getMonth();
        if (!hasText(year) || !hasText(month)) throw new EntityNotFoundException(Msg.EMPTY_RESULT.getMsg());

//        4) keyword 생성
        PFOpenDto.Keyword req = new PFOpenDto.Keyword();
        req.setYear(year);
        req.setMonth(month);
        req.setSggCd(agency);

        return req;
    }


    // 데이터 매핑 로직
    private void mappingPFOpen(PFOpenDto.Req req, ExcelManager.CellInfo info) {
        switch (info.getColIdx()) {
            case 0:   // 일련번호
                req.setMngNo(info.cellData);
                break;
            case 1:
//                종류. 8:부설, 9:사유지
                req.setLotType(info.cellData.equals("부설") ? "8" : "9");
                break;
//            case 2:   // 내부 연도 데이터 받지 않고 파일 업로드 정보로 일괄 적용
//                req.setYear(info.cellData);
//                break;
            case 3:
                req.setSeq(info.cellData);
                break;
//            case 4:   // 구군코드는 원본파일 입력과 같이 사용합니다
//                req.setSggCd(info.cellData);
//                break;
            case 5:
                req.setLotNm(info.cellData);
                break;
            case 6:
                req.setAddress(info.cellData);
                break;
            case 7:
                req.setSpcs(hasLong(info.cellData));
                break;
            case 8:
                req.setArea(info.cellData);
                break;
            case 9:
                req.setOpenTm(info.cellData);
                break;
            case 10:
                req.setOpenDay(info.cellData);
                break;
            case 11:
                req.setLat(info.cellData);
                break;
            case 12:
                req.setLng(info.cellData);
                break;
            default:
                break;
        }
    }

}
