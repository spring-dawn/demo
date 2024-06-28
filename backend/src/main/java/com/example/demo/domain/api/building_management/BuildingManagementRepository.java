package com.example.demo.domain.api.building_management;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface BuildingManagementRepository extends JpaRepository<BuildingManagement, Long> {

    List<BuildingManagement> findBySigunguCdAndLonIsNotNull(Integer sgg);
    List<BuildingManagement> findAllBySigunguCd(Integer sgg);

    void deleteAllBySigunguCd(Integer sggCode);

}