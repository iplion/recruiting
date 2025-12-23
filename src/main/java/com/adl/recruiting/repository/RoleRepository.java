package com.adl.recruiting.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.adl.recruiting.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
