package com.hei.school.tessaproject.repository;

import com.hei.school.tessaproject.domain.Workspace;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceRepository extends JpaRepository<Workspace, String> {
    Optional<Workspace> findByInviteCode(String inviteCode);
}
