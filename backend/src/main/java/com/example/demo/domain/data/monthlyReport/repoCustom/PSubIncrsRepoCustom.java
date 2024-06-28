package com.example.demo.domain.data.monthlyReport.repoCustom;

import com.example.demo.domain.data.monthlyReport.PSubIncrs;
import com.example.demo.domain.data.monthlyReport.QPSubIncrs;
import com.example.demo.dto.data.monthlyReport.PSubIncrsDto;
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
public class PSubIncrsRepoCustom {
    private final JPAQueryFactory factory;
    QPSubIncrs incrs = QPSubIncrs.pSubIncrs;

    public List<PSubIncrsDto> search(PSubIncrsDto.Keyword req) {
        return factory
                .selectFrom(incrs)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                        , eqSggCd(req.getSggCd())
                        //, containPermitNo(req.getPermitNo())
                        //, containBuildNm(req.getBuildNm())
                        //, containBuildOwner(req.getBuildOwner())
                )
                .orderBy(incrs.sggCd.asc())
                .fetch()
                .stream().map(PSubIncrs::toRes)
                .sorted(Comparator.comparing(PSubIncrsDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }

    /*
   exprressions 분리
    */
    private BooleanExpression eqYear(String year) {
        return hasText(year) ? incrs.year.contains(year) : null;
    }

    private BooleanExpression eqMonth(String month) {
        return hasText(month) ? incrs.month.contains(month) : null;
    }

    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? incrs.sggCd.contains(sggCd) : null;
    }

    private BooleanExpression containBuildNm(String name) {
        return hasText(name) ? incrs.buildNm.contains(name) : null;
    }

    private BooleanExpression containBuildOwner(String owner) {
        return hasText(owner) ? incrs.buildOwner.contains(owner) : null;
    }

    private BooleanExpression containPermitNo(String no) {
        return hasText(no) ? incrs.permitNo.contains(no) : null;
    }

}
