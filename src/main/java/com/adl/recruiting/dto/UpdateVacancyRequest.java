package com.adl.recruiting.dto;

import com.adl.recruiting.entity.VacancyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateVacancyRequest(
    @NotBlank @Size(max = 255) String title,
    @Size(max = 100) String level,
    @NotNull VacancyStatus status
) {}
