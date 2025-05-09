package com.jetset.fly.controller;

import com.jetset.fly.dto.ScheduleViewDTO;
import com.jetset.fly.model.*;
import com.jetset.fly.model.Class;
import com.jetset.fly.repository.*;
import com.jetset.fly.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

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
    
    @GetMapping("/admin/view-schedules")
    public String viewSchedules(Model model) {
        List<Class> allClasses = classService.getAllClasses(); // For dynamic headers

        List<FlightSchedule> schedules = flightScheduleService.findAll();
        List<ScheduleViewDTO> dtos = new ArrayList<>();

        for (FlightSchedule schedule : schedules) {
            ScheduleViewDTO dto = new ScheduleViewDTO();
            dto.setId(schedule.getId());
            dto.setAirlineName(schedule.getFlight().getAirline().getAname());
            dto.setFlightNumber(schedule.getFlightNumber());
            dto.setSourceCity(schedule.getSource().getCityname());
            dto.setDestinationCity(schedule.getDestination().getCityname());
            dto.setDepartAt(schedule.getDepartAt());
            dto.setArriveAt(schedule.getArriveAt());

            Map<String, Double> rates = new HashMap<>();
            List<FlightScheduleRate> scheduleRates = flightScheduleRateService.getBySchedule(schedule.getId());
            for (FlightScheduleRate rate : scheduleRates) {
                rates.put(rate.getFlightClass().getName(), rate.getRate());
            }
            dto.setClassRates(rates);

            dtos.add(dto);
        }

        model.addAttribute("allClasses", allClasses);
        model.addAttribute("schedules", dtos);
        return "admin/viewSchedule";
    }

}
