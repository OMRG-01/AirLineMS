package com.jetset.fly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jetset.fly.model.FlightClass;

public interface FlightClassRepository extends JpaRepository<FlightClass, Long> {
    List<FlightClass> findByFlightId(Long flightId);
}
