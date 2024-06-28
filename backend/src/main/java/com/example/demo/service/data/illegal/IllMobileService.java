package com.example.demo.service.data.illegal;

import com.example.demo.domain.data.illegal.IllFixed;
import com.example.demo.domain.data.illegal.IllMobile;
import com.example.demo.domain.data.illegal.repo.IllMobileRepository;
import com.example.demo.dto.data.illegal.IllFixedDto;
import com.example.demo.dto.data.illegal.IllMobileDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IllMobileService {
    private final String THIS = "불법주정차단속 고정형";
    private final IllMobileRepository repo;

    // 복합키. insert == update
    public List<IllMobileDto> selectList() {
        return repo.findAll().stream().map(IllMobile::toRes).collect(Collectors.toList());
    }

    /*
    proc
     */
    @Transactional
    public IllMobileDto insert(IllMobileDto req) {
        return repo.save(
                IllMobile.builder()
                        .seq(req.getSeq())
                        .year(req.getYear())
                        .month(req.getMonth())
                        .sgg(req.getSgg())
                        .vhclNm(req.getVhclNm())
                        .prchsYmd(req.getPrchsYmd())
                        .crdnPrd(req.getCrdnPrd())
                        .crdnCtrM(req.getCrdnCtrM())
                        .crdnNocs(req.getCrdnNocs())
                        .rmrk(req.getRmrk())
                        .levyAmt(req.getLevyAmt())
                        .clctnNocs(req.getClctnNocs())
                        .clctnAmt(req.getClctnAmt())
                        .build()
        ).toRes();

    }
/*
    @Transactional
    public IllFixedDto delete(IllFixedDto.Keyword req) {

    }

*/

}
