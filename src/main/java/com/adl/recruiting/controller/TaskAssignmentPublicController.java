package com.adl.recruiting.controller;

import com.adl.recruiting.dto.AssignmentResponse;
import com.adl.recruiting.dto.SubmitSolutionRequest;
import com.adl.recruiting.service.TaskAssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/public/assignments")
public class TaskAssignmentPublicController {

    private final TaskAssignmentService taskAssignmentService;

    @PatchMapping("/{id}/submit")
    public AssignmentResponse submit(
        @PathVariable("id") long id,
        @RequestParam("token") String token,
        @Valid @RequestBody SubmitSolutionRequest body
    ) {
        return taskAssignmentService.submitByToken(id, token, body);
    }
}
