package com.hei.school.tessaproject.config;

import com.hei.school.tessaproject.domain.Permission;
import com.hei.school.tessaproject.domain.RoleName;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class RolePermissionCatalog {
    private static final Map<RoleName, Set<Permission>> PERMISSIONS = Map.of(
            RoleName.OWNER, ordered(
                    Permission.CREATE_WORKSPACE,
                    Permission.EDIT_WORKSPACE,
                    Permission.DELETE_WORKSPACE,
                    Permission.MANAGE_WORKSPACE_SETTINGS,
                    Permission.ADD_MEMBER,
                    Permission.CHANGE_MEMBER_ROLE,
                    Permission.REMOVE_MEMBER,
                    Permission.CREATE_PROJECT,
                    Permission.EDIT_PROJECT,
                    Permission.DELETE_PROJECT,
                    Permission.CREATE_TASK,
                    Permission.EDIT_TASK,
                    Permission.DELETE_TASK,
                    Permission.VIEW_ONLY),
            RoleName.ADMIN, ordered(
                    Permission.ADD_MEMBER,
                    Permission.CREATE_PROJECT,
                    Permission.EDIT_PROJECT,
                    Permission.DELETE_PROJECT,
                    Permission.CREATE_TASK,
                    Permission.EDIT_TASK,
                    Permission.DELETE_TASK,
                    Permission.MANAGE_WORKSPACE_SETTINGS,
                    Permission.VIEW_ONLY),
            RoleName.MEMBER, ordered(
                    Permission.VIEW_ONLY,
                    Permission.CREATE_TASK,
                    Permission.EDIT_TASK));

    private RolePermissionCatalog() {
    }

    public static Set<Permission> permissions(RoleName roleName) {
        return PERMISSIONS.getOrDefault(roleName, Set.of());
    }

    private static LinkedHashSet<Permission> ordered(Permission... permissions) {
        return new LinkedHashSet<>(java.util.Arrays.asList(permissions));
    }
}
