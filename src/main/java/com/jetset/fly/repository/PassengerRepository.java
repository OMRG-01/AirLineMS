package com.jetset.fly.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jetset.fly.model.Passenger;
import com.jetset.fly.model.Role;


public interface PassengerRepository extends JpaRepository<Passenger, Long> {
	@Query("SELECT COUNT(p) FROM Passenger p WHERE p.schedule.id = :scheduleId AND p.flightClass.id = :classId AND p.status = 'ACTIVE'")
	int countByScheduleIdAndFlightClassId(@Param("scheduleId") Long scheduleId, @Param("classId") Long classId);
	
	List<Passenger> findByBookingId(Long bookingId);
}
