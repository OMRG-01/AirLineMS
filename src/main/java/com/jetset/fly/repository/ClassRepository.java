package com.jetset.fly.repository;

import java.util.Optional;
import com.jetset.fly.model.Class;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByName(String name);
}
