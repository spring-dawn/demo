package com.example.demo.domain.data.illegal.repCustom;

import com.example.demo.domain.data.illegal.IllFixed;
import com.example.demo.domain.data.illegal.QIllFixed;
import com.example.demo.dto.data.illegal.IllFixedDto;
import com.example.demo.dto.data.monthlyReport.PResiDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class IllFixedRepoCustom {
    private final JPAQueryFactory factory;
    QIllFixed fixed = QIllFixed.illFixed;

    public List<IllFixedDto> search(IllFixedDto.Keyword req) {
        return factory
                .selectFrom(fixed)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                        , eqGugun(req.getGugun())
                )
                .fetch()
                .stream().map(IllFixed::toRes)
                .sorted(Comparator.comparing(IllFixedDto::getInstlYmd).reversed())
                .collect(Collectors.toList());
    }

    /*
   exprressions 분리
    */
    private BooleanExpression eqYear(String year) {
        if (year == null) {
            return null;
        }
        else
            return fixed.year.eq(year);
    }

    private BooleanExpression eqMonth(String month) {
        if (month == null) {
            return null;
        }
        else
            return fixed.month.eq(month);
    }

    private BooleanExpression eqGugun(String gugun) {
        if (gugun == null) {
            return null;
        }
        else
            return fixed.sgg.eq(gugun);
    }
}
