package com.jetset.fly.repository;

import com.jetset.fly.model.FlightScheduleRate;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightScheduleRateRepository extends JpaRepository<FlightScheduleRate, Long> {
	
	 List<FlightScheduleRate> findByScheduleId(Long scheduleId);
}
