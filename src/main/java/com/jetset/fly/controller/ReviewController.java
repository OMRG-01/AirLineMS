package com.jetset.fly.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import java.security.Principal;

import com.jetset.fly.model.CameFrom;
import com.jetset.fly.model.Review;
import com.jetset.fly.model.User;
import com.jetset.fly.repository.ReviewRepository;
import com.jetset.fly.repository.UserRepository;
import com.jetset.fly.service.EmailService;

import jakarta.servlet.http.HttpSession;


@Controller
public class ReviewController {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    // Show review form to user
    @GetMapping("/user/review")
    public String showReviewForm(Model model, HttpSession session) {
        model.addAttribute("review", new Review());
        model.addAttribute("cameFromOptions", CameFrom.values());

        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            model.addAttribute("error", "Please login to submit a review.");
            return "user/doReview";
        }

        model.addAttribute("user", user);
        return "user/doReview";
    }

    @Autowired
    private EmailService emailService;
    @PostMapping("/user/submitReview")
    public String submitReview(@ModelAttribute Review review, HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");

        if (user == null) {
            model.addAttribute("error", "Please login to submit a review.");
            return "user/doReview";
        }

        review.setUser(user);
        reviewRepository.save(review);

        // ✅ Send email using the user's email
        try {
            emailService.sendReviewEmail(user.getEmail());
        } catch (Exception e) {
            e.printStackTrace(); // optional: log error
            model.addAttribute("emailError", "Review saved, but email could not be sent.");
        }

        model.addAttribute("success", "Thank you for your feedback!");
        model.addAttribute("review", new Review()); // reset form
        model.addAttribute("cameFromOptions", CameFrom.values());
        model.addAttribute("user", user);

        return "user/doReview";
    }




    // Admin view all reviews
    @GetMapping("/admin/feedback")
    public String viewAllReviews(Model model) {
        List<Review> allReviews = reviewRepository.findAll();
        Double averageRating = reviewRepository.getAverageRating();
        model.addAttribute("reviews", allReviews);
        model.addAttribute("averageRating", averageRating != null ? averageRating : 0.0);
        return "admin/viewReviews";
    }
}
