package com.example.demo.domain.data.illegal.repo;

import com.example.demo.domain.data.illegal.IllBusMounted;
import com.example.demo.domain.data.illegal.IllFireplug;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IllFireplugRepository extends JpaRepository<IllFireplug, String> {
    List<IllFireplug> findAllBySggAndMonthAndYear(String sgg, String month, String year);
}
