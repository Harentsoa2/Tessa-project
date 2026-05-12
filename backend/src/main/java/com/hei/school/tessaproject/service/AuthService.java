package com.hei.school.tessaproject.service;

import com.hei.school.tessaproject.config.SessionKeys;
import com.hei.school.tessaproject.domain.Account;
import com.hei.school.tessaproject.domain.Member;
import com.hei.school.tessaproject.domain.Provider;
import com.hei.school.tessaproject.domain.Role;
import com.hei.school.tessaproject.domain.RoleName;
import com.hei.school.tessaproject.domain.User;
import com.hei.school.tessaproject.domain.Workspace;
import com.hei.school.tessaproject.dto.LoginRequest;
import com.hei.school.tessaproject.dto.RegisterRequest;
import com.hei.school.tessaproject.exception.BadRequestException;
import com.hei.school.tessaproject.exception.ErrorCode;
import com.hei.school.tessaproject.exception.NotFoundException;
import com.hei.school.tessaproject.exception.UnauthorizedException;
import com.hei.school.tessaproject.mapper.ApiMapper;
import com.hei.school.tessaproject.repository.AccountRepository;
import com.hei.school.tessaproject.repository.MemberRepository;
import com.hei.school.tessaproject.repository.RoleRepository;
import com.hei.school.tessaproject.repository.UserRepository;
import com.hei.school.tessaproject.repository.WorkspaceRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.time.Instant;
import java.util.Map;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final WorkspaceRepository workspaceRepository;
    private final RoleRepository roleRepository;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ApiMapper mapper;

    public AuthService(
            UserRepository userRepository,
            AccountRepository accountRepository,
            WorkspaceRepository workspaceRepository,
            RoleRepository roleRepository,
            MemberRepository memberRepository,
            BCryptPasswordEncoder passwordEncoder,
            ApiMapper mapper) {
        this.userRepository = userRepository;
        this.accountRepository = accountRepository;
        this.workspaceRepository = workspaceRepository;
        this.roleRepository = roleRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Transactional
    public void register(RegisterRequest request) {
        String email = normalizeEmail(request.email());
        if (userRepository.existsByEmailIgnoreCase(email)) {
            throw new BadRequestException("Email already exists", ErrorCode.AUTH_EMAIL_ALREADY_EXISTS);
        }

        User user = new User();
        user.setName(request.name().trim());
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(request.password()));
        user = userRepository.save(user);

        Account account = new Account();
        account.setProvider(Provider.EMAIL);
        account.setProviderId(email);
        account.setUser(user);
        accountRepository.save(account);

        Workspace workspace = new Workspace();
        workspace.setName("My Workspace");
        workspace.setDescription("Workspace created for " + user.getName());
        workspace.setOwner(user);
        workspace = workspaceRepository.save(workspace);

        Role ownerRole = roleRepository.findByName(RoleName.OWNER)
                .orElseThrow(() -> new NotFoundException("Owner role not found"));

        Member member = new Member();
        member.setUser(user);
        member.setWorkspace(workspace);
        member.setRole(ownerRole);
        member.setJoinedAt(Instant.now());
        memberRepository.save(member);

        user.setCurrentWorkspace(workspace);
        userRepository.save(user);
    }

    @Transactional
    public Map<String, Object> login(LoginRequest request, HttpServletRequest servletRequest) {
        String email = normalizeEmail(request.email());
        Account account = accountRepository.findByProviderAndProviderIdIgnoreCase(Provider.EMAIL, email)
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        User user = account.getUser();
        if (user == null || user.getPassword() == null || !passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        user.setLastLogin(Instant.now());
        userRepository.save(user);

        HttpSession session = servletRequest.getSession(true);
        session.setAttribute(SessionKeys.CURRENT_USER_ID, user.getId());

        return mapper.userForLogin(user);
    }

    public void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
