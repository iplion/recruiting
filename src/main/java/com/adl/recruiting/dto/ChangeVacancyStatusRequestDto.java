package com.adl.recruiting.dto;

import jakarta.validation.constraints.NotNull;
import com.adl.recruiting.entity.VacancyStatus;

public record ChangeVacancyStatusRequestDto(
    @NotNull VacancyStatus status
) {}