package com.jetset.fly.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.*;

import com.jetset.fly.model.AirFlight;
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
}
