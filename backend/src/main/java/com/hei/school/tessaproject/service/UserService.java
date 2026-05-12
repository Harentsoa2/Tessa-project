package com.hei.school.tessaproject.service;

import com.hei.school.tessaproject.domain.User;
import com.hei.school.tessaproject.exception.BadRequestException;
import com.hei.school.tessaproject.mapper.ApiMapper;
import com.hei.school.tessaproject.repository.UserRepository;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ApiMapper mapper;

    public UserService(UserRepository userRepository, ApiMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getCurrentUser(String userId) {
        User user = getRequiredUser(userId);
        return mapper.currentUser(user);
    }

    @Transactional(readOnly = true)
    public User getRequiredUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found"));
    }
}
