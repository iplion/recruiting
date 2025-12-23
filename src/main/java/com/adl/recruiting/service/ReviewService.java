package com.adl.recruiting.service;

import com.adl.recruiting.dto.CreateReviewRequest;
import com.adl.recruiting.dto.ReviewResponse;
import com.adl.recruiting.entity.Candidate;
import com.adl.recruiting.entity.CandidateStatus;
import com.adl.recruiting.entity.Review;
import com.adl.recruiting.entity.ReviewDecision;
import com.adl.recruiting.entity.User;
import com.adl.recruiting.entity.Vacancy;
import com.adl.recruiting.repository.CandidateRepository;
import com.adl.recruiting.repository.CandidateStatusRepository;
import com.adl.recruiting.repository.ReviewRepository;
import com.adl.recruiting.repository.UserRepository;
import com.adl.recruiting.repository.VacancyRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CandidateRepository candidateRepository;
    private final UserRepository userRepository;
    private final VacancyRepository vacancyRepository;
    private final CandidateStatusRepository candidateStatusRepository;

    @Transactional
    public ReviewResponse create(CreateReviewRequest req) {
        Candidate candidate = candidateRepository.findById(req.candidateId())
            .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + req.candidateId()));

        User reviewer = userRepository.findById(req.reviewerId())
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + req.reviewerId()));

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

        return toResponse(r);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> listByCandidate(long candidateId) {
        return reviewRepository.findAllByCandidateIdOrderByCreatedAtDesc(candidateId).stream()
            .map(this::toResponse)
            .toList();
    }

    private void applyCandidateStatusByReviewerAndDecision(Candidate candidate, User reviewer, ReviewDecision decision) {
        String role = reviewer.getRole().getName();

        if ("teamlead".equals(role)) {
            setCandidateStatus(candidate, "tech_review");
            return;
        }

        if ("pm".equals(role)) {
            setCandidateStatus(candidate, "pm_review");
            return;
        }

        if ("director".equals(role)) {
            if (decision == ReviewDecision.INVITE) {
                setCandidateStatus(candidate, "invited");
            } else if (decision == ReviewDecision.REJECT) {
                setCandidateStatus(candidate, "rejected");
            } else if (decision == ReviewDecision.PAUSE) {
                setCandidateStatus(candidate, "paused");
            } else {
                setCandidateStatus(candidate, "director_review");
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

    private ReviewResponse toResponse(Review r) {
        Long vacancyId = (r.getRecommendedVacancy() == null) ? null : r.getRecommendedVacancy().getId();
        String reviewerRole = (r.getReviewer() == null || r.getReviewer().getRole() == null)
            ? null
            : r.getReviewer().getRole().getName();

        return new ReviewResponse(
            r.getId(),
            r.getCandidate().getId(),
            r.getReviewer().getId(),
            reviewerRole,
            vacancyId,
            r.getScore(),
            r.getDecision(),
            r.getComment(),
            r.getCreatedAt()
        );
    }
}
