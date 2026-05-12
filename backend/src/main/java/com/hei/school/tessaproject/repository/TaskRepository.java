package com.hei.school.tessaproject.repository;

import com.hei.school.tessaproject.domain.Task;
import com.hei.school.tessaproject.domain.TaskStatus;
import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TaskRepository extends JpaRepository<Task, String>, JpaSpecificationExecutor<Task> {
    long countByWorkspace_Id(String workspaceId);

    long countByWorkspace_IdAndDueDateBeforeAndStatusNot(String workspaceId, Instant dueDate, TaskStatus status);

    long countByWorkspace_IdAndStatus(String workspaceId, TaskStatus status);

    long countByProject_Id(String projectId);

    long countByProject_IdAndDueDateBeforeAndStatusNot(String projectId, Instant dueDate, TaskStatus status);

    long countByProject_IdAndStatus(String projectId, TaskStatus status);

    Optional<Task> findByIdAndWorkspace_IdAndProject_Id(String id, String workspaceId, String projectId);

    Optional<Task> findByIdAndWorkspace_Id(String id, String workspaceId);

    void deleteAllByWorkspace_Id(String workspaceId);

    void deleteAllByProject_Id(String projectId);
}
