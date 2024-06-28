package com.example.demo.domain.data.monthlyReport.repoCustom;

import com.example.demo.domain.data.monthlyReport.PResi;
import com.example.demo.domain.data.monthlyReport.QPResi;
import com.example.demo.dto.data.monthlyReport.PResiDto;
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
public class PResiRepoCustom {
    private final JPAQueryFactory factory;
    QPResi resi = QPResi.pResi;

    public List<PResiDto> search(PResiDto.Keyword req) {
        return factory
                .selectFrom(resi)
                .where(
                        //eqYear(req.getYear())
                        //, eqMonth(req.getMonth())
                        eqSggCd(req.getSggCd())
                )
                .fetch()
                .stream().map(PResi::toRes)
                .sorted(Comparator.comparing(PResiDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }

    public List<PResiDto> searchPrev(PResiDto.Keyword req) {
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
                .selectFrom(resi)
                .where(
                        eqYear(prevYear)
                        , eqMonth(prevMonth)
                        , eqSggCd(req.getSggCd())
                )
                .fetch()
                .stream().map(PResi::toRes)
                .sorted(Comparator.comparing(PResiDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }

    public List<PResiDto> searchAll(PResiDto.Keyword req) {

        return factory
                .selectFrom(resi)
                .fetch()
                .stream().map(PResi::toRes)
                //.sorted((s1, s2) -> Integer.compare(Integer.parseInt(s1.getYear()), Integer.parseInt(s2.getYear())) )
                //.sorted((s1, s2) -> Integer.compare(Integer.parseInt(s1.getMonth()), Integer.parseInt(s2.getMonth())))
                //.sorted(Comparator.comparing(PResiDto::getYear).reversed().thenComparing(PResiDto::getMonth).reversed())
                .sorted(Comparator.comparing(PResiDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }


    /*
   exprressions 분리
    */
    private BooleanExpression eqYear(String year) {
        return hasText(year) ? resi.year.contains(year) : null;
    }

    private BooleanExpression eqMonth(String month) {
        return hasText(month) ? resi.month.contains(month) : null;
    }

    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? resi.sggCd.contains(sggCd) : null;
    }
}
