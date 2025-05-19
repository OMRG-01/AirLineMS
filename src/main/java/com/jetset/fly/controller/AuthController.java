package com.jetset.fly.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.jetset.fly.model.AirFlight;
import com.jetset.fly.model.Airline;
import com.jetset.fly.model.City;
import com.jetset.fly.model.FlightClass;
import com.jetset.fly.model.FlightSchedule;
import com.jetset.fly.model.FlightScheduleRate;
import com.jetset.fly.model.Role;
import com.jetset.fly.model.User;
import com.jetset.fly.repository.FlightScheduleRepository;
import com.jetset.fly.repository.RoleRepository;
import com.jetset.fly.repository.UserRepository;
import com.jetset.fly.service.AdminService;
import com.jetset.fly.service.AirFlightService;
import com.jetset.fly.service.AirlineService;
import com.jetset.fly.service.CityService;
import com.jetset.fly.service.FlightClassService;
import com.jetset.fly.service.FlightScheduleRateService;
import com.jetset.fly.service.FlightScheduleService;
import com.jetset.fly.service.PassengerService;
import com.jetset.fly.service.UserService;
import jakarta.servlet.http.HttpSession;


@Controller
public class AuthController {
	@Autowired
    private UserService userService;
	
	@Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private AirlineService airlineService;
    @Autowired
    private AirFlightService airFlightService;
    @Autowired
    private FlightScheduleRepository flightScheduleRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
    @Autowired
    private FlightScheduleService flightScheduleService;
    
    @Autowired
    private FlightScheduleRateService flightScheduleRateService;
	 @GetMapping("/")
	    public String home(Model model) {
		 List<City> cities = cityService.getAllActiveCities();
		 model.addAttribute("cities", cities);
	        return "index"; // Redirects to index.html in 'static'
	    }
	 
	 @Autowired
	 private FlightClassService flightClassService;
	 
	 @Autowired
	 private PassengerService passengerService;
	 
	 @GetMapping("/fair-flight")
	 public String searchFlights(@RequestParam("from") Long fromCityId,
	                             @RequestParam("to") Long toCityId,
	                             @RequestParam("d_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
	                             @RequestParam("nop") int passengers,
	                             Model model) {

	     List<FlightSchedule> flights = flightScheduleService.findFlightsForRouteAndDate(fromCityId, toCityId, date);

	     // Map<scheduleId, List<FlightScheduleRate>>
	     Map<Long, List<FlightScheduleRate>> scheduleRatesMap = new HashMap<>();

	     // Map<scheduleId-classId, availableSeatCount>
	     Map<String, Integer> availableSeatsMap = new HashMap<>();

	     for (FlightSchedule flight : flights) {
	    	    List<FlightScheduleRate> rates = flightScheduleRateService.getRatesByScheduleId(flight.getId());
	    	    scheduleRatesMap.put(flight.getId(), rates);

	    	    for (FlightScheduleRate rate : rates) {
	    	        Long flightId = rate.getFlight().getId();
	    	        Long classId = rate.getFlightClass().getId();

	    	        // Fetch seat capacity from FlightClass using flight + class
	    	        FlightClass flightClass = flightClassService.findByFlightIdAndClassId(flightId, classId);
	    	        int totalCapacity = flightClass.getSeat();

	    	        int bookedSeats = passengerService.countPassengersByScheduleAndClass(flight.getId(), classId);

	    	        int availableSeats = totalCapacity - bookedSeats;
	    	        String key = flight.getId() + "-" + classId;

	    	        availableSeatsMap.put(key, availableSeats);
	    	    }
	    	}


	     City fromCity = cityService.getCityById(fromCityId);
	     City toCity = cityService.getCityById(toCityId);

	     model.addAttribute("flights", flights);
	     model.addAttribute("scheduleRatesMap", scheduleRatesMap);
	     model.addAttribute("availableSeatsMap", availableSeatsMap); // 🆕
	     model.addAttribute("from", fromCity.getCityname());
	     model.addAttribute("to", toCity.getCityname());
	     model.addAttribute("date", date);
	     model.addAttribute("passengers", passengers);
	     return "user/fairFlight";
	 }

	 
	 @GetMapping("/flightstatus")
	 public String searchFlightsStatus(@RequestParam("from") Long fromCityId,
	                             @RequestParam("to") Long toCityId,
	                             @RequestParam("d_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
	                            
	                             Model model) {

	     List<FlightSchedule> flights = flightScheduleService.findFlightsStatus(fromCityId, toCityId, date);
	     
	     
	     // Prepare a map of FlightSchedule ID -> List of FlightScheduleRate
	     Map<Long, List<FlightScheduleRate>> scheduleRatesMap = new HashMap<>();
	     for (FlightSchedule flight : flights) {
	         List<FlightScheduleRate> rates = flightScheduleRateService.getRatesByScheduleId(flight.getId());
	         scheduleRatesMap.put(flight.getId(), rates);
	     }
	     
	     City fromCity = cityService.getCityById(fromCityId); // Replace with your actual method
	     City toCity = cityService.getCityById(toCityId);

	     model.addAttribute("flights", flights);
	     model.addAttribute("scheduleRatesMap", scheduleRatesMap);
	     model.addAttribute("from", fromCity.getCityname());
	     model.addAttribute("to", toCity.getCityname());
	     model.addAttribute("date", date);
	     model.addAttribute("now", LocalDateTime.now());

	     return "user/fairStatus";
	 }


	 
	 @GetMapping("/loading")
	 public String showLoadingPage(@RequestParam Long from,
	                               @RequestParam Long to,
	                               @RequestParam String d_date,
	                               @RequestParam int nop,
	                               Model model) {

	     // Fetch city names using city service
	     City fromCity = cityService.getCityById(from);
	     City toCity = cityService.getCityById(to);

	     model.addAttribute("from", from);
	     model.addAttribute("to", to);
	     model.addAttribute("fromCity", fromCity.getCityname());
	     model.addAttribute("toCity", toCity.getCityname());
	     model.addAttribute("d_date", d_date);
	     model.addAttribute("nop", nop);

	     return "user/loading";  // maps to loading.html Thymeleaf template
	 }


	 

	 @Autowired
	    private CityService cityService;
	 
	 @GetMapping("/login1")
	    public String showLoginPage() {
		 
	        return "login"; // Shows login.html
	    }
	 @GetMapping("/admin/login")
	    public String adminLoginPage() {
	        return "admin/adminLogIn"; // maps to templates/admin/adminLogIn.html
	    }

	 @Autowired
	    private UserService adminService;

	 @PostMapping("/admin/doLogin")
	 public String login(@RequestParam String email,
	                     @RequestParam String password,
	                     HttpSession session) {
	     User user = adminService.findByEmailAndPassword(email, password);

	     if (user != null && user.getRole().getId() == 1) { // Ensure role = 1 => ADMIN
	         session.setAttribute("admin", user); // store entire User object as 'admin'
	         return "redirect:/admin/dashboard";
	     }

	     return "redirect:/admin/login?error";
	 }

	    @GetMapping("/admin/dashboard")
	    public String adminDashboard(HttpSession session, Model model) {
	    	User admin = (User) session.getAttribute("admin");
	        if (session.getAttribute("admin") == null) {
	            return "redirect:/admin/login"; // Not logged in
	        }
	        
	        
	       
	        
	        List<Airline> airlines = airlineService.getActiveAirlines(); // Only ACTIVE airlines
	        List<Map<String, Object>> airlineFlightData = new ArrayList<>();
	        
	        for (Airline airline : airlines) {
	            int flightCount = airFlightService.countByAirlineAndStatus(airline, "ACTIVE");
	            Map<String, Object> data = new HashMap<>();
	            data.put("label", airline.getAname());
	            data.put("value", flightCount);
	            airlineFlightData.add(data);
	        }
	        
	        List<FlightSchedule> schedules = flightScheduleRepository.findByStatus("ACTIVE");

	        int notDeparted = 0;
	        int inFlight = 0;
	        int arrived = 0;
	        
	        LocalDateTime now = LocalDateTime.now();

	        for (FlightSchedule schedule : schedules) {
	            if (now.isBefore(schedule.getDepartAt())) {
	                notDeparted++;
	            } else if (now.isAfter(schedule.getDepartAt()) && now.isBefore(schedule.getArriveAt())) {
	                inFlight++;
	            } else if (now.isAfter(schedule.getArriveAt()) || now.isEqual(schedule.getArriveAt())) {
	                arrived++;
	            }
	        }

	        model.addAttribute("notDeparted", notDeparted);
	        model.addAttribute("inFlight", inFlight);
	        model.addAttribute("arrived", arrived);
	        
	        
	        model.addAttribute("airlineFlightData", airlineFlightData);
	        
	        // Airline data
	        long airlineCount = airlineService.countByStatus("ACTIVE");
	        Airline latestAirline = airlineService.findLatestByStatus("ACTIVE");

	        // Flight data
	        long flightCount = airFlightService.countByStatus("ACTIVE");
	        AirFlight latestFlight = airFlightService.findLatestByStatus("ACTIVE");

	        
	        List<String> selectedClocks = (List<String>) session.getAttribute("selectedClocks");
	        if (selectedClocks == null) {
	            selectedClocks = Arrays.asList("UK", "USA", "India", "Dubai");
	        }
	        model.addAttribute("selectedClocks", selectedClocks);
	        
	        model.addAttribute("airlineCount", airlineCount);
	        model.addAttribute("latestAirlineName", latestAirline != null ? latestAirline.getAname() : "N/A");
	        model.addAttribute("admin", admin);
	        model.addAttribute("flightCount", flightCount);
	        model.addAttribute("latestFlightNumber", latestFlight != null ? latestFlight.getFnumber() : "N/A");
	        model.addAttribute("visibleClocks", selectedClocks);
	        
	        return "admin/adminDash";
	    }

	    
	    //admin profile edit 
	    
	    @GetMapping("/admin/editProfile")
	    public String editAdminProfile(HttpSession session, Model model) {
	        User admin = (User) session.getAttribute("admin");

	        if (admin == null || admin.getRole().getId() != 1) {
	            return "redirect:/admin/login";
	        }

	        model.addAttribute("admin", admin);
	        return "admin/editProfile"; // maps to templates/admin/editProfile.html
	    }
	    
	    @PostMapping("/admin/updateProfile")
	    public String updateProfile(@RequestParam String name,
	                                @RequestParam String mobile,
	                                @RequestParam String currentPassword,
	                                @RequestParam(required = false) String newPassword,
	                                @RequestParam(required = false) String confirmPassword,
	                                HttpSession session,
	                                RedirectAttributes redirectAttributes) {
	        User admin = (User) session.getAttribute("admin");

	        if (admin == null) {
	            return "redirect:/admin/login";
	        }

	        if (!passwordEncoder.matches(currentPassword, admin.getPassword())) {
	            redirectAttributes.addFlashAttribute("error", "Current password is incorrect.");
	            return "redirect:/admin/editProfile";
	        }

	        // Update name and mobile
	        admin.setName(name);
	        admin.setMobile(mobile);

	        // If new password is provided and matches confirm password, update it
	        if (newPassword != null && !newPassword.isEmpty()) {
	            if (!newPassword.equals(confirmPassword)) {
	                redirectAttributes.addFlashAttribute("error", "New password and confirm password do not match.");
	                return "redirect:/admin/editProfile";
	            }
	            admin.setPassword(passwordEncoder.encode(newPassword));
	        }

	        userService.save(admin); // Update in DB
	        redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
	        return "redirect:/admin/editProfile";
	    }


	    //add memeber as admin
	    
	    @GetMapping("/admin/add")
	    public String showAddAdminForm() {
	        return "admin/addAdmin";
	    }

	    @PostMapping("/admin/saveAdmin")
	    public String saveAdmin(@RequestParam String title,
	                            @RequestParam String firstName,
	                            @RequestParam String lastName,
	                            @RequestParam String email,
	                            @RequestParam String password,
	                            @RequestParam String mobile,
	                            RedirectAttributes redirectAttributes) {

	        if (userService.existsByEmail(email)) {
	            redirectAttributes.addFlashAttribute("error", "Email already in use.");
	            return "redirect:/admin/add";
	        }

	        User admin = new User();
	        admin.setTitle(title);
	        admin.setName(firstName + " " + lastName);
	        admin.setEmail(email);
	        admin.setPassword(passwordEncoder.encode(password));
	        admin.setMobile(mobile);

	        Role adminRole = new Role();
	        adminRole.setId(1L); // ADMIN
	        admin.setRole(adminRole);

	        userService.save(admin);
	        redirectAttributes.addFlashAttribute("success", "Admin added successfully.");
	        return "redirect:/admin/add";
	    }

	    
	    @PostMapping("/login/submit")
	    public String processLogin(@RequestParam String email,
	                               @RequestParam String password,
	                               HttpSession session) {

	        User user = userService.login(email, password);
	        if (user != null) {
	            session.setAttribute("loggedInUser", user);
	            Long roleId = user.getRole().getId();

	            if (roleId == 1) {
	                return "redirect:/admin/dashAdmin";
	            } else if (roleId == 2) {
	                return "redirect:/user/userDash";
	            } else {
	                return "redirect:/login1?error=unauthorized";
	            }
	        }

	        return "redirect:/login1?error=true";
	    }
	    
	    @GetMapping("/user/userDash")
	    public String userDash(Model model, HttpSession session) {
	        List<City> cities = cityService.getAllActiveCities();
	        model.addAttribute("cities", cities);

	        // Use the same key: "loggedInUser"
	        User user = (User) session.getAttribute("loggedInUser");

	        if (user == null) {
	            return "redirect:/login1"; // User not logged in
	        }

	        model.addAttribute("currentUser", user);
	        return "user/dashUser";
	    }



	    @PostMapping("/register/submit")
	    public String registerUser(
	            @RequestParam("title") String title,
	            @RequestParam("fname") String firstName,
	            @RequestParam("lname") String lastName,
	            @RequestParam("email") String email,
	            @RequestParam("password") String password,
	            @RequestParam("mnumber") String mobile,
	            Model model
	    ) {
	        String fullName = firstName.toUpperCase() + " " + lastName.toUpperCase();

	        if (userRepository.findByEmail(email).isPresent()) {
	            model.addAttribute("emailExists", true);
	            return "user/registration";  // stay on the registration page
	        }

	        Role userRole = roleRepository.findById(2L)
	            .orElseThrow(() -> new RuntimeException("Default role not found"));

	        User user = new User();
	        user.setTitle(title);
	        user.setName(fullName);
	        user.setEmail(email.toLowerCase());
	        user.setPassword(passwordEncoder.encode(password));
	        user.setMobile(mobile);
	        user.setRole(userRole);

	        userRepository.save(user);
	        model.addAttribute("registrationSuccess", true);
	        return "user/registration";  // stay on page, let JS handle redirect
	    }


	    @GetMapping("/register")
	    public String showRegisterForm() {
	        return "user/registration"; // Points to register.html Thymeleaf page
	    }
	    @GetMapping("/forgetPass")
	    public String showForgetPassForm(HttpSession session) {
	        session.invalidate(); // clear any old data
	        return "user/forgetPass";
	    }

	 @GetMapping("/aboutus")
	 	public String showAboutUsPage() {
		 return "user/aboutus";
	 }
	 
	 @GetMapping("/admin/logout")
	 public String logout(HttpSession session) {
	     session.invalidate(); // Clear session data
	     return "redirect:/admin/login?logout"; // Redirect to login with a message
	 }
	 
	 @GetMapping("/admin/clock-settings")
	 public String settingsPage(HttpSession session, Model model) {
	        List<String> selectedClocks = (List<String>) session.getAttribute("selectedClocks");
	        if (selectedClocks == null) {
	            selectedClocks = Arrays.asList("UK", "USA", "India", "Dubai"); // Default
	        }
	        model.addAttribute("selectedClocks", selectedClocks);
	     return "admin/settings";
	 }
	 
	 @PostMapping("/admin/save-clock-settings")
	 public String saveClockSettings(@RequestParam(value = "clocks", required = false) List<String> clocks,
             HttpSession session,
             RedirectAttributes redirectAttributes) {
			if (clocks == null) {
			clocks = new ArrayList<>();
			}
			session.setAttribute("selectedClocks", clocks);
			redirectAttributes.addFlashAttribute("success", "Clock settings updated!");
	     return "redirect:/admin/clock-settings";
	 }



	
}
