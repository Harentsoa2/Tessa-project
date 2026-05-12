package com.hei.school.tessaproject.dto;

import com.hei.school.tessaproject.domain.TaskPriority;
import com.hei.school.tessaproject.domain.TaskStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record TaskRequest(
        @NotBlank @Size(max = 255) String title,
        String description,
        @NotNull TaskPriority priority,
        @NotNull TaskStatus status,
        String assignedTo,
        String dueDate) {
}
