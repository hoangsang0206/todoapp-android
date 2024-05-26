package com.example.todoapp.utils;

import java.security.SecureRandom;

public class RandomString {
    public static String random(int length) {
        SecureRandom random = new SecureRandom();
        String characters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder strBuilder = new StringBuilder(length);

        for(int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            strBuilder.append(randomChar);
        }

        return strBuilder.toString();
    }
}
