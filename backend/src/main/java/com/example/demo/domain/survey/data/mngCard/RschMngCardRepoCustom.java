package com.example.demo.domain.survey.data.mngCard;

import com.example.demo.dto.survey.mngCard.RschSummaryDto;
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
public class RschMngCardRepoCustom {
    /*
    실태조사 관리카드 총괄표 데이터 쿼리
     */
    private final JPAQueryFactory factory;
    QRschSummary summary = QRschSummary.rschSummary;

    public List<RschSummaryDto> search(RschSummaryDto.Keyword req) {
        return factory
                .selectFrom(summary)
                .where(
                        eqYear(req.getYear())
                        , eqSggNm(req.getSggNm())
                )
                .fetch()
                .stream().map(RschSummary::toRes)
                .sorted(Comparator.comparing(RschSummaryDto::getCreateDtm))
                .collect(Collectors.toList());
    }

    //
    private BooleanExpression eqYear(String year) {
        return hasText(year) ? summary.year.eq(year) : null;
    }

    private BooleanExpression eqSggNm(String sggNm) {
        return hasText(sggNm) ? summary.sggNm.eq(sggNm) : null;
    }

}
