package com.example.demo.service.data.monthlyReport;

import com.example.demo.atech.ExcelManager;
import com.example.demo.atech.FileManager;
import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.atech.enums.RoleCd;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.common.file.FileInfoRepository;
import com.example.demo.domain.data.facility.file.PFData;
import com.example.demo.domain.data.monthlyReport.MrData;
import com.example.demo.domain.data.monthlyReport.repo.MrDataRepository;
import com.example.demo.domain.data.monthlyReport.repo.PPublicRepository;
import com.example.demo.domain.system.user.User;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.dto.data.UploadDataDto;
import com.example.demo.dto.data.monthlyReport.MrDataDto;
import com.example.demo.service.data.monthlyReport.mrMng.MrService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.*;
import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.atech.ExcelManager.*;
import static com.example.demo.atech.MyUtil.*;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MrDataService {
    /*
    사용자 직접 입력 -> 월간보고 엑셀 파일을 사용자가 업로드
    엑셀 파일 업로드단 서비스 로직
     */

    private final String THIS = "월간보고 엑셀 파일";
    private final FileManager fm;
    private final MrDataRepository repo;
    private final PPublicRepository repoPPublic;
    private final FileInfoRepository fileRepo;
    private final MrService mrService;

    private final UserRepository userRepo;
    // select
    public MrDataDto selectOne(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)))
                .toRes();
    }

    // insert
    @Transactional
    public MrDataDto createOne(MrDataDto.Req req) {
//        1) 유효검사
        if (!hasText(req.getYear()) || !hasText(req.getSggCd()) || !hasText(req.getMonth()) || req.getFiles().isEmpty())
            throw new NullPointerException(Msg.NPE.getMsg());
        if (req.getFiles().size() > 1) throw new IllegalArgumentException("1개 파일만 첨부해주세요.");
        // 파일명 유효검사
        MultipartFile excel = req.getFiles().get(0);
        String attachNm = excel.getOriginalFilename();
        String correctNm = MyUtil.makeStandardFileNm4Mr(req.getYear(), req.getMonth(), req.getSggCd());
        if (!hasText(attachNm) || !attachNm.equals(correctNm + format))
            throw new IllegalArgumentException(getEnum(Msg.NOT_STANDARD_NM, correctNm + format));

//        2) 중복 검사
        try (XSSFWorkbook wb = readExcelFile(excel)) {
            XSSFSheet sheet = wb.getSheet("공영주차장 현황_표준양식");
            dupChkPPublic(req, sheet);
        } catch (Exception e) {
            logErr(e);
            throw new IllegalArgumentException(Msg.DUP_CHK_ERR.getMsg());
        }

//        3) 문서 우선 생성
        MrData data;
        try {
            data = repo.save(
                    MrData.builder()
                            .year(req.getYear())
                            .month(req.getMonth())
                            .sggCd(req.getSggCd())
                            .dataNm(correctNm)
                            .comment(req.getComment())
                            .dupType(req.getDupType())
                            .dupInfo(req.getDupInfo())
                            .collectYn("N")
                            .build()
            );

//        4) 첨부파일 업로드
            FileManager.Res detail = fm.saveFile(excel);
            List<FileInfo> attaches = new ArrayList<>();

            attaches.add(
                    fileRepo.save(FileInfo.builder()
                            .fileNm(detail.getOriginNm())
                            .fileNmStored(detail.getSavedNm())
                            .filePath(detail.getPath())
                            .mrData(data)
                            .build()
                    )
            );
            data.addAttaches(attaches);
        } catch (Exception e) {
            logErr(e);
            throw new RuntimeException(Msg.INSERT_ERR.getMsg());
        }

//        5) res
        return data.toRes();
    }

    /**
     * DB화 이후에는 정보 수정/삭제 불가.
     * @param req 첨부파일 제외한 부분
     * @return 수정 후 파일 데이터 상태
     */
    @Transactional
    public MrDataDto updateOne(MrDataDto.Req req) {
        // 첨부파일은 변경 불가.
        MrData target = repo.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        // 데이터명 디폴트 지정
        String fileNm = makeStandardFileNm4Mr(req.getYear(), req.getMonth(), req.getSggCd());
        req.setDataNm(fileNm);

        try {
            target.update(req);
        } catch (Exception e) {
            logErr(e);
            throw new RuntimeException(Msg.UPDATE_ERR.getMsg());
        }
        return target.toRes();
    }

    // db
    @Transactional
    public MrDataDto collectData(Long id) {
//        1) 타겟팅, 기본 유효 검사
        MrData doc = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        if (doc.getAttaches() == null && doc.getAttaches().isEmpty())
            throw new NullPointerException(Msg.NO_FILES.getMsg());

//        2) 중복 유무: 완전중복 행이 있는 파일이면 DB화 불가.
        if (doc.getDupType().equals("2")) throw new DataIntegrityViolationException("해당 파일은 승인할 수 없습니다.");
        if (doc.getDupType().equals("1")) {
            // 부분중복은 관리자 권한 검사 후 승인
            String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
            User user = userRepo.findByUserId(userId).orElse(null);

            if (user == null || !user.getRole().getEncodedNm().contains("관리자"))
                throw new AccessDeniedException(Msg.NO_ACCESS.getMsg());
        }

//        3) DB화. 원천 데이터, 관리대장 동시 적재.
        try {
            mrService.collectMrData(doc);
        } catch (Exception e) {
            logErr(e);
            throw new IllegalArgumentException(Msg.COLLECT_DATA_ERR.getMsg());
        }
//        4) res
        return doc.toRes();
    }

    // delete
    @Transactional
    public MrDataDto deleteOne(Long id) {
        MrData target = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
//        if(target.getCollectYn().equals("Y")) throw new DataIntegrityViolationException(Msg.CANNOT_DELETE.getMsg());

        // 실제 파일 삭제. 월간보고는 1개씩만 첨부하도록 제한. [240318] 실제 파일 삭제 여부는 따지지 않도록 변경
//        if (target.getAttaches().isEmpty()) throw new NullPointerException(Msg.NO_FILES.getMsg());
        FileInfo info = target.getAttaches().get(0);
        if (!fm.rmFile(info.getFilePath(), info.getFileNmStored()))
            log.error(THIS + " 이 발견되지 않았습니다.");
//            throw new RuntimeException(getEnum(Msg.RM_ERR, THIS));

        // 파일 문서가 삭제돼도 DB화 된 데이터는 삭제하지 않음. 복합키 데이터의 경우 신규 데이터화로 덮어쓰기 가능.
        try {
            repo.delete(target);
        } catch (Exception e) {
            logErr(e);
            throw new RuntimeException(Msg.DELETE_ERR.getMsg());
        }
        return target.toRes();
    }


    /**
     * 데이터 반려(거부). collectYn(data_yn) 을 "X" 로 변경하여 해당 데이터는 승인할 수 없게 만듦.
     * @param id 파일 문서 pk
     * @return collectYn == "X"
     */
    @Transactional
    public MrDataDto reject(Long id) {
//        1) 관리자만 가능한 기능. 권한 유효검사
        String userNm = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userRepo.findByUserId(userNm).orElseThrow(() -> new EntityNotFoundException(Msg.NO_ACCESS.getMsg()));

        if (!user.getRole().getEncodedNm().contains(RoleCd.ROLE_1ST.getRole()))
            throw new AccessDeniedException(Msg.NO_ACCESS.getMsg());

//        2) 대상 파일 타겟팅, 상태 확인
        MrData doc = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        if (!doc.getCollectYn().equals("N")) throw new IllegalArgumentException(Msg.CANNOT_UPDATE.getMsg());

//        3) res
//        [240416]이 컬럼은 nn인데 null 을 넣기 위해 alter 문을 사용해야 하면 이관 시 작업이 까다로워지므로 "X" 로 대체합니다
        doc.updateCollectYn("X");
        return doc.toRes();
    }



    /*
    ===================================================================== 월간보고 각 시트 DB화 메서드
    이후 1개 트랜잭션 안에 묶습니다.
     */

    /**
     * 월간보고-주차장(확보)현황. 주로 계산 값이므로 Long 타입 추천.
     * @param year 실시연도
     * @param month 월
     * @param file 엑셀 파일
     */
    private void insertPStatus(String year, String month, File file){

    }
    private void insertMrOut(){

    }


    //중복체크
    public void dupChkPPublic(MrDataDto.Req req, XSSFSheet sheet) {
        // 주차장 유형별 데이터베이스가 0인 경우 중복검사 하지 않음.
        if (repoPPublic.count() == 0) {
            req.setDupType("0");
            return;
        }
        // 디폴트값
        req.setDupType("0");

        String lotType = "";
        for (int i = 5; i < sheet.getLastRowNum() + 5; i++) {
            XSSFRow row = sheet.getRow(i);
            // 일련번호가 없는 경우에는 중복검사 하지 않음
            //if(row == null || !hasCell(row.getCell(0))) continue;
            // 일련번호가 없을 경우만 중복 로직 적용
            if(row == null || !hasCell(row.getCell(0)) || hasCell(row.getCell(1))) continue;
//            if (!hasCell(row.getCell(0))) continue;
//            if (hasCell(row.getCell(1))) continue;

            String lotNm = getCellData(row.getCell(2));
            String address = getCellData(row.getCell(4));
            String totalSpcs = getCellData(row.getCell(14));

            if (getCellData(row.getCell(24)).equals("노상")) {
                lotType = "1";
            } else if (getCellData(row.getCell(24)).equals("노외")) {
                lotType = "2";
            } else if (getCellData(row.getCell(24)).equals("부설")) {
                lotType = "3";
            }

            // 부분 중복을 허용하는 정책에선 중복 검사 결과가 2개 이상 걸릴 수 있음 -> countBy 로 교체
            if (repoPPublic.countByLotTypeAndDupChk1(lotType, lotNm + address + totalSpcs) > 0) {
                lotNm = lotNm == null ? "" : lotNm;
                String dupRowInfo = row.getRowNum() + 1 + "행, " + lotNm;
                req.setDupType("2");
                req.setDupInfo(dupRowInfo);
                break;
            } else if (
                    repoPPublic.countByLotTypeAndDupChk2(lotType, lotNm + address) > 0
                            || repoPPublic.countByLotTypeAndDupChk3(lotType, lotNm + totalSpcs) > 0
                            || repoPPublic.countByLotTypeAndDupChk3(lotType, address + totalSpcs) > 0
            ) {
                lotNm = lotNm == null ? "" : lotNm;
                String dupRowInfo = row.getRowNum() + 1 + "행, " + lotNm;
                req.setDupType("1");
                req.setDupInfo(dupRowInfo);
                break;
            }
        }
    }



}
