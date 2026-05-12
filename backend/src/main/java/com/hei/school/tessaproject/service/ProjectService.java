package com.hei.school.tessaproject.service;

import com.hei.school.tessaproject.domain.Project;
import com.hei.school.tessaproject.domain.TaskStatus;
import com.hei.school.tessaproject.domain.User;
import com.hei.school.tessaproject.domain.Workspace;
import com.hei.school.tessaproject.dto.ProjectRequest;
import com.hei.school.tessaproject.exception.NotFoundException;
import com.hei.school.tessaproject.mapper.ApiMapper;
import com.hei.school.tessaproject.repository.ProjectRepository;
import com.hei.school.tessaproject.repository.TaskRepository;
import com.hei.school.tessaproject.repository.UserRepository;
import com.hei.school.tessaproject.repository.WorkspaceRepository;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ApiMapper mapper;

    public ProjectService(
            ProjectRepository projectRepository,
            WorkspaceRepository workspaceRepository,
            UserRepository userRepository,
            TaskRepository taskRepository,
            ApiMapper mapper) {
        this.projectRepository = projectRepository;
        this.workspaceRepository = workspaceRepository;
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Map<String, Object> createProject(String userId, String workspaceId, ProjectRequest request) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new NotFoundException("Workspace not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Project project = new Project();
        project.setName(request.name().trim());
        project.setDescription(trimToNull(request.description()));
        if (request.emoji() != null && !request.emoji().trim().isEmpty()) {
            project.setEmoji(request.emoji().trim());
        }
        project.setWorkspace(workspace);
        project.setCreatedBy(user);

        project = projectRepository.save(project);
        return mapper.project(project);
    }

    @Transactional(readOnly = true)
    public PageResult getProjectsInWorkspace(String workspaceId, int pageSize, int pageNumber) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new NotFoundException("Workspace not found");
        }
        int safePageSize = Math.max(pageSize, 1);
        int safePageNumber = Math.max(pageNumber, 1);
        PageRequest pageRequest = PageRequest.of(
                safePageNumber - 1,
                safePageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Project> page = projectRepository.findAllByWorkspace_Id(workspaceId, pageRequest);
        List<Map<String, Object>> projects = page.getContent().stream()
                .map(mapper::project)
                .toList();

        return new PageResult(
                projects,
                page.getTotalElements(),
                safePageSize,
                safePageNumber,
                page.getTotalPages(),
                (safePageNumber - 1) * safePageSize);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getProjectByIdAndWorkspaceId(String workspaceId, String projectId) {
        Project project = projectRepository.findByIdAndWorkspace_Id(projectId, workspaceId)
                .orElseThrow(() -> new NotFoundException(
                        "Project not found or does not belong to the specified workspace"));
        return mapper.project(project);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getProjectAnalytics(String workspaceId, String projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(
                        "Project not found or does not belong to this workspace"));
        if (!project.getWorkspace().getId().equals(workspaceId)) {
            throw new NotFoundException("Project not found or does not belong to this workspace");
        }

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("totalTasks", taskRepository.countByProject_Id(projectId));
        analytics.put("overdueTasks", taskRepository.countByProject_IdAndDueDateBeforeAndStatusNot(
                projectId, Instant.now(), TaskStatus.DONE));
        analytics.put("completedTasks", taskRepository.countByProject_IdAndStatus(projectId, TaskStatus.DONE));
        return analytics;
    }

    @Transactional
    public Map<String, Object> updateProject(String workspaceId, String projectId, ProjectRequest request) {
        Project project = projectRepository.findByIdAndWorkspace_Id(projectId, workspaceId)
                .orElseThrow(() -> new NotFoundException(
                        "Project not found or does not belong to the specified workspace"));

        project.setName(request.name().trim());
        project.setDescription(trimToNull(request.description()));
        if (request.emoji() != null && !request.emoji().trim().isEmpty()) {
            project.setEmoji(request.emoji().trim());
        }

        project = projectRepository.save(project);
        return mapper.project(project);
    }

    @Transactional
    public void deleteProject(String workspaceId, String projectId) {
        Project project = projectRepository.findByIdAndWorkspace_Id(projectId, workspaceId)
                .orElseThrow(() -> new NotFoundException(
                        "Project not found or does not belong to the specified workspace"));

        taskRepository.deleteAllByProject_Id(project.getId());
        projectRepository.delete(project);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    public record PageResult(
            List<Map<String, Object>> projects,
            long totalCount,
            int pageSize,
            int pageNumber,
            int totalPages,
            int skip) {
    }
}
