package com.example.demo.domain.data.facility.read;

import com.example.demo.atech.Msg;
import com.example.demo.dto.data.UploadDataDto;
import com.example.demo.dto.data.facility.PFOpenDto;
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
public class PFOpenRepoCustom {
    private final JPAQueryFactory factory;
    QPFOpen open = QPFOpen.pFOpen;

    // search
    public List<PFOpenDto> search(PFOpenDto.Keyword req, String lotType) {
        return factory
                .selectFrom(open)
                .where(
                        eqLotType(lotType), // 8: 부설 개방, 9: 사유지 개방
                        eqYear(req.getYear()),
                        eqSggCd(req.getSggCd()),
                        containNm(req.getLotNm()),
                        containAddress(req.getAddress()),
                        goeTotalSpcs(req.getMinSpcs()),
                        loeTotalSpcs(req.getMaxSpcs())
                )
                .orderBy(open.createDtm.asc())
                .fetch()
                .stream().map(PFOpen::toRes)
                .collect(Collectors.toList());
    }

    /**
     * 구군, 주차유형별 존재하는 가장 최신 일자 구하기
     * @param sggCd 구군코드
     * @param lotType 주차유형 8: 부설개방, 9:사유지개방
     * @return 최신 엑셀다운로드용 year, month 정보
     */
    public PFOpenDto.Keyword getLatestDt(String sggCd, String lotType) {
        Integer year = factory
                .select(open.year.max().castToNum(Integer.class))
                .from(open)
                .where(
                        eqSggCd(sggCd)
                        , eqLotType(lotType)
                )
                .fetchOne();
        if (year == null) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());

        Integer month = factory
                .select(open.month.max().castToNum(Integer.class))
                .from(open)
                .where(
                        eqSggCd(sggCd)
                        , eqYear(year.toString())
                        , eqLotType(lotType)
                )
                .fetchOne();

        PFOpenDto.Keyword req = new PFOpenDto.Keyword();
        req.setYear(year.toString());
        req.setMonth(month.toString());
        return req;
    }



    /*
  exprressions 분리
   */

    private BooleanExpression eqYear(String year) {
        return hasText(year) ? open.year.eq(year) : null;
    }

    private BooleanExpression eqLotType(String lotType) {
        return hasText(lotType) ? open.lotType.eq(lotType) : null;

    }

//    private BooleanExpression eqMonth(String month) {
//        return hasText(month) ? open.month.eq(month) : null;
//    }

    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? open.sggCd.eq(sggCd) : null;
    }

    private BooleanExpression containNm(String lotNm) {
        return hasText(lotNm) ? open.lotNm.contains(lotNm) : null;
    }

    private BooleanExpression containAddress(String address) {
        return hasText(address) ? open.address.contains(address) : null;
    }

    private BooleanExpression goeTotalSpcs(Long minSpcs) {
        return minSpcs != null ? open.spcs.goe(minSpcs) : null;
    }

    private BooleanExpression loeTotalSpcs(Long maxSpcs) {
        return maxSpcs != null ? open.spcs.loe(maxSpcs) : null;
    }

}
