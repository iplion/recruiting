package com.adl.recruiting.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.adl.recruiting.entity.Review;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Query("""
        select r from Review r
        join fetch r.reviewer
        left join fetch r.recommendedVacancy
        where r.candidate.id = :candidateId
        order by r.createdAt desc
    """)
    List<Review> findByCandidateIdWithReviewer(@Param("candidateId") Long candidateId);

}
