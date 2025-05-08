package com.jetset.fly.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jetset.fly.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
