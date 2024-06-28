package com.example.demo.domain.data.research.shp;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ShpResultOptionRepository extends JpaRepository<ShpResultOption, Long> {

    Optional<ShpResultOption> findBySubType(String subType);
}