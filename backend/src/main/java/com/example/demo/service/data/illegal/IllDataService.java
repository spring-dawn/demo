package com.example.demo.service.data.illegal;

import com.example.demo.atech.FileManager;
import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.atech.enums.RoleCd;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.common.file.FileInfoRepository;
import com.example.demo.domain.data.facility.file.PFData;
import com.example.demo.domain.data.illegal.file.IllData;
import com.example.demo.domain.data.illegal.file.IllDataRepository;
import com.example.demo.domain.system.user.User;
import com.example.demo.domain.system.user.UserRepository;
import com.example.demo.dto.data.UploadDataDto;
import com.example.demo.dto.data.illegal.IllDataDto;
import com.example.demo.dto.system.CodeDto;
import com.example.demo.service.data.illegalMng.IllegalService;
import com.example.demo.service.system.CodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.atech.ExcelManager.format;
import static com.example.demo.atech.MyUtil.getEnum;
import static com.example.demo.atech.MyUtil.makeStandardFileNm4TmpIllegal;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class IllDataService {
    private final String THIS = "불법주정차 단속 엑셀"; // 이름 바뀔 수 있음

    private final IllDataRepository repo;
    private final FileManager fm;
    private final FileInfoRepository fileRepo;
    private final UserRepository userRepo;

    private final IllService illService;
    private final IllegalTmpService tmpService;


    @Transactional
    public IllDataDto createOne(IllDataDto.Req req) {
//        1) 유효검사
        if (!hasText(req.getYear()) || !hasText(req.getMonth())
                || req.getFiles().isEmpty())
            throw new NullPointerException(Msg.NPE.getMsg());
        if (req.getFiles().size() > 1) throw new IllegalArgumentException("1개 파일만 첨부해주세요.");

        // [240402] 임시 제목짓기
        MultipartFile file = req.getFiles().get(0);
        String attachNm = file.getOriginalFilename();

        String correctNm = makeStandardFileNm4TmpIllegal(req.getYear(), req.getMonth(), req.getSggCd());
        if (!hasText(attachNm) || !attachNm.equals(correctNm + format))
            throw new IllegalArgumentException(getEnum(Msg.NOT_STANDARD_NM, correctNm + format));

//        2) 문서 우선 생성
        IllData data = repo.save(
                IllData.builder()
                        .year(req.getYear())
                        .month(req.getMonth())
                        .sggCd(req.getSggCd())
                        .dataType(req.getDataType())
                        .dataNm(correctNm)
                        .comment(req.getComment())
                        .collectYn("N")
                        .build()
        );

//        3) 첨부파일 업로드
        FileManager.Res detail = fm.saveFile(file);
        List<FileInfo> attaches = new ArrayList<>();

        attaches.add(
                fileRepo.save(FileInfo.builder()
                        .fileNm(detail.getOriginNm())
                        .fileNmStored(detail.getSavedNm())
                        .filePath(detail.getPath())
                        .illData(data)
                        .build()
                )
        );
        data.addAttaches(attaches);
        //
        return data.toRes();
    }

    @Transactional
    public IllDataDto updateOne(IllDataDto.Req req) {
        IllData target = repo.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        target.update(req);

        return target.toRes();
    }


    /**
     * 불법주정차 단속(주정차과태료 적발대장) 데이터 승인
     *
     * @param id 첨부파일 문서 pk
     * @return DB화 결과 반영된 dto
     */
    @Transactional
    public IllDataDto collectData(Long id) {
//        1) 타겟팅
        IllData doc = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

//        2) DB화 // [240402] 5월 이후까지 땜빵할 임시 불법주정차 단속실적 엑셀 읽기 기능.
        if(!doc.getDataType().equals("1")) throw new IllegalArgumentException(Msg.NOT_SUPPORT_TYPE.getMsg());

        tmpService.insert(doc);
//        illService.insert(doc);
        return doc.toRes();
    }


    @Transactional
    public IllDataDto deleteOne(Long id) {
        IllData target = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
//        if(target.getCollectYn().equals("Y")) throw new DataIntegrityViolationException(Msg.CANNOT_DELETE.getMsg());

        // 실제 파일 삭제. 월간보고는 1개씩만 첨부하도록 제한.
        if (target.getAttaches().isEmpty()) throw new NullPointerException(Msg.NO_FILES.getMsg());
        FileInfo info = target.getAttaches().get(0);
        if (!fm.rmFile(info.getFilePath(), info.getFileNmStored()))
//            throw new RuntimeException(getEnum(Msg.RM_ERR, THIS));
            log.error(getEnum(Msg.RM_ERR, THIS));

        // 파일 문서(원본파일) 삭제 시 그에 파생된 원천 데이터(raw)도 일괄 삭제
        repo.delete(target);
        return target.toRes();
    }

    @Transactional
    public IllDataDto reject(Long id) {
//        1) 최고 관리자만 가능한 기능. 권한 유효검사
        String userNm = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        User user = userRepo.findByUserId(userNm).orElseThrow(() -> new EntityNotFoundException(Msg.NO_ACCESS.getMsg()));

        if (!user.getRole().getEncodedNm().contains(RoleCd.ROLE_1ST.getRole()))
            throw new AccessDeniedException(Msg.NO_ACCESS.getMsg());

//        2) 대상 파일 타겟팅, 상태 확인
        IllData doc = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        if (!doc.getCollectYn().equals("N")) throw new IllegalArgumentException(Msg.CANNOT_UPDATE.getMsg());

//        3) res
//        [240416]이 컬럼은 nn인데 null 을 넣기 위해 alter 문을 사용해야 하면 이관 시 작업이 까다로워지므로 "X" 로 대체합니다
        doc.updateCollectYn("X");
        return doc.toRes();
    }

}
