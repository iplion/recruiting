package com.adl.recruiting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangeCandidateStatusRequestDto(
    @NotBlank @Size(max = 50) String status
) {}
