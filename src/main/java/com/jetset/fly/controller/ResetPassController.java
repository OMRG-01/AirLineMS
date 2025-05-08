package com.jetset.fly.controller;

import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;

import com.jetset.fly.model.User;
import com.jetset.fly.repository.UserRepository;
import com.jetset.fly.service.EmailService;
import com.jetset.fly.service.OtpService;

@Controller
public class ResetPassController {
	
	@Autowired
	private PasswordEncoder passwordEncoder; // Add this to the controller
	@Autowired private EmailService emailService;
	@Autowired private OtpService otpService;
	@Autowired private UserRepository userRepository;
	
	@PostMapping("/forgot-password")
	public String sendOtp(@RequestParam("email") String email, Model model,HttpSession session) {
	    Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase());
	    if (userOpt.isEmpty()) {
	        model.addAttribute("emailNotFound", true);
	        return "user/forgetPass";
	    }

	    String otp = String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
	    otpService.saveOtp(email, otp); // Custom service to store OTP with expiry

	    emailService.sendOtpEmail(email, otp); // Send using SMTP
	    
	    session.setAttribute("otpSent", true);
	    model.addAttribute("otpSent", true);
	    model.addAttribute("email", email);
	    return "user/forgetPass";
	}
	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam("email") String email,
	                        @RequestParam("otp") String otp,
	                        Model model,HttpSession session) {
	    if (!otpService.verifyOtp(email, otp)) {
	        model.addAttribute("otpSent", true);
	        model.addAttribute("email", email);
	        model.addAttribute("invalidOtp", true);
	        return "user/forgetPass";
	    }
	    
	    session.setAttribute("otpVerified", true);
	    model.addAttribute("otpVerified", true);
	    model.addAttribute("email", email);
	    model.addAttribute("token", otp); // For extra validation
	    return "user/forgetPass";
	}

	@PostMapping("/reset-password")
	public String resetPassword(@RequestParam("email") String email,
	                            @RequestParam("password") String password,
	                            @RequestParam("confirmPassword") String confirmPassword,
	                            @RequestParam("token") String token,
	                            Model model,HttpSession session) {
	    if (!otpService.verifyOtp(email, token)) {
	        model.addAttribute("otpSent", true);
	        model.addAttribute("email", email);
	        model.addAttribute("otpExpired", true);
	        return "user/forgetPass";
	    }

	    if (!password.equals(confirmPassword)) {
	        model.addAttribute("otpVerified", true);
	        model.addAttribute("email", email);
	        model.addAttribute("token", token);
	        model.addAttribute("passwordMismatch", true);
	        return "user/forgetPass";
	    }

	    if (password.length() < 8) {
	        model.addAttribute("otpVerified", true);
	        model.addAttribute("email", email);
	        model.addAttribute("token", token);
	        model.addAttribute("passwordTooWeak", true);
	        return "user/forgetPass";
	    }

	    Optional<User> userOpt = userRepository.findByEmail(email.toLowerCase());
	    if (userOpt.isPresent()) {
	        User user = userOpt.get();
	        user.setPassword(passwordEncoder.encode(password));
	        userRepository.save(user);
	    }
	    
	    session.invalidate();
	    model.addAttribute("resetSuccess", true);
	    return "user/forgetPass";
	}
	
	// Handles browser refresh or GET on forgot-password
	@GetMapping("/forgot-password")
	public String handleForgotPasswordGet(HttpSession session, RedirectAttributes redirectAttributes) {
	    redirectAttributes.addFlashAttribute("error", "Please enter your email again.");
	    session.invalidate();
	    return "redirect:/forgetPass";
	}

	// Handles browser refresh or GET on verify-otp
	@GetMapping("/verify-otp")
	public String handleVerifyOtpGet(HttpSession session, RedirectAttributes redirectAttributes) {
	    redirectAttributes.addFlashAttribute("error", "Please enter your email again.");
	    session.invalidate();
	    return "redirect:/forgetPass";
	}

	// Handles browser refresh or GET on reset-password
	@GetMapping("/reset-password")
	public String handleResetPasswordGet(HttpSession session, RedirectAttributes redirectAttributes) {
	    redirectAttributes.addFlashAttribute("error", "Please enter your email again.");
	    session.invalidate();
	    return "redirect:/forgetPass";
	}

	


}
