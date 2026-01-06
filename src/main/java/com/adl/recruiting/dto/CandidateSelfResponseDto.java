package com.adl.recruiting.dto;

public record CandidateSelfResponseDto(
    Long id,
    String fullName,
    String contacts,
    String experience,
    String status,
    Long plannedVacancyId
) {}
