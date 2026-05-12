package com.hei.school.tessaproject.controller;

import com.hei.school.tessaproject.service.UserService;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/current")
    public Map<String, Object> current(Authentication authentication) {
        return Map.of(
                "message", "User fetch successfully",
                "user", userService.getCurrentUser(authentication.getName()));
    }
}
