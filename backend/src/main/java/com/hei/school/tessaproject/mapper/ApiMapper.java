package com.hei.school.tessaproject.mapper;

import com.hei.school.tessaproject.domain.Member;
import com.hei.school.tessaproject.domain.Project;
import com.hei.school.tessaproject.domain.Role;
import com.hei.school.tessaproject.domain.Task;
import com.hei.school.tessaproject.domain.User;
import com.hei.school.tessaproject.domain.Workspace;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class ApiMapper {
    public Map<String, Object> userForLogin(User user) {
        Map<String, Object> map = userBase(user);
        put(map, "currentWorkspace", user.getCurrentWorkspace() == null ? null : user.getCurrentWorkspace().getId());
        return map;
    }

    public Map<String, Object> currentUser(User user) {
        Map<String, Object> map = userBase(user);
        put(map, "currentWorkspace", user.getCurrentWorkspace() == null ? null : workspace(user.getCurrentWorkspace()));
        return map;
    }

    public Map<String, Object> userSummary(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        put(map, "_id", user.getId());
        put(map, "name", user.getName());
        put(map, "email", user.getEmail());
        put(map, "profilePicture", user.getProfilePicture());
        return map;
    }

    public Map<String, Object> assigneeSummary(User user) {
        if (user == null) {
            return null;
        }
        Map<String, Object> map = new LinkedHashMap<>();
        put(map, "_id", user.getId());
        put(map, "name", user.getName());
        put(map, "profilePicture", user.getProfilePicture());
        return map;
    }

    public Map<String, Object> workspace(Workspace workspace) {
        Map<String, Object> map = new LinkedHashMap<>();
        put(map, "_id", workspace.getId());
        put(map, "name", workspace.getName());
        put(map, "description", workspace.getDescription());
        put(map, "owner", workspace.getOwner().getId());
        put(map, "inviteCode", workspace.getInviteCode());
        put(map, "createdAt", workspace.getCreatedAt());
        put(map, "updatedAt", workspace.getUpdatedAt());
        put(map, "__v", 0);
        return map;
    }

    public Map<String, Object> workspaceMember(Member member) {
        Map<String, Object> map = memberBase(member);
        put(map, "userId", member.getUser().getId());
        put(map, "role", role(member.getRole()));
        return map;
    }

    public Map<String, Object> memberWithUser(Member member) {
        Map<String, Object> map = memberBase(member);
        put(map, "userId", userSummary(member.getUser()));
        put(map, "role", roleSummary(member.getRole()));
        return map;
    }

    public Map<String, Object> role(Role role) {
        Map<String, Object> map = roleSummary(role);
        put(map, "permissions", role.getPermissions().stream().map(Enum::name).toList());
        put(map, "createdAt", role.getCreatedAt());
        put(map, "updatedAt", role.getUpdatedAt());
        put(map, "__v", 0);
        return map;
    }

    public Map<String, Object> roleSummary(Role role) {
        Map<String, Object> map = new LinkedHashMap<>();
        put(map, "_id", role.getId());
        put(map, "name", role.getName().name());
        return map;
    }

    public Map<String, Object> project(Project project) {
        Map<String, Object> map = new LinkedHashMap<>();
        put(map, "_id", project.getId());
        put(map, "emoji", project.getEmoji());
        put(map, "name", project.getName());
        put(map, "description", project.getDescription());
        put(map, "workspace", project.getWorkspace().getId());
        put(map, "createdBy", assigneeSummary(project.getCreatedBy()));
        put(map, "createdAt", project.getCreatedAt());
        put(map, "updatedAt", project.getUpdatedAt());
        put(map, "__v", 0);
        return map;
    }

    public Map<String, Object> projectSummary(Project project) {
        Map<String, Object> map = new LinkedHashMap<>();
        put(map, "_id", project.getId());
        put(map, "emoji", project.getEmoji());
        put(map, "name", project.getName());
        return map;
    }

    public Map<String, Object> task(Task task) {
        Map<String, Object> map = new LinkedHashMap<>();
        put(map, "_id", task.getId());
        put(map, "taskCode", task.getTaskCode());
        put(map, "title", task.getTitle());
        put(map, "description", task.getDescription());
        put(map, "project", projectSummary(task.getProject()));
        put(map, "workspace", task.getWorkspace().getId());
        put(map, "status", task.getStatus().name());
        put(map, "priority", task.getPriority().name());
        put(map, "assignedTo", assigneeSummary(task.getAssignedTo()));
        put(map, "createdBy", task.getCreatedBy().getId());
        put(map, "dueDate", task.getDueDate());
        put(map, "createdAt", task.getCreatedAt());
        put(map, "updatedAt", task.getUpdatedAt());
        put(map, "__v", 0);
        return map;
    }

    private Map<String, Object> userBase(User user) {
        Map<String, Object> map = new LinkedHashMap<>();
        put(map, "_id", user.getId());
        put(map, "name", user.getName());
        put(map, "email", user.getEmail());
        put(map, "profilePicture", user.getProfilePicture());
        put(map, "isActive", user.isActive());
        put(map, "lastLogin", user.getLastLogin());
        put(map, "createdAt", user.getCreatedAt());
        put(map, "updatedAt", user.getUpdatedAt());
        put(map, "__v", 0);
        return map;
    }

    private Map<String, Object> memberBase(Member member) {
        Map<String, Object> map = new LinkedHashMap<>();
        put(map, "_id", member.getId());
        put(map, "workspaceId", member.getWorkspace().getId());
        put(map, "joinedAt", member.getJoinedAt());
        put(map, "createdAt", member.getCreatedAt());
        put(map, "updatedAt", member.getUpdatedAt());
        put(map, "__v", 0);
        return map;
    }

    private void put(Map<String, Object> map, String key, Object value) {
        map.put(key, value);
    }
}
