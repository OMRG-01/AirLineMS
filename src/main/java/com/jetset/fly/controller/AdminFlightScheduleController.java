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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
                               @RequestParam Map<String, String> params,
                               RedirectAttributes redirectAttributes) {

        AirFlight flight = airFlightService.findById(flightId);

        // 🔍 Check for overlapping schedules
        List<FlightSchedule> overlappingSchedules = flightScheduleService.findOverlappingSchedules(
                flightId, departAt, arriveAt);

        if (!overlappingSchedules.isEmpty()) {
            // ❌ Add error message & redirect back to same schedule page
            redirectAttributes.addFlashAttribute("error", "Flight " + flight.getFnumber() + " is already scheduled in this time range on " + departAt.toLocalDate());
            return "redirect:/admin/flights/schedule/" + flightId;
        }

        // ✅ Save Schedule
        FlightSchedule schedule = new FlightSchedule();
        schedule.setFlight(flight);
        schedule.setFlightNumber(flight.getFnumber());
        schedule.setAirlineId(flight.getAirline().getId());
        schedule.setSource(new City(sourceId));
        schedule.setDestination(new City(destinationId));
        schedule.setDepartAt(departAt);
        schedule.setArriveAt(arriveAt);
        FlightSchedule savedSchedule = flightScheduleService.save(schedule);

        // Save Class Rates
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

        redirectAttributes.addFlashAttribute("success", "Flight " + flight.getFnumber() + " is scheduled successfully!");

        
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
            @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {

			AirFlight flight = airFlightService.findById(flightId);
			
			// 🔍 Check for overlapping schedules
			List<FlightSchedule> overlappingSchedules = flightScheduleService.findOverlappingSchedules(
			flightId, departAt, arriveAt);
			
			if (!overlappingSchedules.isEmpty()) {
			// ❌ Add error message & redirect back to same schedule page
			redirectAttributes.addFlashAttribute("error", "Flight " + flight.getFnumber() + " is already scheduled in this time range on " + departAt.toLocalDate());
			return "redirect:/admin/flights/schedule/manual";
			}
			
			// ✅ Save Schedule
			FlightSchedule schedule = new FlightSchedule();
			schedule.setFlight(flight);
			schedule.setFlightNumber(flight.getFnumber());
			schedule.setAirlineId(flight.getAirline().getId());
			schedule.setSource(new City(sourceId));
			schedule.setDestination(new City(destinationId));
			schedule.setDepartAt(departAt);
			schedule.setArriveAt(arriveAt);
			FlightSchedule savedSchedule = flightScheduleService.save(schedule);
			
			// Save Class Rates
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
			
			redirectAttributes.addFlashAttribute("success", "Flight " + flight.getFnumber() + " is scheduled successfully!");
			
			
			return "redirect:/admin/flights/view-schedules";
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
    
    @Autowired
    private AirFlightService flightService;
    
    @GetMapping("/bulk-schedule")
    public String showBulkScheduleForm(Model model) {
        List<Airline> airlines = flightService.getActiveAirlines();
        List<City> cities = cityService.getAllActiveCities(); // Make sure this method exists
        model.addAttribute("airlines", airlines);
        model.addAttribute("cities", cities);
        return "admin/addBulkSchedule";
    }


   

    // --- Step 2: Controller Method to Save Bulk Schedules ---

    

    @PostMapping("/bulk-schedule/save")
    public String saveBulkSchedule(@RequestParam Long flightId,
                                   @RequestParam Long sourceId,
                                   @RequestParam Long destinationId,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime departTime,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime arriveTime,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
                                   @RequestParam List<String> days, // e.g., ["MONDAY", "FRIDAY"]
                                   @RequestParam Map<String, String> params,
                                   RedirectAttributes redirectAttributes) {

    	try {
            flightScheduleService.createBulkSchedules(flightId, sourceId, destinationId,
                    departTime, arriveTime, startDate, endDate, days, params);
            redirectAttributes.addFlashAttribute("success", "Bulk schedule created successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/flights/view-schedules";
    }

    @GetMapping("/edit-schedule/{scheduleId}")
    public String editSchedule(@PathVariable Long scheduleId, Model model) {
        FlightSchedule schedule = flightScheduleService.findById(scheduleId);
        AirFlight flight = schedule.getFlight();
        Airline airline = airlineService.findById(schedule.getAirlineId());
        List<City> cities = cityService.getAllActiveCities();

        // All FlightClass for this flight
        List<FlightClass> allClasses = flightClassService.findByFlightAndFlightStatus(flight.getId(), "ACTIVE");

        // Map<ClassId, SeatCount>
        Map<Long, Integer> seatMap = allClasses.stream()
            .collect(Collectors.toMap(fc -> fc.getFlightClass().getId(), FlightClass::getSeat));

        // All FlightScheduleRate for this schedule
        List<FlightScheduleRate> rates = flightScheduleRateService.getBySchedule(scheduleId);

        // Map<ClassId, Rate>
        Map<Long, Double> rateMap = rates.stream()
            .collect(Collectors.toMap(rate -> rate.getFlightClass().getId(), FlightScheduleRate::getRate));
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        String departAtFormatted = schedule.getDepartAt().format(formatter);
        String arriveAtFormatted = schedule.getArriveAt().format(formatter);
        
        model.addAttribute("schedule", schedule);
        model.addAttribute("flight", flight);
        model.addAttribute("airline", airline);
        model.addAttribute("cities", cities);
        model.addAttribute("allClasses", allClasses);
        model.addAttribute("seatMap", seatMap);
        model.addAttribute("rateMap", rateMap);
        model.addAttribute("departAtFormatted", departAtFormatted);
        model.addAttribute("arriveAtFormatted", arriveAtFormatted);

        return "admin/editSchedule";
    }


    @PostMapping("/schedule/update")
    public String updateSchedule(
            @RequestParam("scheduleId") Long scheduleId,
            @RequestParam("flightId") Long flightId,
            @RequestParam("sourceId") Long sourceCityId,
            @RequestParam("destinationId") Long destinationCityId,
            @RequestParam("departAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departAt,
            @RequestParam("arriveAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arriveAt,
            @RequestParam Map<String, String> allRequestParams, // for dynamic cost inputs like rate_1, rate_2 etc.
            RedirectAttributes redirectAttributes) {

        try {
            FlightSchedule schedule = flightScheduleService.findById(scheduleId);
            if (schedule == null) {
                redirectAttributes.addFlashAttribute("error", "Schedule not found");
                return "redirect:/admin/flights/schedule/edit/" + scheduleId;
            }

            // Update schedule fields
            schedule.setFlight(flightService.findById(flightId));
            schedule.setSource(cityService.findById(sourceCityId));
            schedule.setDestination(cityService.findById(destinationCityId));
            schedule.setDepartAt(departAt);
            schedule.setArriveAt(arriveAt);

            flightScheduleService.save(schedule);

            // Update class-wise costs (rates)
            for (String key : allRequestParams.keySet()) {
                if (key.startsWith("rate_")) {
                    Long classId = Long.parseLong(key.substring(5));
                    Double cost = Double.parseDouble(allRequestParams.get(key));
                    flightScheduleRateService.updateOrCreateRate(scheduleId, classId, cost);
                }
            }

            redirectAttributes.addFlashAttribute("success", "Schedule updated successfully");
            return "redirect:/admin/flights/view-schedules";

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Failed to update schedule: " + e.getMessage());
            return "redirect:/admin/flights/edit-schedule/" + scheduleId;
        }
    }



}
