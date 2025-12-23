package com.adl.recruiting.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.adl.recruiting.entity.TaskAssignment;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {
    List<TaskAssignment> findAllByCandidateId(Long candidateId);
}
