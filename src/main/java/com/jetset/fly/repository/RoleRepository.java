package com.jetset.fly.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jetset.fly.model.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
