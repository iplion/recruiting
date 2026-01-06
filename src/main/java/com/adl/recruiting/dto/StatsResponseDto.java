package com.adl.recruiting.dto;

import com.adl.recruiting.entity.VacancyStatus;
import java.util.List;

/**
 * Aggregated statistics for the admin dashboard.
 * Contract is aligned with the frontend (GET /api/v1/admin/stats).
 */
public record StatsResponseDto(
    List<CountRowDto> candidateStatus,
    List<CountRowDto> vacancyStatus,
    List<CountRowDto> assignmentStatus,
    List<PlannedVacancyRowDto> plannedVacancy,
    TokensDto tokens
) {
    public record CountRowDto(String status, long count) {}

    public record PlannedVacancyRowDto(
        long vacancyId,
        String title,
        String level,
        VacancyStatus status,
        long count
    ) {}

    public record TokensDto(long expired, long expiringSoon) {}
}
