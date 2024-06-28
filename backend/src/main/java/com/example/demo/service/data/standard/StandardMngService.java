package com.example.demo.service.data.standard;

import com.example.demo.atech.ExcelManager;
import com.example.demo.atech.Msg;
import com.example.demo.domain.data.standardSet.StandardMng;
import com.example.demo.domain.data.standardSet.StandardMngRepo;
import com.example.demo.domain.data.standardSet.StandardMngRepoCustom;
import com.example.demo.dto.api.BuildingManagementDto;
import com.example.demo.dto.data.UploadDataDto;
import com.example.demo.dto.data.standard.StandardMngDto;
import com.example.demo.dto.data.standard.StandardSetDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.parser.Entity;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.atech.ExcelManager.*;
import static com.example.demo.atech.MyUtil.*;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StandardMngService {
    /*
    표준 데이터셋 관리대장 서비스.
    주로 다른 도메인 서비스 로직에 추가로 들어갈 확률이 높으므로(거의 유틸리티 역할)
    @Transactional 을 선언해도 되는지 여부를 검토해야 합니다.
     */
    private final StandardMngRepo repo;
    private final StandardMngRepoCustom query;

    /*
    엑셀 다운로드 시 양식
     */
    @Value("${spring.servlet.multipart2.standardExcel.download.standard}")
    private String standardPath;

    private final String EXCEL_NM = "STANDARD_SET.xlsx";

    private final EntityManager em;

    public List<StandardSetDto> selectStandardSet(StandardSetDto.Keyword req){
        List<StandardSetDto> list = new ArrayList<>();

        // 동적쿼리
        String conditionQuery = "";
        if(hasText(req.getLotType())){
            conditionQuery += " and COALESCE(pbl.pklt_type, prv.pklt_type, open.pklt_type) = :lotType ";
        }
        if(hasText(req.getMngNo())){
            conditionQuery += " and COALESCE(pbl.mng_no, prv.mng_no, open.mng_no) like :mngNo ";
        }
        if(hasText(req.getSggCd())){
            conditionQuery += " and COALESCE(pbl.sgg_cd, prv.sgg_cd, open.sgg_cd) = :sggCd ";
        }
        if(hasText(req.getLotNm())){
            conditionQuery += " and COALESCE(pbl.pklt_nm, prv.pklt_nm, open.pklt_nm) like :lotNm ";
        }
        if(req.getMinSpcs() != null){
            conditionQuery += " and COALESCE(pbl.tot_spcs, open.open_spcs, prv.tot_spcs) >= :minSpcs ";
        }
        if(req.getMaxSpcs() != null){
            conditionQuery += " and COALESCE(pbl.tot_spcs, open.open_spcs, prv.tot_spcs) <= :maxSpcs ";
        }

        String nativeQuery = "SELECT \n" +
                "mng.mng_no,\n" +
                "    coalesce (pbl.sgg_cd, prv.sgg_cd, open.sgg_cd) as sgg_cd,\n" +
                "    coalesce (pbl.pklt_nm  , prv.pklt_nm, open.pklt_nm) as lot_nm,\n" +
                "    coalesce (pbl.pklt_type , prv.pklt_type, open.pklt_type) as lot_type ,\n" +
                "    coalesce (prv.stnm_addr) as stAddr,\n" +
                "    coalesce (open.addr, prv.addr, pbl.lctn) as addr,\n" +
                "    coalesce (pbl.tot_spcs, open.open_spcs, prv.tot_spcs) as ttl_spcs,\n" +
                "    coalesce (prv.land_grd)  as landRank,\n" +
                "    coalesce(open.open_dy) as workDay,\n" +
                "    coalesce(pbl.oper_wkdy) as weekOpenTm,\n" +
                "    coalesce(pbl.oper_stdy) as satOpenTm,\n" +
                "    coalesce(pbl.oper_hldy) as holiOpenTm,\n" +
                "    coalesce(prv.oper_info, pbl.pay_yn) as payInfo,\n" +
                "    coalesce(pbl.pay_hr) as parkingPay,    \n" +
                "    coalesce(pbl.pay_day) as payByDay,    \n" +
                "    coalesce(pbl.mngagc) as agency,    \n" +
                "    coalesce (prv.rprs_telno) as agencyTel,\n" +
                "    coalesce (pbl.lat , pbl.lat, pbl.lat) as lat,\n" +
                "    coalesce (pbl.lot, prv.lot, open.lot) as lot,\n" +
                "    coalesce (prv.pwdbs_pk_spcs) as hasDisSpcs,\n" +
                "    coalesce (pbl.yr, prv.yr, open.yr) as year,\n" +
                "    coalesce (pbl.mm, prv.mm, open.mm) as month\n" +
//                "    coalesce (pbl.crt_ymd, prv.crt_ymd, open.crt_ymd) as createDtm\n" +
                "FROM fct_std_mng_ldgr_t mng \n" +
                "LEFT JOIN (\n" +
                "    SELECT p.*\n" +
                "    FROM dim_pk_fclt_prvt_t p\n" +
                "    JOIN (\n" +
                "        SELECT sgg_cd,\n" +
                "               pklt_type,\n" +
                "               MAX(CAST(yr AS INTEGER)) AS max_yr,\n" +
                "               MAX(CAST(mm AS INTEGER)) AS max_mm\n" +
                "        FROM dim_pk_fclt_prvt_t\n" +
                "        GROUP BY sgg_cd, pklt_type\n" +
                "    ) AS latest_yr_mm ON p.sgg_cd = latest_yr_mm.sgg_cd\n" +
                "                     AND p.pklt_type = latest_yr_mm.pklt_type\n" +
                "                     AND CAST(p.yr AS INTEGER) = latest_yr_mm.max_yr\n" +
                "                     AND CAST(p.mm AS INTEGER) = latest_yr_mm.max_mm\n" +
                ") AS prv \n" +
                "ON mng.mng_no = prv.mng_no\n" +
                "LEFT JOIN (\n" +
                "    SELECT p.*\n" +
                "    FROM dim_ps_pbl_t p\n" +
                "    JOIN (\n" +
                "        SELECT sgg_cd,\n" +
                "               pklt_type,\n" +
                "               MAX(CAST(yr AS INTEGER)) AS max_yr,\n" +
                "               MAX(CAST(mm AS INTEGER)) AS max_mm\n" +
                "        FROM dim_ps_pbl_t\n" +
                "        GROUP BY sgg_cd, pklt_type\n" +
                "    ) AS latest_yr_mm ON p.sgg_cd = latest_yr_mm.sgg_cd\n" +
                "                     AND p.pklt_type = latest_yr_mm.pklt_type\n" +
                "                     AND CAST(p.yr AS INTEGER) = latest_yr_mm.max_yr\n" +
                "                     AND CAST(p.mm AS INTEGER) = latest_yr_mm.max_mm\n" +
                ") AS pbl \n" +
                "ON mng.mng_no = pbl.mng_no\n" +
                "LEFT JOIN (\n" +
                "    SELECT o.*\n" +
                "    FROM dim_pk_fclt_open_t o\n" +
                "    JOIN (\n" +
                "        SELECT sgg_cd,\n" +
                "               pklt_type,\n" +
                "               MAX(CAST(yr AS INTEGER)) AS max_yr\n" +
                "        FROM dim_pk_fclt_open_t\n" +
                "        GROUP BY sgg_cd, pklt_type\n" +
                "    ) AS latest_yr_mm ON o.sgg_cd = latest_yr_mm.sgg_cd\n" +
                "                     AND o.pklt_type = latest_yr_mm.pklt_type\n" +
                "                     AND CAST(o.yr AS INTEGER) = latest_yr_mm.max_yr\n" +
                ") AS open \n" +
                "ON mng.mng_no = open.mng_no\n" +
                "where COALESCE(pbl.pklt_type, prv.pklt_type, open.pklt_type) IS NOT null\n ";

        String orderByQuery = "order by mng.mng_no ";
        Query query = em.createNativeQuery(nativeQuery + conditionQuery + orderByQuery);

        // 동적 검색조건 추가
        if (hasText(req.getLotType())) {
            query.setParameter("lotType", req.getLotType());
        }
        if (hasText(req.getMngNo())) {
            query.setParameter("mngNo", "%" + req.getMngNo() + "%");
        }
        if (hasText(req.getSggCd())) {
            query.setParameter("sggCd", req.getSggCd());
        }
        if (hasText(req.getLotNm())) {
            query.setParameter("lotNm", "%" + req.getLotNm() + "%");
        }
        if (req.getMinSpcs() != null) {
            query.setParameter("minSpcs", req.getMinSpcs());
        }
        if (req.getMaxSpcs() != null) {
            query.setParameter("maxSpcs", req.getMaxSpcs());
        }

        // dto 에 쿼리 결과 바인딩
        List<Object[]> rows = query.getResultList();
        for (Object[] row : rows) {
            int idx = 0;
            StandardSetDto dto = new StandardSetDto();
            dto.setMngNo((String) row[idx++]);
            dto.setSggCd((String) row[idx++]);
            dto.setLotNm((String) row[idx++]);
            dto.setLotType((String) row[idx++]);
            dto.setStAddress((String) row[idx++]);
            dto.setAddress((String) row[idx++]);
            dto.setTotalSpcs(((BigInteger) row[idx++]).longValue());
            dto.setLandRank((String) row[idx++]);
            dto.setWorkDay((String) row[idx++]);
            dto.setWeekOpenTm((String) row[idx++]);
            dto.setSatOpenTm((String) row[idx++]);
            dto.setHoliOpenTm((String) row[idx++]);
            dto.setPayInfo((String) row[idx++]);
            dto.setParkingPay((String) row[idx++]);
            dto.setPayByDay((String) row[idx++]);
            dto.setAgency((String) row[idx++]);
            dto.setAgencyTel((String) row[idx++]);
            dto.setLat((String) row[idx++]);
            dto.setLon((String) row[idx++]);
            dto.setHasDisSpcs((String) row[idx++]);
            dto.setYear((String) row[idx++]);
            dto.setMonth((String) row[idx++]);
            list.add(dto);
        }

        return list;
    }


    // TODO: 표준 관리대장은 관리자에게만 조회 허용할 것으로 예상. 작업 후순위.
    // selectList. search 로 대체될 수 있음.

    /**
     * 표준관리대장에서 주차장 유형별 마지막 일련번호 인덱스 구하기
     *
     * @param lotType 주차장 유형 ex) 민영노외 "5"
     * @return 해당 유형 관리대장의 가장 최신 인덱스
     */
    public Integer getLatestIdx(String lotType) {
        StandardMng latest = repo.findFirstByLotTypeOrderByCreateDtmDesc(lotType).orElse(null);
        return latest == null ? 0 : hasInteger(latest.getIdxByType());
    }

    // insert
    public StandardMngDto insertOne(StandardMngDto.Req req, int latestMngIdx) {
//        StandardMng latest = repo
//                .findFirstByLotTypeOrderByCreateDtmDesc(req.getLotType())
//                .orElse(null);
//        int idx = latest == null ? 0 : hasInteger(latest.getIdxByType());

//            1) 일련번호 생성.
        // 구군코드는 아마 코드테이블 내용대로 들어올 것, 일련번호에 맞게 변환.
        String mngSggCd;
        switch (req.getSggCd()) {
            case "31110":    // 중구
                mngSggCd = "192";
                break;
            case "31140":    // 남구
                mngSggCd = "193";
                break;
            case "31170":    // 동구
                mngSggCd = "194";
                break;
            case "31200":    // 북구
                mngSggCd = "195";
                break;
            case "31710":    // 울주군
                mngSggCd = "196";
                break;
            default:
                throw new IllegalArgumentException(Msg.OUT_DOMAIN.getMsg());
        }

        // 6자리 인덱스. 신규 등록 데이터는 +1, 남는 자릿수는 0으로 채움.
        String resultIdx = String.format("%06d", ++latestMngIdx);
        // 신규 일련번호 완성
        String serialNo = mngSggCd + "-" + req.getLotType() + "-" + resultIdx;

//            2) 기타 정보 set
        StandardMng build = StandardMng.builder()
                // 기본 정보
                .id(serialNo)
                .year(req.getYear())
                .month(req.getMonth())
                .sggCd(mngSggCd)
                .lotType(req.getLotType())
                .idxByType(resultIdx)
                // 중복 검사용
                .dupChk1(req.getDupChk1())
                .dupChk2(req.getDupChk2())
                .dupChk3(req.getDupChk3())
                .dupChk4(req.getDupChk4())
                .build();

//        3) 적재
        return repo.save(build).toRes();
    }


    /**
     * 표준관리대장 데이터 적재(일련번호 채번)
     * 주차장 유형별 인덱스를 채번할 때 reqList의 첫 번째 lotType(유형코드)을 기준으로 진행됩니다
     * 트랜잭션이 끝나고 커밋되기 전까지는 테이블의 마지막 일련번호가 갱신되지 않습니다. *트랜잭션 시점 차이 주의.
     *
     * @param reqList 엑셀 데이터를 DB화 할 때 관리대장에 저장할 값들
     * @return 적재된 표준관리대장 데이터 dto
     */
    public List<StandardMngDto> insert(List<StandardMngDto.Req> reqList) {
//        한꺼번에 데이터가 여럿 들어올 수 있으므로 기본 다수 대상, 반복
//        중복 검사는 이전의 다른 로직에서 끝났다고 전제. 여기서는 insert 만 합니다.

        // 일련번호 생성 시 가장 마지막 번호 다음부터 갱신.
        String lotType = reqList.get(0).getLotType();
        StandardMng latest = repo
                .findFirstByLotTypeOrderByCreateDtmDesc(lotType)
                .orElse(null);
        int idx = latest == null ? 0 : hasInteger(latest.getIdxByType());

        List<StandardMng> list = new ArrayList<>();
        for (StandardMngDto.Req req : reqList) {
//            1) 일련번호 생성.
            // 구군코드는 아마 코드테이블 내용대로 들어올 것, 일련번호에 맞게 변환.
            String mngSggCd;
            switch (req.getSggCd()) {
                case "31110":    // 중구
                    mngSggCd = "192";
                    break;
                case "31140":    // 남구
                    mngSggCd = "193";
                    break;
                case "31170":    // 동구
                    mngSggCd = "194";
                    break;
                case "31200":    // 북구
                    mngSggCd = "195";
                    break;
                case "31710":    // 울주군
                    mngSggCd = "196";
                    break;
                default:
                    throw new IllegalArgumentException(Msg.OUT_DOMAIN.getMsg());
            }
            // 6자리 인덱스. 신규 등록 데이터는 +1, 남는 자릿수는 0으로 채움.
            String resultIdx = String.format("%06d", ++idx);
            // 신규 일련번호 완성
            String serialNo = mngSggCd + "-" + lotType + "-" + resultIdx;

//            2) 기타 정보 set
            StandardMng build = StandardMng.builder()
                    // 기본 정보
                    .id(serialNo)
                    .year(req.getYear())
                    .month(req.getMonth())
                    .sggCd(mngSggCd)
                    .lotType(lotType)
                    .idxByType(resultIdx)
                    // 중복 검사용
                    .dupChk1(req.getDupChk1())
                    .dupChk2(req.getDupChk2())
                    .dupChk3(req.getDupChk3())
                    .dupChk4(req.getDupChk4())
                    .build();
            list.add(build);
        }
//        3) 적재
        List<StandardMng> saved = repo.saveAll(list);
        if (list.size() != saved.size()) throw new RuntimeException(getEnum(Msg.MISSING, "관리대장"));

//        4) res
        return saved.stream().map(StandardMng::toRes).collect(Collectors.toList());
    }
    public List<StandardMngDto> insert(List<StandardMngDto.Req> reqList, String lotType) {
//        한꺼번에 데이터가 여럿 들어올 수 있으므로 기본 다수 대상, 반복
//        중복 검사는 이전의 다른 로직에서 끝났다고 전제. 여기서는 insert 만 합니다.

        // 일련번호 생성 시 가장 마지막 번호 다음부터 갱신.
        StandardMng latest = repo
                .findFirstByLotTypeOrderByCreateDtmDesc(lotType)
                .orElse(null);
        int idx = latest == null ? 0 : hasInteger(latest.getIdxByType());

        List<StandardMng> list = new ArrayList<>();
        for (StandardMngDto.Req req : reqList) {
//            1) 일련번호 생성.
            // 구군코드는 아마 코드테이블 내용대로 들어올 것, 일련번호에 맞게 변환.
            String mngSggCd;
            switch (req.getSggCd()) {
                case "31110":    // 중구
                    mngSggCd = "192";
                    break;
                case "31140":    // 남구
                    mngSggCd = "193";
                    break;
                case "31170":    // 동구
                    mngSggCd = "194";
                    break;
                case "31200":    // 북구
                    mngSggCd = "195";
                    break;
                case "31710":    // 울주군
                    mngSggCd = "196";
                    break;
                default:
                    throw new IllegalArgumentException(Msg.OUT_DOMAIN.getMsg());
            }
            // 6자리 인덱스. 신규 등록 데이터는 +1, 남는 자릿수는 0으로 채움.
            String resultIdx = String.format("%06d", ++idx);
            // 신규 일련번호 완성
            String serialNo = mngSggCd + "-" + lotType + "-" + resultIdx;

//            2) 기타 정보 set
            StandardMng build = StandardMng.builder()
                    // 기본 정보
                    .id(serialNo)
                    .year(req.getYear())
                    .month(req.getMonth())
                    .sggCd(mngSggCd)
                    .lotType(lotType)
                    .idxByType(resultIdx)
                    // 중복 검사용
                    .dupChk1(req.getDupChk1())
                    .dupChk2(req.getDupChk2())
                    .dupChk3(req.getDupChk3())
                    .dupChk4(req.getDupChk4())
                    .build();
            list.add(build);
        }
//        3) 적재
        List<StandardMng> saved = repo.saveAll(list);
        if (list.size() != saved.size()) throw new RuntimeException(getEnum(Msg.MISSING, "관리대장"));

//        4) res
        return saved.stream().map(StandardMng::toRes).collect(Collectors.toList());
    }


    /**
     * 표준 데이터셋 엑셀 다운로드
     *
     * @param response HttpServletResponse. 네트워크 요청.
     * @param req      검색어
     */
    public void excelDownload(HttpServletResponse response, StandardSetDto.Keyword req) {
//        1) 엑셀 데이터 확보
//        List<StandardSetDto> dataList = query.search(req);
        List<StandardSetDto> dataList = selectStandardSet(req);
        if (dataList.isEmpty()) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());

//        2) 엑셀 객체 생성
        XSSFWorkbook wb = readExcelFile(new File(standardPath + EXCEL_NM));
        XSSFSheet sheet = wb.getSheetAt(0);
        XSSFRow row;

//        3) 데이터 렌더링..?
        int rowNo = 1;
        for (StandardSetDto dto : dataList) {
            // 주차장 구분, 유형 분류 [240202] 확장 가능성 있는 상태
            HashMap<String, String> lotTypeMap = getLotSectionAndType(dto.getLotType());
            String sggNm = getSggCd2Nm(dto.getSggCd());

            // 기타 데이터 변환
            String hasDisSpcs = null;
            if (hasText(dto.getHasDisSpcs())) hasDisSpcs = Integer.parseInt(dto.getHasDisSpcs()) > 0 ? "보유" : "미보유";

            String payInfo = null;
            if (hasText(dto.getPayInfo())) {
                if (dto.getPayInfo().equals("Y")) {
                    payInfo = "유료";
                } else if (dto.getPayInfo().equals("N")) {
                    payInfo = "무료";
                } else {
                    payInfo = dto.getPayInfo();
                }
            }

            // 데이터 렌더링?
            row = sheet.createRow(rowNo++);
            int cellNo = 0;

            row.createCell(cellNo++).setCellValue(dto.getMngNo());
            row.createCell(cellNo++).setCellValue(sggNm);
            row.createCell(cellNo++).setCellValue(dto.getLotNm());
            row.createCell(cellNo++).setCellValue(lotTypeMap.get("section"));
            row.createCell(cellNo++).setCellValue(lotTypeMap.get("type"));
            row.createCell(cellNo++).setCellValue(dto.getStAddress());
            row.createCell(cellNo++).setCellValue(dto.getAddress());
            row.createCell(cellNo++).setCellValue(dto.getTotalSpcs());
            row.createCell(cellNo++).setCellValue(dto.getLandRank());
            row.createCell(cellNo++).setCellValue(dto.getExecType());
            row.createCell(cellNo++).setCellValue(dto.getWorkDay());
            row.createCell(cellNo++).setCellValue(dto.getWeekOpenTm());
            row.createCell(cellNo++).setCellValue(dto.getWeekCloseTm());
            row.createCell(cellNo++).setCellValue(dto.getSatOpenTm());
            row.createCell(cellNo++).setCellValue(dto.getSatCloseTm());
            row.createCell(cellNo++).setCellValue(dto.getHoliOpenTm());
            row.createCell(cellNo++).setCellValue(dto.getHoliCloseTm());
            row.createCell(cellNo++).setCellValue(payInfo);
            row.createCell(cellNo++).setCellValue(dto.getParkingTm());
            row.createCell(cellNo++).setCellValue(dto.getParkingPay());
            row.createCell(cellNo++).setCellValue(dto.getPlusTmUnit());
            row.createCell(cellNo++).setCellValue(dto.getPlusPayUnit());
            row.createCell(cellNo++).setCellValue(dto.getPayTmByDay());
            row.createCell(cellNo++).setCellValue(dto.getPayByDay());
            row.createCell(cellNo++).setCellValue(dto.getPayByMonth());
            row.createCell(cellNo++).setCellValue(dto.getHowPay());
            row.createCell(cellNo++).setCellValue(dto.getComment());
            row.createCell(cellNo++).setCellValue(dto.getAgency());
            row.createCell(cellNo++).setCellValue(dto.getAgencyTel());
            row.createCell(cellNo++).setCellValue(dto.getLat());
            row.createCell(cellNo++).setCellValue(dto.getLon());
            row.createCell(cellNo++).setCellValue(hasDisSpcs);
            row.createCell(cellNo++).setCellValue(dto.getIndrAutoUtcnt());
            row.createCell(cellNo++).setCellValue(dto.getIndrMechUtcnt());
            row.createCell(cellNo++).setCellValue(dto.getOutdrAutoUtcnt());
            row.createCell(cellNo++).setCellValue(dto.getOutdrMechUtcnt());
            row.createCell(cellNo).setCellValue(dto.getYear() + "-" + dto.getMonth());
        }

        // 서식 적용
        XSSFCellStyle left = setDefaultStyle(wb, false, false, true, false, false, 12);
        XSSFCellStyle center = setDefaultStyle(wb, false, true, false, true, false, 12);
        for (int i = 1; i < dataList.size() + 1; i++) {
            row = sheet.getRow(i);
            for (Cell cell : row) {
                switch (cell.getColumnIndex()) {
                    case 2:
                    case 5:
                    case 6:
                    case 17:
                        cell.setCellStyle(left);
                        break;
                    default:
                        cell.setCellStyle(center);
                        break;
                }
            }
        }

//        3) 다운로드. 제목: 표준 데이터셋
        String year = req.getYear();
        String month = req.getMonth();
//        String fileNm = year + "년_" + month + "월_표준 데이터셋";
        String fileNm = "표준 데이터셋_" + timestamp().substring(0, 10);
        ExcelManager.writeExcelFile(response, wb, fileNm);
    }
}
