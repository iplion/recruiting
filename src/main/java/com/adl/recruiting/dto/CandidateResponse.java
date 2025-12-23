package com.adl.recruiting.dto;

import java.time.OffsetDateTime;

public record CandidateResponse(
    Long id,
    String accessToken,
    OffsetDateTime tokenExpiresAt,
    String fullName,
    String contacts,
    String experience,
    String status,
    Long plannedVacancyId
) {}
