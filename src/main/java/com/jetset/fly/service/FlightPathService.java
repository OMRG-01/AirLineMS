package com.jetset.fly.service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jetset.fly.model.City;
import com.jetset.fly.model.FlightSchedule;
import com.jetset.fly.repository.FlightScheduleRepository;

@Service
public class FlightPathService {

    @Autowired
    private FlightScheduleRepository scheduleRepo;

    private static final Duration MIN_LAYOVER = Duration.ofHours(1);

    public Map<City, List<FlightSchedule>> buildGraph(List<FlightSchedule> schedules) {
        Map<City, List<FlightSchedule>> graph = new HashMap<>();
        for (FlightSchedule schedule : schedules) {
            graph.computeIfAbsent(schedule.getSource(), k -> new ArrayList<>()).add(schedule);
        }
        return graph;
    }

    public List<City> findPath(City source, City destination, Map<City, List<FlightSchedule>> graph) {
        Queue<City> queue = new LinkedList<>();
        Map<City, City> parentMap = new HashMap<>();
        Set<City> visited = new HashSet<>();

        queue.add(source);
        visited.add(source);

        while (!queue.isEmpty()) {
            City current = queue.poll();
            if (current.equals(destination)) break;

            for (FlightSchedule fs : graph.getOrDefault(current, List.of())) {
                City neighbor = fs.getDestination();
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    parentMap.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        List<City> path = new ArrayList<>();
        for (City at = destination; at != null; at = parentMap.get(at)) {
            path.add(0, at);
        }

        return path.size() > 1 && path.get(0).equals(source) ? path : List.of();
    }

//    public List<List<FlightSchedule>> getConnectingFlights(List<City> path) {
//        List<List<FlightSchedule>> result = new ArrayList<>();
//        LocalDateTime arrivalTime = null;
//
//        for (int i = 0; i < path.size() - 1; i++) {
//            City from = path.get(i);
//            City to = path.get(i + 1);
//
//            List<FlightSchedule> segmentFlights = scheduleRepo.findBySourceAndDestinationAndStatus(from, to, "ACTIVE");
//            List<FlightSchedule> validFlights = new ArrayList<>();
//
//            for (FlightSchedule fs : segmentFlights) {
//                if (arrivalTime == null || !fs.getDepartAt().isBefore(arrivalTime.plus(MIN_LAYOVER))) {
//                    validFlights.add(fs);
//                }
//            }
//
//            if (validFlights.isEmpty()) {
//                return List.of(); // No valid connection
//            }
//
//            // Sort valid flights and pick the first one
//            validFlights.sort(Comparator.comparing(FlightSchedule::getDepartAt));
//            FlightSchedule selected = validFlights.get(0);
//            arrivalTime = selected.getArriveAt();
//
//            result.add(List.of(selected));
//        }
//
//        return result;
//    }
    public List<List<FlightSchedule>> getConnectingFlights(List<City> path, LocalDate selectedDate) {
        List<List<FlightSchedule>> allSegments = new ArrayList<>();
        LocalDateTime arrivalTime = null;

        for (int i = 0; i < path.size() - 1; i++) {
            City from = path.get(i);
            City to = path.get(i + 1);

            List<FlightSchedule> segmentFlights;
            if (i == 0) {
                LocalDateTime start = selectedDate.atStartOfDay();
                LocalDateTime end = selectedDate.plusDays(1).atStartOfDay();
                segmentFlights = scheduleRepo.findBySourceAndDestinationAndStatusAndDepartAtBetween(from, to, "ACTIVE", start, end);
            } else {
                segmentFlights = scheduleRepo.findBySourceAndDestinationAndStatus(from, to, "ACTIVE");
            }

            List<FlightSchedule> validFlights = new ArrayList<>();
            for (FlightSchedule fs : segmentFlights) {
                if (i == 0 || (arrivalTime != null && fs.getDepartAt().isAfter(arrivalTime.plus(MIN_LAYOVER)))) {
                    validFlights.add(fs);
                }
            }

            if (validFlights.isEmpty()) {
                return List.of(); // No valid options for this segment
            }

            // Sort for better UI grouping
            validFlights.sort(Comparator.comparing(FlightSchedule::getDepartAt));

            // Update arrivalTime to EARLIEST of all flights (not just 0)
            arrivalTime = validFlights.get(0).getArriveAt();

            allSegments.add(validFlights);
        }

        return allSegments;
    }
    
    public List<FlightSchedule> getDirectFlights(String sourceCity, String destinationCity, LocalDate date) {
        return scheduleRepo.findDirectFlights(sourceCity, destinationCity, date);
    }




}
