//package com.example.demo.backup.rschDoc;
//
//import com.example.demo.atech.FileManager;
//import com.example.demo.atech.Msg;
//import com.example.demo.atech.MyUtil;
//import com.example.demo.domain.common.file.FileInfo;
//import com.example.demo.domain.common.file.FileInfoRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.persistence.EntityNotFoundException;
//import java.util.List;
//import java.util.stream.Collectors;
//
//import static org.springframework.util.StringUtils.hasText;
//
//@Slf4j
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//public class RschDocService {
//    private final String THIS = "실태조사 관리카드";
//
//    @Value("${spring.servlet.multipart2.tmp}")
//    private String FILE_DIR;
//
//    private final RschDocRepository repo;
//    private final FileInfoRepository fileRepo;
//
//
//    public List<RschDocDto> selectList() {
//        return repo.findAll().stream().map(RschDoc::toRes).collect(Collectors.toList());
//    }
//
//
////    @Transactional
////    public RschDocDto createOne(RschDocDto.Req req, List<MultipartFile> files) {
////        if (!hasText(req.getYear()) || !hasText(req.getMonth()) || !hasText(req.getSggNm()))
////            throw new NullPointerException(Msg.NPE.getMsg());
////
////        // doc 우선 저장
////        RschDoc doc = repo.save(RschDoc.builder()
////                .year(req.getYear())
////                .month(req.getMonth())
////                .sggNm(req.getSggNm())
////                .title(req.getTitle())
////                .content(req.getContent())
////                .comment(req.getComment())
////                .build()
////        );
////
////        // 파일이 업로드되었는지 확인하고 저장
////        if (files != null && !files.isEmpty()) {
////            if (files.size() > 1) throw new RuntimeException("실태조사 관리카드 파일은 1개씩 업로드해주세요.");
////
////            MultipartFile file = files.get(0);
////            String originNm = file.getOriginalFilename();
////            String uuid = UUID.randomUUID().toString();
////            String ext = MyUtil.getFileNmOrExt(originNm, false);
////            String savedNm = uuid + ext;
////
////            try {
////                List<FileInfo> list = new ArrayList();
////                file.transferTo(new File(FILE_DIR + savedNm));
////
////                // 엔티티 생성, 문서에 연결
////                FileInfo fileInfo = FileInfo.builder()
////                        .filePath(FILE_DIR)
////                        .fileNm(originNm)
////                        .fileNmStored(savedNm)
//////                        .rschDoc(doc)
////                        .build();
////
////                // 문서에 첨부 파일 추가
////                list.add(fileRepo.save(fileInfo));
//////                doc.addAttach(list);
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
////        }
////
////        return doc.toRes();
////    }
//
//
//    @Transactional
//    public RschDocDto createOne(RschDocDto.Req req, List<MultipartFile> files) {
//        if (!hasText(req.getYear()) || !hasText(req.getMonth()) || !hasText(req.getSggNm()))
//            throw new NullPointerException(Msg.NPE.getMsg());
//
//        // doc 우선 저장
//        RschDoc doc = repo.save(RschDoc.builder()
//                .year(req.getYear())
//                .month(req.getMonth())
//                .sggNm(req.getSggNm())
//                .title(req.getTitle())
//                .content(req.getContent())
//                .comment(req.getComment())
//                .build()
//        );
//
//        // 파일이 업로드되었는지 확인하고 저장
//        if (files != null && !files.isEmpty()) {
//            if (files.size() > 1) throw new RuntimeException("실태조사 관리카드 파일은 1개씩 업로드해주세요.");
//            MultipartFile attach = files.get(0);
//            FileManager.Res res = FileManager.saveFile(attach);
//
//            // FileInfo 생성, 저장.
//            fileRepo.save(FileInfo.builder()
//                            .filePath(res.getPath())
//                            .fileNm(res.getOriginNm())
//                            .fileNmStored(res.getSavedNm())
//                    .build());
//        }
//
//        return doc.toRes();
//    }
//
//
//    @Transactional
//    public RschDocDto deleteOne(Long id) {
//        RschDoc doc = repo.findById(id)
//                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
//        repo.delete(doc);
//        // TODO: 문서 삭제 후 실제 파일도 삭제
//
//
//        return doc.toRes();
//    }
//
//}
