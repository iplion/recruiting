package com.adl.recruiting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.adl.recruiting.dto.ChangeVacancyStatusRequestDto;
import com.adl.recruiting.dto.CreateVacancyRequestDto;
import com.adl.recruiting.dto.UpdateVacancyRequestDto;
import com.adl.recruiting.dto.VacancyResponseDto;
import com.adl.recruiting.entity.VacancyStatus;
import com.adl.recruiting.service.VacancyService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/vacancies")
public class VacancyAdminController {

    private final VacancyService vacancyService;

    @PreAuthorize("hasRole('DIRECTOR')")
    @PostMapping
    public VacancyResponseDto create(@Valid @RequestBody CreateVacancyRequestDto req) {
        return vacancyService.create(req);
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','TEAMLEAD','PM')")
    @GetMapping
    public List<VacancyResponseDto> list(@RequestParam(required = false) VacancyStatus status) {
        return vacancyService.list(status);
    }

    @PreAuthorize("hasRole('DIRECTOR')")
    @PutMapping("/{id}")
    public VacancyResponseDto putUpdate(@PathVariable long id,
                                        @Valid @RequestBody UpdateVacancyRequestDto req) {
        return vacancyService.update(id, req);
    }

    @PreAuthorize("hasAnyRole('DIRECTOR','TEAMLEAD','PM')")
    @PatchMapping("/{id}/status")
    public VacancyResponseDto changeStatus(@PathVariable long id,
                                           @Valid @RequestBody ChangeVacancyStatusRequestDto req) {
        return vacancyService.changeStatus(id, req);
    }
}