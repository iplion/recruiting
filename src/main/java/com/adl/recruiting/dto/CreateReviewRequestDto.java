package com.adl.recruiting.dto;

import com.adl.recruiting.entity.ReviewDecision;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateReviewRequestDto(
    @NotNull Long candidateId,
    Long recommendedVacancyId,
    Integer score,
    @NotNull ReviewDecision decision,
    @Size(max = 5000) String comment
) {}
