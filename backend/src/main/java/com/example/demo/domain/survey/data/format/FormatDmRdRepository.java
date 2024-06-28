package com.example.demo.domain.survey.data.format;

import com.example.demo.domain.survey.data.pk.RschFmDmPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormatDmRdRepository extends JpaRepository<FormatDmRd, RschFmDmPk> {
}
