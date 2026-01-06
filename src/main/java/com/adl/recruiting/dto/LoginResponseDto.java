package com.adl.recruiting.dto;

public record LoginResponseDto(
    String token,
    String fullName,
    String role
) {}
