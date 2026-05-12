package com.hei.school.tessaproject.service;

import com.hei.school.tessaproject.domain.Member;
import com.hei.school.tessaproject.domain.Role;
import com.hei.school.tessaproject.domain.RoleName;
import com.hei.school.tessaproject.domain.User;
import com.hei.school.tessaproject.domain.Workspace;
import com.hei.school.tessaproject.dto.ChangeWorkspaceMemberRoleRequest;
import com.hei.school.tessaproject.dto.WorkspaceRequest;
import com.hei.school.tessaproject.exception.BadRequestException;
import com.hei.school.tessaproject.exception.NotFoundException;
import com.hei.school.tessaproject.mapper.ApiMapper;
import com.hei.school.tessaproject.repository.MemberRepository;
import com.hei.school.tessaproject.repository.ProjectRepository;
import com.hei.school.tessaproject.repository.RoleRepository;
import com.hei.school.tessaproject.repository.TaskRepository;
import com.hei.school.tessaproject.repository.UserRepository;
import com.hei.school.tessaproject.repository.WorkspaceRepository;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WorkspaceService {
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ApiMapper mapper;

    public WorkspaceService(
            WorkspaceRepository workspaceRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            MemberRepository memberRepository,
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            ApiMapper mapper) {
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.memberRepository = memberRepository;
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Map<String, Object> createWorkspace(String userId, WorkspaceRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Role ownerRole = roleRepository.findByName(RoleName.OWNER)
                .orElseThrow(() -> new NotFoundException("Owner role not found"));

        Workspace workspace = new Workspace();
        workspace.setName(request.name().trim());
        workspace.setDescription(trimToNull(request.description()));
        workspace.setOwner(user);
        workspace = workspaceRepository.save(workspace);

        Member member = new Member();
        member.setUser(user);
        member.setWorkspace(workspace);
        member.setRole(ownerRole);
        member.setJoinedAt(Instant.now());
        memberRepository.save(member);

        user.setCurrentWorkspace(workspace);
        userRepository.save(user);

        return mapper.workspace(workspace);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllWorkspacesUserIsMember(String userId) {
        return memberRepository.findAllByUser_Id(userId).stream()
                .map(Member::getWorkspace)
                .map(mapper::workspace)
                .toList();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getWorkspaceById(String workspaceId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workspace not found"));
        Map<String, Object> result = mapper.workspace(workspace);
        List<Map<String, Object>> members = memberRepository.findAllByWorkspace_Id(workspaceId).stream()
                .map(mapper::workspaceMember)
                .toList();
        result.put("members", members);
        return result;
    }

    @Transactional(readOnly = true)
    public MembersAndRoles getWorkspaceMembers(String workspaceId) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new NotFoundException("Workspace not found");
        }

        List<Map<String, Object>> members = memberRepository.findAllByWorkspace_Id(workspaceId).stream()
                .map(mapper::memberWithUser)
                .toList();
        List<Map<String, Object>> roles = roleRepository.findAll().stream()
                .map(mapper::roleSummary)
                .toList();

        return new MembersAndRoles(members, roles);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getWorkspaceAnalytics(String workspaceId) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new NotFoundException("Workspace not found");
        }

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("totalTasks", taskRepository.countByWorkspace_Id(workspaceId));
        analytics.put("overdueTasks", taskRepository.countByWorkspace_IdAndDueDateBeforeAndStatusNot(
                workspaceId, Instant.now(), com.hei.school.tessaproject.domain.TaskStatus.DONE));
        analytics.put("completedTasks", taskRepository.countByWorkspace_IdAndStatus(
                workspaceId, com.hei.school.tessaproject.domain.TaskStatus.DONE));
        return analytics;
    }

    @Transactional
    public Map<String, Object> changeMemberRole(String workspaceId, ChangeWorkspaceMemberRoleRequest request) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new NotFoundException("Workspace not found");
        }
        Role role = roleRepository.findById(request.roleId())
                .orElseThrow(() -> new NotFoundException("Role not found"));
        Member member = memberRepository.findByUser_IdAndWorkspace_Id(request.memberId(), workspaceId)
                .orElseThrow(() -> new NotFoundException("Member not found in the workspace"));

        member.setRole(role);
        member = memberRepository.save(member);
        return mapper.memberWithUser(member);
    }

    @Transactional
    public Map<String, Object> updateWorkspace(String workspaceId, WorkspaceRequest request) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workspace not found"));

        workspace.setName(request.name().trim());
        workspace.setDescription(trimToNull(request.description()));
        workspace = workspaceRepository.save(workspace);
        return mapper.workspace(workspace);
    }

    @Transactional
    public String deleteWorkspace(String workspaceId, String userId) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workspace not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        if (!workspace.getOwner().getId().equals(userId)) {
            throw new BadRequestException("You are not authorized to delete this workspace");
        }

        Workspace nextWorkspace = memberRepository.findAllByUser_Id(userId).stream()
                .map(Member::getWorkspace)
                .filter(candidate -> !candidate.getId().equals(workspaceId))
                .findFirst()
                .orElse(null);

        if (user.getCurrentWorkspace() != null && user.getCurrentWorkspace().getId().equals(workspaceId)) {
            user.setCurrentWorkspace(nextWorkspace);
            userRepository.save(user);
        }

        taskRepository.deleteAllByWorkspace_Id(workspaceId);
        projectRepository.deleteAllByWorkspace_Id(workspaceId);
        memberRepository.deleteAllByWorkspace_Id(workspaceId);
        workspaceRepository.delete(workspace);

        return user.getCurrentWorkspace() == null ? null : user.getCurrentWorkspace().getId();
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    public record MembersAndRoles(List<Map<String, Object>> members, List<Map<String, Object>> roles) {
    }
}
