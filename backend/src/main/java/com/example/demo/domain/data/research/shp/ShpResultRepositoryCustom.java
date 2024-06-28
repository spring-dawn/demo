package com.example.demo.domain.data.research.shp;

import com.example.demo.domain.common.file.FileInfoRepository;
import com.example.demo.dto.data.research.ShpResultDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class ShpResultRepositoryCustom {
    /*
    querydsl 쿼리
     */
    private final FileInfoRepository fileInfoRepo;
    private final JPAQueryFactory factory;
    QShpResult shpResult = QShpResult.shpResult;

    // SHP RESULT 검색
    public List<ShpResultDto.ShpResultRes> searchShp(ShpResultDto.ShpSearchReq req) {
        List<ShpResult> resultList = factory
                .selectFrom(shpResult)
                .where(
                        containName(req.getName()),
                        eqYear(req.getYear()),
                        eqReg(req.getReg()),
                        eqViewYn(req.getViewYn()),
                        eqState(req.getState())
                )
                .orderBy(shpResult.resultNo.desc())
                .fetch();

        List<ShpResultDto.ShpResultRes> shpResultRes = resultList.stream()
                .map(ShpResult::toShpResultRes)
                .collect(Collectors.toList());

        return shpResultRes;
    }

    /*
    exprressions 분리
     */

    private BooleanExpression containName(String name) {
        return hasText(name) ? shpResult.name.contains(name) : null;
    }

    private BooleanExpression eqYear(String year) {
        return hasText(year) ? shpResult.year.eq(year) : null;
    }

    private BooleanExpression eqReg(String reg) {
        return hasText(reg) ? shpResult.regCode.eq(reg) : null;
    }

    private BooleanExpression eqViewYn(String viewYn) {
        return hasText(viewYn) ? shpResult.viewYn.eq(viewYn) : null;
    }

    private BooleanExpression eqState(String state) {
        return hasText(state) ? shpResult.state.eq(Integer.valueOf(state)) : null;
    }
}


