package com.example.demo.domain.survey.data.mngCard;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RschMngCardRepository extends JpaRepository<RschSummary, String> {
    // 복합키 구분
    Optional<RschSummary> findByYearAndSggNmAndHjDongAndBlockAndDayNight(String year, String sggNm, String hjDong, String block, String dayNight);

    boolean existsByYearAndSggNmAndHjDongAndBlockAndDayNight(String year, String sggNm, String hjDong, String block, String dayNight);
}
