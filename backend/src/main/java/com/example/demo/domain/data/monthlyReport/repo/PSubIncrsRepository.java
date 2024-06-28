package com.example.demo.domain.data.monthlyReport.repo;

import com.example.demo.domain.data.monthlyReport.PSubIncrs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PSubIncrsRepository extends JpaRepository<PSubIncrs, Long> {
}
