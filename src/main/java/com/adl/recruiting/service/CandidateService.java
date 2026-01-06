package com.adl.recruiting.service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.adl.recruiting.dto.CandidateResponseDto;
import com.adl.recruiting.dto.ChangeCandidateStatusRequestDto;
import com.adl.recruiting.dto.CreateCandidateRequestDto;
import com.adl.recruiting.entity.Candidate;
import com.adl.recruiting.entity.CandidateStatus;
import com.adl.recruiting.entity.Vacancy;
import com.adl.recruiting.repository.CandidateRepository;
import com.adl.recruiting.repository.CandidateStatusRepository;
import com.adl.recruiting.repository.VacancyRepository;

@Service
@RequiredArgsConstructor
public class CandidateService {

    private static final String STATUS_NEW = "new";

    private final CandidateRepository candidateRepository;
    private final CandidateStatusRepository candidateStatusRepository;
    private final VacancyRepository vacancyRepository;

    @Value("${telegram.bot-username:}")
    private String botUsername;

    @Transactional
    public CandidateResponseDto create(CreateCandidateRequestDto req) {
        CandidateStatus status = candidateStatusRepository.findByName(STATUS_NEW)
            .orElseThrow(() -> new IllegalStateException("Candidate status not found in DB: " + STATUS_NEW));

        Vacancy plannedVacancy = null;
        if (req.plannedVacancyId() != null) {
            plannedVacancy = vacancyRepository.findById(req.plannedVacancyId())
                .orElseThrow(() -> new IllegalArgumentException("Vacancy not found: " + req.plannedVacancyId()));
        }

        Candidate c = new Candidate();
        c.setAccessToken(UUID.randomUUID().toString());
        c.setTokenExpiresAt(OffsetDateTime.now().plusDays(30));
        c.setFullName(req.fullName());
        c.setContacts(req.contacts());
        c.setExperience(req.experience());
        c.setStatus(status);
        c.setPlannedVacancy(plannedVacancy);

        c = candidateRepository.save(c);


        return toResponse(c);
    }

    @Transactional(readOnly = true)
    public CandidateResponseDto getById(long id) {
        Candidate c = candidateRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + id));
        return toResponse(c);
    }

    @Transactional(readOnly = true)
    public List<CandidateResponseDto> list() {
        return candidateRepository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional
    public CandidateResponseDto changeStatus(long id, ChangeCandidateStatusRequestDto req) {
        Candidate c = candidateRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Candidate not found: " + id));

        CandidateStatus status = candidateStatusRepository.findByName(req.status())
            .orElseThrow(() -> new IllegalArgumentException("Unknown candidate status: " + req.status()));

        c.setStatus(status);
        return toResponse(c);
    }

    private CandidateResponseDto toResponse(Candidate c) {
        Long vacancyId = (c.getPlannedVacancy() == null) ? null : c.getPlannedVacancy().getId();
        String statusName = (c.getStatus() == null) ? null : c.getStatus().getName();
        String tgLink = null;
        if (botUsername != null && !botUsername.isBlank() && c.getAccessToken() != null) {
            tgLink = "https://t.me/" + botUsername + "?start=" + c.getAccessToken();
        }

        return new CandidateResponseDto(
            c.getId(),
            c.getTokenExpiresAt(),
            c.getFullName(),
            c.getContacts(),
            c.getExperience(),
            statusName,
            vacancyId,
            tgLink
        );
    }
}
