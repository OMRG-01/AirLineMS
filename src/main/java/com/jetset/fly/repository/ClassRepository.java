package com.jetset.fly.repository;

import java.util.List;
import java.util.Optional;

import com.jetset.fly.model.City;
import com.jetset.fly.model.Class;
import com.jetset.fly.model.FlightClass;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassRepository extends JpaRepository<Class, Long> {
    Optional<Class> findByName(String name);
    
    List<Class> findByStatus(String status);
    
}
