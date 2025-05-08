package com.jetset.fly.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String adminPass = "admin123";
        String userPass = "user123";

        String adminHash = encoder.encode(adminPass);
        String userHash = encoder.encode(userPass);

        System.out.println("Admin password hash: " + adminHash);
        System.out.println("User password hash: " + userHash);
    }
}
