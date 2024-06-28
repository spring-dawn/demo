package com.example.demo.domain.data.illegal.repCustom;

import com.example.demo.domain.data.illegal.IllFixed;
import com.example.demo.domain.data.illegal.IllMobile;
import com.example.demo.domain.data.illegal.QIllFixed;
import com.example.demo.domain.data.illegal.QIllMobile;
import com.example.demo.dto.data.illegal.IllFixedDto;
import com.example.demo.dto.data.illegal.IllMobileDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class IllMobileRepoCustom {
    private final JPAQueryFactory factory;
    QIllMobile mobile = QIllMobile.illMobile;

    public List<IllMobileDto> search(IllMobileDto.Keyword req) {
        return factory
                .selectFrom(mobile)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                        , eqGugun(req.getGugun())
                )
                .fetch()
                .stream().map(IllMobile::toRes)
                .sorted(Comparator.comparing(IllMobileDto::getPrchsYmd).reversed())
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
            return mobile.year.eq(year);
    }

    private BooleanExpression eqMonth(String month) {
        if (month == null) {
            return null;
        }
        else
            return mobile.month.eq(month);
    }

    private BooleanExpression eqGugun(String gugun) {
        if (gugun == null) {
            return null;
        }
        else
            return mobile.sgg.eq(gugun);
    }
}
