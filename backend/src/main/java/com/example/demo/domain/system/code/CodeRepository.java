package com.example.demo.domain.system.code;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CodeRepository extends JpaRepository<Code, Long> {
    List<Code> findByParentIsNull();
    Optional<Code> findByName(String CodeNm);

    Page<Code> findAll(Pageable pageable);

    Page<Code> findByNameContainsIgnoreCase(String name, Pageable pageable);
}