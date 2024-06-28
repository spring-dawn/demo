package com.example.demo.domain.data.illegal.file;

import com.example.demo.dto.data.illegal.IllDataDto;
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
public class IllDataRepoCustom {
    private final JPAQueryFactory factory;
    QIllData illData = QIllData.illData;

    public List<IllDataDto> searchIllData(IllDataDto.Keyword req) {
        return factory
                .selectFrom(illData)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                        , eqSggCd(req.getSggCd())
                        , containNm(req.getDataNm())
                        , eqCollectYn(req.getCollectYn())
                )
                .fetch()
                .stream().map(IllData::toRes)
                .sorted(Comparator.comparing(IllDataDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }

    /*
   exprressions 분리
    */
    private BooleanExpression containNm(String dataNm) {
        return hasText(dataNm) ? illData.dataNm.contains(dataNm) : null;
    }

    private BooleanExpression eqYear(String year) {
        return hasText(year) ? illData.year.eq(year) : null;
    }
    private BooleanExpression eqMonth(String month) {
        return hasText(month) ? illData.month.eq(month) : null;
    }

    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? illData.sggCd.eq(sggCd) : null;
    }

    private BooleanExpression eqCollectYn(String collectYn) {
        return hasText(collectYn) ? illData.collectYn.eq(collectYn) : null;
    }
}
