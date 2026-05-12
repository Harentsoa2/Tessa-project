package com.hei.school.tessaproject.controller;

import com.hei.school.tessaproject.domain.Permission;
import com.hei.school.tessaproject.domain.TaskPriority;
import com.hei.school.tessaproject.domain.TaskStatus;
import com.hei.school.tessaproject.dto.TaskRequest;
import com.hei.school.tessaproject.service.MemberService;
import com.hei.school.tessaproject.service.TaskService;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/task")
public class TaskController {
    private final TaskService taskService;
    private final MemberService memberService;

    public TaskController(TaskService taskService, MemberService memberService) {
        this.taskService = taskService;
        this.memberService = memberService;
    }

    @PostMapping("/project/{projectId}/workspace/{workspaceId}/create")
    public Map<String, Object> create(
            @PathVariable String projectId,
            @PathVariable String workspaceId,
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), workspaceId, Permission.CREATE_TASK);
        return Map.of(
                "message", "Task created successfully",
                "task", taskService.createTask(workspaceId, projectId, authentication.getName(), request));
    }

    @PutMapping({
            "/{id}/project/{projectId}/workspace/{workspaceId}/update",
            "/{id}/project/{projectId}/workspace/{workspaceId}/update/"
    })
    public Map<String, Object> update(
            @PathVariable String id,
            @PathVariable String projectId,
            @PathVariable String workspaceId,
            @Valid @RequestBody TaskRequest request,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), workspaceId, Permission.EDIT_TASK);
        return Map.of(
                "message", "Task updated successfully",
                "task", taskService.updateTask(workspaceId, projectId, id, request));
    }

    @GetMapping("/workspace/{workspaceId}/all")
    public Map<String, Object> all(
            @PathVariable String workspaceId,
            @RequestParam(required = false) String projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String assignedTo,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String dueDate,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), workspaceId, Permission.VIEW_ONLY);
        TaskService.TaskFilters filters = new TaskService.TaskFilters(
                projectId,
                parseEnumList(status, TaskStatus.class),
                parseEnumList(priority, TaskPriority.class),
                splitList(assignedTo),
                keyword,
                dueDate);
        TaskService.PageResult result = taskService.getAllTasks(workspaceId, filters, pageSize, pageNumber);
        return Map.of(
                "message", "All tasks fetched successfully",
                "tasks", result.tasks(),
                "pagination", Map.of(
                        "pageSize", result.pageSize(),
                        "pageNumber", result.pageNumber(),
                        "totalCount", result.totalCount(),
                        "totalPages", result.totalPages(),
                        "skip", result.skip()));
    }

    @GetMapping("/{id}/project/{projectId}/workspace/{workspaceId}")
    public Map<String, Object> get(
            @PathVariable String id,
            @PathVariable String projectId,
            @PathVariable String workspaceId,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), workspaceId, Permission.VIEW_ONLY);
        return Map.of(
                "message", "Task fetched successfully",
                "task", taskService.getTaskById(workspaceId, projectId, id));
    }

    @DeleteMapping("/{id}/workspace/{workspaceId}/delete")
    public Map<String, Object> delete(
            @PathVariable String id,
            @PathVariable String workspaceId,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), workspaceId, Permission.DELETE_TASK);
        taskService.deleteTask(workspaceId, id);
        return Map.of("message", "Task deleted successfully");
    }

    private <T extends Enum<T>> List<T> parseEnumList(String value, Class<T> enumType) {
        return splitList(value).stream()
                .map(enumValue -> Enum.valueOf(enumType, enumValue))
                .toList();
    }

    private List<String> splitList(String value) {
        if (value == null || value.isBlank()) {
            return List.of();
        }
        return Arrays.stream(value.split(","))
                .map(String::trim)
                .filter(item -> !item.isBlank())
                .toList();
    }
}
