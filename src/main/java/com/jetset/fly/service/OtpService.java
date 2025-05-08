package com.jetset.fly.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class OtpService {
    private final Map<String, OtpEntry> otpStorage = new ConcurrentHashMap<>();

    public void saveOtp(String email, String otp) {
        otpStorage.put(email.toLowerCase(), new OtpEntry(otp, LocalDateTime.now().plusMinutes(5)));
    }

    public boolean verifyOtp(String email, String otp) {
        OtpEntry entry = otpStorage.get(email.toLowerCase());
        if (entry == null || entry.getExpiry().isBefore(LocalDateTime.now())) return false;
        return entry.getOtp().equals(otp);
    }

    private static class OtpEntry {
        private final String otp;
        private final LocalDateTime expiry;
        public OtpEntry(String otp, LocalDateTime expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }
        public String getOtp() { return otp; }
        public LocalDateTime getExpiry() { return expiry; }
    }
}
