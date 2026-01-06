package com.adl.recruiting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCandidateRequestDto(
    @NotBlank @Size(max = 255) String fullName,
    String contacts,
    String experience,
    Long plannedVacancyId
) {}
