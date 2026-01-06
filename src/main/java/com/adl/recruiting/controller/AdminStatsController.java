package com.adl.recruiting.controller;

import com.adl.recruiting.dto.StatsResponseDto;
import com.adl.recruiting.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/stats")
public class AdminStatsController {

    private final StatsService statsService;

    @PreAuthorize("hasAnyRole('DIRECTOR','TEAMLEAD','PM')")
    @GetMapping
    public StatsResponseDto get() {
        return statsService.getStats();
    }
}
