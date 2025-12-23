package com.adl.recruiting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank @Size(max = 100) String fullName,
    @NotBlank @Size(max = 100) String login,
    @NotBlank @Size(max = 255) String password,
    @NotBlank @Size(max = 30) String role
) {}
