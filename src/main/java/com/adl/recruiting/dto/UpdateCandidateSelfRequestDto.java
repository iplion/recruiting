package com.adl.recruiting.dto;

import jakarta.validation.constraints.Size;

public record UpdateCandidateSelfRequestDto(
    @Size(max = 255) String fullName,
    String contacts,
    String experience
) {}
