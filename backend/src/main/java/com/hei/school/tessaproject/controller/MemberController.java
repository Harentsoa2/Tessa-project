package com.hei.school.tessaproject.controller;

import com.hei.school.tessaproject.service.MemberService;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/member")
public class MemberController {
    private final MemberService memberService;

    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    @PostMapping("/workspace/{inviteCode}/join")
    public Map<String, Object> join(@PathVariable String inviteCode, Authentication authentication) {
        MemberService.JoinResult result = memberService.joinWorkspaceByInvite(authentication.getName(), inviteCode);
        return Map.of(
                "message", "Successfully joined the workspace",
                "workspaceId", result.workspaceId(),
                "role", result.role());
    }
}
