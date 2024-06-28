package com.example.demo.service.data.monthlyReport;

import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.domain.data.monthlyReport.PSubIncrs;
import com.example.demo.domain.data.monthlyReport.repo.PSubIncrsRepository;
import com.example.demo.dto.data.monthlyReport.PSubIncrsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PSubIncrsService {
    private final String THIS = "증가 현황";
    private final PSubIncrsRepository repo;

    public List<PSubIncrsDto> selectList() {
        return repo.findAll().stream().map(PSubIncrs::toRes)
                .sorted(Comparator.comparing(PSubIncrsDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }

    /*
    proc
     */
    @Transactional
    public PSubIncrsDto insert(PSubIncrsDto req) {
        return repo.save(
                PSubIncrs.builder()
                        .year(req.getYear())
                        .month(req.getMonth())
                        .sggCd(req.getSggCd())
                        .buildType(req.getBuildType())
                        .buildNm(req.getBuildNm())
                        .permitNo(req.getPermitNo())
                        .buildOwner(req.getBuildOwner())
                        .location(req.getLocation())
                        .approvalDt(req.getApprovalDt())
                        .mainUse(req.getMainUse())
                        .subUse(req.getSubUse())
                        .totalArea(req.getTotalArea())
                        .spaces(req.getSpaces())
                        .households(req.getHouseholds())
                        .generation(req.getGeneration())
                        .prmsnYmd(req.getPrmsnYmd())
                        .ttlFlarea(req.getTtlFlarea())
                        .bldHo(req.getBldHo())
                        .addPkspaceCnt(req.getAddPkspaceCnt())
                        .subau(req.getSubau())
                        .subse(req.getSubse())
                        .rmrk(req.getRmrk())
                        .build()
        ).toRes();
    }

    @Transactional
    public PSubIncrsDto update(PSubIncrsDto req) {
        PSubIncrs target = repo.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        target.update(req);
        return target.toRes();
    }

    @Transactional
    public PSubIncrsDto delete(Long id) {
        PSubIncrs target = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        repo.delete(target);
        return target.toRes();
    }

}
