package com.adl.recruiting.service;

import com.adl.recruiting.dto.CreateReviewRequestDto;
import com.adl.recruiting.dto.ReviewResponseDto;
import com.adl.recruiting.entity.Candidate;
import com.adl.recruiting.entity.CandidateStatus;
import com.adl.recruiting.entity.Review;
import com.adl.recruiting.entity.ReviewDecision;
import com.adl.recruiting.entity.User;
import com.adl.recruiting.entity.Vacancy;
import com.adl.recruiting.repository.CandidateRepository;
import com.adl.recruiting.repository.CandidateStatusRepository;
import com.adl.recruiting.repository.ReviewRepository;
import com.adl.recruiting.repository.VacancyRepository;
import com.adl.recruiting.util.AuthUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CandidateRepository candidateRepository;
    private final VacancyRepository vacancyRepository;
    private final CandidateStatusRepository candidateStatusRepository;
    private final AuthUtils authUtils;
    private final TelegramNotificationService telegramNotificationService;

    @Transactional
    public ReviewResponseDto create(CreateReviewRequestDto req) {
        Candidate candidate = candidateRepository.findById(req.candidateId())
            .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + req.candidateId()));

        User reviewer = authUtils.resolveCurrentUser();

        Vacancy recommendedVacancy = null;
        if (req.recommendedVacancyId() != null) {
            recommendedVacancy = vacancyRepository.findById(req.recommendedVacancyId())
                .orElseThrow(() -> new IllegalArgumentException("Vacancy not found: " + req.recommendedVacancyId()));
        }

        Review r = new Review();
        r.setCandidate(candidate);
        r.setReviewer(reviewer);
        r.setRecommendedVacancy(recommendedVacancy);
        r.setScore(req.score());
        r.setDecision(req.decision());
        r.setComment(req.comment());

        r = reviewRepository.save(r);

        applyCandidateStatusByReviewerAndDecision(candidate, reviewer, req.decision());

        CandidateStatus newStatus = candidate.getStatus();
        String status = (newStatus == null) ? null : newStatus.getName();
        String candidateLabel = candidate.getFullName() + " (id=" + candidate.getId() + ")";

        if ("pm_review".equals(status)) {
            telegramNotificationService.notifyRole("pm",
                "Требуется проектная оценка кандидата.\nКандидат: " + candidateLabel);
        }

        if ("director_review".equals(status)) {
            telegramNotificationService.notifyRole("director",
                "Требуется финальное решение директора.\nКандидат: " + candidateLabel);
        }

        if ("invited".equals(status) || "rejected".equals(status) || "paused".equals(status)) {
            String msg;
            switch (status) {
                case "invited" -> msg = "Спасибо! Мы готовы пригласить вас на следующий этап. Мы свяжемся с вами.";
                case "rejected" -> msg = "Спасибо за участие. К сожалению, сейчас мы не готовы продолжить процесс.";
                case "paused" -> msg = "Процесс временно приостановлен. Мы вернёмся с обновлениями позже.";
                default -> msg = "Статус обновлён: " + status;
            }
            telegramNotificationService.notifyCandidate(candidate, msg);
        }

        return toResponse(r);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponseDto> listByCandidate(long candidateId) {
        return reviewRepository.findByCandidateIdWithReviewer(candidateId).stream()
            .map(this::toResponse)
            .toList();
    }

    private void applyCandidateStatusByReviewerAndDecision(Candidate candidate, User reviewer, ReviewDecision decision) {
        String role = reviewer.getRole() != null ? reviewer.getRole().getName() : null;
        if (role == null) {
            throw new IllegalArgumentException("Reviewer role is null");
        }

        if ("teamlead".equalsIgnoreCase(role)) {
            switch (decision) {
                case RECOMMEND -> setCandidateStatus(candidate, "pm_review");
                case NOT_RECOMMEND, REJECT -> setCandidateStatus(candidate, "rejected");
                case PAUSE -> setCandidateStatus(candidate, "paused");
                default -> setCandidateStatus(candidate, "tech_review");
            }
            return;
        }

        if ("pm".equalsIgnoreCase(role)) {
            switch (decision) {
                case RECOMMEND -> setCandidateStatus(candidate, "director_review");
                case NOT_RECOMMEND, REJECT -> setCandidateStatus(candidate, "rejected");
                case PAUSE -> setCandidateStatus(candidate, "paused");
                default -> setCandidateStatus(candidate, "pm_review");
            }
            return;
        }

        if ("director".equalsIgnoreCase(role)) {
            switch (decision) {
                case INVITE -> setCandidateStatus(candidate, "invited");
                case REJECT, NOT_RECOMMEND -> setCandidateStatus(candidate, "rejected");
                case PAUSE -> setCandidateStatus(candidate, "paused");
                default -> setCandidateStatus(candidate, "director_review");
            }
            return;
        }

        throw new IllegalArgumentException("Unsupported reviewer role: " + role);
    }

    private void setCandidateStatus(Candidate candidate, String statusName) {
        CandidateStatus s = candidateStatusRepository.findByName(statusName)
            .orElseThrow(() -> new IllegalArgumentException("Candidate status not found: " + statusName));
        candidate.setStatus(s);
    }

    private ReviewResponseDto toResponse(Review r) {
        Long vacancyId = (r.getRecommendedVacancy() == null) ? null : r.getRecommendedVacancy().getId();

        User reviewer = r.getReviewer();
        if (reviewer == null) {
            throw new IllegalStateException("Reviewer is null for review id=" + r.getId());
        }

        String reviewerRole = reviewer.getRole() == null
            ? null
            : reviewer.getRole().getName();

        String reviewerName = reviewer.getFullName();
        if (reviewerName == null || reviewerName.isBlank()) {
            reviewerName = reviewer.getLogin();
        }
        return new ReviewResponseDto(
            r.getId(),
            r.getCandidate().getId(),
            reviewer.getId(),
            reviewerName,
            reviewerRole,
            vacancyId,
            r.getScore(),
            r.getDecision(),
            r.getComment(),
            r.getCreatedAt()
        );
    }
}
