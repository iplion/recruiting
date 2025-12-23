package com.adl.recruiting.service;

import java.util.List;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.adl.recruiting.dto.AssignmentResponse;
import com.adl.recruiting.dto.CreateAssignmentRequest;
import com.adl.recruiting.dto.SubmitSolutionRequest;
import com.adl.recruiting.entity.Candidate;
import com.adl.recruiting.entity.CandidateStatus;
import com.adl.recruiting.entity.TaskAssignment;
import com.adl.recruiting.entity.TaskAssignmentStatus;
import com.adl.recruiting.entity.TestTask;
import com.adl.recruiting.entity.User;
import com.adl.recruiting.repository.CandidateRepository;
import com.adl.recruiting.repository.CandidateStatusRepository;
import com.adl.recruiting.repository.TaskAssignmentRepository;
import com.adl.recruiting.repository.TestTaskRepository;
import com.adl.recruiting.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class TaskAssignmentService {

    private final TaskAssignmentRepository taskAssignmentRepository;
    private final CandidateRepository candidateRepository;
    private final TestTaskRepository testTaskRepository;
    private final UserRepository userRepository;
    private final CandidateStatusRepository candidateStatusRepository;

    @Transactional
    public AssignmentResponse assign(CreateAssignmentRequest req) {
        Candidate candidate = candidateRepository.findById(req.candidateId())
            .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + req.candidateId()));

        TestTask task = testTaskRepository.findById(req.taskId())
            .orElseThrow(() -> new IllegalArgumentException("Test task not found: " + req.taskId()));

        User assignedBy = userRepository.findById(req.assignedByUserId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.assignedByUserId()));

        TaskAssignment a = new TaskAssignment();
        a.setCandidate(candidate);
        a.setTask(task);
        a.setAssignedBy(assignedBy);
        a.setStatus(TaskAssignmentStatus.ASSIGNED);

        a = taskAssignmentRepository.save(a);

        setCandidateStatus(candidate, "test_sent");

        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponse> listByCandidate(long candidateId) {
        return taskAssignmentRepository.findAllByCandidateId(candidateId).stream()
            .map(this::toResponse)
            .toList();
    }

    private AssignmentResponse toResponse(TaskAssignment a) {
        return new AssignmentResponse(
            a.getId(),
            a.getCandidate().getId(),
            a.getTask().getId(),
            a.getAssignedBy().getId(),
            a.getStatus().name(),
            a.getSolutionLink(),
            a.getSolutionText()
        );
    }

    @Transactional
    public AssignmentResponse submitByToken(long assignmentId, String token, SubmitSolutionRequest req) {
        TaskAssignment a = taskAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        Candidate c = a.getCandidate();

        if (c.getAccessToken() == null || !c.getAccessToken().equals(token)) {
            throw new IllegalArgumentException("Invalid token for assignment: " + assignmentId);
        }
        if (c.getTokenExpiresAt() != null && c.getTokenExpiresAt().isBefore(OffsetDateTime.now())) {
            throw new IllegalArgumentException("Token expired");
        }

        a.setSolutionLink(req.solutionLink());
        a.setSolutionText(req.solutionText());
        a.setStatus(TaskAssignmentStatus.SUBMITTED);

        setCandidateStatus(c, "test_received");

        return toResponse(a);
    }

    @Transactional
    public AssignmentResponse markReviewed(long assignmentId) {
        TaskAssignment a = taskAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        if (a.getStatus() != TaskAssignmentStatus.SUBMITTED) {
            throw new IllegalArgumentException("Assignment must be SUBMITTED to mark REVIEWED");
        }

        a.setStatus(TaskAssignmentStatus.REVIEWED);

        setCandidateStatus(a.getCandidate(), "tech_review");

        return toResponse(a);
    }

    private void setCandidateStatus(Candidate candidate, String statusName) {
        CandidateStatus s = candidateStatusRepository.findByName(statusName)
            .orElseThrow(() -> new IllegalArgumentException("Candidate status not found: " + statusName));
        candidate.setStatus(s);
    }

}
