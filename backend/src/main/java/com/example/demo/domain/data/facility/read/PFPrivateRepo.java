package com.example.demo.domain.data.facility.read;

import com.example.demo.domain.data.facility.FacilityPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PFPrivateRepo extends JpaRepository<PFPrivate, FacilityPk> {
    Long countBy();
    // 중복 검사 로직.
    boolean existsByDupChk1(String dupChk1);

    Long countByDupChk2(String dupChk2);

    Long countByDupChk3(String dupChk3);

    Long countByDupChk4(String dupChk4);

    // 구군별 가장 최신 데이터 확인
    Optional<PFPrivate> findFirstBySggCdOrderByCreateDtmDesc(String sggCd);

}
