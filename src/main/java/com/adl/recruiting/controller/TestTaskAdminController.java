package com.adl.recruiting.controller;

import com.adl.recruiting.dto.CreateTestTaskRequestDto;
import com.adl.recruiting.dto.TestTaskResponseDto;
import com.adl.recruiting.service.TestTaskService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@PreAuthorize("hasAnyRole('DIRECTOR','TEAMLEAD','PM')")
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/test-tasks")
public class TestTaskAdminController {

    private final TestTaskService testTaskService;

    @PostMapping
    public TestTaskResponseDto create(@Valid @RequestBody CreateTestTaskRequestDto req) {
        return testTaskService.create(req);
    }

    @GetMapping
    public List<TestTaskResponseDto> list() {
        return testTaskService.list();
    }
}
