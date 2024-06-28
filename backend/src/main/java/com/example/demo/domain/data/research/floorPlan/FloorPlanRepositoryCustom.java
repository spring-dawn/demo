package com.example.demo.domain.data.research.floorPlan;

import com.example.demo.domain.common.file.FileInfoRepository;
import com.example.demo.dto.data.research.FloorPlanDto;
import com.example.demo.dto.data.research.ReportDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class FloorPlanRepositoryCustom {
    /*
    querydsl 쿼리
     */
    private final FileInfoRepository fileInfoRepo;
    private final JPAQueryFactory factory;
    QFloorPlan floorPlan = QFloorPlan.floorPlan;

    // 보고서 검색
    public List<FloorPlanDto.FloorPlanRes> searchFloorPlan(FloorPlanDto.FloorPlanSearchReq req) {
        List<FloorPlan> resultList = factory
                .selectFrom(floorPlan)
                .where(
                        containName(req.getName()),
                        eqYear(req.getYear()),
                        eqReg(req.getRegCode())
                )
                .orderBy(floorPlan.createDtm.desc())
                .fetch();

        return resultList.stream().map(FloorPlan::toFloorPlanRes).collect(Collectors.toList());
    }

    /*
    exprressions 분리
     */

    private BooleanExpression containName(String name) {
        return hasText(name) ? floorPlan.name.contains(name) : null;
    }

    private BooleanExpression eqYear(String year) {
        return hasText(year) ? floorPlan.year.eq(year) : null;
    }

    private BooleanExpression eqReg(String reg) {
        return hasText(reg) ? floorPlan.regName.eq(reg) : null;
    }
}


