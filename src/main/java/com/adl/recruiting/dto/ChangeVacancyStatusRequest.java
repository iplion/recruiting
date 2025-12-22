package com.adl.recruiting.dto;

import jakarta.validation.constraints.NotNull;
import com.adl.recruiting.entity.VacancyStatus;

public record ChangeVacancyStatusRequest(
    @NotNull VacancyStatus status
) {}