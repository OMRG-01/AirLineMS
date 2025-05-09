package com.jetset.fly.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

import com.jetset.fly.model.AirFlight;
import com.jetset.fly.model.Airline;
import com.jetset.fly.model.Class;
import com.jetset.fly.model.FlightClass;
import com.jetset.fly.repository.*;

import jakarta.transaction.Transactional;
@Service
public class FlightService {

    @Autowired
    private AirFlightRepository airFlightRepository;

    @Autowired
    private FlightClassRepository flightClassRepository;

    @Autowired
    private AirlineRepository airlineRepository;

    @Autowired
    private ClassRepository classRepository;

    @Transactional
    public void addFlightWithClasses(Long airlineId, String fnumber, int totalSeat,
                                     List<Long> classIds, Map<String, String> allParams) {

        // 1. Save the flight
        AirFlight flight = new AirFlight();
        flight.setAirline(airlineRepository.findById(airlineId).orElseThrow());
        flight.setFnumber(fnumber);
        flight.setTotalSeat(totalSeat);
        airFlightRepository.save(flight); // Save first to get flight ID

        // 2. For each class selected, create a FlightClass entry
        for (Long classId : classIds) {
            String seatParamKey = "seat_" + classId;
            if (allParams.containsKey(seatParamKey)) {
                int seat = Integer.parseInt(allParams.get(seatParamKey));

                FlightClass fc = new FlightClass();
                fc.setFlight(flight);
                fc.setFlightClass(classRepository.findById(classId).orElseThrow());
                fc.setSeat(seat);
                flightClassRepository.save(fc);
            }
        }
    }
    public List<AirFlight> getAllActiveFlights() {
        List<AirFlight> flights = airFlightRepository.findByStatus("ACTIVE");
        for (AirFlight flight : flights) {
            // Trigger lazy loading manually
            flight.getFlightClasses().size();
        }
        return flights;
    }


    public List<Class> getAllClasses() {
        return classRepository.findAll();
    }

    public int getSeatForClass(AirFlight flight, Class flightClass) {
        return flight.getFlightClasses().stream()
            .filter(fc -> fc.getFlightClass().getId().equals(flightClass.getId()))
            .map(FlightClass::getSeat)
            .findFirst()
            .orElse(0);
    }
    
    public boolean softDeleteFlight(Long flightId) {
        Optional<AirFlight> optionalFlight = airFlightRepository.findById(flightId);
        if (optionalFlight.isPresent()) {
            AirFlight flight = optionalFlight.get();
            flight.setStatus("DELETED");
            airFlightRepository.save(flight);
            return true;
        }
        return false;
    }


}
