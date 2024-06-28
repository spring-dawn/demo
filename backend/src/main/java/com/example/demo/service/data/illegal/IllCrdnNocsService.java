package com.example.demo.service.data.illegal;

import com.example.demo.atech.enums.SggCd;
import com.example.demo.domain.data.illegal.IllCrdnNocs;
import com.example.demo.domain.data.illegal.repo.IllCrdnNocsRepository;
import com.example.demo.dto.data.illegal.IllCrdnNocsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IllCrdnNocsService {
    private final IllCrdnNocsRepository repo;

    public List<IllCrdnNocsDto> selectList() {
        // tmp
        List<IllCrdnNocs> list = new ArrayList<>();
        SggCd[] sggCds = {SggCd.JUNG, SggCd.BUK, SggCd.DONG, SggCd.NAM, SggCd.ULJU};
        for (SggCd cd : sggCds) {
            String cdStr = cd.toString();
            IllCrdnNocs inst = repo.findFirstBySggOrderByCreateDtmDesc(cdStr).orElse(null);

            if (inst == null) continue;
            String year = inst.getYear();
            String month = inst.getMonth();
            list.addAll(repo.findByYearAndMonthAndSgg(year, month, cdStr));
        }
        return list.stream().map(IllCrdnNocs::toRes).collect(Collectors.toList());
    }
}
