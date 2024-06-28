package com.example.demo.domain.data.illegal.file;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IllDataRepository extends JpaRepository<IllData, Long> {
}
