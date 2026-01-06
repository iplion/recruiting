package com.adl.recruiting.service;

import com.adl.recruiting.dto.AssignmentResponseDto;
import com.adl.recruiting.dto.CandidateAssignmentDetailsResponseDto;
import com.adl.recruiting.dto.CandidateAssignmentItemResponseDto;
import com.adl.recruiting.dto.CreateAssignmentRequestDto;
import com.adl.recruiting.dto.SubmitSolutionRequestDto;
import com.adl.recruiting.entity.Candidate;
import com.adl.recruiting.entity.CandidateStatus;
import com.adl.recruiting.entity.TaskAssignment;
import com.adl.recruiting.entity.TaskAssignmentStatus;
import com.adl.recruiting.entity.TestTask;
import com.adl.recruiting.entity.User;
import com.adl.recruiting.exception.NotFoundException;
import com.adl.recruiting.repository.CandidateRepository;
import com.adl.recruiting.repository.CandidateStatusRepository;
import com.adl.recruiting.repository.TaskAssignmentRepository;
import com.adl.recruiting.repository.TestTaskRepository;
import com.adl.recruiting.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskAssignmentService {

    private final TaskAssignmentRepository taskAssignmentRepository;
    private final CandidateRepository candidateRepository;
    private final TestTaskRepository testTaskRepository;
    private final CandidateStatusRepository candidateStatusRepository;
    private final AuthUtils authUtils;
    private final TelegramNotificationService telegramNotificationService;

    @Transactional
    public AssignmentResponseDto assign(CreateAssignmentRequestDto req) {
        Candidate candidate = candidateRepository.findById(req.candidateId())
            .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + req.candidateId()));

        TestTask task = testTaskRepository.findById(req.taskId())
            .orElseThrow(() -> new IllegalArgumentException("Test task not found: " + req.taskId()));

        User assignedBy = authUtils.resolveCurrentUser();

        TaskAssignment a = new TaskAssignment();
        a.setCandidate(candidate);
        a.setTask(task);
        a.setAssignedBy(assignedBy);
        a.setStatus(TaskAssignmentStatus.ASSIGNED);

        a = taskAssignmentRepository.save(a);

        setCandidateStatus(candidate, "test_sent");

        telegramNotificationService.notifyCandidate(candidate,
            "Вам назначено тестовое задание: \"" + task.getTitle() + "\".\n" +
                "Откройте портал кандидата и загрузите решение."
        );

        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public List<AssignmentResponseDto> listByCandidate(long candidateId) {
        return taskAssignmentRepository.findAllByCandidateId(candidateId).stream()
            .map(this::toResponse)
            .toList();
    }

    @Transactional
    public AssignmentResponseDto submitByToken(long assignmentId, String token, SubmitSolutionRequestDto req) {
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

        telegramNotificationService.notifyRole("teamlead",
            "📥 Кандидат загрузил решение.\n" +
                "Кандидат: " + c.getFullName() + " (id=" + c.getId() + ")\n" +
                "Назначение: id=" + a.getId()
        );

        return toResponse(a);
    }

    @Transactional
    public AssignmentResponseDto markReviewed(long assignmentId) {
        TaskAssignment a = taskAssignmentRepository.findById(assignmentId)
            .orElseThrow(() -> new IllegalArgumentException("Assignment not found: " + assignmentId));

        if (a.getStatus() != TaskAssignmentStatus.SUBMITTED) {
            throw new IllegalArgumentException("Assignment must be SUBMITTED to mark REVIEWED");
        }

        Candidate candidate = a.getCandidate();
        String candidateStatus = candidate.getStatus() != null ? candidate.getStatus().getName() : null;
        if (!"test_received".equals(candidateStatus)) {
            throw new IllegalStateException("Candidate must be in status 'test_received' to start tech review. Current: " + candidateStatus);
        }

        a.setStatus(TaskAssignmentStatus.REVIEWED);

        setCandidateStatus(a.getCandidate(), "tech_review");

        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public List<CandidateAssignmentItemResponseDto> listForCandidate(long candidateId) {
        return taskAssignmentRepository.findAllByCandidateId(candidateId).stream()
            .map(this::toCandidateItem)
            .toList();
    }

    @Transactional(readOnly = true)
    public CandidateAssignmentDetailsResponseDto getForCandidate(long assignmentId, long candidateId) {
        TaskAssignment a = taskAssignmentRepository.findByIdAndCandidateId(assignmentId, candidateId)
            .orElseThrow(() -> new NotFoundException("Assignment not found: " + assignmentId));
        return toCandidateDetails(a);
    }

    @Transactional
    public CandidateAssignmentDetailsResponseDto submitByCandidate(long assignmentId,
                                                                   long candidateId,
                                                                   SubmitSolutionRequestDto req) {
        if (req == null || !req.hasAny()) {
            throw new IllegalArgumentException("Solution is empty");
        }

        TaskAssignment a = taskAssignmentRepository.findByIdAndCandidateId(assignmentId, candidateId)
            .orElseThrow(() -> new NotFoundException("Assignment not found: " + assignmentId));

        // чтобы кандидат не переписывал после начала техревью
        if (a.getStatus() == TaskAssignmentStatus.REVIEWED) {
            throw new IllegalStateException("Assignment already REVIEWED");
        }

        TaskAssignmentStatus prevStatus = a.getStatus();

        a.setSolutionLink(req.solutionLink());
        a.setSolutionText(req.solutionText());
        a.setStatus(TaskAssignmentStatus.SUBMITTED);

        setCandidateStatus(a.getCandidate(), "test_received");

        // шлём только если это первая отправка (не повторная правка)
        if (prevStatus != TaskAssignmentStatus.SUBMITTED) {
            Candidate c = a.getCandidate();
            telegramNotificationService.notifyRole("teamlead",
                "📥 Кандидат загрузил решение.\n" +
                    "Кандидат: " + c.getFullName() + " (id=" + c.getId() + ")\n" +
                    "Назначение: id=" + a.getId()
            );
        }

        return toCandidateDetails(a);
    }

    private void setCandidateStatus(Candidate candidate, String statusName) {
        CandidateStatus s = candidateStatusRepository.findByName(statusName)
            .orElseThrow(() -> new IllegalArgumentException("Candidate status not found: " + statusName));
        candidate.setStatus(s);
    }

    private AssignmentResponseDto toResponse(TaskAssignment a) {
        String assignedByName = null;

        if (a.getAssignedBy() != null) {
            String fn = a.getAssignedBy().getFullName();
            assignedByName = (fn != null && !fn.isBlank())
                ? fn
                : a.getAssignedBy().getLogin();
        }

        return new AssignmentResponseDto(
            a.getId(),
            a.getCandidate().getId(),
            a.getTask().getId(),
            assignedByName,
            a.getStatus().name(),
            a.getSolutionLink(),
            a.getSolutionText()
        );
    }

    private CandidateAssignmentDetailsResponseDto toCandidateDetails(TaskAssignment a) {
        TestTask t = a.getTask();
        return new CandidateAssignmentDetailsResponseDto(
            a.getId(),
            a.getStatus().name(),
            a.getSolutionLink(),
            a.getSolutionText(),
            t.getId(),
            t.getTitle(),
            t.getDescription(),
            t.getComplexityLevel()
        );
    }

    private CandidateAssignmentItemResponseDto toCandidateItem(TaskAssignment a) {
        TestTask t = a.getTask();
        return new CandidateAssignmentItemResponseDto(
            a.getId(),
            a.getStatus().name(),
            t.getId(),
            t.getTitle(),
            t.getComplexityLevel()
        );
    }
}
