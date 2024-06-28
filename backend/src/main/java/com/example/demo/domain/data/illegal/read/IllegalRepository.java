package com.example.demo.domain.data.illegal.read;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IllegalRepository extends JpaRepository<Illegal, Long> {
}
