package com.example.demo.domain.data.monthlyReport.repo;

import com.example.demo.domain.data.monthlyReport.PStatus;
import com.example.demo.domain.data.monthlyReport.pk.PStatusPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PStatusRepository extends JpaRepository<PStatus, PStatusPk> {
}
