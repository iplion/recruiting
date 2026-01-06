package com.adl.recruiting.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.adl.recruiting.entity.TaskAssignment;
import com.adl.recruiting.entity.TaskAssignmentStatus;

public interface TaskAssignmentRepository extends JpaRepository<TaskAssignment, Long> {

    List<TaskAssignment> findAllByCandidateId(Long candidateId);

    Optional<TaskAssignment> findByCandidateIdAndTaskId(Long candidateId, Long taskId);

    Optional<TaskAssignment> findByIdAndCandidateId(Long id, Long candidateId);

    interface StatusCount {
        TaskAssignmentStatus getStatus();
        long getCount();
    }

    @Query("select ta.status as status, count(ta) as count from TaskAssignment ta group by ta.status")
    List<StatusCount> countByStatus();
}
