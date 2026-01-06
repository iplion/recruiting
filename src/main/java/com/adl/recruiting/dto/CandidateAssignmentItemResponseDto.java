package com.adl.recruiting.dto;

public record CandidateAssignmentItemResponseDto(
    Long assignmentId,
    String status,
    Long taskId,
    String taskTitle,
    String complexityLevel
) {}
