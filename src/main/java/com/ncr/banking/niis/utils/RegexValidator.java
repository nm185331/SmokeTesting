package com.ncr.banking.niis.utils;


public class RegexValidator {

    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\+1 \\(\\d{3}\\) \\d{3}-\\d{4}$");
    }
}