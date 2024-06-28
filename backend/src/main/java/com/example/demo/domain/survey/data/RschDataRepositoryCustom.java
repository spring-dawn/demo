package com.example.demo.domain.survey.data;


import com.example.demo.dto.data.UploadDataDto;
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
public class RschDataRepositoryCustom {
    /*
    실태조사 > 조사자료 > 관리카드 (파일 데이터)
    querydsl 쿼리
     */
    private final JPAQueryFactory factory;
    QRschData rschData = QRschData.rschData;

    // 실태조사 관리카드 검색
    public List<UploadDataDto> search(UploadDataDto.Keyword req) {
        return factory
                .selectFrom(rschData)
                .where(
                        eqYear(req.getYear())
                        , eqSggCd(req.getSggCd())
                        , containNm(req.getDataNm())
                        , eqCollectYn(req.getCollectYn())
                        , eqRschType(req.getRschType())
                )
                .fetch()
                .stream().map(RschData::toRes)
                .sorted(Comparator.comparing(UploadDataDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }

    /*
    exprressions 분리
     */
    private BooleanExpression containNm(String dataNm) {
        return hasText(dataNm) ? rschData.dataNm.contains(dataNm) : null;
    }

    private BooleanExpression eqYear(String year) {
        return hasText(year) ? rschData.year.eq(year) : null;
    }

    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? rschData.sggCd.eq(sggCd) : null;
    }

    private BooleanExpression eqRschType(String rschType) {
        return hasText(rschType) ? rschData.rschType.eq(rschType) : null;
    }

    private BooleanExpression eqCollectYn(String collectYn) {
        return hasText(collectYn) ? rschData.collectYn.eq(collectYn) : null;
    }
}


