package com.adl.recruiting.dto;

import jakarta.validation.constraints.Size;

public record SubmitSolutionRequest(
    @Size(max = 2000) String solutionLink,
    @Size(max = 20000) String solutionText
) {}
