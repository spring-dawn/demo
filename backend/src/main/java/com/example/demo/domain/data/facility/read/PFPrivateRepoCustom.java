package com.example.demo.domain.data.facility.read;

import com.example.demo.atech.Msg;
import com.example.demo.dto.data.UploadDataDto;
import com.example.demo.dto.data.facility.PFPrivateDto;
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
public class PFPrivateRepoCustom {
    private final JPAQueryFactory factory;
    QPFPrivate prv = QPFPrivate.pFPrivate;

    // search
    public List<PFPrivateDto> search(PFPrivateDto.Keyword req) {
        return factory
                .selectFrom(prv)
                .where(
                        eqYear(req.getYear()),
                        eqMonth(req.getMonth()),
                        eqSggCd(req.getSggCd()),
                        containNm(req.getLotNm()),
                        containAddress(req.getAddress()),
                        goeTotalSpcs(req.getMinSpcs()),
                        loeTotalSpcs(req.getMaxSpcs())
                )
                .orderBy(prv.createDtm.asc())
                .fetch()
                .stream().map(PFPrivate::toRes)
//                .sorted(Comparator.comparing(PFPrivateDto::getCreateDtm))
                .collect(Collectors.toList());
    }

    /**
     * 구군별 존재하는 가장 최신 일자 구하기
     * @param sggCd 구군코드
     * @return 민영주차장(5)의 해당 구군 데이터 중 가장 최신 일자의 year, month
     */
    public PFPrivateDto.Keyword getLatestDt(String sggCd) {
        Integer year = factory.select(prv.year.max().castToNum(Integer.class))
                .from(prv)
                .where(eqSggCd(sggCd))
                .fetchOne();
        if (year == null) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());

        Integer month = factory.select(prv.month.max().castToNum(Integer.class))
                .from(prv)
                .where(
                        eqSggCd(sggCd)
                        , eqYear(year.toString())
                )
                .fetchOne();

        PFPrivateDto.Keyword req = new PFPrivateDto.Keyword();
        req.setYear(year.toString());
        req.setMonth(month.toString());
        return req;
    }


    /*
  exprressions 분리
   */

    private BooleanExpression eqYear(String year) {
        return hasText(year) ? prv.year.eq(year) : null;
    }

    private BooleanExpression eqMonth(String month) {
        return hasText(month) ? prv.month.eq(month) : null;
    }

    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? prv.sggCd.eq(sggCd) : null;
    }

    private BooleanExpression containNm(String lotNm) {
        return hasText(lotNm) ? prv.lotNm.contains(lotNm) : null;
    }

    private BooleanExpression containAddress(String address) {
        return hasText(address) ? prv.address.contains(address) : null;
    }

    private BooleanExpression goeTotalSpcs(Long minSpcs) {
        return minSpcs != null ? prv.totalSpcs.goe(minSpcs) : null;
    }

    private BooleanExpression loeTotalSpcs(Long maxSpcs) {
        return maxSpcs != null ? prv.totalSpcs.loe(maxSpcs) : null;
    }


}
