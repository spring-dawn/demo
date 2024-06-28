package com.example.demo.service.data.illegal;

import com.example.demo.domain.data.illegal.IllFireplug;
import com.example.demo.domain.data.illegal.IllMobile;
import com.example.demo.domain.data.illegal.repo.IllFireplugRepository;
import com.example.demo.domain.data.illegal.repo.IllMobileRepository;
import com.example.demo.dto.GisDto;
import com.example.demo.dto.data.illegal.IllFireplugDto;
import com.example.demo.dto.data.illegal.IllMobileDto;
import com.example.demo.dto.system.CodeDto;
import com.example.demo.service.api.fh.FireHydrantApiService;
import com.example.demo.service.system.CodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class IllFilreplugService {
    private final IllFireplugRepository repo;
    private final FireHydrantApiService fireHydrantApiService;
    private final CodeService codeService;

    // 소화전 임시로 API 데이터 반환
    public List<HashMap> search(IllFireplugDto.Keyword req) {
        HashMap<String, Object> objectHashMap = fireHydrantApiService.getData();

        ArrayList<HashMap> arrayList = (ArrayList<HashMap>) objectHashMap.get("data");

        if (req.getSggCd() == null) {
            return arrayList;
        } else {
            CodeDto codeDto = codeService.selectCodeByName(req.getSggCd());

            List<HashMap> filterList = arrayList.stream().filter(map -> map.get("구군명") != null && map.get("구군명").equals(codeDto.getValue()))
                    .collect(Collectors.toList());

            return filterList;
        }
    }
}
