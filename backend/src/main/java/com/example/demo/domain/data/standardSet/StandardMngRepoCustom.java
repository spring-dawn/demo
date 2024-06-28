package com.example.demo.domain.data.standardSet;

import com.example.demo.domain.api.building_management.QBuildingManagement;
import com.example.demo.domain.data.facility.read.QPFOpen;
import com.example.demo.domain.data.facility.read.QPFPrivate;
import com.example.demo.domain.data.monthlyReport.QPPublic;
import com.example.demo.dto.data.standard.StandardSetDto;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.util.StringUtils.hasText;

@Slf4j
@Repository
@RequiredArgsConstructor
public class StandardMngRepoCustom {
    /*
    표준관리대장 쿼리
    주차시설 표준데이터셋 = 공영(월간보고) + 민영(노외) + 부설(api) + 부설 개방 + 사유지 개방 + 표준관리대장 일련번호(
     */

    private final JPAQueryFactory factory;
    // 표준관리대장(기준)
    QStandardMng mng = QStandardMng.standardMng;

    // 원천 데이터
    QPPublic pbl = QPPublic.pPublic;    // 공영
    QPFPrivate prv = QPFPrivate.pFPrivate;      //  민영노외(5)
    QBuildingManagement sub = QBuildingManagement.buildingManagement;   // 부설
    QPFOpen open = QPFOpen.pFOpen;      // 부설 개방(8), 사유지 개방(9)


    /**
     * 표준데이터셋
     *
     * @param req 검색어(연, 월, 구군)
     * @return List<StandardSetDto> 정렬 미정
     */
    public List<StandardSetDto> search(StandardSetDto.Keyword req) {
        // 최신 연/월 디폴트값 세팅
        String[] currentYmd = LocalDate.now().toString().split("-");

        return factory
                .select(
                        // StandardSetDto 투영
                        Projections.bean(StandardSetDto.class,
                                // dto 에 정의된 필드명과 동일하게 바인딩
                                mng.id.as("mngNo"),
                                prv.sggCd.coalesce(open.sggCd).coalesce(pbl.sggCd).as("sggCd"),
                                prv.lotNm.coalesce(open.lotNm).coalesce(pbl.name).as("lotNm"),
                                prv.lotType.coalesce(open.lotType).coalesce(pbl.lotType).as("lotType"),
                                prv.streetAddr.as("stAddress"),
                                prv.address.coalesce(open.address).coalesce(pbl.location).as("address"),
                                prv.totalSpcs.coalesce(open.spcs).coalesce(pbl.totalSpaces).as("totalSpcs"),
                                prv.landRank.as("landRank"),
                                open.openDay.as("workDay"),
                                pbl.wh.as("weekOpenTm"),
//                                pbl.wh.as("weekCloseTm"),
                                pbl.whSaturday.as("satOpenTm"),
//                                pbl.whSaturday.as("satCloseTm"),
                                pbl.whHoliday.as("holiOpenTm"),
//                                pbl.whHoliday.as("holiCloseTm"),
                                prv.operateInfo.coalesce(pbl.payYn).as("payInfo"),
                                pbl.pay4Hour.as("parkingPay"),
                                pbl.pay4Day.as("payByDay"),
                                pbl.agency.as("agency"),
                                prv.ceoCellNo.as("agencyTel"),
                                prv.lat.coalesce(open.lat).coalesce(pbl.lat).as("lat"),
                                prv.lng.coalesce(open.lng).coalesce(pbl.lon).as("lon"),
                                prv.disabledSpcs.as("hasDisSpcs"),
                                prv.createDtm.coalesce(open.createDtm).coalesce(pbl.createDtm).as("createDtm"),
                                prv.year.coalesce(open.year).coalesce(pbl.year).as("year"),
                                prv.month.coalesce(open.month).coalesce(pbl.month).as("month")
                        )
                )
                .from(mng)
                .leftJoin(pbl).on(mng.id.eq(pbl.mngNo))
                .leftJoin(prv).on(mng.id.eq(prv.mngNo))
                .leftJoin(open).on(mng.id.eq(open.mngNo))
                .where(
                        eqYear(req.getYear()),
                        eqMonth(req.getMonth()),
                        eqSggCd(req.getSggCd()),
                        eqLotType(req.getLotType()),
                        pbl.lotType.isNotNull().or(prv.lotType.isNotNull()).or(open.lotType.isNotNull())
                )
//                .groupBy(mng.id)
                .orderBy(mng.id.asc(), prv.createDtm.coalesce(open.createDtm).coalesce(pbl.createDtm).desc())
                .fetch();
    }

    public List<StandardSetDto> latestStandardSet(StandardSetDto.Keyword req) {
        List<StandardSetDto> fetch = factory
                .select(
                        // StandardSetDto 투영
                        Projections.bean(StandardSetDto.class,
                                // dto 에 정의된 필드명과 동일하게 바인딩
                                mng.id.as("mngNo"),
                                prv.year.coalesce(open.year).coalesce(pbl.year).as("year"),
                                prv.month.coalesce(open.month).coalesce(pbl.month).as("month"),
                                prv.sggCd.coalesce(open.sggCd).coalesce(pbl.sggCd).as("sggCd"),
                                prv.createDtm.coalesce(open.createDtm).coalesce(pbl.createDtm).as("createDtm")
                        )
                )
                .from(mng)
                .leftJoin(pbl).on(mng.id.eq(pbl.mngNo))
                .leftJoin(prv).on(mng.id.eq(prv.mngNo))
                // 개방주차장은 연/월에 따른 최신 정보 개념이 없으므로(연 1회 등록) 범위에서 제외
//                .leftJoin(open).on(mng.id.eq(open.mngNo))
                .where(
                        eqSggCd(req.getSggCd()),
                        eqLotType(req.getLotType()),
                        pbl.lotType.isNotNull().or(prv.lotType.isNotNull()).or(open.lotType.isNotNull())
                )
                .orderBy(
                        prv.createDtm.coalesce(open.createDtm).coalesce(pbl.createDtm).desc()
                )
                .limit(1)
                .fetch();

//        2)
        StandardSetDto latest = fetch.get(0);
        String year = latest.getYear();
        String month = latest.getMonth();

//        3)
        return factory
                .select(
                        // StandardSetDto 투영
                        Projections.bean(StandardSetDto.class,
                                // dto 에 정의된 필드명과 동일하게 바인딩
                                mng.id.as("mngNo"),
                                prv.sggCd.coalesce(open.sggCd).coalesce(pbl.sggCd).as("sggCd"),
                                prv.lotNm.coalesce(open.lotNm).coalesce(pbl.name).as("lotNm"),
                                prv.lotType.coalesce(open.lotType).coalesce(pbl.lotType).as("lotType"),
                                prv.streetAddr.as("stAddress"),
                                prv.address.coalesce(open.address).coalesce(pbl.location).as("address"),
                                prv.totalSpcs.coalesce(open.spcs).coalesce(pbl.totalSpaces.stringValue()).as("totalSpcs"),
                                prv.landRank.as("landRank"),
                                open.openDay.as("workDay"),
                                pbl.wh.as("weekOpenTm"),
//                                pbl.wh.as("weekCloseTm"),
                                pbl.whSaturday.as("satOpenTm"),
//                                pbl.whSaturday.as("satCloseTm"),
                                pbl.whHoliday.as("holiOpenTm"),
//                                pbl.whHoliday.as("holiCloseTm"),
                                prv.operateInfo.coalesce(pbl.payYn).as("payInfo"),
                                pbl.pay4Hour.as("parkingPay"),
                                pbl.pay4Day.as("payByDay"),
                                pbl.agency.as("agency"),
                                prv.ceoCellNo.as("agencyTel"),
                                prv.lat.coalesce(open.lat).coalesce(pbl.lat).as("lat"),
                                prv.lng.coalesce(open.lng).coalesce(pbl.lon).as("lon"),
                                prv.disabledSpcs.as("hasDisSpcs"),
                                prv.createDtm.coalesce(open.createDtm).coalesce(pbl.createDtm).as("createDtm"),
                                prv.year.coalesce(open.year).coalesce(pbl.year).as("year"),
                                prv.month.coalesce(open.month).coalesce(pbl.month).as("month")
                        )
                )
                .from(mng)
                .leftJoin(pbl).on(mng.id.eq(pbl.mngNo))
                .leftJoin(prv).on(mng.id.eq(prv.mngNo))
                .leftJoin(open).on(mng.id.eq(open.mngNo))
                .where(
                        eqYear(year),
                        eqMonth(month),
                        pbl.lotType.isNotNull().or(prv.lotType.isNotNull()).or(open.lotType.isNotNull())
                )
                .orderBy(
                        prv.createDtm.coalesce(open.createDtm).coalesce(pbl.createDtm).asc()
                )
                .fetch();
    }

    /*
    expressions 분리
     */
    private BooleanExpression eqYear(String year) {
        if (!hasText(year)) return null;
        BooleanExpression pblCheck = pbl.year.eq(year);
        BooleanExpression prvCheck = prv.year.eq(year);
        BooleanExpression openCheck = open.year.eq(year);

        return pblCheck.or(prvCheck).or(openCheck);
    }

    private BooleanExpression eqMonth(String month) {
        if (!hasText(month)) return null;
        // 개방(open) 주차장은 연간 갱신되므로 month 가 의미없음. 어떤 월을 고르든 전부 조회되게 함.
        BooleanExpression pblCheck = pbl.month.eq(month);
        BooleanExpression prvCheck = prv.month.eq(month);
        BooleanExpression openCheck = open.month.isNotNull();

        return pblCheck.or(prvCheck).or(openCheck);
    }


    private BooleanExpression eqSggCd(String sggCd) {
        if (!hasText(sggCd)) return null;
        BooleanExpression pblCheck = pbl.sggCd.eq(sggCd);
        BooleanExpression prvCheck = prv.sggCd.eq(sggCd);
        BooleanExpression openCheck = open.sggCd.eq(sggCd);

        return pblCheck.or(prvCheck).or(openCheck);
    }

    private BooleanExpression eqLotType(String lotType) {
        if (!hasText(lotType)) return null;

        BooleanExpression pblCheck = pbl.lotType.eq(lotType);
        BooleanExpression prvCheck = prv.lotType.eq(lotType);
        BooleanExpression openCheck = open.lotType.eq(lotType);
        return pblCheck.or(prvCheck).or(openCheck);
    }


}
