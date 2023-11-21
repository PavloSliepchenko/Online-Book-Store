package com.example.onlinebookstore.repository;

import com.example.onlinebookstore.model.Role;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Set<Role> findAllByName(Role.RoleName name);
}
