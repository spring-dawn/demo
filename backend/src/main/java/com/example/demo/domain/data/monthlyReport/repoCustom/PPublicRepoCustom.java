package com.example.demo.domain.data.monthlyReport.repoCustom;

import com.example.demo.atech.Msg;
import com.example.demo.domain.data.illegal.IllCrdnPrfmnc;
import com.example.demo.domain.data.monthlyReport.PPublic;
import com.example.demo.domain.data.monthlyReport.QPPublic;
import com.example.demo.dto.data.facility.PFOpenDto;
import com.example.demo.dto.data.monthlyReport.PPublicDto;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Repository
@RequiredArgsConstructor
public class PPublicRepoCustom {
    private final JPAQueryFactory factory;
    QPPublic pbl = QPPublic.pPublic;

    public List<PPublicDto> search(PPublicDto.Keyword req) {
        return factory
                .selectFrom(pbl)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                        , eqSggCd(req.getSggCd())
                )
                .orderBy(pbl.sggCd.asc())
                .fetch()
                .stream().map(PPublic::toRes)
                .sorted(Comparator.comparing(PPublicDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }

    public List<PPublicDto> searchLast(PPublicDto.Keyword req) {
        PPublic lastData = factory
                .selectFrom(pbl)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                        , eqSggCd(req.getSggCd())
                )
                .orderBy(
                        pbl.year.castToNum(Integer.class).desc(),
                        pbl.month.castToNum(Integer.class).desc()
                )
                .limit(1)
                .fetchOne();

        if (lastData != null) {
            return factory
                    .selectFrom(pbl)
                    .where(
                            eqYear(lastData.getYear())
                            , eqMonth(lastData.getMonth())
                            , eqSggCd(req.getSggCd())
                    )
                    .orderBy(pbl.sggCd.asc())
                    .fetch()
                    .stream().map(PPublic::toRes)
                    .sorted(Comparator.comparing(PPublicDto::getCreateDtm).reversed())
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public List<PPublicDto> searchPrev(PPublicDto.Keyword req) {
        String prevYear=req.getYear();
        String prevMonth=req.getMonth();
        if (req.getMonth().equals("1")){
            prevYear=(Integer.toString(Integer.parseInt(req.getYear())- 1));
            prevMonth = "12";
        }
        else if (req.getMonth() != null) {
            prevMonth = (Integer.toString(Integer.parseInt(req.getMonth()) - 1));
        }
        return factory
                .selectFrom(pbl)
                .where(
                        eqYear(prevYear)
                        , eqMonth(prevMonth)
                        , eqSggCd(req.getSggCd())
                )
                .fetch()
                .stream().map(PPublic::toRes)
                .sorted(Comparator.comparing(PPublicDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }


    /**
     * 월간보고 구군별 최신 데이터 다운로드 키워드 구하기.
     * @param sggCd 구군코드
     * @return 해당 구군의 데이터 승인된 최신 데이터의 year, month
     */
    public PPublicDto.Keyword getLatestDt(String sggCd) {
        Integer year = factory
                .select(pbl.year.max().castToNum(Integer.class))
                .from(pbl)
                .where(
                        eqSggCd(sggCd)
                )
                .fetchOne();
        if (year == null) throw new NullPointerException(Msg.EMPTY_RESULT.getMsg());

        Integer month = factory
                .select(pbl.month.max().castToNum(Integer.class))
                .from(pbl)
                .where(
                        eqSggCd(sggCd)
                        , eqYear(year.toString())
                )
                .fetchOne();

        PPublicDto.Keyword req = new PPublicDto.Keyword();
        req.setYear(year.toString());
        req.setMonth(month.toString());
        return req;
    }

    /*
   exprressions 분리
    */
    private BooleanExpression eqYear(String year) {
        return hasText(year) ? pbl.year.contains(year) : null;
    }

    private BooleanExpression eqMonth(String month) {
        return hasText(month) ? pbl.month.contains(month) : null;
    }

    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? pbl.sggCd.contains(sggCd) : null;
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
