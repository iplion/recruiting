package com.adl.recruiting.controller;

import java.util.List;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.adl.recruiting.dto.CandidateResponse;
import com.adl.recruiting.dto.ChangeCandidateStatusRequest;
import com.adl.recruiting.dto.CreateCandidateRequest;
import com.adl.recruiting.service.CandidateService;

@RestController
@PreAuthorize("hasAnyRole('DIRECTOR','TEAMLEAD','PM')")
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/candidates")
public class CandidateAdminController {

    private final CandidateService candidateService;

    @PreAuthorize("hasRole('DIRECTOR')")
    @PostMapping
    public CandidateResponse create(@Valid @RequestBody CreateCandidateRequest req) {
        return candidateService.create(req);
    }

    @GetMapping
    public List<CandidateResponse> list() {
        return candidateService.list();
    }

    @GetMapping("/{id}")
    public CandidateResponse getById(@PathVariable long id) {
        return candidateService.getById(id);
    }

    @PatchMapping("/{id}/status")
    public CandidateResponse changeStatus(@PathVariable long id,
                                          @Valid @RequestBody ChangeCandidateStatusRequest req) {
        return candidateService.changeStatus(id, req);
    }
}
