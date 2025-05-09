package com.jetset.fly.controller;

import com.jetset.fly.dto.ScheduleViewDTO;
import com.jetset.fly.model.*;
import com.jetset.fly.model.Class;
import com.jetset.fly.repository.*;
import com.jetset.fly.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/flights")
public class AdminFlightScheduleController {

    @Autowired
    private AirFlightService airFlightService;

    @Autowired
    private AirlineService airlineService;

    @Autowired
    private ClassService classService;

    @Autowired
    private FlightClassService flightClassService;

    @Autowired
    private CityService cityService;

    
    @GetMapping("/schedule/{flightId}")
    public String showAddSchedulePage(@PathVariable Long flightId, Model model) {
        AirFlight flight = airFlightService.findById(flightId);
        List<City> cities = cityService.getAllActiveCities();
        List<FlightClass> flightClasses = flightClassService.getByFlightId(flightId);
        List<Class> allClasses = classService.getAllClasses();

        Map<Long, Integer> seatMap = new HashMap<>();
        for (FlightClass fc : flightClasses) {
            seatMap.put(fc.getFlightClass().getId(), fc.getSeat());
        }

        model.addAttribute("flight", flight);
        model.addAttribute("airline", flight.getAirline());
        model.addAttribute("flightClasses", flightClasses);
        model.addAttribute("allClasses", allClasses);
        model.addAttribute("cities", cities);
        model.addAttribute("seatMap", seatMap);

        return "admin/addSchedule";
    }

    
    @Autowired
    private FlightScheduleService flightScheduleService;
    
    @Autowired
    private FlightScheduleRateService flightScheduleRateService;
    
    @PostMapping("/schedule/save")
    public String saveSchedule(@RequestParam("flightId") Long flightId,
                               @RequestParam("sourceId") Long sourceId,
                               @RequestParam("destinationId") Long destinationId,
                               @RequestParam("departAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departAt,
                               @RequestParam("arriveAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arriveAt,
                               @RequestParam Map<String, String> params) {

        AirFlight flight = airFlightService.findById(flightId);

        // Create FlightSchedule
        FlightSchedule schedule = new FlightSchedule();
        schedule.setFlight(flight);
        schedule.setFlightNumber(flight.getFnumber());
        schedule.setAirlineId(flight.getAirline().getId());
        schedule.setSource(new City(sourceId));
        schedule.setDestination(new City(destinationId));
        schedule.setDepartAt(departAt);
        schedule.setArriveAt(arriveAt);

        FlightSchedule savedSchedule = flightScheduleService.save(schedule);

        // Save FlightScheduleRate for each class
        for (String key : params.keySet()) {
            if (key.startsWith("rate_")) {
                Long classId = Long.parseLong(key.replace("rate_", ""));
                Double rate = Double.parseDouble(params.get(key));

                FlightScheduleRate fsr = new FlightScheduleRate();
                fsr.setFlight(flight);
                fsr.setSchedule(savedSchedule);
                fsr.setFlightClass(new Class(classId));
                fsr.setRate(rate);

                flightScheduleRateService.save(fsr);
            }
        }

        return "redirect:/admin/flights/view";
    }
    
    @GetMapping("/schedule/manual")
    public String showManualSchedulePage(Model model) {
        model.addAttribute("airlines", airlineService.getAllAirlines());
        model.addAttribute("cities", cityService.getAllActiveCities());
        return "admin/manualSchedule";
    }
    
    @PostMapping("/schedule/saveManual")
    public String saveManualSchedule(@RequestParam("flightId") Long flightId,
                                     @RequestParam("sourceId") Long sourceId,
                                     @RequestParam("destinationId") Long destinationId,
                                     @RequestParam("departAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departAt,
                                     @RequestParam("arriveAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arriveAt,
                                     @RequestParam Map<String, String> params) {

        AirFlight flight = airFlightService.findById(flightId);

        FlightSchedule schedule = new FlightSchedule();
        schedule.setFlight(flight);
        schedule.setFlightNumber(flight.getFnumber());
        schedule.setAirlineId(flight.getAirline().getId());
        schedule.setSource(new City(sourceId));
        schedule.setDestination(new City(destinationId));
        schedule.setDepartAt(departAt);
        schedule.setArriveAt(arriveAt);

        FlightSchedule savedSchedule = flightScheduleService.save(schedule);

        for (String key : params.keySet()) {
            if (key.startsWith("rate_")) {
                Long classId = Long.parseLong(key.replace("rate_", ""));
                Double rate = Double.parseDouble(params.get(key));

                FlightScheduleRate fsr = new FlightScheduleRate();
                fsr.setFlight(flight);
                fsr.setSchedule(savedSchedule);
                fsr.setFlightClass(new Class(classId));
                fsr.setRate(rate);

                flightScheduleRateService.save(fsr);
            }
        }

        return "redirect:/admin/flights/view";
    }
    
    @GetMapping("/view-schedules")
    public String viewSchedules(Model model) {
        List<FlightSchedule> schedules = flightScheduleService.getActiveAirlines();

        List<Class> classList = classService.getAllClasses();
        Map<Long, String> classMap = classList.stream()
                .collect(Collectors.toMap(Class::getId, Class::getName));

        List<Map<String, Object>> scheduleViewData = new ArrayList<>();

        for (int i = 0; i < schedules.size(); i++) {
            FlightSchedule schedule = schedules.get(i);
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("srNo", i + 1);

            // Airline name
            Airline airline = airlineService.getById(schedule.getAirlineId());
            row.put("airlineName", airline != null ? airline.getAname() : "Unknown");

            // Other flight details
            row.put("flightNumber", schedule.getFlightNumber());
            row.put("source", schedule.getSource().getCityname());
            row.put("destination", schedule.getDestination().getCityname());
            row.put("departAt", schedule.getDepartAt());
            row.put("arriveAt", schedule.getArriveAt());
            row.put("scheduleId", schedule.getId());


            // Class cost map
            Map<Long, Double> classCosts = new HashMap<>();
            List<FlightScheduleRate> rates = flightScheduleRateService.getBySchedule(schedule.getId());

            for (FlightScheduleRate rate : rates) {
                Long classId = rate.getFlightClass().getId();
                classCosts.put(classId, rate.getRate());
            }


            row.put("classCosts", classCosts);
            scheduleViewData.add(row);
        }

        model.addAttribute("columns", classList); // instead of just names
        model.addAttribute("schedules", scheduleViewData);

        return "admin/viewSchedule"; // Thymeleaf file
    }
    
    @PostMapping("/delete-schedule/{id}")
    @ResponseBody
    public ResponseEntity<String> deleteSchedule(@PathVariable Long id) {
        FlightSchedule schedule = flightScheduleService.findById(id);
        if (schedule != null) {
            schedule.setStatus("DELETED");
            flightScheduleService.save(schedule);
            return ResponseEntity.ok("Deleted");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Not found");
        }
    }



}
