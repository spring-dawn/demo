package com.example.demo.domain.data.monthlyReport.repo;

import com.example.demo.domain.data.monthlyReport.MrData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MrDataRepository extends JpaRepository<MrData, Long> {
}
