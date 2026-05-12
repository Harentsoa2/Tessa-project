package com.hei.school.tessaproject.controller;

import com.hei.school.tessaproject.domain.Permission;
import com.hei.school.tessaproject.dto.ProjectRequest;
import com.hei.school.tessaproject.service.MemberService;
import com.hei.school.tessaproject.service.ProjectService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/project")
public class ProjectController {
    private final ProjectService projectService;
    private final MemberService memberService;

    public ProjectController(ProjectService projectService, MemberService memberService) {
        this.projectService = projectService;
        this.memberService = memberService;
    }

    @PostMapping("/workspace/{workspaceId}/create")
    public ResponseEntity<Map<String, Object>> create(
            @PathVariable String workspaceId,
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), workspaceId, Permission.CREATE_PROJECT);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Project created successfully",
                "project", projectService.createProject(authentication.getName(), workspaceId, request)));
    }

    @PutMapping("/{id}/workspace/{workspaceId}/update")
    public Map<String, Object> update(
            @PathVariable String id,
            @PathVariable String workspaceId,
            @Valid @RequestBody ProjectRequest request,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), workspaceId, Permission.EDIT_PROJECT);
        return Map.of(
                "message", "Project updated successfully",
                "project", projectService.updateProject(workspaceId, id, request));
    }

    @DeleteMapping("/{id}/workspace/{workspaceId}/delete")
    public Map<String, Object> delete(
            @PathVariable String id,
            @PathVariable String workspaceId,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), workspaceId, Permission.DELETE_PROJECT);
        projectService.deleteProject(workspaceId, id);
        return Map.of("message", "Project deleted successfully");
    }

    @GetMapping("/workspace/{workspaceId}/all")
    public Map<String, Object> all(
            @PathVariable String workspaceId,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(defaultValue = "1") int pageNumber,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), workspaceId, Permission.VIEW_ONLY);
        ProjectService.PageResult result = projectService.getProjectsInWorkspace(workspaceId, pageSize, pageNumber);
        return Map.of(
                "message", "Project fetched successfully",
                "projects", result.projects(),
                "pagination", Map.of(
                        "totalCount", result.totalCount(),
                        "pageSize", result.pageSize(),
                        "pageNumber", result.pageNumber(),
                        "totalPages", result.totalPages(),
                        "skip", result.skip(),
                        "limit", result.pageSize()));
    }

    @GetMapping("/{id}/workspace/{workspaceId}/analytics")
    public Map<String, Object> analytics(
            @PathVariable String id,
            @PathVariable String workspaceId,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), workspaceId, Permission.VIEW_ONLY);
        return Map.of(
                "message", "Project analytics retrieved successfully",
                "analytics", projectService.getProjectAnalytics(workspaceId, id));
    }

    @GetMapping("/{id}/workspace/{workspaceId}")
    public Map<String, Object> get(
            @PathVariable String id,
            @PathVariable String workspaceId,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), workspaceId, Permission.VIEW_ONLY);
        return Map.of(
                "message", "Project fetched successfully",
                "project", projectService.getProjectByIdAndWorkspaceId(workspaceId, id));
    }
}
