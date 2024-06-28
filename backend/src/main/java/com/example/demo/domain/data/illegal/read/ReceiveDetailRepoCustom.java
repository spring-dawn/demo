package com.example.demo.domain.data.illegal.read;

import com.example.demo.dto.data.illegal.ReceiveDetailDto;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class ReceiveDetailRepoCustom {
    private final JPAQueryFactory factory;
    QReceiveDetail rd = QReceiveDetail.receiveDetail;

    public List<ReceiveDetailDto> search(ReceiveDetailDto.Keyword req) {
        return factory
                .selectFrom(rd)
//                .where()
                .fetch()
                .stream().map(ReceiveDetail::toRes)
                .collect(Collectors.toList());
    }
}
