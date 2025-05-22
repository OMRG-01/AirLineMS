package com.jetset.fly.repository;

import com.jetset.fly.model.Airline;
import com.jetset.fly.model.City;
import com.jetset.fly.model.FlightSchedule;

import java.time.LocalDate;
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
	

	@Query("SELECT DISTINCT FUNCTION('DATE', fs.departAt) FROM FlightSchedule fs WHERE fs.source.id = :fromId AND fs.destination.id = :toId AND fs.departAt >= CURRENT_DATE")
	List<java.sql.Date> findDistinctDepartureDates(@Param("fromId") int fromId, @Param("toId") int toId);





	@Query("SELECT fs FROM FlightSchedule fs " +
		       "WHERE fs.source.cityname = :fromCity AND fs.destination.cityname = :toCity " +
		       "AND fs.departAt BETWEEN :start AND :end " +
		       "AND fs.status = 'ACTIVE'")
		List<FlightSchedule> findByCityNamesAndDate(@Param("fromCity") String fromCity,
		                                            @Param("toCity") String toCity,
		                                            @Param("start") LocalDateTime start,
		                                            @Param("end") LocalDateTime end);

	List<FlightSchedule> findBySourceIdAndDestinationIdAndDepartAtBetweenAndStatus(
		    Long sourceId, Long destinationId, LocalDateTime start, LocalDateTime end, String status
		);

	List<FlightSchedule> findBySourceAndDestinationAndStatus(City source, City destination, String status);
	
	@Query("SELECT fs FROM FlightSchedule fs WHERE fs.source = :source AND fs.destination = :destination AND fs.status = :status AND FUNCTION('DATE', fs.departAt) = :date")
	List<FlightSchedule> findDirectFlightsByDate(
	    @Param("source") City source,
	    @Param("destination") City destination,
	    @Param("status") String status,
	    @Param("date") LocalDate date
	);

	List<FlightSchedule> findBySourceAndDestinationAndStatusAndDepartAtBetween(
		    City source, City destination, String status,
		    LocalDateTime start, LocalDateTime end
		);

	@Query("SELECT fs FROM FlightSchedule fs " +
		       "WHERE fs.source.cityname = :source " +
		       "AND fs.destination.cityname = :destination " +
		       "AND DATE(fs.departAt) = :travelDate " +
		       "AND fs.status = 'ACTIVE'")
		List<FlightSchedule> findDirectFlights(@Param("source") String source,
		                                       @Param("destination") String destination,
		                                       @Param("travelDate") LocalDate travelDate);


}
