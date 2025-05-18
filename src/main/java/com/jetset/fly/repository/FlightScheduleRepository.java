package com.jetset.fly.repository;

import com.jetset.fly.model.Airline;
import com.jetset.fly.model.FlightSchedule;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FlightScheduleRepository extends JpaRepository<FlightSchedule, Long> {
	
	List<FlightSchedule> findByStatus(String status);
	
	@Query("SELECT fs FROM FlightSchedule fs WHERE fs.flight.id = :flightId " +
		       "AND DATE(fs.departAt) = DATE(:departAt) " +
		       "AND ((fs.departAt < :arriveAt AND fs.arriveAt > :departAt))")
		List<FlightSchedule> findOverlappingSchedules(@Param("flightId") Long flightId,
		                                              @Param("departAt") LocalDateTime departAt,
		                                              @Param("arriveAt") LocalDateTime arriveAt);


}
