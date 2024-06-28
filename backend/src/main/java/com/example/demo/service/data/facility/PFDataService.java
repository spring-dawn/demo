package com.example.demo.service.data.facility;

import com.example.demo.atech.ExcelManager;
import com.example.demo.atech.FileManager;
import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.atech.enums.RoleCd;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.common.file.FileInfoRepository;
import com.example.demo.domain.data.facility.file.PFData;
import com.example.demo.domain.data.facility.file.PFDataRepository;
import com.example.demo.domain.data.standardSet.StandardMngRepo;
import com.example.demo.domain.system.user.User;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.dto.data.UploadDataDto;
import com.example.demo.service.data.standard.StandardMngService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.demo.atech.ExcelManager.*;
import static com.example.demo.atech.MyUtil.*;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PFDataService {
    private final String THIS = "주차시설 엑셀 파일";
    private final PFDataRepository repo;
    private final FileManager fm;
    private final FileInfoRepository fileRepo;
    private final UserRepository userRepo;

    /*
    데이터 중복 검사용 repo & 엑셀 DB화 service -> repo 부분은 service 로직에 분리할 것.
     */
    private final PFPrivateService prvService;
    private final PFOpenService openService;


//    --------------------------------------------------- 이하 서비스 로직

    // selectList 필요 없음, search 로 대체.
    // selectOne
    public UploadDataDto selectOne(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)))
                .toRes();
    }


    /**
     * 주차시설 엑셀 파일 단일건 업로드
     *
     * @param req 업로드 할 파일 정보, 첨부엑셀파일
     * @return 적재 성공한 파일관리 dto
     */
    @Transactional
    public UploadDataDto insert(UploadDataDto.Req req) {
//        1) 유효검사
        if (!hasText(req.getYear()) || !hasText(req.getSggCd()) || !hasText(req.getMonth()) || req.getFiles().isEmpty())
            throw new NullPointerException(Msg.NPE.getMsg());
        // 파일 데이터
        if (req.getFiles().size() > 1)
            throw new IllegalArgumentException("1개 파일만 첨부해주세요.");
        // 파일명
        MultipartFile excel = req.getFiles().get(0);
        String attachNm = excel.getOriginalFilename();
        String correctNm = makeStandardFileNm4Pf(req.getYear(), req.getMonth(), req.getSggCd(), req.getLotType());
        if (!hasText(attachNm) || !attachNm.equals(correctNm + format))
            throw new IllegalArgumentException(getEnum(Msg.NOT_STANDARD_NM, correctNm + format));

//        2) 주차장 유형별 중복 검사. 파일명 기초 유효 검사는 readExcelFile 에서 실행.
        try (XSSFWorkbook wb = readExcelFile(excel)) {
            XSSFSheet sheet = wb.getSheetAt(0);

            switch (req.getLotType()) {
                case "5": // 민영(노외)
                    prvService.dupChkPfPrv(req, sheet);
                    break;
                case "8":   // 부설 개방
                case "9":   // 사유지 개방
                    openService.dupChkPfOpen(req, sheet);
                    break;
                default:
                    throw new IllegalArgumentException("취급하지 않는 데이터입니다. 다시 확인해주세요.");
            }
        } catch (IOException e) {
            logErr(e);
            throw new RuntimeException(Msg.DUP_CHK_ERR.getMsg());
        }

//        3) 문서 엔티티 생성
        PFData data;
        try {
            data = repo.save(
                    PFData.builder()
                            .year(req.getYear())
                            .month(req.getMonth())
                            .sggCd(req.getSggCd())
                            .dataNm(correctNm)
                            .lotType(req.getLotType())
                            .dupType(req.getDupType())
                            .dupInfo(req.getDupInfo())
                            .comment(req.getComment())
                            .collectYn("N")
                            .build()
            );

//        4) 파일 저장, 첨부파일(FileInfo) 생성 후 문서에 연결
            FileManager.Res detail = fm.saveFile(excel);
            List<FileInfo> attaches = new ArrayList<>();

            attaches.add(
                    fileRepo.save(FileInfo.builder()
                            .fileNm(detail.getOriginNm())
                            .fileNmStored(detail.getSavedNm())
                            .filePath(detail.getPath())
                            .pfData(data)
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
     * 파일관리 -> 엑셀 DB화
     *
     * @param id 파일 문서 pk
     * @return 데이터 승인 여부 확인 "Y"
     */
    @Transactional
    public UploadDataDto collectData(Long id) {
//        1) 타겟팅, 기본 유효 검사
        PFData doc = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        if (doc.getAttaches() == null && doc.getAttaches().isEmpty())
            throw new NullPointerException(Msg.NO_FILES.getMsg());
        // 본인이 업로드한 파일인지 확인
        String userId = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userRepo.findByUserId(userId).orElse(null);
        // 본인이 업로드한 게 아니면서 관리자도 아닌 경우 승인 거부.
        if (!userId.equals(doc.getCreateId()) && !user.getRole().getEncodedNm().contains("관리자"))
            throw new AccessDeniedException(Msg.NOT_MINE.getMsg());

//        2) 중복 유무: 완전중복 행이 있는 파일이면 DB화 불가.
        if (doc.getDupType().equals("2")) throw new DataIntegrityViolationException(Msg.ABSOLUTE_DUP.getMsg());
        if (doc.getDupType().equals("1")) {
            // 부분중복은 관리자 권한 검사 후 승인
            if (user == null || !user.getRole().getEncodedNm().contains("관리자"))
                throw new AccessDeniedException(Msg.NO_ACCESS.getMsg());
        }

//        3) 각 유형에 일치하는 DB화 로직 호출. 원천 데이터, 관리대장 동시 적재.
        try {
            switch (doc.getLotType()) {
                case "5": // 민영(노외?)
                    prvService.insert(doc);
                    break;
                case "8":   // 부설 개방
                case "9":   // 사유지 개방
                    openService.insert(doc);
                    break;
                default:
                    throw new IllegalArgumentException(Msg.NOT_SUPPORT_TYPE.getMsg());
            }
        } catch (Exception e) {
            logErr(e);
            throw new RuntimeException(Msg.COLLECT_DATA_ERR.getMsg());
        }
//        4) 데이터 승인여부 update(롤백을 위해 각 DB화 로직에서 실행), res
        return doc.toRes();
    }

    // update
    @Transactional
    public UploadDataDto update(UploadDataDto.Req req) {
        // 첨부파일은 변경 불가. 데이터 중복검사를 다시 해야 하는데 권장되지 않음.
//        TODO: [240125] 첨부파일 교체 시 기존 파생 데이터 일괄 삭제??
        PFData target = repo.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        // 데이터명 디폴트 지정
        String fileNm = makeStandardFileNm4Pf(req.getYear(), req.getMonth(), req.getSggCd(), req.getLotType());
        req.setDataNm(fileNm);

        target.update(req);
        return target.toRes();
    }

    // delete
    @Transactional
    public UploadDataDto delete(Long id) {
        PFData target = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
//        if(target.getCollectYn().equals("Y")) throw new DataIntegrityViolationException(Msg.CANNOT_DELETE.getMsg());

        // 실제 파일 삭제. 월간보고는 1개씩만 첨부하도록 제한. [240318] 실제 파일 삭제 여부는 따지지 않도록 변경
//        if (target.getAttaches().isEmpty()) throw new NullPointerException(Msg.NO_FILES.getMsg());
        FileInfo info = target.getAttaches().get(0);
        if (!fm.rmFile(info.getFilePath(), info.getFileNmStored()))
            log.error(THIS + " 이 발견되지 않았습니다.");
//            throw new RuntimeException(getEnum(Msg.RM_ERR, THIS));

        // 파일 문서 삭제 시 파생 된 데이터도 일괄 삭제
        repo.delete(target);
        return target.toRes();
    }

    /**
     * 데이터 반려(거부). collectYn(data_yn) 을 "X" 로 변경하여 해당 데이터는 승인할 수 없게 만듦.
     * @param id 파일 문서 pk
     * @return collectYn == "X"
     */
    @Transactional
    public UploadDataDto reject(Long id) {
//        1) 최고 관리자만 가능한 기능. 권한 유효검사
        String userNm = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userRepo.findByUserId(userNm).orElseThrow(() -> new EntityNotFoundException(Msg.NO_ACCESS.getMsg()));

        if (!user.getRole().getEncodedNm().contains(RoleCd.ROLE_1ST.getRole()))
            throw new AccessDeniedException(Msg.NO_ACCESS.getMsg());

//        2) 대상 파일 타겟팅, 상태 확인
        PFData doc = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        if (!doc.getCollectYn().equals("N")) throw new IllegalArgumentException(Msg.CANNOT_UPDATE.getMsg());

//        3) res
//        [240416]이 컬럼은 nn인데 null 을 넣기 위해 alter 문을 사용해야 하면 이관 시 작업이 까다로워지므로 "X" 로 대체합니다
        doc.updateCollectYn("X");
        return doc.toRes();
    }

}
