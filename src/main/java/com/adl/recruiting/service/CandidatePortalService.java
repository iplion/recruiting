package com.adl.recruiting.service;

import com.adl.recruiting.dto.CandidateSelfResponseDto;
import com.adl.recruiting.dto.UpdateCandidateSelfRequestDto;
import com.adl.recruiting.entity.Candidate;
import com.adl.recruiting.exception.NotFoundException;
import com.adl.recruiting.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CandidatePortalService {

    private final CandidateRepository candidateRepository;

    @Transactional(readOnly = true)
    public CandidateSelfResponseDto getCurrent(long candidateId) {
        Candidate c = candidateRepository.findById(candidateId)
            .orElseThrow(() -> new NotFoundException("Candidate not found: " + candidateId));
        return toSelfResponse(c);
    }

    @Transactional
    public CandidateSelfResponseDto updateCurrent(long candidateId, UpdateCandidateSelfRequestDto req) {
        Candidate c = candidateRepository.findById(candidateId)
            .orElseThrow(() -> new NotFoundException("Candidate not found: " + candidateId));

        if (req.fullName() != null) c.setFullName(req.fullName());
        if (req.contacts() != null) c.setContacts(req.contacts());
        if (req.experience() != null) c.setExperience(req.experience());

        return toSelfResponse(c);
    }

    private CandidateSelfResponseDto toSelfResponse(Candidate c) {
        Long vacancyId = c.getPlannedVacancy() == null ? null : c.getPlannedVacancy().getId();
        String status = c.getStatus() == null ? null : c.getStatus().getName();

        return new CandidateSelfResponseDto(
            c.getId(),
            c.getFullName(),
            c.getContacts(),
            c.getExperience(),
            status,
            vacancyId
        );
    }
}
