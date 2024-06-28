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
public class IllCrdnPrfmncRepoCustom {
    private final JPAQueryFactory factory;
    QIllCrdnPrfmnc illCrdnPrfmnc = QIllCrdnPrfmnc.illCrdnPrfmnc;

    public List<IllCrdnPrfmncDto> search(IllKeywordDto req) {
        if (req.getMonth()==null||req.getMonth().equals("")) {
            IllCrdnPrfmnc lastData = factory
                    .selectFrom(illCrdnPrfmnc)
                    .where(
                            eqYear(req.getYear())
                            , eqMonth(req.getMonth())
                            , eqGugun(req.getSggCd())
                    )
                    .orderBy(
                            illCrdnPrfmnc.year.castToNum(Integer.class).desc(),
                            illCrdnPrfmnc.month.castToNum(Integer.class).desc()
                    )
                    .limit(1)
                    .fetchOne();

            return factory
                    .selectFrom(illCrdnPrfmnc)
                    .where(
                            eqYear(lastData.getYear())
                            , eqMonth(lastData.getMonth())
                            , eqGugun(req.getSggCd())
                    )
                    .fetch()
                    .stream().map(IllCrdnPrfmnc::toRes)
                    .sorted(Comparator.comparing(IllCrdnPrfmncDto::getYear).reversed())
                    .collect(Collectors.toList());
        } else {
            return factory
                    .selectFrom(illCrdnPrfmnc)
                    .where(
                            eqYear(req.getYear())
                            , eqMonth(req.getMonth())
                            , eqGugun(req.getSggCd())
                    )
                    .fetch()
                    .stream().map(IllCrdnPrfmnc::toRes)
                    .sorted(Comparator.comparing(IllCrdnPrfmncDto::getYear).reversed())
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
            return illCrdnPrfmnc.year.eq(year);
    }

    private BooleanExpression eqMonth(String month) {
        if (month == null) {
            return null;
        }
        else
            return illCrdnPrfmnc.month.eq(month);
    }

    private BooleanExpression eqGugun(String gugun) {
        if (gugun == null) {
            return null;
        }
        else
            return illCrdnPrfmnc.sgg.eq(gugun);
    }
}
