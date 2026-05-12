package com.hei.school.tessaproject.domain;

import java.util.UUID;

public final class InviteCodeGenerator {
    private InviteCodeGenerator() {
    }

    public static String newInviteCode() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public static String newTaskCode() {
        return "task-" + UUID.randomUUID().toString().replace("-", "").substring(0, 3);
    }
}
