package com.example.vertonowsky;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Regex {

    DISCOUNT_PATTERN("^[a-zA-Z0-9]+"),
    POLISH_TEXT_PATTERN("^[a-zA-Z0-9ąĄćĆśŚęĘóÓłŁńŃżŻźŹ ~!@#$%^&*()-_=+'?,.<>\\[\\]{}|]+"),
    EMAIL_PATTERN("^[a-z\\d]+[\\w\\d.-]*@(?:[a-z\\d]+[a-z\\d-]+\\.){1,5}[a-z]{2,6}$"),
    PASSWORD_PATTERN("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[ąĄćĆśŚęĘóÓłŁńŃżŻźŹ$&+,:;=?@#|<>.^*()%!-]).{8,32}$"),

    UUID_PATTERN("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$");


    private final String pattern;

    Regex(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }

    public boolean matches(String string) {
        Pattern pattern = Pattern.compile(getPattern());
        Matcher matcher = pattern.matcher(string);
        return matcher.matches();
    }
}
