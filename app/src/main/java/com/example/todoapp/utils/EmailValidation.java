package com.example.todoapp.utils;

import java.util.regex.Pattern;

public class EmailValidation {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w.-]+@[\\w-]+\\.[\\w-]{2,}$");

    public static boolean validateEmail(String email) {
        return EMAIL_PATTERN.matcher(email).matches();
    }
}
