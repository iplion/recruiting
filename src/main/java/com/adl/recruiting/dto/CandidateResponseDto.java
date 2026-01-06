package com.adl.recruiting.dto;

import java.time.OffsetDateTime;

public record CandidateResponseDto(
    Long id,
    OffsetDateTime tokenExpiresAt,
    String fullName,
    String contacts,
    String experience,
    String status,
    Long plannedVacancyId,
    String telegramInviteLink
) {}
