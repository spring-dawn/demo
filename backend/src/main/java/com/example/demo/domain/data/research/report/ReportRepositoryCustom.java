package com.example.demo.domain.data.research.report;

import com.example.demo.domain.common.file.FileInfoRepository;
import com.example.demo.dto.data.research.ReportDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class ReportRepositoryCustom {
    /*
    querydsl 쿼리
     */
    private final FileInfoRepository fileInfoRepo;
    private final JPAQueryFactory factory;
    QReport report = QReport.report;

    // 보고서 검색
    public List<ReportDto.ReportRes> searchReport(ReportDto.ReportSearchReq req) {
        List<Report> resultList = factory
                .selectFrom(report)
                .where(
                        containName(req.getName()),
                        eqYear(req.getYear()),
                        eqReg(req.getRegCode())
                )
                .orderBy(report.createDtm.desc())
                .fetch();

        return resultList.stream().map(Report::toReportRes).collect(Collectors.toList());
    }

    /*
    exprressions 분리
     */

    private BooleanExpression containName(String name) {
        return hasText(name) ? report.name.contains(name) : null;
    }

    private BooleanExpression eqYear(String year) {
        return hasText(year) ? report.year.eq(year) : null;
    }

    private BooleanExpression eqReg(String reg) {
        return hasText(reg) ? report.regName.eq(reg) : null;
    }
}


