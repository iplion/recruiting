package com.adl.recruiting.dto;

public record CandidateAssignmentDetailsResponseDto(
    Long assignmentId,
    String status,
    String solutionLink,
    String solutionText,
    Long taskId,
    String taskTitle,
    String taskDescription,
    String complexityLevel
) {}
