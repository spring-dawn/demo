package com.example.demo.service.data.research;

import com.example.demo.domain.data.research.shp.ShpResult;
import com.example.demo.domain.data.research.shp.ShpResultRepository;
import com.example.demo.domain.system.code.Code;
import com.example.demo.domain.system.code.CodeRepository;
import com.example.demo.dto.data.research.ShpResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShpResultService {
    private final CodeRepository codeRepository;
    private final ShpResultRepository shpResultRepository;

    @Transactional
    public ShpResult save(ShpResultDto.ShpResultReq resultReq) {
        ShpResult toEntity = resultReq.toEntity();

        // +) SHP 정보 디비에 저장
        Optional<Code> codeOptional = codeRepository.findByName(resultReq.getRegCode());
        if (codeOptional.isPresent()) {
            toEntity.updateRegCode(codeOptional.get());
        }

        return shpResultRepository.saveAndFlush(toEntity);
    }
}
