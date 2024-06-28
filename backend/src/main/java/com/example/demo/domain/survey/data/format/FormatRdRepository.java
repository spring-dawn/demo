package com.example.demo.domain.survey.data.format;

import com.example.demo.domain.survey.data.pk.RschFmPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormatRdRepository extends JpaRepository<FormatRd, RschFmPk> {
}
