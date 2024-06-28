package com.example.demo.domain.data.facility.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PFDataRepository extends JpaRepository<PFData, Long> {
}
