package com.adl.recruiting.dto;

public record TestTaskResponse(
    Long id,
    String title,
    String description,
    String complexityLevel
) {}
