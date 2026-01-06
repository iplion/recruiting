package com.adl.recruiting.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.adl.recruiting.dto.CreateTestTaskRequestDto;
import com.adl.recruiting.dto.TestTaskResponseDto;
import com.adl.recruiting.entity.TestTask;
import com.adl.recruiting.repository.TestTaskRepository;

@Service
@RequiredArgsConstructor
public class TestTaskService {

    private final TestTaskRepository testTaskRepository;

    @Transactional
    public TestTaskResponseDto create(CreateTestTaskRequestDto req) {
        TestTask t = new TestTask();
        t.setTitle(req.title());
        t.setDescription(req.description());
        t.setComplexityLevel(req.complexityLevel());

        t = testTaskRepository.save(t);
        return toResponse(t);
    }

    @Transactional(readOnly = true)
    public List<TestTaskResponseDto> list() {
        return testTaskRepository.findAll().stream().map(this::toResponse).toList();
    }

    private TestTaskResponseDto toResponse(TestTask t) {
        return new TestTaskResponseDto(t.getId(), t.getTitle(), t.getDescription(), t.getComplexityLevel());
    }
}
