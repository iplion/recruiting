package com.adl.recruiting.dto;

import com.adl.recruiting.entity.VacancyStatus;

public record VacancyResponse(
    Long id,
    String title,
    String level,
    VacancyStatus status
) {}