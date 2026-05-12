package com.hei.school.tessaproject.repository;

import com.hei.school.tessaproject.domain.Project;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, String> {
    Page<Project> findAllByWorkspace_Id(String workspaceId, Pageable pageable);

    Optional<Project> findByIdAndWorkspace_Id(String id, String workspaceId);

    long countByWorkspace_Id(String workspaceId);

    void deleteAllByWorkspace_Id(String workspaceId);
}
