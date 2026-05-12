package com.hei.school.tessaproject.service;

import com.hei.school.tessaproject.domain.Member;
import com.hei.school.tessaproject.domain.Project;
import com.hei.school.tessaproject.domain.Task;
import com.hei.school.tessaproject.domain.TaskPriority;
import com.hei.school.tessaproject.domain.TaskStatus;
import com.hei.school.tessaproject.domain.User;
import com.hei.school.tessaproject.dto.TaskRequest;
import com.hei.school.tessaproject.exception.BadRequestException;
import com.hei.school.tessaproject.exception.NotFoundException;
import com.hei.school.tessaproject.mapper.ApiMapper;
import com.hei.school.tessaproject.repository.MemberRepository;
import com.hei.school.tessaproject.repository.ProjectRepository;
import com.hei.school.tessaproject.repository.TaskRepository;
import com.hei.school.tessaproject.repository.UserRepository;
import jakarta.persistence.criteria.Predicate;
import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final ApiMapper mapper;

    public TaskService(
            TaskRepository taskRepository,
            ProjectRepository projectRepository,
            UserRepository userRepository,
            MemberRepository memberRepository,
            ApiMapper mapper) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.memberRepository = memberRepository;
        this.mapper = mapper;
    }

    @Transactional
    public Map<String, Object> createTask(String workspaceId, String projectId, String userId, TaskRequest request) {
        Project project = getProjectInWorkspace(projectId, workspaceId);
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        User assignee = resolveAssignee(workspaceId, request.assignedTo());

        Task task = new Task();
        task.setTitle(request.title().trim());
        task.setDescription(trimToNull(request.description()));
        task.setPriority(request.priority());
        task.setStatus(request.status());
        task.setAssignedTo(assignee);
        task.setCreatedBy(creator);
        task.setWorkspace(project.getWorkspace());
        task.setProject(project);
        task.setDueDate(parseInstant(request.dueDate()));

        task = taskRepository.save(task);
        return mapper.task(task);
    }

    @Transactional
    public Map<String, Object> updateTask(String workspaceId, String projectId, String taskId, TaskRequest request) {
        getProjectInWorkspace(projectId, workspaceId);
        Task task = taskRepository.findByIdAndWorkspace_IdAndProject_Id(taskId, workspaceId, projectId)
                .orElseThrow(() -> new NotFoundException("Task not found or does not belong to this project"));

        task.setTitle(request.title().trim());
        task.setDescription(trimToNull(request.description()));
        task.setPriority(request.priority());
        task.setStatus(request.status());
        task.setAssignedTo(resolveAssignee(workspaceId, request.assignedTo()));
        task.setDueDate(parseInstant(request.dueDate()));

        task = taskRepository.save(task);
        return mapper.task(task);
    }

    @Transactional(readOnly = true)
    public PageResult getAllTasks(String workspaceId, TaskFilters filters, int pageSize, int pageNumber) {
        int safePageSize = Math.max(pageSize, 1);
        int safePageNumber = Math.max(pageNumber, 1);
        PageRequest pageRequest = PageRequest.of(
                safePageNumber - 1,
                safePageSize,
                Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Task> page = taskRepository.findAll(specification(workspaceId, filters), pageRequest);
        List<Map<String, Object>> tasks = page.getContent().stream()
                .map(mapper::task)
                .toList();

        return new PageResult(
                tasks,
                page.getTotalElements(),
                safePageSize,
                safePageNumber,
                page.getTotalPages(),
                (safePageNumber - 1) * safePageSize);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTaskById(String workspaceId, String projectId, String taskId) {
        getProjectInWorkspace(projectId, workspaceId);
        Task task = taskRepository.findByIdAndWorkspace_IdAndProject_Id(taskId, workspaceId, projectId)
                .orElseThrow(() -> new NotFoundException("Task not found."));
        return mapper.task(task);
    }

    @Transactional
    public void deleteTask(String workspaceId, String taskId) {
        Task task = taskRepository.findByIdAndWorkspace_Id(taskId, workspaceId)
                .orElseThrow(() -> new NotFoundException(
                        "Task not found or does not belong to the specified workspace"));
        taskRepository.delete(task);
    }

    private Project getProjectInWorkspace(String projectId, String workspaceId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException(
                        "Project not found or does not belong to this workspace"));
        if (!project.getWorkspace().getId().equals(workspaceId)) {
            throw new NotFoundException("Project not found or does not belong to this workspace");
        }
        return project;
    }

    private User resolveAssignee(String workspaceId, String assignedTo) {
        if (assignedTo == null || assignedTo.trim().isEmpty()) {
            return null;
        }

        User user = userRepository.findById(assignedTo.trim())
                .orElseThrow(() -> new NotFoundException("Assigned user not found"));
        boolean member = memberRepository.existsByUser_IdAndWorkspace_Id(user.getId(), workspaceId);
        if (!member) {
            throw new BadRequestException("Assigned user is not a member of this workspace.");
        }
        return user;
    }

    private Specification<Task> specification(String workspaceId, TaskFilters filters) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(builder.equal(root.get("workspace").get("id"), workspaceId));

            if (filters.projectId() != null && !filters.projectId().isBlank()) {
                predicates.add(builder.equal(root.get("project").get("id"), filters.projectId()));
            }
            if (!filters.status().isEmpty()) {
                predicates.add(root.get("status").in(filters.status()));
            }
            if (!filters.priority().isEmpty()) {
                predicates.add(root.get("priority").in(filters.priority()));
            }
            if (!filters.assignedTo().isEmpty()) {
                predicates.add(root.get("assignedTo").get("id").in(filters.assignedTo()));
            }
            if (filters.keyword() != null && !filters.keyword().isBlank()) {
                predicates.add(builder.like(
                        builder.lower(root.get("title")),
                        "%" + filters.keyword().trim().toLowerCase() + "%"));
            }
            if (filters.dueDate() != null && !filters.dueDate().isBlank()) {
                predicates.add(builder.equal(root.get("dueDate"), parseInstant(filters.dueDate())));
            }

            return builder.and(predicates.toArray(Predicate[]::new));
        };
    }

    private Instant parseInstant(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Instant.parse(value.trim());
        } catch (DateTimeParseException exception) {
            throw new BadRequestException("Invalid date format. Please provide a valid date string.");
        }
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    public record TaskFilters(
            String projectId,
            List<TaskStatus> status,
            List<TaskPriority> priority,
            List<String> assignedTo,
            String keyword,
            String dueDate) {
    }

    public record PageResult(
            List<Map<String, Object>> tasks,
            long totalCount,
            int pageSize,
            int pageNumber,
            int totalPages,
            int skip) {
    }
}
