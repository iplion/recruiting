package com.adl.recruiting.dto;

public record AssignmentResponseDto(
    Long id,
    Long candidateId,
    Long taskId,
    String assignedByName,
    String status,
    String solutionLink,
    String solutionText
) {}
