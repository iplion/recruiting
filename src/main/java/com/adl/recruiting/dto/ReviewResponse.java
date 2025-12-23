package com.adl.recruiting.dto;

import com.adl.recruiting.entity.ReviewDecision;
import java.time.OffsetDateTime;

public record ReviewResponse(
    Long id,
    Long candidateId,
    Long reviewerId,
    String reviewerRole,
    Long recommendedVacancyId,
    Integer score,
    ReviewDecision decision,
    String comment,
    OffsetDateTime createdAt
) {}
