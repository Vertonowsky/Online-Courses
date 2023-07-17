package com.example.vertonowsky.user;

import com.example.vertonowsky.Regex;
import com.example.vertonowsky.security.RegistrationMethod;

public class UserDto {


    private RegistrationMethod registrationMethod = RegistrationMethod.DEFAULT;
    private String email;
    private String password;
    private String passwordRepeat;
    private boolean terms;


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    public boolean areTermsChecked() {
        return terms;
    }

    public void setTerms(boolean terms) {
        this.terms = terms;
    }

    public RegistrationMethod getRegistrationMethod() {
        return registrationMethod;
    }

    public void setRegistrationMethod(RegistrationMethod registrationMethod) {
        this.registrationMethod = registrationMethod;
    }


    public UserDto() {
    }

    public UserDto(String email, String password, String passwordRepeat, boolean terms) {
        this.email = email;
        this.password = password;
        this.passwordRepeat = passwordRepeat;
        this.terms = terms;
    }

    public UserDto(String email, String password, String passwordRepeat, boolean terms, RegistrationMethod registrationMethod) {
        this.email = email;
        this.password = password;
        this.passwordRepeat = passwordRepeat;
        this.terms = terms;
        this.registrationMethod = registrationMethod;
    }



    public static boolean isPasswordValid(String password) {
        if (password == null || password.isEmpty()) return false;
        if (password.length() < 8 || password.length() > 32) return false;

        return Regex.PASSWORD_PATTERN.matches(password);
    }

    public static boolean isEmailValid(String email) {
        if (email == null || email.isEmpty()) return false;
        if (email.length() > 64) return false;

        return Regex.EMAIL_PATTERN.matches(email);
    }

    public boolean arePasswordsEqual() {
        return password.equals(passwordRepeat);
    }
}
