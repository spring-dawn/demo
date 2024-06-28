package com.example.demo.domain.data.monthlyReport.repo;

import com.example.demo.domain.data.monthlyReport.PPublic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PPublicRepository extends JpaRepository<PPublic, Long> {
    Long countByLotTypeAndDupChk1(String lotType,String dupChk1);

    Long countByLotTypeAndDupChk2(String lotType,String dupChk2);

    Long countByLotTypeAndDupChk3(String lotType,String dupChk3);

    Long countByLotTypeAndDupChk4(String lotType,String dupChk4);
}
