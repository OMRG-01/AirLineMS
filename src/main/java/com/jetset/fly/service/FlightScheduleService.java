package com.jetset.fly.service;

import com.jetset.fly.model.AirFlight;
import com.jetset.fly.dto.FlightWithClassDetailsDTO;
import com.jetset.fly.model.*;
import com.jetset.fly.model.Class;
import com.jetset.fly.model.FlightSchedule;
import com.jetset.fly.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlightScheduleService {

    @Autowired
    private FlightScheduleRepository scheduleRepository;

    @Autowired
    private FlightScheduleRateRepository flightScheduleRateRepository;

    @Autowired
    private AirFlightRepository airFlightRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private FlightClassRepository flightClassRepository;

    public FlightSchedule save(FlightSchedule schedule) {
        return scheduleRepository.save(schedule);
    }

    public List<FlightSchedule> getActiveAirlines() {
        return scheduleRepository.findByStatus("ACTIVE");
    }

    public FlightSchedule findById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    public void delete(Long id) {
        scheduleRepository.deleteById(id);
    }
    
    public List<FlightSchedule> getAll() {
        return scheduleRepository.findAll();
    }
    
    public List<FlightSchedule> findOverlappingSchedules(Long flightId, LocalDateTime departAt, LocalDateTime arriveAt) {
        return scheduleRepository.findOverlappingSchedules(flightId, departAt, arriveAt);
    }

    public void createBulkSchedules(Long flightId,
            Long sourceId,
            Long destinationId,
            LocalTime departTime,
            LocalTime arriveTime,
            LocalDate startDate,
            LocalDate endDate,
            List<String> days,
            Map<String, String> params) {

			AirFlight flight = airFlightRepository.findById(flightId).orElseThrow();
			City source = cityRepository.findById(sourceId).orElseThrow();
			City destination = cityRepository.findById(destinationId).orElseThrow();
			
			List<FlightClass> flightClasses = flightClassRepository.findByFlightId(flightId);
			List<LocalDate> skippedDates = new ArrayList<>();
			
			for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
			DayOfWeek currentDay = date.getDayOfWeek();
			if (days.contains(currentDay.toString())) {
			LocalDateTime departAt = LocalDateTime.of(date, departTime);
			LocalDateTime arriveAt = LocalDateTime.of(date, arriveTime);
			
			 List<FlightSchedule> existing = findOverlappingSchedules(flightId, departAt, arriveAt);
		        if (!existing.isEmpty()) {
		            skippedDates.add(date);
		            continue; // skip this day
		        }
		        
			FlightSchedule schedule = new FlightSchedule();
			schedule.setSource(source);
			schedule.setDestination(destination);
			schedule.setDepartAt(departAt);
			schedule.setArriveAt(arriveAt);
			schedule.setFlight(flight);
			schedule.setFlightNumber(flight.getFnumber());
			schedule.setAirlineId(flight.getAirline().getId());
			schedule.setStatus("ACTIVE");
			
			scheduleRepository.save(schedule);
			
			for (FlightClass flightClass : flightClasses) {
			String paramKey = "price_" + flightClass.getFlightClass().getId();
			if (params.containsKey(paramKey)) {
			Double rate = Double.valueOf(params.get(paramKey));
			
			FlightScheduleRate scheduleRate = new FlightScheduleRate();
			scheduleRate.setFlight(flight);
			scheduleRate.setSchedule(schedule);
			scheduleRate.setFlightClass(flightClass.getFlightClass());
			scheduleRate.setRate(rate);
			
			flightScheduleRateRepository.save(scheduleRate);
			}
			}
			}
			if (!skippedDates.isEmpty()) {
		        String msg = "Schedules skipped for dates: " + skippedDates.stream()
		                .map(LocalDate::toString)
		                .collect(Collectors.joining(", "));
		        throw new RuntimeException(msg);
		    }
			}
}
    
    @Autowired
    private FlightService flightService;
    
//    public void updateSchedule(Long scheduleId, Long flightId, Long sourceId, Long destinationId,
//            LocalDateTime departAt, LocalDateTime arriveAt, Map<String, String> params) {
//
//FlightSchedule schedule = scheduleRepository.findById(scheduleId)
//.orElseThrow(() -> new RuntimeException("Schedule not found"));
//
//Flight flight = flightRepository.findById(flightId)
//.orElseThrow(() -> new RuntimeException("Flight not found"));
//City source = cityRepository.findById(sourceId)
//.orElseThrow(() -> new RuntimeException("Source city not found"));
//City destination = cityRepository.findById(destinationId)
//.orElseThrow(() -> new RuntimeException("Destination city not found"));
//
//schedule.setFlight(flight);
//schedule.setSource(source);
//schedule.setDestination(destination);
//schedule.setDepartAt(departAt);
//schedule.setArriveAt(arriveAt);
//
//flightScheduleRepository.save(schedule);
//
//// Update rates
//for (String key : params.keySet()) {
//if (key.startsWith("rate_")) {
//Long classId = Long.parseLong(key.substring(5));
//double rate = Double.parseDouble(params.get(key));
//
//FlightClass flightClass = flightClassRepository.findById(classId)
// .orElseThrow(() -> new RuntimeException("Flight class not found"));
//
//FlightScheduleRate rateObj = flightScheduleRateRepository
// .findByFlightSchedule_IdAndFlightClass_Id(scheduleId, classId)
// .orElse(new FlightScheduleRate(schedule, flightClass));
//
//rateObj.setCost(rate);
//flightScheduleRateRepository.save(rateObj);
//}
//}
//}
    public List<LocalDate> findAvailableDatesBetweenCities(int fromId, int toId) {
        List<java.sql.Date> sqlDates = scheduleRepository.findDistinctDepartureDates(fromId, toId);
        return sqlDates.stream()
                       .map(java.sql.Date::toLocalDate)
                       .collect(Collectors.toList());
    }



    
    public List<FlightSchedule> findFlightsForRouteAndDate(Long sourceId, Long destinationId, LocalDate date) {
        LocalDateTime start;
        LocalDateTime end = date.atTime(23, 59, 59); // End of the selected day

        if (date.equals(LocalDate.now())) {
            start = LocalDateTime.now(); // Current time to avoid past flights
        } else {
            start = date.atStartOfDay(); // 00:00 if future date
        }

        return scheduleRepository.findBySourceIdAndDestinationIdAndDepartAtBetweenAndStatus(
            sourceId, destinationId, start, end, "ACTIVE"
        );
    }

    
    public List<FlightSchedule> findFlightsStatus(Long fromId, Long toId, LocalDate date) {
        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();

        return scheduleRepository.findBySourceIdAndDestinationIdAndDepartAtBetweenAndStatus(
            fromId, toId, start, end, "ACTIVE"
        );
    }

    @Autowired
    private FlightScheduleRateRepository rateRepository;


    public List<FlightWithClassDetailsDTO> getFlightWithClassDetails(Long from, Long to, LocalDate date) {
        List<FlightSchedule> schedules = findFlightsForRouteAndDate(from, to, date);

        List<FlightWithClassDetailsDTO> dtos = new ArrayList<>();
        for (FlightSchedule schedule : schedules) {
            List<FlightScheduleRate> rates = rateRepository.findBySchedule(schedule);

            List<FlightWithClassDetailsDTO.ClassRateDetail> rateDetails = rates.stream().map(rate -> {
                FlightWithClassDetailsDTO.ClassRateDetail detail = new FlightWithClassDetailsDTO.ClassRateDetail();
                detail.setClassName(rate.getFlightClass().getName());
                detail.setRate(rate.getRate());
                detail.setAvailableSeats(0); // or calculate available
                detail.setClassId(rate.getFlightClass().getId());
                return detail;
            }).collect(Collectors.toList());

            FlightWithClassDetailsDTO dto = new FlightWithClassDetailsDTO();
            dto.setSchedule(schedule);
            dto.setClassRates(rateDetails);

            dtos.add(dto);
        }

        return dtos;
    }


    
    }
