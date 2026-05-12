package com.hei.school.tessaproject.repository;

import com.hei.school.tessaproject.domain.Role;
import com.hei.school.tessaproject.domain.RoleName;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
    Optional<Role> findByName(RoleName name);
}
