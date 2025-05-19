package com.jetset.fly.repository;

import com.jetset.fly.model.FlightClass;
import com.jetset.fly.model.FlightSchedule;

import com.jetset.fly.model.Class;
import com.jetset.fly.model.FlightScheduleRate;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FlightScheduleRateRepository extends JpaRepository<FlightScheduleRate, Long> {
	
	 List<FlightScheduleRate> findByScheduleId(Long scheduleId);
	 
	 
	 Optional<FlightScheduleRate> findByScheduleAndFlightClass(FlightSchedule schedule, Class flightClass);

	 @Transactional
	    @Modifying
	    @Query("DELETE FROM FlightScheduleRate r WHERE r.schedule.id = :scheduleId")
	    void deleteByScheduleId(@Param("scheduleId") Long scheduleId);
	 List<FlightScheduleRate> findBySchedule(FlightSchedule schedule);
	 


}
