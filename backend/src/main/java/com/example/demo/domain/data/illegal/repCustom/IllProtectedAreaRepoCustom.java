package com.example.demo.domain.data.illegal.repCustom;

import com.example.demo.domain.data.illegal.*;
import com.example.demo.dto.data.illegal.IllCrdnPrfmncDto;
import com.example.demo.dto.data.illegal.IllFireplugDto;
import com.example.demo.dto.data.illegal.IllKeywordDto;
import com.example.demo.dto.data.illegal.IllProtectedAreaDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class IllProtectedAreaRepoCustom {
    private final JPAQueryFactory factory;
    QIllProtectedArea illProtectedArea = QIllProtectedArea.illProtectedArea;

    public List<IllProtectedAreaDto> search(IllKeywordDto req) {
        if (req.getMonth()==null||req.getMonth().equals("")) {
            IllProtectedArea lastData = factory
                    .selectFrom(illProtectedArea)
                    .where(
                            eqYear(req.getYear())
                            , eqMonth(req.getMonth())
                            , eqGugun(req.getSggCd())
                    )
                    .orderBy(
                            illProtectedArea.year.castToNum(Integer.class).desc(),
                            illProtectedArea.month.castToNum(Integer.class).desc()
                    )
                    .limit(1)
                    .fetchOne();

            return factory
                    .selectFrom(illProtectedArea)
                    .where(
                            eqYear(lastData.getYear())
                            , eqMonth(lastData.getMonth())
                            , eqGugun(req.getSggCd())
                    )
                    .fetch()
                    .stream().map(IllProtectedArea::toRes)
                    .sorted(Comparator.comparing(IllProtectedAreaDto::getYear).reversed())
                    .collect(Collectors.toList());
        } else {
            return factory
                    .selectFrom(illProtectedArea)
                    .where(
                            eqYear(req.getYear())
                            , eqMonth(req.getMonth())
                            , eqGugun(req.getSggCd())
                    )
                    .fetch()
                    .stream().map(IllProtectedArea::toRes)
                    .sorted(Comparator.comparing(IllProtectedAreaDto::getYear).reversed())
                    .collect(Collectors.toList());
        }
    }

    /*
   exprressions 분리
    */
    private BooleanExpression eqYear(String year) {
        if (year == null) {
            return null;
        }
        else
            return illProtectedArea.year.eq(year);
    }

    private BooleanExpression eqMonth(String month) {
        if (month == null) {
            return null;
        }
        else
            return illProtectedArea.month.eq(month);
    }

    private BooleanExpression eqGugun(String gugun) {
        if (gugun == null) {
            return null;
        }
        else
            return illProtectedArea.sgg.eq(gugun);
    }
}
