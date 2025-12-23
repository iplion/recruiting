package com.adl.recruiting.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.adl.recruiting.dto.ChangeVacancyStatusRequest;
import com.adl.recruiting.dto.CreateVacancyRequest;
import com.adl.recruiting.dto.VacancyResponse;
import com.adl.recruiting.dto.UpdateVacancyRequest;
import com.adl.recruiting.entity.Vacancy;
import com.adl.recruiting.entity.VacancyStatus;
import com.adl.recruiting.repository.VacancyRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;

    @Transactional
    public VacancyResponse create(CreateVacancyRequest req) {
        Vacancy v = Vacancy.builder()
            .title(req.title())
            .level(req.level())
            .status(VacancyStatus.DRAFT)
            .build();
        v = vacancyRepository.save(v);
        return toResponse(v);
    }

    @Transactional(readOnly = true)
    public List<VacancyResponse> list(VacancyStatus status) {
        List<Vacancy> vacancies = (status == null)
            ? vacancyRepository.findAll()
            : vacancyRepository.findAllByStatus(status);
        return vacancies.stream().map(this::toResponse).toList();
    }

    @Transactional
    public VacancyResponse update(long id, UpdateVacancyRequest req) {
        Vacancy v = vacancyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Vacancy not found: " + id));

        v.setTitle(req.title());
        v.setLevel(req.level());
        v.setStatus(req.status());

        return toResponse(v);
    }

    @Transactional
    public VacancyResponse changeStatus(long id, ChangeVacancyStatusRequest req) {
        Vacancy v = vacancyRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Vacancy not found: " + id));
        v.setStatus(req.status());
        return toResponse(v);
    }

    private VacancyResponse toResponse(Vacancy v) {
        return new VacancyResponse(v.getId(), v.getTitle(), v.getLevel(), v.getStatus());
    }
}
