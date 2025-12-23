package com.adl.recruiting.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.adl.recruiting.entity.Candidate;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    Optional<Candidate> findByAccessToken(String accessToken);
}
