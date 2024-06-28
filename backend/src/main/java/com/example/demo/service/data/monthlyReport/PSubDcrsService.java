package com.example.demo.service.data.monthlyReport;

import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.domain.data.monthlyReport.PSubDcrs;
import com.example.demo.domain.data.monthlyReport.repo.PSubDcrsRepository;
import com.example.demo.dto.data.monthlyReport.PSubDcrsDto;
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
public class PSubDcrsService {
    private final String THIS = "감소 현황";
    private final PSubDcrsRepository repo;

    public List<PSubDcrsDto> selectList() {
        return repo.findAll().stream().map(PSubDcrs::toRes)
                .sorted(Comparator.comparing(PSubDcrsDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }

    /*
    proc
     */
    @Transactional
    public PSubDcrsDto insert(PSubDcrsDto req) {
        return repo.save(
                PSubDcrs.builder()
                        .year(req.getYear())
                        .month(req.getMonth())
                        .sggCd(req.getSggCd())
                        .reportNo(req.getReportNo())
                        .location(req.getLocation())
                        .owner(req.getOwner())
                        .type(req.getType())
                        .spaces(req.getSpaces())
                        .totalArea(req.getTotalArea())
                        .demolitionDt(req.getDemolitionDt())
                        .demolitionReason(req.getDemolitionReason())
                        .structure(req.getStructure())
                        .buildUsage(req.getBuildUsage())
                        .build()
        ).toRes();
    }

    @Transactional
    public PSubDcrsDto update(PSubDcrsDto req) {
        PSubDcrs target = repo.findById(req.getId())
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        target.update(req);
        return target.toRes();
    }

    @Transactional
    public PSubDcrsDto delete(Long id) {
        PSubDcrs target = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        repo.delete(target);
        return target.toRes();
    }
}
