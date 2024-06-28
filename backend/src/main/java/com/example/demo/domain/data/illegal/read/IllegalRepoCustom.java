package com.example.demo.domain.data.illegal.read;

import com.example.demo.domain.data.illegal.file.IllData;
import com.example.demo.dto.data.illegal.IllDataDto;
import com.example.demo.dto.data.illegal.IllegalDto;
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
public class IllegalRepoCustom {
    private final JPAQueryFactory factory;
    QIllegal ill = QIllegal.illegal;

    public List<IllegalDto> search(IllegalDto.Keyword req) {
        return factory
                .selectFrom(ill)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                )
                .fetch()
                .stream().map(Illegal::toRes)
                .sorted(Comparator.comparing(IllegalDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }

    private BooleanExpression eqYear(String year) {
        return hasText(year) ? ill.year.eq(year) : null;
    }

    private BooleanExpression eqMonth(String month) {
        return hasText(month) ? ill.month.eq(month) : null;
    }
}
