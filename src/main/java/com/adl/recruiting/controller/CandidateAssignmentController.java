package com.adl.recruiting.controller;

import com.adl.recruiting.dto.CandidateAssignmentDetailsResponseDto;
import com.adl.recruiting.dto.CandidateAssignmentItemResponseDto;
import com.adl.recruiting.dto.SubmitSolutionRequestDto;
import com.adl.recruiting.service.TaskAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/candidate/assignments")
public class CandidateAssignmentController {

    private final TaskAssignmentService taskAssignmentService;

    @GetMapping
    public List<CandidateAssignmentItemResponseDto> list(Authentication authentication) {
        long candidateId = ((Long) authentication.getPrincipal()).longValue();
        return taskAssignmentService.listForCandidate(candidateId);
    }

    @GetMapping("/{id}")
    public CandidateAssignmentDetailsResponseDto get(@PathVariable("id") long id,
                                                     Authentication authentication) {
        long candidateId = ((Long) authentication.getPrincipal()).longValue();
        return taskAssignmentService.getForCandidate(id, candidateId);
    }

    @PatchMapping("/{id}/submit")
    public CandidateAssignmentDetailsResponseDto submit(@PathVariable("id") long id,
                                                        Authentication authentication,
                                                        @Valid @RequestBody SubmitSolutionRequestDto body) {
        long candidateId = ((Long) authentication.getPrincipal()).longValue();
        return taskAssignmentService.submitByCandidate(id, candidateId, body);
    }
}
