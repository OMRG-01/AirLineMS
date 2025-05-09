package com.jetset.fly.repository;

import com.jetset.fly.model.Airline;
import com.jetset.fly.model.FlightSchedule;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightScheduleRepository extends JpaRepository<FlightSchedule, Long> {
	
	List<FlightSchedule> findByStatus(String status);
}
