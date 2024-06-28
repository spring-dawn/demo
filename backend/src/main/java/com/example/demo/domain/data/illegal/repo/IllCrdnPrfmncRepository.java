package com.example.demo.domain.data.illegal.repo;

import com.example.demo.domain.data.illegal.IllCrdnPrfmnc;
import com.example.demo.domain.data.illegal.IllProtectedArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IllCrdnPrfmncRepository extends JpaRepository<IllCrdnPrfmnc, String> {
    Optional<IllCrdnPrfmnc> findFirstBySggOrderByCreateDtmDesc(String sgg);
    List<IllCrdnPrfmnc> findByYearAndMonthAndSgg(String year, String month, String sgg);
}
