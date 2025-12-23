package com.adl.recruiting.dto;

import jakarta.validation.constraints.NotNull;

public record CreateAssignmentRequest(
    @NotNull Long candidateId,
    @NotNull Long taskId,
    @NotNull Long assignedByUserId
) {}
