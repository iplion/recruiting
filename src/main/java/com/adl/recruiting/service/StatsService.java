package com.adl.recruiting.service;

import com.adl.recruiting.dto.StatsResponseDto;
import com.adl.recruiting.dto.StatsResponseDto.CountRowDto;
import com.adl.recruiting.dto.StatsResponseDto.PlannedVacancyRowDto;
import com.adl.recruiting.dto.StatsResponseDto.TokensDto;
import com.adl.recruiting.entity.TaskAssignmentStatus;
import com.adl.recruiting.entity.VacancyStatus;
import com.adl.recruiting.repository.CandidateRepository;
import com.adl.recruiting.repository.CandidateStatusRepository;
import com.adl.recruiting.repository.TaskAssignmentRepository;
import com.adl.recruiting.repository.VacancyRepository;
import java.time.OffsetDateTime;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final CandidateRepository candidateRepository;
    private final CandidateStatusRepository candidateStatusRepository;
    private final VacancyRepository vacancyRepository;
    private final TaskAssignmentRepository taskAssignmentRepository;

    public StatsResponseDto getStats() {
        // Candidate statuses: хотим ВСЕ статусы (в т.ч. с нулями) и в порядке из справочника
        Map<String, Long> candidateCounts = new HashMap<>();
        for (var row : candidateRepository.countByStatus()) {
            candidateCounts.put(row.getStatus(), row.getCount());
        }
        List<CountRowDto> candidateStatusRows = candidateStatusRepository.findAllByOrderByIdAsc().stream()
            .map(cs -> new CountRowDto(cs.getName(), candidateCounts.getOrDefault(cs.getName(), 0L)))
            .toList();

        // Vacancy statuses: хотим все enum значения (в т.ч. 0)
        Map<VacancyStatus, Long> vacancyCounts = new EnumMap<>(VacancyStatus.class);
        for (var row : vacancyRepository.countByStatus()) {
            vacancyCounts.put(row.getStatus(), row.getCount());
        }
        List<CountRowDto> vacancyStatusRows = List.of(VacancyStatus.values()).stream()
            .map(st -> new CountRowDto(st.name(), vacancyCounts.getOrDefault(st, 0L)))
            .toList();

        // Assignment statuses: хотим все enum значения (в т.ч. 0)
        Map<TaskAssignmentStatus, Long> assignmentCounts = new EnumMap<>(TaskAssignmentStatus.class);
        for (var row : taskAssignmentRepository.countByStatus()) {
            assignmentCounts.put(row.getStatus(), row.getCount());
        }
        List<CountRowDto> assignmentStatusRows = List.of(TaskAssignmentStatus.values()).stream()
            .map(st -> new CountRowDto(st.name(), assignmentCounts.getOrDefault(st, 0L)))
            .toList();

        // Planned vacancy distribution (только candidates, у которых plannedVacancy != null)
        List<PlannedVacancyRowDto> plannedVacancyRows = candidateRepository.countByPlannedVacancy().stream()
            .map(r -> new PlannedVacancyRowDto(
                r.getVacancyId(),
                r.getTitle(),
                r.getLevel(),
                r.getStatus(),
                r.getCount()
            ))
            .toList();

        // Tokens
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime soon = now.plusDays(3);
        long expired = candidateRepository.countExpiredTokens(now);
        long expiringSoon = candidateRepository.countExpiringTokens(now, soon);

        return new StatsResponseDto(
            candidateStatusRows,
            vacancyStatusRows,
            assignmentStatusRows,
            plannedVacancyRows,
            new TokensDto(expired, expiringSoon)
        );
    }
}
