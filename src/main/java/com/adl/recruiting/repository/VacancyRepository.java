package com.adl.recruiting.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.adl.recruiting.entity.Vacancy;
import com.adl.recruiting.entity.VacancyStatus;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {

    List<Vacancy> findAllByStatus(VacancyStatus status);

    interface StatusCount {
        VacancyStatus getStatus();
        long getCount();
    }

    @Query("select v.status as status, count(v) as count from Vacancy v group by v.status")
    List<StatusCount> countByStatus();
}
