package com.example.demo.domain.api.building_management;

import com.example.demo.dto.api.BuildingManagementDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class BuildingManagementRepoCustom {
    private final JPAQueryFactory factory;
    QBuildingManagement bm = QBuildingManagement.buildingManagement;

    @Cacheable(value = "pfSubDefault", condition = "#req.year == null && #req.month == null && #req.sggCd == null")
    public List<BuildingManagementDto.BuildingManagementRes> search(BuildingManagementDto.BuildingManagementReq req) {
        return
                factory
                        .selectFrom(bm)
                        .where(
                                eqYear(req.getYear())
                                , eqMonth(req.getMonth())
                                , eqSggCd(req.getSggCd())
                                , sumIsNotZero()
                        )
                        .orderBy(bm.bmNo.asc(), bm.sigunguCd.asc())
                        .fetch()
                        .stream().map(BuildingManagement::toRes)
                        .collect(Collectors.toList());
    }

    private BooleanExpression eqYear(String year) {
        return hasText(year) ? bm.crtnDay.substring(0, 4).eq(year) : null;
    }

    private BooleanExpression eqMonth(String month) {
        return hasText(month) ? bm.crtnDay.substring(4, 6).eq(month) : null;
    }

    //
    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? bm.sigunguCd.eq(Integer.valueOf(sggCd)) : null;
    }

    private BooleanExpression sumIsNotZero() {
        return bm.indrAutoUtcnt.castToNum(Integer.class)
                .add(bm.indrMechUtcnt.castToNum(Integer.class))
                .add(bm.oudrAutoUtcnt.castToNum(Integer.class))
                .add(bm.oudrMechUtcnt.castToNum(Integer.class))
                .ne(0); // not equal
    }

}
