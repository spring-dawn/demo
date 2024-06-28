package com.example.demo.domain.data.monthlyReport.repoCustom;

import com.example.demo.atech.Msg;
import com.example.demo.domain.data.monthlyReport.MrData;
import com.example.demo.domain.data.monthlyReport.QMrData;
import com.example.demo.dto.data.monthlyReport.MrDataDto;
import com.querydsl.core.Tuple;
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
public class MrDataRepoCustom {
    private final JPAQueryFactory factory;
    QMrData mrData = QMrData.mrData;

    public List<MrDataDto> searchMrData(MrDataDto.Keyword req) {
        return factory
                .selectFrom(mrData)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                        , eqSggCd(req.getSggCd())
                        , containNm(req.getDataNm())
                        , eqCollectYn(req.getCollectYn())
                )
                .orderBy(mrData.createDtm.desc())
                .fetch()
                .stream().map(MrData::toRes)
//                .sorted(Comparator.comparing(MrDataDto::getCreateDtm))
                .collect(Collectors.toList());
    }


    /*
    exprressions 분리
     */
    private BooleanExpression containNm(String dataNm) {
        return hasText(dataNm) ? mrData.dataNm.contains(dataNm) : null;
    }

    private BooleanExpression eqYear(String year) {
        return hasText(year) ? mrData.year.eq(year) : null;
    }
    private BooleanExpression eqMonth(String month) {
        return hasText(month) ? mrData.month.eq(month) : null;
    }

    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? mrData.sggCd.eq(sggCd) : null;
    }

    private BooleanExpression eqCollectYn(String collectYn) {
        return hasText(collectYn) ? mrData.collectYn.eq(collectYn) : null;
    }
}
