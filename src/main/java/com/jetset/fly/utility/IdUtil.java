package com.jetset.fly.utility;


import java.util.Base64;

public class IdUtil {

    // Encode Long ID to Base64 String
    public static String encodeId(Long id) {
        return Base64.getUrlEncoder().encodeToString(id.toString().getBytes());
    }

    // Decode Base64 String back to Long ID
    public static Long decodeId(String encodedId) {
        try {
            String decoded = new String(Base64.getUrlDecoder().decode(encodedId));
            return Long.parseLong(decoded);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid encoded ID: " + encodedId);
        }
    }
}
