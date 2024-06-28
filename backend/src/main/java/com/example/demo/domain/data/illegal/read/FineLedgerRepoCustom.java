package com.example.demo.domain.data.illegal.read;

import com.example.demo.dto.data.illegal.FineLedgerDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FineLedgerRepoCustom {
    private final JPAQueryFactory factory;
    QFineLedger fl = QFineLedger.fineLedger;

    public List<FineLedgerDto> search(FineLedgerDto.Keyword req) {
        return factory
                .selectFrom(fl)
//                .where()
                .fetch()
                .stream().map(FineLedger::toRes)
                .collect(Collectors.toList());
    }


}
