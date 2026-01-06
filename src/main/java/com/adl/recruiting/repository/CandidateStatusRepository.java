package com.adl.recruiting.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.adl.recruiting.entity.CandidateStatus;

public interface CandidateStatusRepository extends JpaRepository<CandidateStatus, Long> {

    Optional<CandidateStatus> findByName(String name);

    List<CandidateStatus> findAllByOrderByIdAsc();

}
