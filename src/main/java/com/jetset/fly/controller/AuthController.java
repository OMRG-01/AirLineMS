package com.jetset.fly.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.jetset.fly.model.AirFlight;
import com.jetset.fly.model.Airline;
import com.jetset.fly.model.Role;
import com.jetset.fly.model.User;
import com.jetset.fly.repository.RoleRepository;
import com.jetset.fly.repository.UserRepository;
import com.jetset.fly.service.AdminService;
import com.jetset.fly.service.AirFlightService;
import com.jetset.fly.service.AirlineService;
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
    

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	 @GetMapping("/")
	    public String home() {
	        return "index"; // Redirects to index.html in 'static'
	    }
	
	 @GetMapping("/login1")
	    public String showLoginPage() {
	        return "login"; // Shows login.html
	    }
	 @GetMapping("/admin/login")
	    public String adminLoginPage() {
	        return "admin/adminLogIn"; // maps to templates/admin/adminLogIn.html
	    }

	 @Autowired
	    private AdminService adminService;

	    @PostMapping("/submit")
	    public String login(@RequestParam String email,
	                        @RequestParam String password,
	                        HttpSession session) {
	        boolean authenticated = adminService.authenticateAdmin(email, password, session);

	        if (authenticated) {
	            return "redirect:/admin/dashboard"; // success
	        } else {
	            return "redirect:/admin/login?error"; // failure
	        }
	    }
	    @GetMapping("/admin/dashboard")
	    public String adminDashboard(HttpSession session, Model model) {
	        if (session.getAttribute("admin") == null) {
	            return "redirect:/admin/login"; // Not logged in
	        }

	        // Airline data
	        long airlineCount = airlineService.countByStatus("ACTIVE");
	        Airline latestAirline = airlineService.findLatestByStatus("ACTIVE");

	        // Flight data
	        long flightCount = airFlightService.countByStatus("ACTIVE");
	        AirFlight latestFlight = airFlightService.findLatestByStatus("ACTIVE");

	        model.addAttribute("airlineCount", airlineCount);
	        model.addAttribute("latestAirlineName", latestAirline != null ? latestAirline.getAname() : "N/A");

	        model.addAttribute("flightCount", flightCount);
	        model.addAttribute("latestFlightNumber", latestFlight != null ? latestFlight.getFnumber() : "N/A");

	        return "admin/adminDash";
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
	                return "redirect:/user/dashUser";
	            } else {
	                return "redirect:/login1?error=unauthorized";
	            }
	        }

	        return "redirect:/login1?error=true";
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
	
}
