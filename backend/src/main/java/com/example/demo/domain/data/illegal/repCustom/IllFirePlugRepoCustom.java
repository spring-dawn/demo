package com.example.demo.domain.data.illegal.repCustom;

import com.example.demo.domain.data.illegal.IllFireplug;
import com.example.demo.domain.data.illegal.IllFixed;
import com.example.demo.domain.data.illegal.QIllFireplug;
import com.example.demo.domain.data.illegal.QIllFixed;
import com.example.demo.dto.data.illegal.IllFireplugDto;
import com.example.demo.dto.data.illegal.IllFixedDto;
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
public class IllFirePlugRepoCustom {
    private final JPAQueryFactory factory;
    QIllFireplug fireplug = QIllFireplug.illFireplug;

    public List<IllFireplugDto> search(IllKeywordDto req) {
        if (req.getMonth()==null||req.getMonth().equals("")) {
            IllFireplug lastData = factory
                    .selectFrom(fireplug)
                    .where(
                            eqYear(req.getYear())
                            , eqMonth(req.getMonth())
                            , eqGugun(req.getSggCd())
                    )
                    .orderBy(
                            fireplug.year.castToNum(Integer.class).desc(),
                            fireplug.month.castToNum(Integer.class).desc()
                    )
                    .limit(1)
                    .fetchOne();

            return factory
                    .selectFrom(fireplug)
                    .where(
                            eqYear(lastData.getYear())
                            , eqMonth(lastData.getMonth())
                            , eqGugun(req.getSggCd())
                    )
                    .fetch()
                    .stream().map(IllFireplug::toRes)
                    .sorted(Comparator.comparing(IllFireplugDto::getYear).reversed())
                    .collect(Collectors.toList());
        } else {
            return factory
                    .selectFrom(fireplug)
                    .where(
                            eqYear(req.getYear())
                            , eqMonth(req.getMonth())
                            , eqGugun(req.getSggCd())
                    )
                    .fetch()
                    .stream().map(IllFireplug::toRes)
                    .sorted(Comparator.comparing(IllFireplugDto::getYear).reversed())
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
            return fireplug.year.eq(year);
    }

    private BooleanExpression eqMonth(String month) {
        if (month == null) {
            return null;
        }
        else
            return fireplug.month.eq(month);
    }

    private BooleanExpression eqGugun(String gugun) {
        if (gugun == null) {
            return null;
        }
        else
            return fireplug.sgg.eq(gugun);
    }
}
