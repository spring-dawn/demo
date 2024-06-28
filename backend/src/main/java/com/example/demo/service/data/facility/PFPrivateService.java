package com.example.demo.service.data.facility;

import com.example.demo.atech.ExcelManager;
import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.config.mapStruct.MyMapper;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.data.facility.FacilityPk;
import com.example.demo.domain.data.facility.file.PFData;
import com.example.demo.domain.data.facility.file.PFDataRepoCustom;
import com.example.demo.domain.data.facility.file.PFDataRepository;
import com.example.demo.domain.data.facility.read.PFPrivate;
import com.example.demo.domain.data.facility.read.PFPrivateRepo;
import com.example.demo.domain.data.facility.read.PFPrivateRepoCustom;
import com.example.demo.domain.data.standardSet.StandardMng;
import com.example.demo.domain.data.standardSet.StandardMngRepo;
import com.example.demo.domain.system.user.User;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.dto.data.UploadDataDto;
import com.example.demo.dto.data.facility.PFPrivateDto;
import com.example.demo.dto.data.standard.StandardMngDto;
import com.example.demo.service.api.geoCoder.GeoCoderService;
import com.example.demo.service.data.standard.StandardMngService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
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
public class PFPrivateService {
    /*
    주차시설 민영(노외), 부설/사유지 개방 -> 데이터 서비스 로직.
    엑셀 DB화는 이후 PFDataService 의 collectData 에 쓰입니다
     */
    @Value("${spring.servlet.multipart2.standardExcel.download.pf}")
    private String pfPath;
    private final String THIS = "주차시설-민영주차장 데이터";
    private final String EXCEL_NM = "PRV.xlsx";
    private final MyMapper mapper;
    private final PFPrivateRepo repo;
    private final UserRepository userRepo;
    private final PFPrivateRepoCustom query;
    private final StandardMngService mngService;
    // 지오코딩
    private final GeoCoderService geoCoder;


    // select(search 로 대체)
    // select One
    public PFPrivateDto selectOne(String year, String month, String sggCd, String mngNo) {
        return repo.findById(new FacilityPk(year, month, sggCd, mngNo))
                .orElseThrow(() -> new EntityNotFoundException(getEnum(ENTITY_NOT_FOUND, THIS)))
                .toRes();
    }

    /**
     * 민영(노외)주차장 엑셀 파일 -> DB화(데이터 승인)
     *
     * @param origin 원본 파일(PFData)
     */
    public void insert(PFData origin) {
//        1) get file
        FileInfo fi = origin.getAttaches().get(0);
        File file = new File(fi.getFilePath() + fi.getFileNmStored());
        if (!file.exists()) throw new NullPointerException(NO_FILES.getMsg());
        // (result) list, 엔티티 dto 리스트
        List<PFPrivate> list = new ArrayList<>();
        List<PFPrivateDto.Req> reqList = new ArrayList<>();

        // 민영노외 테이블 중 가장 최신 인덱스 구하기: 일련번호 생성하기 위한 정보
        Integer latestMngIdx = mngService.getLatestIdx(origin.getLotType());

//        2) get workbook
        try (XSSFWorkbook wb = readExcelFile(file)) {
            XSSFSheet sheet = wb.getSheetAt(0);
            XSSFRow row;

            // 데이터 범위 지정. 2행부터 시작, 1~21열까지 수집. 지번주소, 총주차면수가 없는 경우 수집X.
            int endIdx = 20;
            for (int i = 1; i < sheet.getLastRowNum() + 2; i++) {
                row = sheet.getRow(i);
                if (row == null || !hasCell(row.getCell(5)) || !hasCell(row.getCell(15))) continue;

                // 행당 dto 생성, 디폴트값 세팅.
                PFPrivateDto.Req req = new PFPrivateDto.Req();
                req.setYear(origin.getYear());
                req.setMonth(origin.getMonth());
                req.setSggCd(origin.getSggCd());
                req.setPfData(origin);

                for (Cell cell : row) {
                    // 그룹(구군) 입력이 샘플에서 정확하지 않아 받지 않음.
                    String cellData = getCellData(cell);
                    mappingPrv(req, new ExcelManager.CellInfo(cellData, cell.getColumnIndex()));
                    if (cell.getColumnIndex() == endIdx) break;
                }
                // 위경도 지오코딩
                if (!hasText(req.getLat())) {
                    HashMap<String, String> geoCodeMap = geoCoder.request(req.getAddress(), "PARCEL");
                    req.setLat(geoCodeMap.get("lat"));
                    req.setLng(geoCodeMap.get("lon"));
                }
                // 일련번호가 없는 경우에는 관리대장 신규 등록.
                if (!hasText(req.getMngNo())) {
                    StandardMngDto.Req mngReq = StandardMngDto.Req
                            .builder()
                            .year(req.getYear())
                            .month(req.getMonth())
                            .sggCd(req.getSggCd())
                            .lotType(req.getLotType())  // 민영노외 5
                            .dupChk1(req.getLotNm() + req.getAddress() + req.getTotalSpcs())
                            .dupChk2(req.getLotNm() + req.getAddress())
                            .dupChk3(req.getLotNm() + req.getTotalSpcs())
                            .dupChk4(req.getAddress() + req.getTotalSpcs())
                            .build();
                    StandardMngDto newMngDto = mngService.insertOne(mngReq, latestMngIdx++);
                    req.setMngNo(newMngDto.getId());
                }
                // 중복검사용 컬럼 세팅
                req.setDupChk1(req.getLotNm() + req.getAddress() + req.getTotalSpcs());
                req.setDupChk2(req.getLotNm() + req.getAddress());
                req.setDupChk3(req.getLotNm() + req.getTotalSpcs());
                req.setDupChk4(req.getAddress() + req.getTotalSpcs());

                reqList.add(req);
            }
        } catch (IOException e) {
            logErr(e);
            throw new IllegalArgumentException("민영주차장 엑셀 파일을 읽던 중 오류가 발생했습니다.");
        }
        if (reqList.isEmpty()) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());

//            3) 적재
        for (PFPrivateDto.Req req : reqList) {
            list.add(mapper.toPFPrivate(req));
        }
        List<PFPrivate> saved = repo.saveAll(list);
        if (list.size() != saved.size()) throw new RuntimeException(getEnum(MISSING, THIS));

//            4) 원본에 데이터 승인 여부 체크
        origin.updateCollectYn("Y");
    }

    // update
    @Transactional
    public PFPrivateDto update(PFPrivateDto.Req req) {
        PFPrivate target = repo.findById(new FacilityPk(req.getYear(), req.getMonth(), req.getSggCd(), req.getMngNo()))
                .orElseThrow(() -> new EntityNotFoundException(getEnum(ENTITY_NOT_FOUND, THIS)));

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
    public PFPrivateDto delete(PFPrivateDto.Req req) {
        PFPrivate target = repo.findById(new FacilityPk(req.getYear(), req.getMonth(), req.getSggCd(), req.getMngNo()))
                .orElseThrow(() -> new EntityNotFoundException(getEnum(ENTITY_NOT_FOUND, THIS)));
        try {
            repo.delete(target);
        } catch (Exception e) {
            logErr(e);
            throw new RuntimeException(DELETE_ERR.getMsg());
        }
        return target.toRes();
    }


    /**
     * 민영노외 주차장 엑셀 파일 업로드 시 중복검사
     *
     * @param req   파일 문서 dto
     * @param sheet 중복검사 대상 시트
     */
    public void dupChkPfPrv(UploadDataDto.Req req, XSSFSheet sheet) {
        // 주차장 유형별 데이터베이스가 0인 경우 중복검사 하지 않음.
        if (repo.countBy() == 0) {
            log.info("비교할 데이터 행이 없음.");
            req.setDupType("0");
            return;
        }

        try {
            for (int i = 1; i < sheet.getLastRowNum() + 1; i++) {
                XSSFRow row = sheet.getRow(i);
                // 일련번호가 있는 경우에는 중복검사 하지 않음
                if (row == null || hasCell(row.getCell(0))) continue;

                String lotNm = getCellData(row.getCell(2));
                String address = getCellData(row.getCell(5));
                String totalSpcs = getCellData(row.getCell(15));

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
                    req.setDupType("1");
                    lotNm = lotNm == null ? "" : lotNm;
                    String dupRowInfo = row.getRowNum() + 1 + "행, " + lotNm;
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
     * 민영(노외) 주차장 원천 데이터 엑셀 다운로드(업로드용에 가까움)
     * @param response HttpServletResponse
     * @param req 연/월/구군 등 검색 키워드. 필터링 결과 제공.
     */
    public void excelDownload(HttpServletResponse response, PFPrivateDto.Keyword req) {
//        1) 엑셀 데이터 확보
        List<PFPrivateDto> dataList = query.search(req);
//        if (dataList.isEmpty()) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());

//        2) 엑셀 객체 생성
        XSSFWorkbook wb = readExcelFile(new File(pfPath + EXCEL_NM));
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row;

        // 변환 필요한 경우
        String lotType = dataList.get(0).getLotType();
        String lotTypeNm = lotType.equals("5") ? "노외" : "미구현";

        // 2행부터(idx:1) 데이터 세팅 시작
        int rowNo = 1;
        for (PFPrivateDto dto : dataList) {
            row = sheet.createRow(rowNo++);
            int cellNo = 0;

            String sggNm = getSggCd2Nm(dto.getSggCd());
            row.createCell(cellNo++).setCellValue(dto.getMngNo()); // 일련번호
            row.createCell(cellNo++).setCellValue(sggNm); // 자동 변환하여 채워주기.
            row.createCell(cellNo++).setCellValue(dto.getLotNm());
            row.createCell(cellNo++).setCellValue(dto.getLotId());
            row.createCell(cellNo++).setCellValue(lotTypeNm);
            row.createCell(cellNo++).setCellValue(dto.getAddress());
            row.createCell(cellNo++).setCellValue(dto.getStreetAddr());
            row.createCell(cellNo++).setCellValue(dto.getCeoCellNo());
            row.createCell(cellNo++).setCellValue(dto.getOperateInfo());
            row.createCell(cellNo++).setCellValue(dto.getLat());
            row.createCell(cellNo++).setCellValue(dto.getLng());
            row.createCell(cellNo++).setCellValue(dto.getCollectInfo());
            row.createCell(cellNo++).setCellValue(dto.getLandRank());
            row.createCell(cellNo++).setCellValue(dto.getSpcs());
            row.createCell(cellNo++).setCellValue(dto.getDisabledSpcs());
            row.createCell(cellNo++).setCellValue(dto.getTotalSpcs());
            row.createCell(cellNo++).setCellValue(dto.getSpcsIn());
            row.createCell(cellNo++).setCellValue(dto.getDisabledSpcsIn());
            row.createCell(cellNo++).setCellValue(dto.getTotalSpcsIn());
            row.createCell(cellNo++).setCellValue(dto.getRegiTm());
            row.createCell(cellNo).setCellValue(dto.getRegiType());
        }

        // 서식 적용
        XSSFCellStyle left = setDefaultStyle(wb, false, false, true, false, false, "맑은 고딕", 12);
        XSSFCellStyle center = setDefaultStyle(wb, false, true, false, true, false, "맑은 고딕", 12);
        for (int i = 1; i < dataList.size() + 1; i++) {   // 1행부터 시작해서 1칸 부족함
            row = sheet.getRow(i);
            for (Cell cell : row) {
                switch (cell.getColumnIndex()) {
                    case 2:
                    case 5:
                    case 6:
                    case 8:
                    case 11:
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
            fileNm = "민영노외_주차시설 표준_조회" + timestamp().substring(0, 10);
        }
        ExcelManager.writeExcelFile(response, wb, fileNm);
    }

    /**
     * 구군 담당자의 월간보고용 표준 양식 다운로드 키워드 제공
     * @return 최신 엑셀 다운로드에 필요한 키워드 year, month, sggCd
     */
    public PFPrivateDto.Keyword getKeyword4manager() {
//        1) 사용자 정보 확인
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(Msg.MANAGER_SESSION_EXPIRED.getMsg()));

//        2) 유효검사
        String agency = user.getAgency();
        if (agency.equals("31000") || !user.getRole().getEncodedNm().contains("담당자"))
            throw new IllegalArgumentException(Msg.ONLY_MANAGER.getMsg());

//        3) 존재하는 가장 최신 행의 year, month 로 검색하여 결과 가져옴.
        PFPrivateDto.Keyword latestDt = query.getLatestDt(agency);
        String year = latestDt.getYear();
        String month = latestDt.getMonth();
        if (!hasText(year) || !hasText(month)) throw new EntityNotFoundException(Msg.EMPTY_RESULT.getMsg());

//        4) return keyword
        PFPrivateDto.Keyword req = new PFPrivateDto.Keyword();
        req.setYear(year);
        req.setMonth(month);
        req.setSggCd(agency);

        return req;
    }


    // 이하 데이터 추출 로직
    private void mappingPrv(PFPrivateDto.Req req, ExcelManager.CellInfo info) {
        switch (info.getColIdx()) {
            case 0:
            // 일련번호
                req.setMngNo(info.cellData);
                break;
//            case 1:   // 그룹(구군) 셀값을 따지 않음. 자주 비어 있는 모양.
//                req.setLotNm(info.cellData);
//                break;
            case 2:
                req.setLotNm(info.cellData);
                break;
            case 3:
                req.setLotId(info.cellData);
                break;
            case 4:
                // 4:민영-노상, 5:민영-노외
                req.setLotType(info.cellData.equals("노상") ? "4" : "5");
                break;
            case 5:
                req.setAddress(info.cellData);
                break;
            case 6:
                req.setStreetAddr(info.cellData);
                break;
            case 7:
                req.setCeoCellNo(info.cellData);
                break;
            case 8:
                req.setOperateInfo(info.cellData);
                break;
            case 9:
                req.setLat(info.cellData);
                break;
            case 10:
                req.setLng(info.cellData);
                break;
            case 11:
                req.setCollectInfo(info.cellData);
                break;
            case 12:
                req.setLandRank(info.cellData);
                break;
            case 13:
                req.setSpcs(info.cellData);
                break;
            case 14:
                req.setDisabledSpcs(info.cellData);
                break;
            case 15:
                req.setTotalSpcs(hasLong(info.cellData));
                break;
            case 16:
                req.setSpcsIn(info.cellData);
                break;
            case 17:
                req.setDisabledSpcsIn(info.cellData);
                break;
            case 18:
                req.setTotalSpcsIn(info.cellData);
                break;
            case 19:
                req.setRegiTm(info.cellData);
                break;
            case 20:
                // 1: 통보, 2: 자체파악 -> [240130] 엑셀 뽑아보니 불편해서 그냥 원형으로 받음.
                req.setRegiType(info.cellData);
                break;
            default:
                break;
        }
    }


}
