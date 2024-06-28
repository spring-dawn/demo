package com.example.demo.service.data.research;

import com.example.demo.atech.FileManager;
import com.example.demo.domain.common.file.FileInfo;
import com.example.demo.domain.common.file.FileInfoRepository;
import com.example.demo.domain.data.research.floorPlan.FloorPlan;
import com.example.demo.domain.data.research.floorPlan.FloorPlanRepository;
import com.example.demo.domain.data.research.report.Report;
import com.example.demo.domain.data.research.report.ReportRepository;
import com.example.demo.domain.system.code.Code;
import com.example.demo.domain.system.code.CodeRepository;
import com.example.demo.dto.data.research.FloorPlanDto;
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
public class FloorPlanService {
    private final FloorPlanRepository floorPlanRepository;
    private final FileInfoRepository fileInfoRepo;
    private final FileService fileService;
    private final FileManager fileManager;
    private final CodeRepository codeRepository;

    // 도면 가져오기
    public List<FloorPlanDto.FloorPlanRes> getList() {
        List<FloorPlan> floorPlans = floorPlanRepository.findAll();

        return floorPlans.stream().map(FloorPlan::toFloorPlanRes).collect(Collectors.toList());
    }

    // 도면 저장
    @Transactional
    public FloorPlanDto.FloorPlanRes insert(FloorPlanDto.FloorPlanReq planReq) {
        List<MultipartFile> files = planReq.getFiles();
        FloorPlan floorPlan = null;

        // row 데이터 저장
        FloorPlan toEntity = planReq.toEntity();
        Optional<Code> codeOptional = codeRepository.findByName(String.valueOf(planReq.getRegCode()));
        if (codeOptional.isPresent()) toEntity.updateRegCode(codeOptional.get());
        floorPlan = floorPlanRepository.save(toEntity);
        log.info("--- 1) 도면 정보 저장 완료");

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
                                .rschFp(floorPlan)
                                .build()
                        )
                );
            }

//            4) 문서에 파일 연결. 트랜잭션(영속성 컨텍스트) 안에서 dirty check 는 save 순서에 상관없이 적용.
            floorPlan.addAttaches(attaches);
        }

        return floorPlan.toFloorPlanRes();
    }

    // 도면 수정
    @Transactional
    public FloorPlanDto.FloorPlanRes update(FloorPlanDto.FloorPlanReq req) {
        Optional<FloorPlan> optional = floorPlanRepository.findById(req.getFpNo());

        if (optional.isPresent()) {
            FloorPlan floorPlan = optional.get();

            floorPlan.update(req);

            FloorPlan save = floorPlanRepository.saveAndFlush(floorPlan);

            return save.toFloorPlanRes();
        }

        return null;
    }

    // 도면 삭제
    @Transactional
    public void delete(Long id) {
        Optional<FloorPlan> floorPlanOptional = floorPlanRepository.findById(id);

        if (floorPlanOptional.isPresent()) {
            FloorPlan floorPlan = floorPlanOptional.get();
            List<FileInfo> attaches = floorPlan.getAttaches();

            // DB 데이터 삭제
            floorPlanRepository.deleteById(floorPlan.getFpNo());

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
