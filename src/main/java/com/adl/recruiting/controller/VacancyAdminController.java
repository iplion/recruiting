package com.adl.recruiting.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.adl.recruiting.dto.ChangeVacancyStatusRequest;
import com.adl.recruiting.dto.CreateVacancyRequest;
import com.adl.recruiting.dto.VacancyResponse;
import com.adl.recruiting.entity.VacancyStatus;
import com.adl.recruiting.service.VacancyService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admin/vacancies")
public class VacancyAdminController {

    private final VacancyService vacancyService;

    @PostMapping
    public VacancyResponse create(@Valid @RequestBody CreateVacancyRequest req) {
        return vacancyService.create(req);
    }

    @GetMapping
    public List<VacancyResponse> list(@RequestParam(required = false) VacancyStatus status) {
        return vacancyService.list(status);
    }

    @PatchMapping("/{id}/status")
    public VacancyResponse changeStatus(@PathVariable long id,
                                        @Valid @RequestBody ChangeVacancyStatusRequest req) {
        return vacancyService.changeStatus(id, req);
    }
}