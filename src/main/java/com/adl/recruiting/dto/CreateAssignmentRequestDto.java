package com.adl.recruiting.dto;

import jakarta.validation.constraints.NotNull;

public record CreateAssignmentRequestDto(
    @NotNull Long candidateId,
    @NotNull Long taskId
) {}
