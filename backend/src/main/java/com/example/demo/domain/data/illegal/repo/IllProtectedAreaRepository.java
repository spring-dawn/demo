package com.example.demo.domain.data.illegal.repo;

import com.example.demo.domain.data.illegal.IllMobile;
import com.example.demo.domain.data.illegal.IllProtectedArea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IllProtectedAreaRepository extends JpaRepository<IllProtectedArea, String> {
}
