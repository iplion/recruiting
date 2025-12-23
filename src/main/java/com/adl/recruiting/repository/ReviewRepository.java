package com.adl.recruiting.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.adl.recruiting.entity.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findAllByCandidateIdOrderByCreatedAtDesc(Long candidateId);
}
