package com.adl.recruiting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTestTaskRequestDto(
    @NotBlank @Size(max = 255) String title,
    String description,
    @Size(max = 100) String complexityLevel
) {}
