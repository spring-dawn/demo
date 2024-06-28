package com.example.demo.domain.data.illegal.repo;

import com.example.demo.domain.data.illegal.IllFixed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IllFixedRepository extends JpaRepository<IllFixed, String> {
}
