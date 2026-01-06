package com.adl.recruiting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateVacancyRequestDto(
    @NotBlank @Size(max = 255) String title,
    @Size(max = 100) String level
) {}