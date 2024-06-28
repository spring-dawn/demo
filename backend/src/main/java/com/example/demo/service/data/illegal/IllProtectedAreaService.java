package com.example.demo.service.data.illegal;

import com.example.demo.domain.data.illegal.repo.IllMobileRepository;
import com.example.demo.domain.data.illegal.repo.IllProtectedAreaRepository;
import com.example.demo.dto.data.illegal.IllFireplugDto;
import com.example.demo.dto.data.illegal.IllProtectedAreaDto;
import com.example.demo.dto.system.CodeDto;
import com.example.demo.service.api.fh.FireHydrantApiService;
import com.example.demo.service.api.pa.ProtectedAreaApiService;
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
public class IllProtectedAreaService {
    private final IllProtectedAreaRepository repo;
    private final ProtectedAreaApiService protectedAreaApiService;
    private final CodeService codeService;

    public List<HashMap> search(IllProtectedAreaDto.Keyword req) {
        HashMap<String, Object> objectHashMap = protectedAreaApiService.getData();

        ArrayList<HashMap> arrayList = (ArrayList<HashMap>) objectHashMap.get("items");

        if (req.getSggCd() == null) {
            return arrayList;
        } else {
            CodeDto codeDto = codeService.selectCodeByName(req.getSggCd());

            List<HashMap> filterList = arrayList.stream().filter(map -> map.get("SIGUN_NM") != null && map.get("SIGUN_NM").equals(codeDto.getValue()))
                    .collect(Collectors.toList());

            return filterList;
        }
    }
}
