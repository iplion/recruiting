package com.adl.recruiting.dto;

public record TestTaskResponseDto(
    Long id,
    String title,
    String description,
    String complexityLevel
) {}
