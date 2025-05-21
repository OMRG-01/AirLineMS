package com.jetset.fly.controller;

import com.jetset.fly.dto.ScheduleViewDTO;
import com.jetset.fly.dto.ViewPassengerDTO;
import com.jetset.fly.model.*;
import com.jetset.fly.model.Class;
import com.jetset.fly.repository.*;
import com.jetset.fly.service.*;
import com.jetset.fly.utility.IdUtil;

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

    
    @GetMapping("/schedule/{encodedId}")
    public String showAddSchedulePage(@PathVariable String encodedId, Model model) {
        // 🔓 Decode the Base64-encoded ID
        byte[] decodedBytes = Base64.getDecoder().decode(encodedId);
        Long flightId = Long.parseLong(new String(decodedBytes));

        // ✅ Proceed as before
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
            String encodedId = Base64.getEncoder().encodeToString(flightId.toString().getBytes());
            redirectAttributes.addFlashAttribute("error",
                "Flight " + flight.getFnumber() + " is already scheduled in this time range on " + departAt.toLocalDate());
            return "redirect:/admin/flights/schedule/" + encodedId;
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
            row.put("encodedScheduleId", IdUtil.encodeId(schedule.getId()));


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

    @GetMapping("/edit-schedule/{encodedId}")
    public String editSchedule(@PathVariable("encodedId") String encodedId, Model model) {
        Long scheduleId = IdUtil.decodeId(encodedId); // 👈 Decode the ID first
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
       model.addAttribute("encodedScheduleId", IdUtil.encodeId(schedule.getId()));

        return "admin/editSchedule";
    }


    @PostMapping("/schedule/update")
    public String updateSchedule(
    		 @RequestParam("scheduleId") String encodedScheduleId,
            @RequestParam("sourceId") Long sourceId,
            @RequestParam("destinationId") Long destinationId,
            @RequestParam("departAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departAt,
            @RequestParam("arriveAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arriveAt,
            @RequestParam Map<String, String> params,
            RedirectAttributes redirectAttributes) {
    	
    	Long scheduleId = IdUtil.decodeId(encodedScheduleId);
    	
        FlightSchedule schedule = flightScheduleService.findById(scheduleId);
        schedule.setSource(new City(sourceId));
        schedule.setDestination(new City(destinationId));
        schedule.setDepartAt(departAt);
        schedule.setArriveAt(arriveAt);

        // Update the schedule
        flightScheduleService.save(schedule);

        // Update rates
        flightScheduleRateService.deleteByScheduleId(scheduleId); // or update individually if preferred
        for (String key : params.keySet()) {
            if (key.startsWith("rate_")) {
                Long classId = Long.parseLong(key.replace("rate_", ""));
                Double rate = Double.parseDouble(params.get(key));

                FlightScheduleRate fsr = new FlightScheduleRate();
                fsr.setSchedule(schedule);
                fsr.setFlight(schedule.getFlight());
                fsr.setFlightClass(new Class(classId));
                fsr.setRate(rate);

                flightScheduleRateService.save(fsr);
            }
        }

        redirectAttributes.addFlashAttribute("success", "Schedule updated successfully!");
        return "redirect:/admin/flights/view-schedules";
    }



//    @PostMapping("/schedule/update")
//    public String updateSchedule(
//            @RequestParam("scheduleId") String encodedScheduleId,
//            @RequestParam("flightId") Long flightId,
//            @RequestParam("sourceId") Long sourceId,
//            @RequestParam("destinationId") Long destinationId,
//            @RequestParam("departAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departAt,
//            @RequestParam("arriveAt") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime arriveAt,
//            @RequestParam Map<String, String> params,
//            RedirectAttributes redirectAttributes) {
//
//        try {
//            Long scheduleId = IdUtil.decodeId(encodedScheduleId);
//
////            flightScheduleService.updateSchedule(scheduleId, flightId, sourceId, destinationId, departAt, arriveAt, params);
//
//            redirectAttributes.addFlashAttribute("success", "Schedule updated successfully.");
//            return "redirect:/admin/flights/view-schedules";
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
//            return "redirect:/admin/flights/edit-schedule/" + encodedScheduleId;
//        }
//    }
    @Autowired
    private PassengerRepository passengerRepository;
    
    @Autowired
    private FlightClassRepository flightClassRepository;
    
    @Autowired
    private PassengerService passengerService;
    
    @GetMapping("/view-passengers/{scheduleId}")
    public String viewPassengersBySchedule(@PathVariable Long scheduleId, Model model) {
        // 1. Get schedule
        FlightSchedule schedule = flightScheduleService.findById(scheduleId);
        if (schedule == null) {
            throw new IllegalArgumentException("Invalid Schedule ID");
        }

        // 2. Get all passengers for the schedule
        List<Passenger> passengers = passengerRepository.findByScheduleId(scheduleId);

        // 3. Get flight classes assigned to this flight
        List<FlightClass> flightClasses = flightClassRepository.findByFlightId(schedule.getFlight().getId());

        // 4. For each class, count passengers and fetch capacity
        Map<String, String> classStats = new LinkedHashMap<>();
        for (FlightClass flightClass : flightClasses) {
            int booked = (int) passengers.stream()
                    .filter(p -> p.getFlightClass().getId().equals(flightClass.getFlightClass().getId()))
                    .count();
            classStats.put(flightClass.getFlightClass().getName(), booked + "/" + flightClass.getSeat());
        }
        
     // 3. Get flight classes assigned to this flight
        List<FlightClass> flightClass = flightClassRepository.findByFlightId(schedule.getFlight().getId());

        // 4. Calculate total seat capacity of the flight
        int totalSeats = flightClass.stream()
                .mapToInt(FlightClass::getSeat)
                .sum();


        // 5. Total booked passengers
        int totalBooked = passengers.size();

        // 6. Add attributes to model
        model.addAttribute("totalSeats", totalSeats);

        model.addAttribute("schedule", schedule);
        model.addAttribute("passengers", passengers);
        model.addAttribute("classStats", classStats);
        model.addAttribute("totalBooked", totalBooked);

        return "admin/viewPassengerBySchedule";
    }

    @GetMapping("/passengers")
    public String viewAllPassengers(Model model) {
        List<ViewPassengerDTO> passengerList = passengerService.getAllPassengerDetails();
        model.addAttribute("passengerList", passengerList);
        return "admin/viewPassengers";
    }


}
