package com.adl.recruiting.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.adl.recruiting.dto.CreateTestTaskRequest;
import com.adl.recruiting.dto.TestTaskResponse;
import com.adl.recruiting.entity.TestTask;
import com.adl.recruiting.repository.TestTaskRepository;

@Service
@RequiredArgsConstructor
public class TestTaskService {

    private final TestTaskRepository testTaskRepository;

    @Transactional
    public TestTaskResponse create(CreateTestTaskRequest req) {
        TestTask t = new TestTask();
        t.setTitle(req.title());
        t.setDescription(req.description());
        t.setComplexityLevel(req.complexityLevel());

        t = testTaskRepository.save(t);
        return toResponse(t);
    }

    @Transactional(readOnly = true)
    public List<TestTaskResponse> list() {
        return testTaskRepository.findAll().stream().map(this::toResponse).toList();
    }

    private TestTaskResponse toResponse(TestTask t) {
        return new TestTaskResponse(t.getId(), t.getTitle(), t.getDescription(), t.getComplexityLevel());
    }
}
