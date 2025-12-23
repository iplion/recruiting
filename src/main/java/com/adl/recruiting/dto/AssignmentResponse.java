package com.adl.recruiting.dto;

public record AssignmentResponse(
    Long id,
    Long candidateId,
    Long taskId,
    Long assignedByUserId,
    String status,
    String solutionLink,
    String solutionText
) {}
