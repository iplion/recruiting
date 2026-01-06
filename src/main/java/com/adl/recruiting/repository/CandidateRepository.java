package com.adl.recruiting.repository;

import com.adl.recruiting.entity.Candidate;
import com.adl.recruiting.entity.VacancyStatus;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    Optional<Candidate> findByAccessToken(String accessToken);

    interface StatusCount {
        String getStatus();
        long getCount();
    }

    interface PlannedVacancyCount {
        long getVacancyId();
        String getTitle();
        String getLevel();
        VacancyStatus getStatus();
        long getCount();
    }

    @Query("""
        select cs.name as status, count(c) as count
        from Candidate c
        join c.status cs
        group by cs.name
    """)
    List<StatusCount> countByStatus();

    @Query("""
        select v.id as vacancyId, v.title as title, v.level as level, v.status as status, count(c) as count
        from Candidate c
        join c.plannedVacancy v
        group by v.id, v.title, v.level, v.status
        order by count(c) desc
    """)
    List<PlannedVacancyCount> countByPlannedVacancy();

    @Query("""
        select count(c)
        from Candidate c
        where c.tokenExpiresAt is not null and c.tokenExpiresAt < :now
    """)
    long countExpiredTokens(@Param("now") OffsetDateTime now);

    @Query("""
        select count(c)
        from Candidate c
        where c.tokenExpiresAt is not null
          and c.tokenExpiresAt >= :now
          and c.tokenExpiresAt <= :soon
    """)
    long countExpiringTokens(@Param("now") OffsetDateTime now, @Param("soon") OffsetDateTime soon);
}
