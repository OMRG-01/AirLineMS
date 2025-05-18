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

        // 🔐 Check for duplicate flight number
        if (airFlightRepository.existsByFnumber(fnumber)) {
            throw new RuntimeException("Flight number '" + fnumber + "' already exists.");
        }

        // ✅ 1. Get the Airline
        Airline airline = airlineRepository.findById(airlineId)
                .orElseThrow(() -> new RuntimeException("Airline not found"));

        // ✅ 2. Create and Save Flight
        AirFlight flight = new AirFlight();
        flight.setAirline(airline);
        flight.setFnumber(fnumber);
        flight.setTotalSeat(totalSeat);
        airFlightRepository.save(flight); // Save to get ID

        // ✅ 3. Create FlightClass records
        for (Long classId : classIds) {
            String seatParamKey = "seat_" + classId;
            if (allParams.containsKey(seatParamKey)) {
                String seatValue = allParams.get(seatParamKey);
                if (seatValue != null && !seatValue.trim().isEmpty()) {
                    int seat = Integer.parseInt(seatValue);

                    FlightClass fc = new FlightClass();
                    fc.setFlight(flight);
                    fc.setFlightClass(classRepository.findById(classId).orElseThrow());
                    fc.setSeat(seat);
                    flightClassRepository.save(fc);
                }
            }
        }

        // ✅ 4. Update airline's flight count
        airline.setNoOfFlight(airline.getNoOfFlight() + 1);
        airlineRepository.save(airline);
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
        return classRepository.findByStatus("ACTIVE");
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
