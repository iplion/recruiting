package com.adl.recruiting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adl.recruiting.entity.Vacancy;
import com.adl.recruiting.entity.VacancyStatus;

import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    List<Vacancy> findAllByStatus(VacancyStatus status);
}