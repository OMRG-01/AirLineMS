package com.jetset.fly.repository;

import com.jetset.fly.model.FlightClass;
import com.jetset.fly.model.FlightSchedule;
import com.jetset.fly.model.FlightScheduleRate;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FlightScheduleRateRepository extends JpaRepository<FlightScheduleRate, Long> {
	
	 List<FlightScheduleRate> findByScheduleId(Long scheduleId);
	 
	 
	 Optional<FlightScheduleRate> findByScheduleAndFlightClass(FlightSchedule schedule, FlightClass flightClass);

}
