package com.example.demo.domain.data.monthlyReport.repoCustom;

import com.example.demo.domain.data.monthlyReport.PSubDcrs;
import com.example.demo.domain.data.monthlyReport.QPSubDcrs;
import com.example.demo.dto.data.monthlyReport.PSubDcrsDto;
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
public class PSubDcrsRepoCustom {
    private final JPAQueryFactory factory;
    QPSubDcrs dcrs = QPSubDcrs.pSubDcrs;

    public List<PSubDcrsDto> search(PSubDcrsDto.Keyword req) {
        return factory
                .selectFrom(dcrs)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                        , eqSggCd(req.getSggCd())
                )
                .orderBy(dcrs.sggCd.asc())
                .fetch()
                .stream().map(PSubDcrs::toRes)
                .sorted(Comparator.comparing(PSubDcrsDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }

    /*
   exprressions 분리
    */
    private BooleanExpression eqYear(String year) {
        return hasText(year) ? dcrs.year.contains(year) : null;
    }

    private BooleanExpression eqMonth(String month) {
        return hasText(month) ? dcrs.month.contains(month) : null;
    }

    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? dcrs.sggCd.contains(sggCd) : null;
    }

//    private BooleanExpression containBuildNm(String name) {
//        return hasText(name) ? dcrs.buildNm.contains(name) : null;
//    }
//
//    private BooleanExpression containBuildOwner(String owner) {
//        return hasText(owner) ? dcrs.buildOwner.contains(owner) : null;
//    }
//
//    private BooleanExpression containPermitNo(String no) {
//        return hasText(no) ? dcrs.permitNo.contains(no) : null;
//    }

}
