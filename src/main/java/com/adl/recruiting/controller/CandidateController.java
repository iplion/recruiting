package com.adl.recruiting.controller;

import com.adl.recruiting.dto.CandidateSelfResponseDto;
import com.adl.recruiting.dto.UpdateCandidateSelfRequestDto;
import com.adl.recruiting.service.CandidatePortalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/candidate")
public class CandidateController {

    private final CandidatePortalService candidatePortalService;

    @GetMapping
    public CandidateSelfResponseDto get(Authentication authentication) {
        long candidateId = ((Long) authentication.getPrincipal()).longValue();
        return candidatePortalService.getCurrent(candidateId);
    }

    @PatchMapping
    public CandidateSelfResponseDto update(Authentication authentication,
                                           @Valid @RequestBody UpdateCandidateSelfRequestDto req) {
        long candidateId = ((Long) authentication.getPrincipal()).longValue();
        return candidatePortalService.updateCurrent(candidateId, req);
    }
}
