package com.example.demo.service.data.monthlyReport;

import com.example.demo.atech.Msg;
import com.example.demo.atech.MyUtil;
import com.example.demo.domain.data.monthlyReport.PResi;
import com.example.demo.domain.data.monthlyReport.pk.PStatusPk;
import com.example.demo.domain.data.monthlyReport.repo.PResiRepository;
import com.example.demo.dto.data.monthlyReport.PResiDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PResiService {
    private final String THIS = "거주자우선 현황";
    private final PResiRepository repo;

    // 복합키. insert == update
    public List<PResiDto> selectList() {
        return repo.findAll().stream().map(PResi::toRes).collect(Collectors.toList());
    }

    /*
    proc
     */
    @Transactional
    public PResiDto insert(PResiDto req) {
        return
                repo.save(
                        PResi.builder()
                                .year(req.getYear())
                                .month(req.getMonth())
                                .sggCd(req.getSggCd())
                                .prevSpaces(req.getPrevSpaces())
                                .newSpaces(req.getNewSpaces())
                                .lostSpaces(req.getLostSpaces())
                                .reSpaces(req.getReSpaces())
                                .variance(req.getNewSpaces() + req.getLostSpaces() + req.getReSpaces())
                                .thisSpaces(req.getThisSpaces())
                                .thisArea(req.getThisArea())
                                .varianceReason(req.getVarianceReason())
                                .nonUse(req.getNonUse())
                                .inUse(req.getThisSpaces() - req.getNonUse())
                                .build()
                ).toRes();

    }

    @Transactional
    public PResiDto delete(PResiDto.Keyword req) {
        PResi target = repo.findById(new PStatusPk(req.getYear(), req.getMonth(), req.getSggCd()))
                .orElseThrow(() -> new EntityNotFoundException(MyUtil.getEnum(Msg.ENTITY_NOT_FOUND, THIS)));
        repo.delete(target);
        return target.toRes();
    }


}
