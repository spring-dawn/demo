package com.example.demo.domain.system.feedback;

import com.example.demo.dto.system.FeedbackDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class FeedbackRepoCustom {
    private final JPAQueryFactory factory;
    QFeedback fb = QFeedback.feedback;

    public List<FeedbackDto> search(FeedbackDto.Keyword req) {
        return factory
                .selectFrom(fb)
                .where(
                        eqSggCd(req.getSggCd())
                        , eqDept(req.getDept())
                        , goeCreateDtm(req.getStartDt())
                        , loeCreateDtm(req.getEndDt())
                )
                .orderBy(fb.createDtm.desc())
                .fetch()
                .stream().map(Feedback::toRes)
                .collect(Collectors.toList());
    }

    //
    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? fb.sggCd.contains(sggCd) : null;
    }

    private BooleanExpression eqDept(String dept) {
        return hasText(dept) ? fb.dept.contains(dept) : null;
    }

    private BooleanExpression containTitle(String title) {
        return hasText(title) ? fb.title.contains(title) : null;
    }

    private BooleanExpression containContents(String contents) {
        return hasText(contents) ? fb.contents.contains(contents) : null;
    }

    private BooleanExpression goeCreateDtm(String startDt) {
        return hasText(startDt) ? fb.createDtm.goe(LocalDate.parse(startDt).atStartOfDay()) : null;
    }

    private BooleanExpression loeCreateDtm(String endDt) {
        return hasText(endDt) ? fb.createDtm.loe(LocalDate.parse(endDt).atTime(LocalTime.MAX)) : null;
    }

}
