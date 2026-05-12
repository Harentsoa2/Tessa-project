package com.hei.school.tessaproject.controller;

import com.hei.school.tessaproject.domain.Permission;
import com.hei.school.tessaproject.dto.ChangeWorkspaceMemberRoleRequest;
import com.hei.school.tessaproject.dto.WorkspaceRequest;
import com.hei.school.tessaproject.service.MemberService;
import com.hei.school.tessaproject.service.WorkspaceService;
import jakarta.validation.Valid;
import java.util.LinkedHashMap;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workspace")
public class WorkspaceController {
    private final WorkspaceService workspaceService;
    private final MemberService memberService;

    public WorkspaceController(WorkspaceService workspaceService, MemberService memberService) {
        this.workspaceService = workspaceService;
        this.memberService = memberService;
    }

    @PostMapping("/create/new")
    public ResponseEntity<Map<String, Object>> create(
            @Valid @RequestBody WorkspaceRequest request,
            Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Workspace created successfully",
                "workspace", workspaceService.createWorkspace(authentication.getName(), request)));
    }

    @PutMapping("/update/{id}")
    public Map<String, Object> update(
            @PathVariable String id,
            @Valid @RequestBody WorkspaceRequest request,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), id, Permission.EDIT_WORKSPACE);
        return Map.of(
                "message", "Workspace updated successfully",
                "workspace", workspaceService.updateWorkspace(id, request));
    }

    @PutMapping("/change/member/role/{id}")
    public Map<String, Object> changeMemberRole(
            @PathVariable String id,
            @Valid @RequestBody ChangeWorkspaceMemberRoleRequest request,
            Authentication authentication) {
        memberService.assertPermission(authentication.getName(), id, Permission.CHANGE_MEMBER_ROLE);
        return Map.of(
                "message", "Member Role changed successfully",
                "member", workspaceService.changeMemberRole(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public Map<String, Object> delete(@PathVariable String id, Authentication authentication) {
        memberService.assertPermission(authentication.getName(), id, Permission.DELETE_WORKSPACE);
        String currentWorkspace = workspaceService.deleteWorkspace(id, authentication.getName());
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Workspace deleted successfully");
        response.put("currentWorkspace", currentWorkspace);
        return response;
    }

    @GetMapping("/all")
    public Map<String, Object> all(Authentication authentication) {
        return Map.of(
                "message", "User workspaces fetched successfully",
                "workspaces", workspaceService.getAllWorkspacesUserIsMember(authentication.getName()));
    }

    @GetMapping("/members/{id}")
    public Map<String, Object> members(@PathVariable String id, Authentication authentication) {
        memberService.assertPermission(authentication.getName(), id, Permission.VIEW_ONLY);
        WorkspaceService.MembersAndRoles result = workspaceService.getWorkspaceMembers(id);
        return Map.of(
                "message", "Workspace members retrieved successfully",
                "members", result.members(),
                "roles", result.roles());
    }

    @GetMapping("/analytics/{id}")
    public Map<String, Object> analytics(@PathVariable String id, Authentication authentication) {
        memberService.assertPermission(authentication.getName(), id, Permission.VIEW_ONLY);
        return Map.of(
                "message", "Workspace analytics retrieved successfully",
                "analytics", workspaceService.getWorkspaceAnalytics(id));
    }

    @GetMapping("/{id}")
    public Map<String, Object> get(@PathVariable String id, Authentication authentication) {
        memberService.getMemberRoleInWorkspace(authentication.getName(), id);
        return Map.of(
                "message", "Workspace fetched successfully",
                "workspace", workspaceService.getWorkspaceById(id));
    }
}
