package com.hei.school.tessaproject.repository;

import com.hei.school.tessaproject.domain.Member;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, String> {
    @EntityGraph(attributePaths = {"role"})
    Optional<Member> findByUser_IdAndWorkspace_Id(String userId, String workspaceId);

    @EntityGraph(attributePaths = {"workspace"})
    List<Member> findAllByUser_Id(String userId);

    @EntityGraph(attributePaths = {"user", "role"})
    List<Member> findAllByWorkspace_Id(String workspaceId);

    boolean existsByUser_IdAndWorkspace_Id(String userId, String workspaceId);

    void deleteAllByWorkspace_Id(String workspaceId);

    Optional<Member> findFirstByUser_Id(String userId);
}
