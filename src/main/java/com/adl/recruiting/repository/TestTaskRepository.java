package com.adl.recruiting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.adl.recruiting.entity.TestTask;

public interface TestTaskRepository extends JpaRepository<TestTask, Long> {
}
