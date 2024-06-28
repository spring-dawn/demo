package com.example.demo.domain.data.illegal.repCustom;

import com.example.demo.domain.data.illegal.IllCrdnNocs;
import com.example.demo.domain.data.illegal.IllCrdnPrfmnc;
import com.example.demo.domain.data.illegal.QIllCrdnNocs;
import com.example.demo.domain.data.illegal.QIllCrdnPrfmnc;
import com.example.demo.dto.data.illegal.IllCrdnNocsDto;
import com.example.demo.dto.data.illegal.IllCrdnPrfmncDto;
import com.example.demo.dto.data.illegal.IllKeywordDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class IllNocsRepoCustom {
    private final JPAQueryFactory factory;
    QIllCrdnNocs illCrdnNocs = QIllCrdnNocs.illCrdnNocs;

    public List<IllCrdnNocsDto> search(IllKeywordDto req) {
        if (req.getMonth()==null||req.getMonth().equals("")) {
            IllCrdnNocs lastData = factory
                    .selectFrom(illCrdnNocs)
                    .where(
                            eqYear(req.getYear())
                            , eqMonth(req.getMonth())
                            , eqGugun(req.getSggCd())
                    )
                    .orderBy(
                            illCrdnNocs.year.castToNum(Integer.class).desc(),
                            illCrdnNocs.month.castToNum(Integer.class).desc()
                    )
                    .limit(1)
                    .fetchOne();

            return factory
                    .selectFrom(illCrdnNocs)
                    .where(
                            eqYear(lastData.getYear())
                            , eqMonth(lastData.getMonth())
                            , eqGugun(req.getSggCd())
                    )
                    .fetch()
                    .stream().map(IllCrdnNocs::toRes)
                    .sorted(Comparator.comparing(IllCrdnNocsDto::getYear).reversed())
                    .collect(Collectors.toList());
        } else {
            return factory
                    .selectFrom(illCrdnNocs)
                    .where(
                            eqYear(req.getYear())
                            , eqMonth(req.getMonth())
                            , eqGugun(req.getSggCd())
                    )
                    .fetch()
                    .stream().map(IllCrdnNocs::toRes)
                    .sorted(Comparator.comparing(IllCrdnNocsDto::getYear).reversed())
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
            return illCrdnNocs.year.eq(year);
    }

    private BooleanExpression eqMonth(String month) {
        if (month == null) {
            return null;
        }
        else
            return illCrdnNocs.month.eq(month);
    }

    private BooleanExpression eqGugun(String gugun) {
        if (gugun == null) {
            return null;
        }
        else
            return illCrdnNocs.sgg.eq(gugun);
    }
}
