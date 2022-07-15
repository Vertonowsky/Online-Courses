package com.example.hello_world.web.dto;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UserDto {

    public static final String EMAIL_PATTERN = "^(?=.{1,64}@)[A-Za-z0-9_-]+(\\.[A-Za-z0-9_-]+)*@"+ "[^-][A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*(\\.[A-Za-z]{2,})$";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[ąĄćĆśŚęĘóÓłŁńŃżŻźŹ$&+,:;=?@#|<>.^*()%!-]).{8,32}$";



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

    public UserDto() {
    }

    public UserDto(String email, String password, String passwordRepeat, boolean terms) {
        this.email = email;
        this.password = password;
        this.passwordRepeat = passwordRepeat;
        this.terms = terms;
    }



    public boolean isPasswordValid() {
        if (password == null || password.equals("") || password.isEmpty()) return false;
        if (password.length() < 8 || password.length() > 32) return false;

        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public boolean isEmailValid() {
        if (email == null || email.equals("") || email.isEmpty()) return false;
        if (email.length() > 64) return false;

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public boolean arePasswordsEqual() {
        return password.equals(passwordRepeat);
    }
}
