package com.example.demo.domain.data.facility.file;

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
public class PFDataRepoCustom {
    private final JPAQueryFactory factory;
    QPFData pfData = QPFData.pFData;

    public List<UploadDataDto> search(UploadDataDto.Keyword req) {
        return factory
                .selectFrom(pfData)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                        , eqSggCd(req.getSggCd())
                        , containNm(req.getDataNm())
                        , eqLotType(req.getLotType())
                        , eqCollectYn(req.getCollectYn())
                )
                .orderBy(pfData.createDtm.desc())
                .fetch()
                .stream().map(PFData::toRes)
//                .sorted(Comparator.comparing(UploadDataDto::getCreateDtm))
                .collect(Collectors.toList());
    }

    /*
   expressions 분리
    */
    private BooleanExpression eqYear(String year) {
        return hasText(year) ? pfData.year.eq(year) : null;
    }

    private BooleanExpression eqMonth(String month) {
        return hasText(month) ? pfData.month.eq(month) : null;
    }

    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? pfData.sggCd.eq(sggCd) : null;
    }

    private BooleanExpression containNm(String dataNm) {
        return hasText(dataNm) ? pfData.dataNm.contains(dataNm) : null;
    }

    private BooleanExpression eqLotType(String lotType) {
        return hasText(lotType) ? pfData.lotType.eq(lotType) : null;

    }

    private BooleanExpression eqCollectYn(String collectYn) {
        return hasText(collectYn) ? pfData.collectYn.eq(collectYn) : null;
    }
}
