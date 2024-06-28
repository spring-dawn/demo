package com.example.demo.domain.data.monthlyReport.repoCustom;

import com.example.demo.domain.data.monthlyReport.PStatus;
import com.example.demo.domain.data.monthlyReport.QPStatus;
import com.example.demo.domain.data.monthlyReport.pk.PStatusPk;
import com.example.demo.domain.data.monthlyReport.repo.PStatusRepository;
import com.example.demo.dto.data.monthlyReport.PStatusDto;
import com.querydsl.core.NonUniqueResultException;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Repository
@RequiredArgsConstructor
public class PStatusRepoCustom {
    private final PStatusRepository repo;
    private final JPAQueryFactory factory;
    QPStatus status = QPStatus.pStatus;
//    TemplateExpression<Integer> nn = Expressions.template(Integer.class, "{0}", Expressions.constant(0));


    public List<PStatusDto> search(PStatusDto.Keyword req) {
        return factory
                .selectFrom(status)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                        , eqSggCd(req.getSggCd())
                )
                .fetch()
                .stream().map(PStatus::toRes)
                .sorted(Comparator.comparing(PStatusDto::getCreateDtm).reversed())
                .collect(Collectors.toList());
    }

    public PStatusDto searchOne(PStatusDto.Keyword req) {
        return factory
                .selectFrom(status)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                        , eqSggCd(req.getSggCd())
                )
                .fetchOne().toRes();
    }

    public PStatusDto findLastOne(PStatusDto.Keyword req) {
        return factory
                .selectFrom(status)
                .where(
                        eqYear(req.getYear())
                        , eqMonth(req.getMonth())
                        , eqSggCd(req.getSggCd())
                )
                .orderBy(status.localDt.desc())
                .limit(1)
                .fetchOne().toRes();
    }

    public Map<String, Object> selectMonthlyTotal(PStatusDto.Keyword req) {
        HashMap<String, Object> map = new HashMap<>();
//        1) 선택된 이번달치 데이터, 기준.
        PStatus thisMonth;
        if (req.getMonth()==null||req.getMonth().equals("")) {
            thisMonth = factory
                    .selectFrom(status)
                    .where(
                            eqYear(req.getYear())
                            , eqMonth(req.getMonth())
                            , eqSggCd(req.getSggCd())
                    )
                    .orderBy(status.localDt.desc())
                    .limit(1)
                    .fetchOne();
        }
        else{
            try {
                thisMonth = factory
                        .selectFrom(status)
                        .where(
                                eqYear(req.getYear())
                                , eqMonth(req.getMonth())
                                , eqSggCd(req.getSggCd())
                        )
                        .fetchOne();
            } catch (NonUniqueResultException e) {
                throw new RuntimeException(e);
            }
        }
        map.put("status", thisMonth == null ? null : thisMonth.toRes());

//        2) 기준월-1달: 전월까지의 누계
        if (thisMonth != null) {
            PStatusDto.Total prevTotal = factory
                    .select(
                            Projections.bean(PStatusDto.Total.class,
//                              증감 합 계산
                                    status.PBLRD_PAY_L_I.sum().subtract(status.PBLRD_PAY_L_D.sum()).as("PBLRD_PAY_L_SUM"),
                                    status.PBLRD_PAY_S_I.sum().subtract(status.PBLRD_PAY_S_D.sum()).as("PBLRD_PAY_S_SUM"),
                                    status.PBLRD_PAY_A_I.sum().subtract(status.PBLRD_PAY_A_D.sum()).as("PBLRD_PAY_A_SUM"),
                                    status.PBLRD_FREE_L_I.sum().subtract(status.PBLRD_FREE_L_D.sum()).as("PBLRD_FREE_L_SUM"),
                                    status.PBLRD_FREE_S_I.sum().subtract(status.PBLRD_FREE_S_D.sum()).as("PBLRD_FREE_S_SUM"),
                                    status.PBLRD_FREE_A_I.sum().subtract(status.PBLRD_FREE_A_D.sum()).as("PBLRD_FREE_A_SUM"),
                                    //
                                    status.PBLRD_RESI_L_I.sum().subtract(status.PBLRD_RESI_L_D.sum()).as("PBLRD_RESI_L_SUM"),
                                    status.PBLRD_RESI_S_I.sum().subtract(status.PBLRD_RESI_S_D.sum()).as("PBLRD_RESI_S_SUM"),
                                    status.PBLRD_RESI_A_I.sum().subtract(status.PBLRD_RESI_A_D.sum()).as("PBLRD_RESI_A_SUM"),
                                    //
                                    status.PBLOUT_PAY_L_I.sum().subtract(status.PBLOUT_PAY_L_D.sum()).as("PBLOUT_PAY_L_SUM"),
                                    status.PBLOUT_PAY_S_I.sum().subtract(status.PBLOUT_PAY_S_D.sum()).as("PBLOUT_PAY_S_SUM"),
                                    status.PBLOUT_PAY_A_I.sum().subtract(status.PBLOUT_PAY_A_D.sum()).as("PBLOUT_PAY_A_SUM"),
                                    status.PBLOUT_FREE_L_I.sum().subtract(status.PBLOUT_FREE_L_D.sum()).as("PBLOUT_FREE_L_SUM"),
                                    status.PBLOUT_FREE_S_I.sum().subtract(status.PBLOUT_FREE_S_D.sum()).as("PBLOUT_FREE_S_SUM"),
                                    status.PBLOUT_FREE_A_I.sum().subtract(status.PBLOUT_FREE_A_D.sum()).as("PBLOUT_FREE_A_SUM"),
                                    // 민영주차장은 다른 분류가 없으므로 각각의 합이 곧 소계
                                    status.PRV_L_I.sum().subtract(status.PRV_L_D.sum()).as("PRV_L_SUM"),
                                    status.PRV_S_I.sum().subtract(status.PRV_S_D.sum()).as("PRV_S_SUM"),
                                    status.PRV_A_I.sum().subtract(status.PRV_A_D.sum()).as("PRV_A_SUM"),
                                    //
                                    status.SUBSE_SUR_L_I.sum().subtract(status.SUBSE_SUR_L_D.sum()).as("SUBSE_SUR_L_SUM"),
                                    status.SUBSE_SUR_S_I.sum().subtract(status.SUBSE_SUR_S_D.sum()).as("SUBSE_SUR_S_SUM"),
                                    status.SUBSE_SUR_A_I.sum().subtract(status.SUBSE_SUR_A_D.sum()).as("SUBSE_SUR_A_SUM"),
                                    status.SUBSE_MOD_L_I.sum().subtract(status.SUBSE_MOD_L_D.sum()).as("SUBSE_MOD_L_SUM"),
                                    status.SUBSE_MOD_S_I.sum().subtract(status.SUBSE_MOD_S_D.sum()).as("SUBSE_MOD_S_SUM"),
                                    status.SUBSE_MOD_A_I.sum().subtract(status.SUBSE_MOD_A_D.sum()).as("SUBSE_MOD_A_SUM"),

                                    status.SUBAU_ATT_L_I.sum().subtract(status.SUBAU_ATT_L_D.sum()).as("SUBAU_ATT_L_SUM"),
                                    status.SUBAU_ATT_S_I.sum().subtract(status.SUBAU_ATT_S_D.sum()).as("SUBAU_ATT_S_SUM"),
                                    status.SUBAU_ATT_A_I.sum().subtract(status.SUBAU_ATT_A_D.sum()).as("SUBAU_ATT_A_SUM"),
                                    status.SUBAU_PRV_L_I.sum().subtract(status.SUBAU_PRV_L_D.sum()).as("SUBAU_PRV_L_SUM"),
                                    status.SUBAU_PRV_S_I.sum().subtract(status.SUBAU_PRV_S_D.sum()).as("SUBAU_PRV_S_SUM"),
                                    status.SUBAU_PRV_A_I.sum().subtract(status.SUBAU_PRV_A_D.sum()).as("SUBAU_PRV_A_SUM"),

                                    status.OWN_HOME_L_I.sum().subtract(status.OWN_HOME_L_D.sum()).as("OWN_HOME_L_SUM"),
                                    status.OWN_HOME_S_I.sum().subtract(status.OWN_HOME_S_D.sum()).as("OWN_HOME_S_SUM"),
                                    status.OWN_HOME_A_I.sum().subtract(status.OWN_HOME_A_D.sum()).as("OWN_HOME_A_SUM"),
                                    status.OWN_APT_L_I.sum().subtract(status.OWN_APT_L_D.sum()).as("OWN_APT_L_SUM"),
                                    status.OWN_APT_S_I.sum().subtract(status.OWN_APT_S_D.sum()).as("OWN_APT_S_SUM"),
                                    status.OWN_APT_A_I.sum().subtract(status.OWN_APT_A_D.sum()).as("OWN_APT_A_SUM"),
//                                subtotal
                                    status.PBLRD_PAY_L_I.sum().subtract(status.PBLRD_PAY_L_D.sum())
                                            .add(status.PBLRD_FREE_L_I.sum().subtract(status.PBLRD_FREE_L_D.sum()))
                                            .add(status.PBLRD_RESI_L_I.sum().subtract(status.PBLRD_RESI_L_D.sum()))
                                            .add(status.PBLOUT_PAY_L_I.sum().subtract(status.PBLOUT_PAY_L_D.sum()))
                                            .add(status.PBLOUT_FREE_L_I.sum().subtract(status.PBLOUT_FREE_L_D.sum()))
                                            .as("PBL_L_SUBTOTAL"),
                                    status.PBLRD_PAY_S_I.sum().subtract(status.PBLRD_PAY_S_D.sum())
                                            .add(status.PBLRD_FREE_S_I.sum().subtract(status.PBLRD_FREE_S_D.sum()))
                                            .add(status.PBLRD_RESI_S_I.sum().subtract(status.PBLRD_RESI_S_D.sum()))
                                            .add(status.PBLOUT_PAY_S_I.sum().subtract(status.PBLOUT_PAY_S_D.sum()))
                                            .add(status.PBLOUT_FREE_S_I.sum().subtract(status.PBLOUT_FREE_S_D.sum()))
                                            .as("PBL_S_SUBTOTAL"),
                                    status.PBLRD_PAY_A_I.sum().subtract(status.PBLRD_PAY_A_D.sum())
                                            .add(status.PBLRD_FREE_A_I.sum().subtract(status.PBLRD_FREE_A_D.sum()))
                                            .add(status.PBLRD_RESI_A_I.sum().subtract(status.PBLRD_RESI_A_D.sum()))
                                            .add(status.PBLOUT_PAY_A_I.sum().subtract(status.PBLOUT_PAY_A_D.sum()))
                                            .add(status.PBLOUT_FREE_A_I.sum().subtract(status.PBLOUT_FREE_A_D.sum()))
                                            .as("PBL_A_SUBTOTAL"),
                                    status.SUBSE_SUR_L_I.sum().subtract(status.SUBSE_SUR_L_D.sum())
                                            .add(status.SUBSE_MOD_L_I.sum().subtract(status.SUBSE_MOD_L_D.sum()))
                                            .add(status.SUBAU_ATT_L_I.sum().subtract(status.SUBAU_ATT_L_D.sum()))
                                            .add(status.SUBAU_PRV_L_I.sum().subtract(status.SUBAU_PRV_L_D.sum()))
                                            .as("SUB_L_SUBTOTAL"),
                                    //
                                    status.SUBSE_SUR_S_I.sum().subtract(status.SUBSE_SUR_S_D.sum())
                                            .add(status.SUBSE_MOD_S_I.sum().subtract(status.SUBSE_MOD_S_D.sum()))
                                            .add(status.SUBAU_ATT_S_I.sum().subtract(status.SUBAU_ATT_S_D.sum()))
                                            .add(status.SUBAU_PRV_S_I.sum().subtract(status.SUBAU_PRV_S_D.sum()))
                                            .as("SUB_S_SUBTOTAL"),
                                    status.SUBSE_SUR_A_I.sum().subtract(status.SUBSE_SUR_A_D.sum())
                                            .add(status.SUBSE_MOD_A_I.sum().subtract(status.SUBSE_MOD_A_D.sum()))
                                            .add(status.SUBAU_ATT_A_I.sum().subtract(status.SUBAU_ATT_A_D.sum()))
                                            .add(status.SUBAU_PRV_A_I.sum().subtract(status.SUBAU_PRV_A_D.sum()))
                                            .as("SUB_A_SUBTOTAL"),
                                    //
                                    status.OWN_HOME_L_I.sum().subtract(status.OWN_HOME_L_D.sum())
                                            .add(status.OWN_APT_L_I.sum().subtract(status.OWN_APT_L_D.sum())).as("OWN_L_SUBTOTAL"),
                                    status.OWN_HOME_S_I.sum().subtract(status.OWN_HOME_S_D.sum())
                                            .add(status.OWN_APT_S_I.sum().subtract(status.OWN_APT_S_D.sum())).as("OWN_S_SUBTOTAL"),
                                    status.OWN_HOME_A_I.sum().subtract(status.OWN_HOME_A_D.sum())
                                            .add(status.OWN_APT_A_I.sum().subtract(status.OWN_APT_A_D.sum())).as("OWN_A_SUBTOTAL"),
                                    // total= 공영 + 민영 + 부설 + 자가
                                    status.PBLRD_PAY_L_I.sum().subtract(status.PBLRD_PAY_L_D.sum())
                                            .add(status.PBLRD_FREE_L_I.sum().subtract(status.PBLRD_FREE_L_D.sum()))
                                            .add(status.PBLRD_RESI_L_I.sum().subtract(status.PBLRD_RESI_L_D.sum()))
                                            .add(status.PBLOUT_PAY_L_I.sum().subtract(status.PBLOUT_PAY_L_D.sum()))
                                            .add(status.PBLOUT_FREE_L_I.sum().subtract(status.PBLOUT_FREE_L_D.sum()))
                                            .add(status.PRV_L_I.sum().subtract(status.PRV_L_D.sum()))
                                            .add(status.SUBSE_SUR_L_I.sum().subtract(status.SUBSE_SUR_L_D.sum())
                                                    .add(status.SUBSE_MOD_L_I.sum().subtract(status.SUBSE_MOD_L_D.sum()))
                                                    .add(status.SUBAU_ATT_L_I.sum().subtract(status.SUBAU_ATT_L_D.sum()))
                                                    .add(status.SUBAU_PRV_L_I.sum().subtract(status.SUBAU_PRV_L_D.sum())))
                                            .add(status.OWN_HOME_L_I.sum().subtract(status.OWN_HOME_L_D.sum())
                                                    .add(status.OWN_APT_L_I.sum().subtract(status.OWN_APT_L_D.sum()))).as("TOTAL_L_SUM"),

                                    status.PBLRD_PAY_S_I.sum().subtract(status.PBLRD_PAY_S_D.sum())
                                            .add(status.PBLRD_FREE_S_I.sum().subtract(status.PBLRD_FREE_S_D.sum()))
                                            .add(status.PBLRD_RESI_S_I.sum().subtract(status.PBLRD_RESI_S_D.sum()))
                                            .add(status.PBLOUT_PAY_S_I.sum().subtract(status.PBLOUT_PAY_S_D.sum()))
                                            .add(status.PBLOUT_FREE_S_I.sum().subtract(status.PBLOUT_FREE_S_D.sum()))
                                            .add(status.PRV_S_I.sum().subtract(status.PRV_S_D.sum()))
                                            .add(status.SUBSE_SUR_S_I.sum().subtract(status.SUBSE_SUR_S_D.sum())
                                                    .add(status.SUBSE_MOD_S_I.sum().subtract(status.SUBSE_MOD_S_D.sum()))
                                                    .add(status.SUBAU_ATT_S_I.sum().subtract(status.SUBAU_ATT_S_D.sum()))
                                                    .add(status.SUBAU_PRV_S_I.sum().subtract(status.SUBAU_PRV_S_D.sum())))
                                            .add(status.OWN_HOME_S_I.sum().subtract(status.OWN_HOME_S_D.sum())
                                                    .add(status.OWN_APT_S_I.sum().subtract(status.OWN_APT_S_D.sum()))).as("TOTAL_S_SUM"),
                                    status.PBLRD_PAY_A_I.sum().subtract(status.PBLRD_PAY_A_D.sum())
                                            .add(status.PBLRD_FREE_A_I.sum().subtract(status.PBLRD_FREE_A_D.sum()))
                                            .add(status.PBLRD_RESI_A_I.sum().subtract(status.PBLRD_RESI_A_D.sum()))
                                            .add(status.PBLOUT_PAY_A_I.sum().subtract(status.PBLOUT_PAY_A_D.sum()))
                                            .add(status.PBLOUT_FREE_A_I.sum().subtract(status.PBLOUT_FREE_A_D.sum()))
                                            .add(status.PRV_A_I.sum().subtract(status.PRV_A_D.sum()))
                                            .add(status.SUBSE_SUR_A_I.sum().subtract(status.SUBSE_SUR_A_D.sum())
                                                    .add(status.SUBSE_MOD_A_I.sum().subtract(status.SUBSE_MOD_A_D.sum()))
                                                    .add(status.SUBAU_ATT_A_I.sum().subtract(status.SUBAU_ATT_A_D.sum()))
                                                    .add(status.SUBAU_PRV_A_I.sum().subtract(status.SUBAU_PRV_A_D.sum())))
                                            .add(status.OWN_HOME_A_I.sum().subtract(status.OWN_HOME_A_D.sum())
                                                    .add(status.OWN_APT_A_I.sum().subtract(status.OWN_APT_A_D.sum()))).as("TOTAL_A_SUM")
                            )
                    )
                    .from(status)
                    .where(
                            eqSggCd(thisMonth.getSggCd())
                            , loeLocalDt(thisMonth.getLocalDt())
                    )
                    .fetchOne();
            // double 타입이므로 모든 결과는 null 이 아닌 0. 클라이언트에서는 prev.year == null? 로 값이 유효한지 검사할 수 있습니다.
            map.put("prevMonth", prevTotal);
        }

        if (thisMonth != null) {
            PStatusDto.Total thisTotal = factory
                    .select(
                            Projections.bean(PStatusDto.Total.class,
//                              증감 합 계산
                                    status.PBLRD_PAY_L_I.sum().subtract(status.PBLRD_PAY_L_D.sum()).as("PBLRD_PAY_L_SUM"),
                                    status.PBLRD_PAY_S_I.sum().subtract(status.PBLRD_PAY_S_D.sum()).as("PBLRD_PAY_S_SUM"),
                                    status.PBLRD_PAY_A_I.sum().subtract(status.PBLRD_PAY_A_D.sum()).as("PBLRD_PAY_A_SUM"),
                                    status.PBLRD_FREE_L_I.sum().subtract(status.PBLRD_FREE_L_D.sum()).as("PBLRD_FREE_L_SUM"),
                                    status.PBLRD_FREE_S_I.sum().subtract(status.PBLRD_FREE_S_D.sum()).as("PBLRD_FREE_S_SUM"),
                                    status.PBLRD_FREE_A_I.sum().subtract(status.PBLRD_FREE_A_D.sum()).as("PBLRD_FREE_A_SUM"),
                                    //
                                    status.PBLRD_RESI_L_I.sum().subtract(status.PBLRD_RESI_L_D.sum()).as("PBLRD_RESI_L_SUM"),
                                    status.PBLRD_RESI_S_I.sum().subtract(status.PBLRD_RESI_S_D.sum()).as("PBLRD_RESI_S_SUM"),
                                    status.PBLRD_RESI_A_I.sum().subtract(status.PBLRD_RESI_A_D.sum()).as("PBLRD_RESI_A_SUM"),
                                    //
                                    status.PBLOUT_PAY_L_I.sum().subtract(status.PBLOUT_PAY_L_D.sum()).as("PBLOUT_PAY_L_SUM"),
                                    status.PBLOUT_PAY_S_I.sum().subtract(status.PBLOUT_PAY_S_D.sum()).as("PBLOUT_PAY_S_SUM"),
                                    status.PBLOUT_PAY_A_I.sum().subtract(status.PBLOUT_PAY_A_D.sum()).as("PBLOUT_PAY_A_SUM"),
                                    status.PBLOUT_FREE_L_I.sum().subtract(status.PBLOUT_FREE_L_D.sum()).as("PBLOUT_FREE_L_SUM"),
                                    status.PBLOUT_FREE_S_I.sum().subtract(status.PBLOUT_FREE_S_D.sum()).as("PBLOUT_FREE_S_SUM"),
                                    status.PBLOUT_FREE_A_I.sum().subtract(status.PBLOUT_FREE_A_D.sum()).as("PBLOUT_FREE_A_SUM"),
                                    // 민영주차장은 다른 분류가 없으므로 각각의 합이 곧 소계
                                    status.PRV_L_I.sum().subtract(status.PRV_L_D.sum()).as("PRV_L_SUM"),
                                    status.PRV_S_I.sum().subtract(status.PRV_S_D.sum()).as("PRV_S_SUM"),
                                    status.PRV_A_I.sum().subtract(status.PRV_A_D.sum()).as("PRV_A_SUM"),
                                    //
                                    status.SUBSE_SUR_L_I.sum().subtract(status.SUBSE_SUR_L_D.sum()).as("SUBSE_SUR_L_SUM"),
                                    status.SUBSE_SUR_S_I.sum().subtract(status.SUBSE_SUR_S_D.sum()).as("SUBSE_SUR_S_SUM"),
                                    status.SUBSE_SUR_A_I.sum().subtract(status.SUBSE_SUR_A_D.sum()).as("SUBSE_SUR_A_SUM"),
                                    status.SUBSE_MOD_L_I.sum().subtract(status.SUBSE_MOD_L_D.sum()).as("SUBSE_MOD_L_SUM"),
                                    status.SUBSE_MOD_S_I.sum().subtract(status.SUBSE_MOD_S_D.sum()).as("SUBSE_MOD_S_SUM"),
                                    status.SUBSE_MOD_A_I.sum().subtract(status.SUBSE_MOD_A_D.sum()).as("SUBSE_MOD_A_SUM"),

                                    status.SUBAU_ATT_L_I.sum().subtract(status.SUBAU_ATT_L_D.sum()).as("SUBAU_ATT_L_SUM"),
                                    status.SUBAU_ATT_S_I.sum().subtract(status.SUBAU_ATT_S_D.sum()).as("SUBAU_ATT_S_SUM"),
                                    status.SUBAU_ATT_A_I.sum().subtract(status.SUBAU_ATT_A_D.sum()).as("SUBAU_ATT_A_SUM"),
                                    status.SUBAU_PRV_L_I.sum().subtract(status.SUBAU_PRV_L_D.sum()).as("SUBAU_PRV_L_SUM"),
                                    status.SUBAU_PRV_S_I.sum().subtract(status.SUBAU_PRV_S_D.sum()).as("SUBAU_PRV_S_SUM"),
                                    status.SUBAU_PRV_A_I.sum().subtract(status.SUBAU_PRV_A_D.sum()).as("SUBAU_PRV_A_SUM"),

                                    status.OWN_HOME_L_I.sum().subtract(status.OWN_HOME_L_D.sum()).as("OWN_HOME_L_SUM"),
                                    status.OWN_HOME_S_I.sum().subtract(status.OWN_HOME_S_D.sum()).as("OWN_HOME_S_SUM"),
                                    status.OWN_HOME_A_I.sum().subtract(status.OWN_HOME_A_D.sum()).as("OWN_HOME_A_SUM"),
                                    status.OWN_APT_L_I.sum().subtract(status.OWN_APT_L_D.sum()).as("OWN_APT_L_SUM"),
                                    status.OWN_APT_S_I.sum().subtract(status.OWN_APT_S_D.sum()).as("OWN_APT_S_SUM"),
                                    status.OWN_APT_A_I.sum().subtract(status.OWN_APT_A_D.sum()).as("OWN_APT_A_SUM"),
//                                subtotal
                                    status.PBLRD_PAY_L_I.sum().subtract(status.PBLRD_PAY_L_D.sum())
                                            .add(status.PBLRD_FREE_L_I.sum().subtract(status.PBLRD_FREE_L_D.sum()))
                                            .add(status.PBLRD_RESI_L_I.sum().subtract(status.PBLRD_RESI_L_D.sum()))
                                            .add(status.PBLOUT_PAY_L_I.sum().subtract(status.PBLOUT_PAY_L_D.sum()))
                                            .add(status.PBLOUT_FREE_L_I.sum().subtract(status.PBLOUT_FREE_L_D.sum()))
                                            .as("PBL_L_SUBTOTAL"),
                                    status.PBLRD_PAY_S_I.sum().subtract(status.PBLRD_PAY_S_D.sum())
                                            .add(status.PBLRD_FREE_S_I.sum().subtract(status.PBLRD_FREE_S_D.sum()))
                                            .add(status.PBLRD_RESI_S_I.sum().subtract(status.PBLRD_RESI_S_D.sum()))
                                            .add(status.PBLOUT_PAY_S_I.sum().subtract(status.PBLOUT_PAY_S_D.sum()))
                                            .add(status.PBLOUT_FREE_S_I.sum().subtract(status.PBLOUT_FREE_S_D.sum()))
                                            .as("PBL_S_SUBTOTAL"),
                                    status.PBLRD_PAY_A_I.sum().subtract(status.PBLRD_PAY_A_D.sum())
                                            .add(status.PBLRD_FREE_A_I.sum().subtract(status.PBLRD_FREE_A_D.sum()))
                                            .add(status.PBLRD_RESI_A_I.sum().subtract(status.PBLRD_RESI_A_D.sum()))
                                            .add(status.PBLOUT_PAY_A_I.sum().subtract(status.PBLOUT_PAY_A_D.sum()))
                                            .add(status.PBLOUT_FREE_A_I.sum().subtract(status.PBLOUT_FREE_A_D.sum()))
                                            .as("PBL_A_SUBTOTAL"),
                                    status.SUBSE_SUR_L_I.sum().subtract(status.SUBSE_SUR_L_D.sum())
                                            .add(status.SUBSE_MOD_L_I.sum().subtract(status.SUBSE_MOD_L_D.sum()))
                                            .add(status.SUBAU_ATT_L_I.sum().subtract(status.SUBAU_ATT_L_D.sum()))
                                            .add(status.SUBAU_PRV_L_I.sum().subtract(status.SUBAU_PRV_L_D.sum()))
                                            .as("SUB_L_SUBTOTAL"),
                                    //
                                    status.SUBSE_SUR_S_I.sum().subtract(status.SUBSE_SUR_S_D.sum())
                                            .add(status.SUBSE_MOD_S_I.sum().subtract(status.SUBSE_MOD_S_D.sum()))
                                            .add(status.SUBAU_ATT_S_I.sum().subtract(status.SUBAU_ATT_S_D.sum()))
                                            .add(status.SUBAU_PRV_S_I.sum().subtract(status.SUBAU_PRV_S_D.sum()))
                                            .as("SUB_S_SUBTOTAL"),
                                    status.SUBSE_SUR_A_I.sum().subtract(status.SUBSE_SUR_A_D.sum())
                                            .add(status.SUBSE_MOD_A_I.sum().subtract(status.SUBSE_MOD_A_D.sum()))
                                            .add(status.SUBAU_ATT_A_I.sum().subtract(status.SUBAU_ATT_A_D.sum()))
                                            .add(status.SUBAU_PRV_A_I.sum().subtract(status.SUBAU_PRV_A_D.sum()))
                                            .as("SUB_A_SUBTOTAL"),
                                    //
                                    status.OWN_HOME_L_I.sum().subtract(status.OWN_HOME_L_D.sum())
                                            .add(status.OWN_APT_L_I.sum().subtract(status.OWN_APT_L_D.sum())).as("OWN_L_SUBTOTAL"),
                                    status.OWN_HOME_S_I.sum().subtract(status.OWN_HOME_S_D.sum())
                                            .add(status.OWN_APT_S_I.sum().subtract(status.OWN_APT_S_D.sum())).as("OWN_S_SUBTOTAL"),
                                    status.OWN_HOME_A_I.sum().subtract(status.OWN_HOME_A_D.sum())
                                            .add(status.OWN_APT_A_I.sum().subtract(status.OWN_APT_A_D.sum())).as("OWN_A_SUBTOTAL"),
                                    // total= 공영 + 민영 + 부설 + 자가
                                    status.PBLRD_PAY_L_I.sum().subtract(status.PBLRD_PAY_L_D.sum())
                                            .add(status.PBLRD_FREE_L_I.sum().subtract(status.PBLRD_FREE_L_D.sum()))
                                            .add(status.PBLRD_RESI_L_I.sum().subtract(status.PBLRD_RESI_L_D.sum()))
                                            .add(status.PBLOUT_PAY_L_I.sum().subtract(status.PBLOUT_PAY_L_D.sum()))
                                            .add(status.PBLOUT_FREE_L_I.sum().subtract(status.PBLOUT_FREE_L_D.sum()))
                                            .add(status.PRV_L_I.sum().subtract(status.PRV_L_D.sum()))
                                            .add(status.SUBSE_SUR_L_I.sum().subtract(status.SUBSE_SUR_L_D.sum())
                                                    .add(status.SUBSE_MOD_L_I.sum().subtract(status.SUBSE_MOD_L_D.sum()))
                                                    .add(status.SUBAU_ATT_L_I.sum().subtract(status.SUBAU_ATT_L_D.sum()))
                                                    .add(status.SUBAU_PRV_L_I.sum().subtract(status.SUBAU_PRV_L_D.sum())))
                                            .add(status.OWN_HOME_L_I.sum().subtract(status.OWN_HOME_L_D.sum())
                                                    .add(status.OWN_APT_L_I.sum().subtract(status.OWN_APT_L_D.sum()))).as("TOTAL_L_SUM"),

                                    status.PBLRD_PAY_S_I.sum().subtract(status.PBLRD_PAY_S_D.sum())
                                            .add(status.PBLRD_FREE_S_I.sum().subtract(status.PBLRD_FREE_S_D.sum()))
                                            .add(status.PBLRD_RESI_S_I.sum().subtract(status.PBLRD_RESI_S_D.sum()))
                                            .add(status.PBLOUT_PAY_S_I.sum().subtract(status.PBLOUT_PAY_S_D.sum()))
                                            .add(status.PBLOUT_FREE_S_I.sum().subtract(status.PBLOUT_FREE_S_D.sum()))
                                            .add(status.PRV_S_I.sum().subtract(status.PRV_S_D.sum()))
                                            .add(status.SUBSE_SUR_S_I.sum().subtract(status.SUBSE_SUR_S_D.sum())
                                                    .add(status.SUBSE_MOD_S_I.sum().subtract(status.SUBSE_MOD_S_D.sum()))
                                                    .add(status.SUBAU_ATT_S_I.sum().subtract(status.SUBAU_ATT_S_D.sum()))
                                                    .add(status.SUBAU_PRV_S_I.sum().subtract(status.SUBAU_PRV_S_D.sum())))
                                            .add(status.OWN_HOME_S_I.sum().subtract(status.OWN_HOME_S_D.sum())
                                                    .add(status.OWN_APT_S_I.sum().subtract(status.OWN_APT_S_D.sum()))).as("TOTAL_S_SUM"),
                                    status.PBLRD_PAY_A_I.sum().subtract(status.PBLRD_PAY_A_D.sum())
                                            .add(status.PBLRD_FREE_A_I.sum().subtract(status.PBLRD_FREE_A_D.sum()))
                                            .add(status.PBLRD_RESI_A_I.sum().subtract(status.PBLRD_RESI_A_D.sum()))
                                            .add(status.PBLOUT_PAY_A_I.sum().subtract(status.PBLOUT_PAY_A_D.sum()))
                                            .add(status.PBLOUT_FREE_A_I.sum().subtract(status.PBLOUT_FREE_A_D.sum()))
                                            .add(status.PRV_A_I.sum().subtract(status.PRV_A_D.sum()))
                                            .add(status.SUBSE_SUR_A_I.sum().subtract(status.SUBSE_SUR_A_D.sum())
                                                    .add(status.SUBSE_MOD_A_I.sum().subtract(status.SUBSE_MOD_A_D.sum()))
                                                    .add(status.SUBAU_ATT_A_I.sum().subtract(status.SUBAU_ATT_A_D.sum()))
                                                    .add(status.SUBAU_PRV_A_I.sum().subtract(status.SUBAU_PRV_A_D.sum())))
                                            .add(status.OWN_HOME_A_I.sum().subtract(status.OWN_HOME_A_D.sum())
                                                    .add(status.OWN_APT_A_I.sum().subtract(status.OWN_APT_A_D.sum()))).as("TOTAL_A_SUM")
                            )
                    )
                    .from(status)
                    .where(
                            eqSggCd(thisMonth.getSggCd())
                    )
                    .fetchOne();
            // double 타입이므로 모든 결과는 null 이 아닌 0. 클라이언트에서는 prev.year == null? 로 값이 유효한지 검사할 수 있습니다.
            map.put("thisMonth", thisTotal);
        }

//        4) res
        return map;
    }

    public PStatus thisMonthStatus(PStatusDto.Keyword req) {
        PStatus thisMonth;
        if (req.getMonth() == null || req.getMonth().equals("")) {
            thisMonth = factory
                    .selectFrom(status)
                    .where(
                            eqYear(req.getYear())
                            , eqMonth(req.getMonth())
                            , eqSggCd(req.getSggCd())
                    )
                    .orderBy(status.localDt.desc())
                    .limit(1)
                    .fetchOne();
        } else {
            try {
                thisMonth = factory
                        .selectFrom(status)
                        .where(
                                eqYear(req.getYear())
                                , eqMonth(req.getMonth())
                                , eqSggCd(req.getSggCd())
                        )
                        .fetchOne();
            } catch (NonUniqueResultException e) {
                throw new RuntimeException(e);
            }
        }
        return thisMonth;
    }

    public PStatusDto.Total prevTotal(PStatusDto.Keyword req) {
        PStatus thisMonth = repo.findById(new PStatusPk(req.getYear(), req.getMonth(), req.getSggCd())).orElse(null);
        if (thisMonth == null) return null;

        return factory
                .select(
                        Projections.bean(PStatusDto.Total.class,
//                              증감 합 계산
                                status.PBLRD_PAY_L_I.sum().subtract(status.PBLRD_PAY_L_D.sum()).as("PBLRD_PAY_L_SUM"),
                                status.PBLRD_PAY_S_I.sum().subtract(status.PBLRD_PAY_S_D.sum()).as("PBLRD_PAY_S_SUM"),
                                status.PBLRD_PAY_A_I.sum().subtract(status.PBLRD_PAY_A_D.sum()).as("PBLRD_PAY_A_SUM"),
                                status.PBLRD_FREE_L_I.sum().subtract(status.PBLRD_FREE_L_D.sum()).as("PBLRD_FREE_L_SUM"),
                                status.PBLRD_FREE_S_I.sum().subtract(status.PBLRD_FREE_S_D.sum()).as("PBLRD_FREE_S_SUM"),
                                status.PBLRD_FREE_A_I.sum().subtract(status.PBLRD_FREE_A_D.sum()).as("PBLRD_FREE_A_SUM"),
                                //
                                status.PBLRD_RESI_L_I.sum().subtract(status.PBLRD_RESI_L_D.sum()).as("PBLRD_RESI_L_SUM"),
                                status.PBLRD_RESI_S_I.sum().subtract(status.PBLRD_RESI_S_D.sum()).as("PBLRD_RESI_S_SUM"),
                                status.PBLRD_RESI_A_I.sum().subtract(status.PBLRD_RESI_A_D.sum()).as("PBLRD_RESI_A_SUM"),
                                //
                                status.PBLOUT_PAY_L_I.sum().subtract(status.PBLOUT_PAY_L_D.sum()).as("PBLOUT_PAY_L_SUM"),
                                status.PBLOUT_PAY_S_I.sum().subtract(status.PBLOUT_PAY_S_D.sum()).as("PBLOUT_PAY_S_SUM"),
                                status.PBLOUT_PAY_A_I.sum().subtract(status.PBLOUT_PAY_A_D.sum()).as("PBLOUT_PAY_A_SUM"),
                                status.PBLOUT_FREE_L_I.sum().subtract(status.PBLOUT_FREE_L_D.sum()).as("PBLOUT_FREE_L_SUM"),
                                status.PBLOUT_FREE_S_I.sum().subtract(status.PBLOUT_FREE_S_D.sum()).as("PBLOUT_FREE_S_SUM"),
                                status.PBLOUT_FREE_A_I.sum().subtract(status.PBLOUT_FREE_A_D.sum()).as("PBLOUT_FREE_A_SUM"),
                                // 민영주차장은 다른 분류가 없으므로 각각의 합이 곧 소계
                                status.PRV_L_I.sum().subtract(status.PRV_L_D.sum()).as("PRV_L_SUM"),
                                status.PRV_S_I.sum().subtract(status.PRV_S_D.sum()).as("PRV_S_SUM"),
                                status.PRV_A_I.sum().subtract(status.PRV_A_D.sum()).as("PRV_A_SUM"),
                                //
                                status.SUBSE_SUR_L_I.sum().subtract(status.SUBSE_SUR_L_D.sum()).as("SUBSE_SUR_L_SUM"),
                                status.SUBSE_SUR_S_I.sum().subtract(status.SUBSE_SUR_S_D.sum()).as("SUBSE_SUR_S_SUM"),
                                status.SUBSE_SUR_A_I.sum().subtract(status.SUBSE_SUR_A_D.sum()).as("SUBSE_SUR_A_SUM"),
                                status.SUBSE_MOD_L_I.sum().subtract(status.SUBSE_MOD_L_D.sum()).as("SUBSE_MOD_L_SUM"),
                                status.SUBSE_MOD_S_I.sum().subtract(status.SUBSE_MOD_S_D.sum()).as("SUBSE_MOD_S_SUM"),
                                status.SUBSE_MOD_A_I.sum().subtract(status.SUBSE_MOD_A_D.sum()).as("SUBSE_MOD_A_SUM"),

                                status.SUBAU_ATT_L_I.sum().subtract(status.SUBAU_ATT_L_D.sum()).as("SUBAU_ATT_L_SUM"),
                                status.SUBAU_ATT_S_I.sum().subtract(status.SUBAU_ATT_S_D.sum()).as("SUBAU_ATT_S_SUM"),
                                status.SUBAU_ATT_A_I.sum().subtract(status.SUBAU_ATT_A_D.sum()).as("SUBAU_ATT_A_SUM"),
                                status.SUBAU_PRV_L_I.sum().subtract(status.SUBAU_PRV_L_D.sum()).as("SUBAU_PRV_L_SUM"),
                                status.SUBAU_PRV_S_I.sum().subtract(status.SUBAU_PRV_S_D.sum()).as("SUBAU_PRV_S_SUM"),
                                status.SUBAU_PRV_A_I.sum().subtract(status.SUBAU_PRV_A_D.sum()).as("SUBAU_PRV_A_SUM"),

                                status.OWN_HOME_L_I.sum().subtract(status.OWN_HOME_L_D.sum()).as("OWN_HOME_L_SUM"),
                                status.OWN_HOME_S_I.sum().subtract(status.OWN_HOME_S_D.sum()).as("OWN_HOME_S_SUM"),
                                status.OWN_HOME_A_I.sum().subtract(status.OWN_HOME_A_D.sum()).as("OWN_HOME_A_SUM"),
                                status.OWN_APT_L_I.sum().subtract(status.OWN_APT_L_D.sum()).as("OWN_APT_L_SUM"),
                                status.OWN_APT_S_I.sum().subtract(status.OWN_APT_S_D.sum()).as("OWN_APT_S_SUM"),
                                status.OWN_APT_A_I.sum().subtract(status.OWN_APT_A_D.sum()).as("OWN_APT_A_SUM"),
//                                subtotal
                                status.PBLRD_PAY_L_I.sum().subtract(status.PBLRD_PAY_L_D.sum())
                                        .add(status.PBLRD_FREE_L_I.sum().subtract(status.PBLRD_FREE_L_D.sum()))
                                        .add(status.PBLRD_RESI_L_I.sum().subtract(status.PBLRD_RESI_L_D.sum()))
                                        .add(status.PBLOUT_PAY_L_I.sum().subtract(status.PBLOUT_PAY_L_D.sum()))
                                        .add(status.PBLOUT_FREE_L_I.sum().subtract(status.PBLOUT_FREE_L_D.sum()))
                                        .as("PBL_L_SUBTOTAL"),
                                status.PBLRD_PAY_S_I.sum().subtract(status.PBLRD_PAY_S_D.sum())
                                        .add(status.PBLRD_FREE_S_I.sum().subtract(status.PBLRD_FREE_S_D.sum()))
                                        .add(status.PBLRD_RESI_S_I.sum().subtract(status.PBLRD_RESI_S_D.sum()))
                                        .add(status.PBLOUT_PAY_S_I.sum().subtract(status.PBLOUT_PAY_S_D.sum()))
                                        .add(status.PBLOUT_FREE_S_I.sum().subtract(status.PBLOUT_FREE_S_D.sum()))
                                        .as("PBL_S_SUBTOTAL"),
                                status.PBLRD_PAY_A_I.sum().subtract(status.PBLRD_PAY_A_D.sum())
                                        .add(status.PBLRD_FREE_A_I.sum().subtract(status.PBLRD_FREE_A_D.sum()))
                                        .add(status.PBLRD_RESI_A_I.sum().subtract(status.PBLRD_RESI_A_D.sum()))
                                        .add(status.PBLOUT_PAY_A_I.sum().subtract(status.PBLOUT_PAY_A_D.sum()))
                                        .add(status.PBLOUT_FREE_A_I.sum().subtract(status.PBLOUT_FREE_A_D.sum()))
                                        .as("PBL_A_SUBTOTAL"),
                                status.SUBSE_SUR_L_I.sum().subtract(status.SUBSE_SUR_L_D.sum())
                                        .add(status.SUBSE_MOD_L_I.sum().subtract(status.SUBSE_MOD_L_D.sum()))
                                        .add(status.SUBAU_ATT_L_I.sum().subtract(status.SUBAU_ATT_L_D.sum()))
                                        .add(status.SUBAU_PRV_L_I.sum().subtract(status.SUBAU_PRV_L_D.sum()))
                                        .as("SUB_L_SUBTOTAL"),
                                //
                                status.SUBSE_SUR_S_I.sum().subtract(status.SUBSE_SUR_S_D.sum())
                                        .add(status.SUBSE_MOD_S_I.sum().subtract(status.SUBSE_MOD_S_D.sum()))
                                        .add(status.SUBAU_ATT_S_I.sum().subtract(status.SUBAU_ATT_S_D.sum()))
                                        .add(status.SUBAU_PRV_S_I.sum().subtract(status.SUBAU_PRV_S_D.sum()))
                                        .as("SUB_S_SUBTOTAL"),
                                status.SUBSE_SUR_A_I.sum().subtract(status.SUBSE_SUR_A_D.sum())
                                        .add(status.SUBSE_MOD_A_I.sum().subtract(status.SUBSE_MOD_A_D.sum()))
                                        .add(status.SUBAU_ATT_A_I.sum().subtract(status.SUBAU_ATT_A_D.sum()))
                                        .add(status.SUBAU_PRV_A_I.sum().subtract(status.SUBAU_PRV_A_D.sum()))
                                        .as("SUB_A_SUBTOTAL"),
                                //
                                status.OWN_HOME_L_I.sum().subtract(status.OWN_HOME_L_D.sum())
                                        .add(status.OWN_APT_L_I.sum().subtract(status.OWN_APT_L_D.sum())).as("OWN_L_SUBTOTAL"),
                                status.OWN_HOME_S_I.sum().subtract(status.OWN_HOME_S_D.sum())
                                        .add(status.OWN_APT_S_I.sum().subtract(status.OWN_APT_S_D.sum())).as("OWN_S_SUBTOTAL"),
                                status.OWN_HOME_A_I.sum().subtract(status.OWN_HOME_A_D.sum())
                                        .add(status.OWN_APT_A_I.sum().subtract(status.OWN_APT_A_D.sum())).as("OWN_A_SUBTOTAL"),
                                // total= 공영 + 민영 + 부설 + 자가
                                status.PBLRD_PAY_L_I.sum().subtract(status.PBLRD_PAY_L_D.sum())
                                        .add(status.PBLRD_FREE_L_I.sum().subtract(status.PBLRD_FREE_L_D.sum()))
                                        .add(status.PBLRD_RESI_L_I.sum().subtract(status.PBLRD_RESI_L_D.sum()))
                                        .add(status.PBLOUT_PAY_L_I.sum().subtract(status.PBLOUT_PAY_L_D.sum()))
                                        .add(status.PBLOUT_FREE_L_I.sum().subtract(status.PBLOUT_FREE_L_D.sum()))
                                        .add(status.PRV_L_I.sum().subtract(status.PRV_L_D.sum()))
                                        .add(status.SUBSE_SUR_L_I.sum().subtract(status.SUBSE_SUR_L_D.sum())
                                                .add(status.SUBSE_MOD_L_I.sum().subtract(status.SUBSE_MOD_L_D.sum()))
                                                .add(status.SUBAU_ATT_L_I.sum().subtract(status.SUBAU_ATT_L_D.sum()))
                                                .add(status.SUBAU_PRV_L_I.sum().subtract(status.SUBAU_PRV_L_D.sum())))
                                        .add(status.OWN_HOME_L_I.sum().subtract(status.OWN_HOME_L_D.sum())
                                                .add(status.OWN_APT_L_I.sum().subtract(status.OWN_APT_L_D.sum()))).as("TOTAL_L_SUM"),

                                status.PBLRD_PAY_S_I.sum().subtract(status.PBLRD_PAY_S_D.sum())
                                        .add(status.PBLRD_FREE_S_I.sum().subtract(status.PBLRD_FREE_S_D.sum()))
                                        .add(status.PBLRD_RESI_S_I.sum().subtract(status.PBLRD_RESI_S_D.sum()))
                                        .add(status.PBLOUT_PAY_S_I.sum().subtract(status.PBLOUT_PAY_S_D.sum()))
                                        .add(status.PBLOUT_FREE_S_I.sum().subtract(status.PBLOUT_FREE_S_D.sum()))
                                        .add(status.PRV_S_I.sum().subtract(status.PRV_S_D.sum()))
                                        .add(status.SUBSE_SUR_S_I.sum().subtract(status.SUBSE_SUR_S_D.sum())
                                                .add(status.SUBSE_MOD_S_I.sum().subtract(status.SUBSE_MOD_S_D.sum()))
                                                .add(status.SUBAU_ATT_S_I.sum().subtract(status.SUBAU_ATT_S_D.sum()))
                                                .add(status.SUBAU_PRV_S_I.sum().subtract(status.SUBAU_PRV_S_D.sum())))
                                        .add(status.OWN_HOME_S_I.sum().subtract(status.OWN_HOME_S_D.sum())
                                                .add(status.OWN_APT_S_I.sum().subtract(status.OWN_APT_S_D.sum()))).as("TOTAL_S_SUM"),
                                status.PBLRD_PAY_A_I.sum().subtract(status.PBLRD_PAY_A_D.sum())
                                        .add(status.PBLRD_FREE_A_I.sum().subtract(status.PBLRD_FREE_A_D.sum()))
                                        .add(status.PBLRD_RESI_A_I.sum().subtract(status.PBLRD_RESI_A_D.sum()))
                                        .add(status.PBLOUT_PAY_A_I.sum().subtract(status.PBLOUT_PAY_A_D.sum()))
                                        .add(status.PBLOUT_FREE_A_I.sum().subtract(status.PBLOUT_FREE_A_D.sum()))
                                        .add(status.PRV_A_I.sum().subtract(status.PRV_A_D.sum()))
                                        .add(status.SUBSE_SUR_A_I.sum().subtract(status.SUBSE_SUR_A_D.sum())
                                                .add(status.SUBSE_MOD_A_I.sum().subtract(status.SUBSE_MOD_A_D.sum()))
                                                .add(status.SUBAU_ATT_A_I.sum().subtract(status.SUBAU_ATT_A_D.sum()))
                                                .add(status.SUBAU_PRV_A_I.sum().subtract(status.SUBAU_PRV_A_D.sum())))
                                        .add(status.OWN_HOME_A_I.sum().subtract(status.OWN_HOME_A_D.sum())
                                                .add(status.OWN_APT_A_I.sum().subtract(status.OWN_APT_A_D.sum()))).as("TOTAL_A_SUM")
                        )
                )
                .from(status)
                .where(
                        eqSggCd(thisMonth.getSggCd())
                        , loeLocalDt(thisMonth.getLocalDt())
                )
                .fetchOne();
    }

    public PStatusDto.Total thisTotal(PStatusDto.Keyword req) {
        PStatus thisMonth;
        if (req.getMonth()== null || req.getMonth().equals("")){
            thisMonth = factory
                    .selectFrom(status)
                    .where(
                            eqYear(req.getYear())
                            , eqMonth(req.getMonth())
                            , eqSggCd(req.getSggCd())
                    )
                    .orderBy(status.localDt.desc())
                    .limit(1)
                    .fetchOne();
        }
        else {
            thisMonth = repo.findById(new PStatusPk(req.getYear(), req.getMonth(), req.getSggCd())).orElse(null);
        }

        if (thisMonth == null) {
            //return null;
            PStatusDto.Total blankTotal = new PStatusDto.Total();
            return blankTotal;
        }

        return factory
                .select(
                        Projections.bean(PStatusDto.Total.class,
//                              증감 합 계산
                                status.PBLRD_PAY_L_I.sum().subtract(status.PBLRD_PAY_L_D.sum()).as("PBLRD_PAY_L_SUM"),
                                status.PBLRD_PAY_S_I.sum().subtract(status.PBLRD_PAY_S_D.sum()).as("PBLRD_PAY_S_SUM"),
                                status.PBLRD_PAY_A_I.sum().subtract(status.PBLRD_PAY_A_D.sum()).as("PBLRD_PAY_A_SUM"),
                                status.PBLRD_FREE_L_I.sum().subtract(status.PBLRD_FREE_L_D.sum()).as("PBLRD_FREE_L_SUM"),
                                status.PBLRD_FREE_S_I.sum().subtract(status.PBLRD_FREE_S_D.sum()).as("PBLRD_FREE_S_SUM"),
                                status.PBLRD_FREE_A_I.sum().subtract(status.PBLRD_FREE_A_D.sum()).as("PBLRD_FREE_A_SUM"),
                                //
                                status.PBLRD_RESI_L_I.sum().subtract(status.PBLRD_RESI_L_D.sum()).as("PBLRD_RESI_L_SUM"),
                                status.PBLRD_RESI_S_I.sum().subtract(status.PBLRD_RESI_S_D.sum()).as("PBLRD_RESI_S_SUM"),
                                status.PBLRD_RESI_A_I.sum().subtract(status.PBLRD_RESI_A_D.sum()).as("PBLRD_RESI_A_SUM"),
                                //
                                status.PBLOUT_PAY_L_I.sum().subtract(status.PBLOUT_PAY_L_D.sum()).as("PBLOUT_PAY_L_SUM"),
                                status.PBLOUT_PAY_S_I.sum().subtract(status.PBLOUT_PAY_S_D.sum()).as("PBLOUT_PAY_S_SUM"),
                                status.PBLOUT_PAY_A_I.sum().subtract(status.PBLOUT_PAY_A_D.sum()).as("PBLOUT_PAY_A_SUM"),
                                status.PBLOUT_FREE_L_I.sum().subtract(status.PBLOUT_FREE_L_D.sum()).as("PBLOUT_FREE_L_SUM"),
                                status.PBLOUT_FREE_S_I.sum().subtract(status.PBLOUT_FREE_S_D.sum()).as("PBLOUT_FREE_S_SUM"),
                                status.PBLOUT_FREE_A_I.sum().subtract(status.PBLOUT_FREE_A_D.sum()).as("PBLOUT_FREE_A_SUM"),
                                // 민영주차장은 다른 분류가 없으므로 각각의 합이 곧 소계
                                status.PRV_L_I.sum().subtract(status.PRV_L_D.sum()).as("PRV_L_SUM"),
                                status.PRV_S_I.sum().subtract(status.PRV_S_D.sum()).as("PRV_S_SUM"),
                                status.PRV_A_I.sum().subtract(status.PRV_A_D.sum()).as("PRV_A_SUM"),
                                //
                                status.SUBSE_SUR_L_I.sum().subtract(status.SUBSE_SUR_L_D.sum()).as("SUBSE_SUR_L_SUM"),
                                status.SUBSE_SUR_S_I.sum().subtract(status.SUBSE_SUR_S_D.sum()).as("SUBSE_SUR_S_SUM"),
                                status.SUBSE_SUR_A_I.sum().subtract(status.SUBSE_SUR_A_D.sum()).as("SUBSE_SUR_A_SUM"),
                                status.SUBSE_MOD_L_I.sum().subtract(status.SUBSE_MOD_L_D.sum()).as("SUBSE_MOD_L_SUM"),
                                status.SUBSE_MOD_S_I.sum().subtract(status.SUBSE_MOD_S_D.sum()).as("SUBSE_MOD_S_SUM"),
                                status.SUBSE_MOD_A_I.sum().subtract(status.SUBSE_MOD_A_D.sum()).as("SUBSE_MOD_A_SUM"),

                                status.SUBAU_ATT_L_I.sum().subtract(status.SUBAU_ATT_L_D.sum()).as("SUBAU_ATT_L_SUM"),
                                status.SUBAU_ATT_S_I.sum().subtract(status.SUBAU_ATT_S_D.sum()).as("SUBAU_ATT_S_SUM"),
                                status.SUBAU_ATT_A_I.sum().subtract(status.SUBAU_ATT_A_D.sum()).as("SUBAU_ATT_A_SUM"),
                                status.SUBAU_PRV_L_I.sum().subtract(status.SUBAU_PRV_L_D.sum()).as("SUBAU_PRV_L_SUM"),
                                status.SUBAU_PRV_S_I.sum().subtract(status.SUBAU_PRV_S_D.sum()).as("SUBAU_PRV_S_SUM"),
                                status.SUBAU_PRV_A_I.sum().subtract(status.SUBAU_PRV_A_D.sum()).as("SUBAU_PRV_A_SUM"),

                                status.OWN_HOME_L_I.sum().subtract(status.OWN_HOME_L_D.sum()).as("OWN_HOME_L_SUM"),
                                status.OWN_HOME_S_I.sum().subtract(status.OWN_HOME_S_D.sum()).as("OWN_HOME_S_SUM"),
                                status.OWN_HOME_A_I.sum().subtract(status.OWN_HOME_A_D.sum()).as("OWN_HOME_A_SUM"),
                                status.OWN_APT_L_I.sum().subtract(status.OWN_APT_L_D.sum()).as("OWN_APT_L_SUM"),
                                status.OWN_APT_S_I.sum().subtract(status.OWN_APT_S_D.sum()).as("OWN_APT_S_SUM"),
                                status.OWN_APT_A_I.sum().subtract(status.OWN_APT_A_D.sum()).as("OWN_APT_A_SUM"),
//                                subtotal
                                status.PBLRD_PAY_L_I.sum().subtract(status.PBLRD_PAY_L_D.sum())
                                        .add(status.PBLRD_FREE_L_I.sum().subtract(status.PBLRD_FREE_L_D.sum()))
                                        .add(status.PBLRD_RESI_L_I.sum().subtract(status.PBLRD_RESI_L_D.sum()))
                                        .add(status.PBLOUT_PAY_L_I.sum().subtract(status.PBLOUT_PAY_L_D.sum()))
                                        .add(status.PBLOUT_FREE_L_I.sum().subtract(status.PBLOUT_FREE_L_D.sum()))
                                        .as("PBL_L_SUBTOTAL"),
                                status.PBLRD_PAY_S_I.sum().subtract(status.PBLRD_PAY_S_D.sum())
                                        .add(status.PBLRD_FREE_S_I.sum().subtract(status.PBLRD_FREE_S_D.sum()))
                                        .add(status.PBLRD_RESI_S_I.sum().subtract(status.PBLRD_RESI_S_D.sum()))
                                        .add(status.PBLOUT_PAY_S_I.sum().subtract(status.PBLOUT_PAY_S_D.sum()))
                                        .add(status.PBLOUT_FREE_S_I.sum().subtract(status.PBLOUT_FREE_S_D.sum()))
                                        .as("PBL_S_SUBTOTAL"),
                                status.PBLRD_PAY_A_I.sum().subtract(status.PBLRD_PAY_A_D.sum())
                                        .add(status.PBLRD_FREE_A_I.sum().subtract(status.PBLRD_FREE_A_D.sum()))
                                        .add(status.PBLRD_RESI_A_I.sum().subtract(status.PBLRD_RESI_A_D.sum()))
                                        .add(status.PBLOUT_PAY_A_I.sum().subtract(status.PBLOUT_PAY_A_D.sum()))
                                        .add(status.PBLOUT_FREE_A_I.sum().subtract(status.PBLOUT_FREE_A_D.sum()))
                                        .as("PBL_A_SUBTOTAL"),
                                status.SUBSE_SUR_L_I.sum().subtract(status.SUBSE_SUR_L_D.sum())
                                        .add(status.SUBSE_MOD_L_I.sum().subtract(status.SUBSE_MOD_L_D.sum()))
                                        .add(status.SUBAU_ATT_L_I.sum().subtract(status.SUBAU_ATT_L_D.sum()))
                                        .add(status.SUBAU_PRV_L_I.sum().subtract(status.SUBAU_PRV_L_D.sum()))
                                        .as("SUB_L_SUBTOTAL"),
                                //
                                status.SUBSE_SUR_S_I.sum().subtract(status.SUBSE_SUR_S_D.sum())
                                        .add(status.SUBSE_MOD_S_I.sum().subtract(status.SUBSE_MOD_S_D.sum()))
                                        .add(status.SUBAU_ATT_S_I.sum().subtract(status.SUBAU_ATT_S_D.sum()))
                                        .add(status.SUBAU_PRV_S_I.sum().subtract(status.SUBAU_PRV_S_D.sum()))
                                        .as("SUB_S_SUBTOTAL"),
                                status.SUBSE_SUR_A_I.sum().subtract(status.SUBSE_SUR_A_D.sum())
                                        .add(status.SUBSE_MOD_A_I.sum().subtract(status.SUBSE_MOD_A_D.sum()))
                                        .add(status.SUBAU_ATT_A_I.sum().subtract(status.SUBAU_ATT_A_D.sum()))
                                        .add(status.SUBAU_PRV_A_I.sum().subtract(status.SUBAU_PRV_A_D.sum()))
                                        .as("SUB_A_SUBTOTAL"),
                                //
                                status.OWN_HOME_L_I.sum().subtract(status.OWN_HOME_L_D.sum())
                                        .add(status.OWN_APT_L_I.sum().subtract(status.OWN_APT_L_D.sum())).as("OWN_L_SUBTOTAL"),
                                status.OWN_HOME_S_I.sum().subtract(status.OWN_HOME_S_D.sum())
                                        .add(status.OWN_APT_S_I.sum().subtract(status.OWN_APT_S_D.sum())).as("OWN_S_SUBTOTAL"),
                                status.OWN_HOME_A_I.sum().subtract(status.OWN_HOME_A_D.sum())
                                        .add(status.OWN_APT_A_I.sum().subtract(status.OWN_APT_A_D.sum())).as("OWN_A_SUBTOTAL"),
                                // total= 공영 + 민영 + 부설 + 자가
                                status.PBLRD_PAY_L_I.sum().subtract(status.PBLRD_PAY_L_D.sum())
                                        .add(status.PBLRD_FREE_L_I.sum().subtract(status.PBLRD_FREE_L_D.sum()))
                                        .add(status.PBLRD_RESI_L_I.sum().subtract(status.PBLRD_RESI_L_D.sum()))
                                        .add(status.PBLOUT_PAY_L_I.sum().subtract(status.PBLOUT_PAY_L_D.sum()))
                                        .add(status.PBLOUT_FREE_L_I.sum().subtract(status.PBLOUT_FREE_L_D.sum()))
                                        .add(status.PRV_L_I.sum().subtract(status.PRV_L_D.sum()))
                                        .add(status.SUBSE_SUR_L_I.sum().subtract(status.SUBSE_SUR_L_D.sum())
                                                .add(status.SUBSE_MOD_L_I.sum().subtract(status.SUBSE_MOD_L_D.sum()))
                                                .add(status.SUBAU_ATT_L_I.sum().subtract(status.SUBAU_ATT_L_D.sum()))
                                                .add(status.SUBAU_PRV_L_I.sum().subtract(status.SUBAU_PRV_L_D.sum())))
                                        .add(status.OWN_HOME_L_I.sum().subtract(status.OWN_HOME_L_D.sum())
                                                .add(status.OWN_APT_L_I.sum().subtract(status.OWN_APT_L_D.sum()))).as("TOTAL_L_SUM"),

                                status.PBLRD_PAY_S_I.sum().subtract(status.PBLRD_PAY_S_D.sum())
                                        .add(status.PBLRD_FREE_S_I.sum().subtract(status.PBLRD_FREE_S_D.sum()))
                                        .add(status.PBLRD_RESI_S_I.sum().subtract(status.PBLRD_RESI_S_D.sum()))
                                        .add(status.PBLOUT_PAY_S_I.sum().subtract(status.PBLOUT_PAY_S_D.sum()))
                                        .add(status.PBLOUT_FREE_S_I.sum().subtract(status.PBLOUT_FREE_S_D.sum()))
                                        .add(status.PRV_S_I.sum().subtract(status.PRV_S_D.sum()))
                                        .add(status.SUBSE_SUR_S_I.sum().subtract(status.SUBSE_SUR_S_D.sum())
                                                .add(status.SUBSE_MOD_S_I.sum().subtract(status.SUBSE_MOD_S_D.sum()))
                                                .add(status.SUBAU_ATT_S_I.sum().subtract(status.SUBAU_ATT_S_D.sum()))
                                                .add(status.SUBAU_PRV_S_I.sum().subtract(status.SUBAU_PRV_S_D.sum())))
                                        .add(status.OWN_HOME_S_I.sum().subtract(status.OWN_HOME_S_D.sum())
                                                .add(status.OWN_APT_S_I.sum().subtract(status.OWN_APT_S_D.sum()))).as("TOTAL_S_SUM"),
                                status.PBLRD_PAY_A_I.sum().subtract(status.PBLRD_PAY_A_D.sum())
                                        .add(status.PBLRD_FREE_A_I.sum().subtract(status.PBLRD_FREE_A_D.sum()))
                                        .add(status.PBLRD_RESI_A_I.sum().subtract(status.PBLRD_RESI_A_D.sum()))
                                        .add(status.PBLOUT_PAY_A_I.sum().subtract(status.PBLOUT_PAY_A_D.sum()))
                                        .add(status.PBLOUT_FREE_A_I.sum().subtract(status.PBLOUT_FREE_A_D.sum()))
                                        .add(status.PRV_A_I.sum().subtract(status.PRV_A_D.sum()))
                                        .add(status.SUBSE_SUR_A_I.sum().subtract(status.SUBSE_SUR_A_D.sum())
                                                .add(status.SUBSE_MOD_A_I.sum().subtract(status.SUBSE_MOD_A_D.sum()))
                                                .add(status.SUBAU_ATT_A_I.sum().subtract(status.SUBAU_ATT_A_D.sum()))
                                                .add(status.SUBAU_PRV_A_I.sum().subtract(status.SUBAU_PRV_A_D.sum())))
                                        .add(status.OWN_HOME_A_I.sum().subtract(status.OWN_HOME_A_D.sum())
                                                .add(status.OWN_APT_A_I.sum().subtract(status.OWN_APT_A_D.sum()))).as("TOTAL_A_SUM")
                        )
                )
                .from(status)
                .where(
                        eqSggCd(thisMonth.getSggCd())
                )
                .fetchOne();
    }


    /*
   exprressions 분리
    */
    private BooleanExpression eqYear(String year) {
        return hasText(year) ? status.year.contains(year) : null;
    }

    private BooleanExpression eqMonth(String month) {
        return hasText(month) ? status.month.contains(month) : null;
    }

    private BooleanExpression eqSggCd(String sggCd) {
        return hasText(sggCd) ? status.sggCd.contains(sggCd) : null;
    }



    private BooleanExpression loeLocalDt(LocalDate thisDt) {
        if (status != null && status.localDt != null) {
            LocalDate compare = thisDt.minusMonths(1L);
            return status.localDt.loe(compare);
        } else {
            // 처리할 로직이나 기본값을 리턴하는 부분 추가
            return null;
        }

    }


}
