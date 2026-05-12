package com.hei.school.tessaproject.service;

import com.hei.school.tessaproject.domain.Member;
import com.hei.school.tessaproject.domain.Permission;
import com.hei.school.tessaproject.domain.Role;
import com.hei.school.tessaproject.domain.RoleName;
import com.hei.school.tessaproject.domain.User;
import com.hei.school.tessaproject.domain.Workspace;
import com.hei.school.tessaproject.exception.BadRequestException;
import com.hei.school.tessaproject.exception.ErrorCode;
import com.hei.school.tessaproject.exception.NotFoundException;
import com.hei.school.tessaproject.exception.UnauthorizedException;
import com.hei.school.tessaproject.repository.MemberRepository;
import com.hei.school.tessaproject.repository.RoleRepository;
import com.hei.school.tessaproject.repository.UserRepository;
import com.hei.school.tessaproject.repository.WorkspaceRepository;
import java.time.Instant;
import java.util.Arrays;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final WorkspaceRepository workspaceRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public MemberService(
            MemberRepository memberRepository,
            WorkspaceRepository workspaceRepository,
            RoleRepository roleRepository,
            UserRepository userRepository) {
        this.memberRepository = memberRepository;
        this.workspaceRepository = workspaceRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Role getMemberRoleInWorkspace(String userId, String workspaceId) {
        if (!workspaceRepository.existsById(workspaceId)) {
            throw new NotFoundException("Workspace not found");
        }

        Member member = memberRepository.findByUser_IdAndWorkspace_Id(userId, workspaceId)
                .orElseThrow(() -> new UnauthorizedException(
                        "You are not a member of this workspace",
                        ErrorCode.ACCESS_UNAUTHORIZED));

        return member.getRole();
    }

    @Transactional(readOnly = true)
    public void assertPermission(String userId, String workspaceId, Permission... permissions) {
        Role role = getMemberRoleInWorkspace(userId, workspaceId);
        boolean hasPermission = Arrays.stream(permissions).allMatch(role.getPermissions()::contains);
        if (!hasPermission) {
            throw new UnauthorizedException("You do not have the necessary permissions to perform this action");
        }
    }

    @Transactional
    public JoinResult joinWorkspaceByInvite(String userId, String inviteCode) {
        Workspace workspace = workspaceRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new NotFoundException("Invalid invite code or workspace not found"));

        if (memberRepository.existsByUser_IdAndWorkspace_Id(userId, workspace.getId())) {
            throw new BadRequestException("You are already a member of this workspace");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
        Role role = roleRepository.findByName(RoleName.MEMBER)
                .orElseThrow(() -> new NotFoundException("Role not found"));

        Member member = new Member();
        member.setUser(user);
        member.setWorkspace(workspace);
        member.setRole(role);
        member.setJoinedAt(Instant.now());
        memberRepository.save(member);

        return new JoinResult(workspace.getId(), role.getName().name());
    }

    public record JoinResult(String workspaceId, String role) {
    }
}
