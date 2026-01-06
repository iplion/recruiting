package com.adl.recruiting.controller;

import com.adl.recruiting.dto.AssignmentResponseDto;
import com.adl.recruiting.dto.CreateAssignmentRequestDto;
import com.adl.recruiting.service.TaskAssignmentService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('DIRECTOR','TEAMLEAD','PM')")
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/assignments")
public class TaskAssignmentAdminController {

    private final TaskAssignmentService taskAssignmentService;

    @PostMapping
    public AssignmentResponseDto assign(@Valid @RequestBody CreateAssignmentRequestDto req) {
        return taskAssignmentService.assign(req);
    }

    @GetMapping
    public List<AssignmentResponseDto> listByCandidate(@RequestParam("candidateId") long candidateId) {
        return taskAssignmentService.listByCandidate(candidateId);
    }

    @PatchMapping("/{id}/reviewed")
    public AssignmentResponseDto reviewed(@PathVariable("id") long id) {
        return taskAssignmentService.markReviewed(id);
    }
}
