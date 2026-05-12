package com.hei.school.tessaproject.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangeWorkspaceMemberRoleRequest(
        @NotBlank String memberId,
        @NotBlank String roleId) {
}
