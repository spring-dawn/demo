package com.example.demo.domain.data.standardSet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StandardMngRepo extends JpaRepository<StandardMng, String> {
    // 중복검사 로직: 부분중복 허용이므로 exists 가 아닌 count 로 검사.
    Long countByLotTypeAndDupChk1(String lotType, String dupChk1);

    Long countByLotTypeAndDupChk2(String lotType, String dupChk2);

    Long countByLotTypeAndDupChk3(String lotType, String dupChk3);

    Long countByLotTypeAndDupChk4(String lotType, String dupChk4);

    //
    Long countByLotType(String lotType);


    // 주차장 유형별, createDtm 기준으로 가장 최신행 찾기
    Optional<StandardMng> findFirstByLotTypeOrderByCreateDtmDesc(String lotType);
}
