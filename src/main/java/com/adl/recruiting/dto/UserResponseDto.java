package com.adl.recruiting.dto;

public record UserResponseDto(
    Long id,
    String fullName,
    String login,
    String role
) {}
