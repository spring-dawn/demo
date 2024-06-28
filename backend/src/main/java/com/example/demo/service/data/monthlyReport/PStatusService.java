package com.example.demo.service.data.monthlyReport;

import com.example.demo.domain.data.monthlyReport.PStatus;
import com.example.demo.domain.data.monthlyReport.pk.PStatusPk;
import com.example.demo.domain.data.monthlyReport.repo.PStatusRepository;
import com.example.demo.domain.data.monthlyReport.repoCustom.PStatusRepoCustom;
import com.example.demo.dto.data.monthlyReport.PStatusDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PStatusService {
    private final PStatusRepository repo;
    private final PStatusRepoCustom query;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public List<PStatusDto> selectList() {
        return repo.findAll().stream().map(PStatus::toRes).collect(Collectors.toList());
    }

    public PStatusDto selectOne(PStatusPk ids) {
        // 복합키는 클라이언트에서 전송한 개별 파라미터를 생성자로 합쳐서 사용해야 합니다.
        PStatus ps = repo.findById(ids).orElse(null);
        return ps != null ? ps.toRes() : null;
    }


    // proc.. 아마 insert 만 있을 듯.
    @Transactional
    public PStatusDto insert(PStatusDto req) {
//        날짜비교용 날짜 컬럼 추가. yyyyMMdd 형태
        LocalDate localDt = LocalDate.parse(req.getYear() + String.format("%02d", Integer.parseInt(req.getMonth())) + "01", formatter);
        return repo.save(
                PStatus.builder()
                        .localDt(localDt)
                        .year(req.getYear())
                        .month(req.getMonth())
                        .sggCd(req.getSggCd())
                        //
                        .PBLRD_PAY_L_I(req.getPBLRD_PAY_L_I())
                        .PBLRD_PAY_L_D(req.getPBLRD_PAY_L_D())
                        .PBLRD_PAY_S_I(req.getPBLRD_PAY_S_I())
                        .PBLRD_PAY_S_D(req.getPBLRD_PAY_S_D())
                        .PBLRD_PAY_A_I(req.getPBLRD_PAY_A_I())
                        .PBLRD_PAY_A_D(req.getPBLRD_PAY_A_D())
                        .PBLRD_FREE_L_I(req.getPBLRD_FREE_L_I())
                        .PBLRD_FREE_L_D(req.getPBLRD_FREE_L_D())
                        .PBLRD_FREE_S_I(req.getPBLRD_FREE_S_I())
                        .PBLRD_FREE_S_D(req.getPBLRD_FREE_S_D())
                        .PBLRD_FREE_A_I(req.getPBLRD_FREE_A_I())
                        .PBLRD_FREE_A_D(req.getPBLRD_FREE_A_D())
                        .PBLRD_RESI_L_I(req.getPBLRD_RESI_L_I())
                        .PBLRD_RESI_L_D(req.getPBLRD_RESI_L_D())
                        .PBLRD_RESI_S_I(req.getPBLRD_RESI_S_I())
                        .PBLRD_RESI_S_D(req.getPBLRD_RESI_S_D())
                        .PBLRD_RESI_A_I(req.getPBLRD_RESI_A_I())
                        .PBLRD_RESI_A_D(req.getPBLRD_RESI_A_D())
                        .PBLOUT_PAY_L_I(req.getPBLOUT_PAY_L_I())
                        .PBLOUT_PAY_L_D(req.getPBLOUT_PAY_L_D())
                        .PBLOUT_PAY_S_I(req.getPBLOUT_PAY_S_I())
                        .PBLOUT_PAY_S_D(req.getPBLOUT_PAY_S_D())
                        .PBLOUT_PAY_A_I(req.getPBLOUT_PAY_A_I())
                        .PBLOUT_PAY_A_D(req.getPBLOUT_PAY_A_D())
                        .PBLOUT_FREE_L_I(req.getPBLOUT_FREE_L_I())
                        .PBLOUT_FREE_L_D(req.getPBLOUT_FREE_L_D())
                        .PBLOUT_FREE_S_I(req.getPBLOUT_FREE_S_I())
                        .PBLOUT_FREE_S_D(req.getPBLOUT_FREE_S_D())
                        .PBLOUT_FREE_A_I(req.getPBLOUT_FREE_A_I())
                        .PBLOUT_FREE_A_D(req.getPBLOUT_FREE_A_D())
                        .PRV_L_I(req.getPRV_L_I())
                        .PRV_L_D(req.getPRV_L_D())
                        .PRV_S_I(req.getPRV_S_I())
                        .PRV_S_D(req.getPRV_S_D())
                        .PRV_A_I(req.getPRV_A_I())
                        .PRV_A_D(req.getPRV_A_D())
                        .SUBSE_SUR_L_I(req.getSUBSE_SUR_L_I())
                        .SUBSE_SUR_L_D(req.getSUBSE_SUR_L_D())
                        .SUBSE_SUR_S_I(req.getSUBSE_SUR_S_I())
                        .SUBSE_SUR_S_D(req.getSUBSE_SUR_S_D())
                        .SUBSE_SUR_A_I(req.getSUBSE_SUR_A_I())
                        .SUBSE_SUR_A_D(req.getSUBSE_SUR_A_D())
                        .SUBSE_MOD_L_I(req.getSUBSE_MOD_L_I())
                        .SUBSE_MOD_L_D(req.getSUBSE_MOD_L_D())
                        .SUBSE_MOD_S_I(req.getSUBSE_MOD_S_I())
                        .SUBSE_MOD_S_D(req.getSUBSE_MOD_S_D())
                        .SUBSE_MOD_A_I(req.getSUBSE_MOD_A_I())
                        .SUBSE_MOD_A_D(req.getSUBSE_MOD_A_D())
                        .SUBAU_ATT_L_I(req.getSUBAU_ATT_L_I())
                        .SUBAU_ATT_L_D(req.getSUBAU_ATT_L_D())
                        .SUBAU_ATT_S_I(req.getSUBAU_ATT_S_I())
                        .SUBAU_ATT_S_D(req.getSUBAU_ATT_S_D())
                        .SUBAU_ATT_A_I(req.getSUBAU_ATT_A_I())
                        .SUBAU_ATT_A_D(req.getSUBAU_ATT_A_D())
                        .SUBAU_PRV_L_I(req.getSUBAU_PRV_L_I())
                        .SUBAU_PRV_L_D(req.getSUBAU_PRV_L_D())
                        .SUBAU_PRV_S_I(req.getSUBAU_PRV_S_I())
                        .SUBAU_PRV_S_D(req.getSUBAU_PRV_S_D())
                        .SUBAU_PRV_A_I(req.getSUBAU_PRV_A_I())
                        .SUBAU_PRV_A_D(req.getSUBAU_PRV_A_D())
                        .OWN_HOME_L_I(req.getOWN_HOME_L_I())
                        .OWN_HOME_L_D(req.getOWN_HOME_L_D())
                        .OWN_HOME_S_I(req.getOWN_HOME_S_I())
                        .OWN_HOME_S_D(req.getOWN_HOME_S_D())
                        .OWN_HOME_A_I(req.getOWN_HOME_A_I())
                        .OWN_HOME_A_D(req.getOWN_HOME_A_D())
                        .OWN_APT_L_I(req.getOWN_APT_L_I())
                        .OWN_APT_L_D(req.getOWN_APT_L_D())
                        .OWN_APT_S_I(req.getOWN_APT_S_I())
                        .OWN_APT_S_D(req.getOWN_APT_S_D())
                        .OWN_APT_A_I(req.getOWN_APT_A_I())
                        .OWN_APT_A_D(req.getOWN_APT_A_D())
                        .build()
        ).toRes();
    }

    //각 시군구 가장 최근까지의 데이터들을 합하여 보여줌
    public Map<String, Object> selectSggTotal(PStatusDto.Keyword req) {
        HashMap<String, Object> map = new HashMap<>();
//        1) 선택된 이번달치 데이터, 기준.
        req.setSggCd("31140");
        PStatusDto.Total namguTotal = query.thisTotal(req);
        map.put("namguTotal", namguTotal);

        req.setSggCd("31710");
        PStatusDto.Total uljuTotal = query.thisTotal(req);
        map.put("uljuTotal", uljuTotal);

        req.setSggCd("31170");
        PStatusDto.Total dongguTotal = query.thisTotal(req);
        map.put("dongguTotal", dongguTotal);
        req.setSggCd("31200");
        PStatusDto.Total bukguTotal = query.thisTotal(req);
        map.put("bukguTotal", bukguTotal);

        req.setSggCd("31110");
        PStatusDto.Total jungguTotal = query.thisTotal(req);
        map.put("jungguTotal", jungguTotal);

        PStatusDto.Total sggTotal = PStatusDto.Total.builder().TOTAL_L_SUM(namguTotal.getTOTAL_L_SUM() + uljuTotal.getTOTAL_L_SUM() + dongguTotal.getTOTAL_L_SUM() + bukguTotal.getTOTAL_L_SUM() + jungguTotal.getTOTAL_L_SUM())
                        .TOTAL_S_SUM(namguTotal.getTOTAL_S_SUM() + uljuTotal.getTOTAL_S_SUM() + dongguTotal.getTOTAL_S_SUM() + bukguTotal.getTOTAL_S_SUM() + jungguTotal.getTOTAL_S_SUM())
                        .TOTAL_A_SUM(namguTotal.getTOTAL_A_SUM() + uljuTotal.getTOTAL_A_SUM() + dongguTotal.getTOTAL_A_SUM() + bukguTotal.getTOTAL_A_SUM() + jungguTotal.getTOTAL_A_SUM())

                        .PBL_L_SUBTOTAL(namguTotal.getPBL_L_SUBTOTAL() + uljuTotal.getPBL_L_SUBTOTAL() + dongguTotal.getPBL_L_SUBTOTAL() + bukguTotal.getPBL_L_SUBTOTAL() + jungguTotal.getPBL_L_SUBTOTAL())
                        .PBL_S_SUBTOTAL(namguTotal.getPBL_S_SUBTOTAL() + uljuTotal.getPBL_S_SUBTOTAL() + dongguTotal.getPBL_S_SUBTOTAL() + bukguTotal.getPBL_S_SUBTOTAL() + jungguTotal.getPBL_S_SUBTOTAL())
                        .PBL_A_SUBTOTAL(namguTotal.getPBL_A_SUBTOTAL() + uljuTotal.getPBL_A_SUBTOTAL() + dongguTotal.getPBL_A_SUBTOTAL() + bukguTotal.getPBL_A_SUBTOTAL() + jungguTotal.getPBL_A_SUBTOTAL())

                        .SUB_L_SUBTOTAL(namguTotal.getSUB_L_SUBTOTAL() + uljuTotal.getSUB_L_SUBTOTAL() + dongguTotal.getSUB_L_SUBTOTAL() + bukguTotal.getSUB_L_SUBTOTAL() + jungguTotal.getSUB_L_SUBTOTAL())
                        .SUB_S_SUBTOTAL(namguTotal.getSUB_S_SUBTOTAL() + uljuTotal.getSUB_S_SUBTOTAL() + dongguTotal.getSUB_S_SUBTOTAL() + bukguTotal.getSUB_S_SUBTOTAL() + jungguTotal.getSUB_S_SUBTOTAL())
                        .SUB_A_SUBTOTAL(namguTotal.getSUB_A_SUBTOTAL() + uljuTotal.getSUB_A_SUBTOTAL() + dongguTotal.getSUB_A_SUBTOTAL() + bukguTotal.getSUB_A_SUBTOTAL() + jungguTotal.getSUB_A_SUBTOTAL())

                        .OWN_L_SUBTOTAL(namguTotal.getOWN_L_SUBTOTAL() + uljuTotal.getOWN_L_SUBTOTAL() + dongguTotal.getOWN_L_SUBTOTAL() + bukguTotal.getOWN_L_SUBTOTAL() + jungguTotal.getOWN_L_SUBTOTAL())
                        .OWN_S_SUBTOTAL(namguTotal.getOWN_S_SUBTOTAL() + uljuTotal.getOWN_S_SUBTOTAL() + dongguTotal.getOWN_S_SUBTOTAL() + bukguTotal.getOWN_S_SUBTOTAL() + jungguTotal.getOWN_S_SUBTOTAL())
                        .OWN_A_SUBTOTAL(namguTotal.getOWN_A_SUBTOTAL() + uljuTotal.getOWN_A_SUBTOTAL() + dongguTotal.getOWN_A_SUBTOTAL() + bukguTotal.getOWN_A_SUBTOTAL() + jungguTotal.getOWN_A_SUBTOTAL())

                        .PBLRD_PAY_L_SUM(namguTotal.getPBLRD_PAY_L_SUM() + uljuTotal.getPBLRD_PAY_L_SUM() + dongguTotal.getPBLRD_PAY_L_SUM() + bukguTotal.getPBLRD_PAY_L_SUM() + jungguTotal.getPBLRD_PAY_L_SUM())
                        .PBLRD_PAY_S_SUM(namguTotal.getPBLRD_PAY_S_SUM() + uljuTotal.getPBLRD_PAY_S_SUM() + dongguTotal.getPBLRD_PAY_S_SUM() + bukguTotal.getPBLRD_PAY_S_SUM() + jungguTotal.getPBLRD_PAY_S_SUM())
                        .PBLRD_PAY_A_SUM(namguTotal.getPBLRD_PAY_A_SUM() + uljuTotal.getPBLRD_PAY_A_SUM() + dongguTotal.getPBLRD_PAY_A_SUM() + bukguTotal.getPBLRD_PAY_A_SUM() + jungguTotal.getPBLRD_PAY_A_SUM())

                        .PBLRD_FREE_L_SUM(namguTotal.getPBLRD_FREE_L_SUM() + uljuTotal.getPBLRD_FREE_L_SUM() + dongguTotal.getPBLRD_FREE_L_SUM() + bukguTotal.getPBLRD_FREE_L_SUM() + jungguTotal.getPBLRD_FREE_L_SUM())
                        .PBLRD_FREE_S_SUM(namguTotal.getPBLRD_FREE_S_SUM() + uljuTotal.getPBLRD_FREE_S_SUM() + dongguTotal.getPBLRD_FREE_S_SUM() + bukguTotal.getPBLRD_FREE_S_SUM() + jungguTotal.getPBLRD_FREE_S_SUM())
                        .PBLRD_FREE_L_SUM(namguTotal.getPBLRD_FREE_A_SUM() + uljuTotal.getPBLRD_FREE_A_SUM() + dongguTotal.getPBLRD_FREE_A_SUM() + bukguTotal.getPBLRD_FREE_A_SUM() + jungguTotal.getPBLRD_FREE_A_SUM())

                        .PBLOUT_PAY_L_SUM(namguTotal.getPBLOUT_PAY_L_SUM() + uljuTotal.getPBLOUT_PAY_L_SUM() + dongguTotal.getPBLOUT_PAY_L_SUM() + bukguTotal.getPBLOUT_PAY_L_SUM() + jungguTotal.getPBLOUT_PAY_L_SUM())
                        .PBLOUT_PAY_S_SUM(namguTotal.getPBLOUT_PAY_S_SUM() + uljuTotal.getPBLOUT_PAY_S_SUM() + dongguTotal.getPBLOUT_PAY_S_SUM() + bukguTotal.getPBLOUT_PAY_S_SUM() + jungguTotal.getPBLOUT_PAY_S_SUM())
                        .PBLOUT_PAY_A_SUM(namguTotal.getPBLOUT_PAY_A_SUM() + uljuTotal.getPBLOUT_PAY_A_SUM() + dongguTotal.getPBLOUT_PAY_A_SUM() + bukguTotal.getPBLOUT_PAY_A_SUM() + jungguTotal.getPBLOUT_PAY_A_SUM())

                        .PBLOUT_FREE_L_SUM(namguTotal.getPBLOUT_FREE_L_SUM() + uljuTotal.getPBLOUT_FREE_L_SUM() + dongguTotal.getPBLOUT_FREE_L_SUM() + bukguTotal.getPBLOUT_FREE_L_SUM() + jungguTotal.getPBLOUT_FREE_L_SUM())
                        .PBLOUT_FREE_S_SUM(namguTotal.getPBLOUT_FREE_S_SUM() + uljuTotal.getPBLOUT_FREE_S_SUM() + dongguTotal.getPBLOUT_FREE_S_SUM() + bukguTotal.getPBLOUT_FREE_S_SUM() + jungguTotal.getPBLOUT_FREE_S_SUM())
                        .PBLOUT_FREE_A_SUM(namguTotal.getPBLOUT_FREE_A_SUM() + uljuTotal.getPBLOUT_FREE_A_SUM() + dongguTotal.getPBLOUT_FREE_A_SUM() + bukguTotal.getPBLOUT_FREE_A_SUM() + jungguTotal.getPBLOUT_FREE_A_SUM())


                        .PBLRD_RESI_L_SUM(namguTotal.getPBLRD_RESI_L_SUM() + uljuTotal.getPBLRD_RESI_L_SUM() + dongguTotal.getPBLRD_RESI_L_SUM() + bukguTotal.getPBLRD_RESI_L_SUM() + jungguTotal.getPBLRD_RESI_L_SUM())
                        .PBLRD_RESI_S_SUM(namguTotal.getPBLRD_RESI_S_SUM() + uljuTotal.getPBLRD_RESI_S_SUM() + dongguTotal.getPBLRD_RESI_S_SUM() + bukguTotal.getPBLRD_RESI_S_SUM() + jungguTotal.getPBLRD_RESI_S_SUM())
                        .PBLRD_RESI_A_SUM(namguTotal.getPBLRD_RESI_A_SUM() + uljuTotal.getPBLRD_RESI_A_SUM() + dongguTotal.getPBLRD_RESI_A_SUM() + bukguTotal.getPBLRD_RESI_A_SUM() + jungguTotal.getPBLRD_RESI_A_SUM())


                        .PRV_L_SUM(namguTotal.getPRV_L_SUM() + uljuTotal.getPRV_L_SUM() + dongguTotal.getPRV_L_SUM() + bukguTotal.getPRV_L_SUM() + jungguTotal.getPRV_L_SUM())
                        .PRV_S_SUM(namguTotal.getPRV_S_SUM() + uljuTotal.getPRV_S_SUM() + dongguTotal.getPRV_S_SUM() + bukguTotal.getPRV_S_SUM() + jungguTotal.getPRV_S_SUM())
                        .PRV_A_SUM(namguTotal.getPRV_A_SUM() + uljuTotal.getPRV_A_SUM() + dongguTotal.getPRV_A_SUM() + bukguTotal.getPRV_A_SUM() + jungguTotal.getPRV_A_SUM())

                        .SUBSE_SUR_L_SUM(namguTotal.getSUBSE_SUR_L_SUM() + uljuTotal.getSUBSE_SUR_L_SUM() + dongguTotal.getSUBSE_SUR_L_SUM() + bukguTotal.getSUBSE_SUR_L_SUM() + jungguTotal.getSUBSE_SUR_L_SUM())
                        .SUBSE_SUR_S_SUM(namguTotal.getSUBSE_SUR_S_SUM() + uljuTotal.getSUBSE_SUR_S_SUM() + dongguTotal.getSUBSE_SUR_S_SUM() + bukguTotal.getSUBSE_SUR_S_SUM() + jungguTotal.getSUBSE_SUR_S_SUM())
                        .SUBSE_SUR_A_SUM(namguTotal.getSUBSE_SUR_A_SUM() + uljuTotal.getSUBSE_SUR_A_SUM() + dongguTotal.getSUBSE_SUR_A_SUM() + bukguTotal.getSUBSE_SUR_A_SUM() + jungguTotal.getSUBSE_SUR_A_SUM())

                        .SUBSE_MOD_L_SUM(namguTotal.getSUBSE_MOD_L_SUM() + uljuTotal.getSUBSE_MOD_L_SUM() + dongguTotal.getSUBSE_MOD_L_SUM() + bukguTotal.getSUBSE_MOD_L_SUM() + jungguTotal.getSUBSE_MOD_L_SUM())
                        .SUBSE_MOD_S_SUM(namguTotal.getSUBSE_MOD_S_SUM() + uljuTotal.getSUBSE_MOD_S_SUM() + dongguTotal.getSUBSE_MOD_S_SUM() + bukguTotal.getSUBSE_MOD_S_SUM() + jungguTotal.getSUBSE_MOD_S_SUM())
                        .SUBSE_MOD_A_SUM(namguTotal.getSUBSE_MOD_A_SUM() + uljuTotal.getSUBSE_MOD_A_SUM() + dongguTotal.getSUBSE_MOD_A_SUM() + bukguTotal.getSUBSE_MOD_A_SUM() + jungguTotal.getSUBSE_MOD_A_SUM())


                        .SUBAU_ATT_L_SUM(namguTotal.getSUBAU_ATT_L_SUM() + uljuTotal.getSUBAU_ATT_L_SUM() + dongguTotal.getSUBAU_ATT_L_SUM() + bukguTotal.getSUBAU_ATT_L_SUM() + jungguTotal.getSUBAU_ATT_L_SUM())
                        .SUBAU_ATT_S_SUM(namguTotal.getSUBAU_ATT_S_SUM() + uljuTotal.getSUBAU_ATT_S_SUM() + dongguTotal.getSUBAU_ATT_S_SUM() + bukguTotal.getSUBAU_ATT_S_SUM() + jungguTotal.getSUBAU_ATT_S_SUM())
                        .SUBAU_ATT_A_SUM(namguTotal.getSUBAU_ATT_A_SUM() + uljuTotal.getSUBAU_ATT_A_SUM() + dongguTotal.getSUBAU_ATT_A_SUM() + bukguTotal.getSUBAU_ATT_A_SUM() + jungguTotal.getSUBAU_ATT_A_SUM())

                        .SUBAU_PRV_L_SUM(namguTotal.getSUBAU_PRV_L_SUM() + uljuTotal.getSUBAU_PRV_L_SUM() + dongguTotal.getSUBAU_PRV_L_SUM() + bukguTotal.getSUBAU_PRV_L_SUM() + jungguTotal.getSUBAU_PRV_L_SUM())
                        .SUBAU_PRV_S_SUM(namguTotal.getSUBAU_PRV_S_SUM() + uljuTotal.getSUBAU_PRV_S_SUM() + dongguTotal.getSUBAU_PRV_S_SUM() + bukguTotal.getSUBAU_PRV_S_SUM() + jungguTotal.getSUBAU_PRV_S_SUM())
                        .SUBAU_PRV_A_SUM(namguTotal.getSUBAU_PRV_A_SUM() + uljuTotal.getSUBAU_PRV_A_SUM() + dongguTotal.getSUBAU_PRV_A_SUM() + bukguTotal.getSUBAU_PRV_A_SUM() + jungguTotal.getSUBAU_PRV_A_SUM())

                        .OWN_HOME_L_SUM(namguTotal.getOWN_HOME_L_SUM() + uljuTotal.getOWN_HOME_L_SUM() + dongguTotal.getOWN_HOME_L_SUM() + bukguTotal.getOWN_HOME_L_SUM() + jungguTotal.getOWN_HOME_L_SUM())
                        .OWN_HOME_S_SUM(namguTotal.getOWN_HOME_S_SUM() + uljuTotal.getOWN_HOME_S_SUM() + dongguTotal.getOWN_HOME_S_SUM() + bukguTotal.getOWN_HOME_S_SUM() + jungguTotal.getOWN_HOME_S_SUM())
                        .OWN_HOME_A_SUM(namguTotal.getOWN_HOME_A_SUM() + uljuTotal.getOWN_HOME_A_SUM() + dongguTotal.getOWN_HOME_A_SUM() + bukguTotal.getOWN_HOME_A_SUM() + jungguTotal.getOWN_HOME_A_SUM())

                        .OWN_APT_L_SUM(namguTotal.getOWN_APT_L_SUM() + uljuTotal.getOWN_APT_L_SUM() + dongguTotal.getOWN_APT_L_SUM() + bukguTotal.getOWN_APT_L_SUM() + jungguTotal.getOWN_APT_L_SUM())
                        .OWN_APT_S_SUM(namguTotal.getOWN_APT_S_SUM() + uljuTotal.getOWN_APT_S_SUM() + dongguTotal.getOWN_APT_S_SUM() + bukguTotal.getOWN_APT_S_SUM() + jungguTotal.getOWN_APT_S_SUM())
                        .OWN_APT_A_SUM(namguTotal.getOWN_APT_A_SUM() + uljuTotal.getOWN_APT_A_SUM() + dongguTotal.getOWN_APT_A_SUM() + bukguTotal.getOWN_APT_A_SUM() + jungguTotal.getOWN_APT_A_SUM())
                        .build();
        map.put("sggTotal", sggTotal);



//        4) res
        return map;
    }

    //각 시군구 가장 최근까지의 데이터들을 합하여 보여줌
    public Map<String, Object> selectSggMainTotal(PStatusDto.Keyword req) {
        HashMap<String, Object> map = new HashMap<>();
//        1) 선택된 이번달치 데이터, 기준.
        map.put("year", req.getYear());
        map.put("month", req.getMonth());
        req.setSggCd("31140");
        Map<String, Object> namguTotal = query.selectMonthlyTotal(req);
        PStatus namguStatus = query.thisMonthStatus(req);
        map.put("31140", namguTotal);

        req.setSggCd("31710");
        Map<String, Object> uljuTotal = query.selectMonthlyTotal(req);
        PStatus uljuStatus = query.thisMonthStatus(req);
        map.put("31710", uljuTotal);

        req.setSggCd("31170");
        Map<String, Object> dongguTotal = query.selectMonthlyTotal(req);
        PStatus  dongguStatus = query.thisMonthStatus(req);
        map.put("31170", dongguTotal);

        req.setSggCd("31200");
        Map<String, Object> bukguTotal = query.selectMonthlyTotal(req);
        PStatus  bukguStatus = query.thisMonthStatus(req);
        map.put("31200", bukguTotal);

        req.setSggCd("31110");
        Map<String, Object> jungguTotal = query.selectMonthlyTotal(req);
        PStatus  jungguStatus = query.thisMonthStatus(req);
        map.put("31110", jungguTotal);
        return map;
    }

    //각 시군구 가장 최근까지 - range 파라미터 까지 범위 데이터 추출
    public ArrayList<Map<String, Object>> selectSggRangeTotal(PStatusDto.Keyword req, int range) {
        String baseDate = req.getYear() + req.getMonth() + "01";;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDate startDate = LocalDate.parse(baseDate, formatter);
        ArrayList<Map<String, Object>> result = new ArrayList<>();

        for (int i = 0; i < range; i++) {
            LocalDate reqDate = startDate.minusMonths(i);
            int reqYear = reqDate.getYear();
            int month = reqDate.getMonth().getValue();

            req.setYear(String.valueOf(reqYear));
            req.setMonth(String.format("%02d", month));

            Map<String, Object> psData = selectSggMainTotal(req);
            result.add(psData);
        }

        return result;
    }
}
