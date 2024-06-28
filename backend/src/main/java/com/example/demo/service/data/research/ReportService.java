package com.example.demo.service.data.research;

import com.example.demo.atech.FileManager;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.common.file.FileInfoRepository;
import com.example.demo.domain.data.research.report.Report;
import com.example.demo.domain.data.research.report.ReportRepository;
import com.example.demo.domain.system.code.Code;
import com.example.demo.domain.system.code.CodeRepository;
import com.example.demo.dto.data.research.ReportDto;
import com.example.demo.service.common.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportRepository reportRepository;
    private final FileInfoRepository fileInfoRepo;
    private final FileService fileService;
    private final FileManager fileManager;
    private final CodeRepository codeRepository;

    // 보고서 가져오기
    public List<ReportDto.ReportRes> getReportAll() {
        List<Report> reportList = reportRepository.findAll();

        return reportList.stream().map(Report::toReportRes).collect(Collectors.toList());
    }

    // 보고서 저장
    @Transactional
    public ReportDto.ReportRes insertReport(ReportDto.ReportReq reportReq) {
        List<MultipartFile> files = reportReq.getFiles();
        Report report = null;

        // row 데이터 저장
        Report toEntity = reportReq.toEntity();
        Optional<Code> codeOptional = codeRepository.findByName(String.valueOf(reportReq.getRegCode()));
        if (codeOptional.isPresent()) toEntity.updateRegCode(codeOptional.get());
        report = reportRepository.save(toEntity);
        log.info("--- 1) 보고서 정보 저장 완료");

//        3) 첨부파일 있으면 같이 업로드
        if (files != null && !files.isEmpty()) {
            List<FileManager.Res> detailList = fileManager.saveFiles(files);
            List<FileInfo> attaches = new ArrayList<>();

            for (FileManager.Res detail : detailList) {
                attaches.add(
                        fileInfoRepo.save(FileInfo.builder()
                                .fileNm(detail.getOriginNm())
                                .fileNmStored(detail.getSavedNm())
                                .filePath(detail.getPath())
                                .rschReport(report)
                                .build()
                        )
                );
            }

//            4) 문서에 파일 연결. 트랜잭션(영속성 컨텍스트) 안에서 dirty check 는 save 순서에 상관없이 적용.
            report.addAttaches(attaches);
        }

        return report.toReportRes();
    }

    @Transactional
    public ReportDto.ReportRes update(ReportDto.ReportReq req) {
        Optional<Report> optional = reportRepository.findById(req.getReportNo());

        if (optional.isPresent()) {
            Report report = optional.get();

            report.update(req);

            Report save = reportRepository.saveAndFlush(report);

            return save.toReportRes();
        }

        return null;
    }

    @Transactional
    public void delete(Long id) {
        Optional<Report> reportOptional = reportRepository.findById(id);

        if (reportOptional.isPresent()) {
            Report report = reportOptional.get();
            List<FileInfo> attaches = report.getAttaches();

            // DB 데이터 삭제
            reportRepository.deleteById(report.getReportNo());

            // 파일 삭제
            for (FileInfo attach : attaches) {
                String filePath = attach.getFilePath();
                String fileName = attach.getFileNmStored();

                // 파일 DB 데이터 삭제
                fileManager.rmFile(filePath, fileName);
            }
            log.info("---1 파일 삭제 완료:");
        }

    }
}
