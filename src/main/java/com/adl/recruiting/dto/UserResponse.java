package com.adl.recruiting.dto;

public record UserResponse(
    Long id,
    String fullName,
    String login,
    String role
) {}
