package com.adl.recruiting.dto;

import jakarta.validation.constraints.Size;

public record SubmitSolutionRequestDto(
    @Size(max = 4000)
    String solutionText,

    @Size(max = 2048)
    String solutionLink
) {
    public boolean hasAny() {
        return (solutionText != null && !solutionText.isBlank())
            || (solutionLink != null && !solutionLink.isBlank());
    }
}
