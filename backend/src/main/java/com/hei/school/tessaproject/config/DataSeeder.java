package com.hei.school.tessaproject.config;

import com.hei.school.tessaproject.domain.Role;
import com.hei.school.tessaproject.domain.RoleName;
import com.hei.school.tessaproject.repository.RoleRepository;
import java.util.LinkedHashSet;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DataSeeder implements ApplicationRunner {
    private final RoleRepository roleRepository;

    public DataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        for (RoleName roleName : RoleName.values()) {
            Role role = roleRepository.findByName(roleName).orElseGet(Role::new);
            role.setName(roleName);
            role.setPermissions(new LinkedHashSet<>(RolePermissionCatalog.permissions(roleName)));
            roleRepository.save(role);
        }
    }
}
