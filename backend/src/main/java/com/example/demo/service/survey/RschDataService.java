package com.example.demo.service.survey;

import com.example.demo.atech.FileManager;
import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.common.file.FileInfoRepository;
import com.example.demo.domain.survey.data.RschData;
import com.example.demo.domain.survey.data.RschDataRepository;
import com.example.demo.dto.data.UploadDataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static com.example.demo.atech.MyUtil.logErr;
import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RschDataService {
    private final String THIS = "실태조사 관리카드";

    private final FileManager fm;
    private final RschDataRepository repo;
    private final FileInfoRepository fileRepo;
    private final RschMngCardService mngCardService;
    private final RschFormatService fmService;

    // 프론트 구성에선 search 를 페이지에서 공용으로 쓰고 있어 selectList 는 사실상 호출되지 않고 있음.
    public UploadDataDto selectOne(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)))
                .toRes();
    }

    @Transactional
    public UploadDataDto createOne(UploadDataDto.Req req) {
//        1) 유효검사
        if (!hasText(req.getYear()) || !hasText(req.getSggCd()) || !hasText(req.getRschType())
                || req.getFiles().isEmpty())
            throw new NullPointerException(Msg.NPE.getMsg());
        // 파일 데이터
        if (req.getFiles().size() > 1) throw new IllegalArgumentException("1개 파일만 첨부해주세요.");
        MultipartFile file = req.getFiles().get(0);

//        2) 문서 우선 생성
        RschData data;
        try {
            data = repo.save(
                    RschData.builder()
                            .year(req.getYear())
                            .sggCd(req.getSggCd())
                            .dataNm(req.getDataNm())
                            .rschType(req.getRschType())
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
                            .rschData(data)
                            .build()
                    )
            );
//        4) 문서에 파일 연결. 트랜잭션(영속성 컨텍스트) 안에서 dirty check 는 save 순서에 상관없이 적용.
            data.addAttaches(attaches);
        } catch (Exception e) {
            logErr(e);
            throw new IllegalArgumentException(Msg.INSERT_ERR.getMsg());
        }
        return data.toRes();
    }

    @Transactional
    public UploadDataDto update(UploadDataDto.Req req) {
        RschData target = repo.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

        try {
            target.update(req);
        } catch (Exception e) {
            logErr(e);
            throw new RuntimeException(Msg.UPDATE_ERR.getMsg());
        }
        return target.toRes();
    }

    @Transactional
    public UploadDataDto deleteOne(Long id) {
        RschData target = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

        // 실제 파일 삭제. 실태조사 관리카드는 1개씩만 첨부하도록 제한.
        if (!target.getAttaches().isEmpty()) {
            FileInfo info = target.getAttaches().get(0);
            if (!fm.rmFile(info.getFilePath(), info.getFileNmStored())) log.error("실태조사 파일이 없습니다.");
//                throw new RuntimeException(MyUtil.getEnum(Msg.RM_ERR, THIS));
        }

        try {
            repo.delete(target);
        } catch (Exception e) {
            logErr(e);
            throw new RuntimeException(Msg.DELETE_ERR.getMsg());
        }
        return target.toRes();
    }


    /**
     * 실태조사 관리카드 엑셀 첨부파일 확인, DB 적재
     *
     * @param id 파일 업로드 pk
     * @return 수집 결과
     */
    @Transactional
    public UploadDataDto collectData(Long id) {
//        1) 타겟팅
        RschData doc = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));

//        2) 데이터 DB화. 트랜잭션 체크를 위해 데이터 승인 여부는 같은 로직에서 처리.
        try {
            if (doc.getRschType().equals("0")) {
                mngCardService.insert(doc);
            } else {
                fmService.insert(doc);
            }
        } catch (Exception e) {
            logErr(e);
            throw new RuntimeException(Msg.COLLECT_DATA_ERR.getMsg());
        }
        return doc.toRes();
    }

}
