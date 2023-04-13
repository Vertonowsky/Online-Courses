package com.example.hello_world;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Regex {

    DISCOUNT_PATTERN("[a-zA-Z0-9]*"),
    POLISH_TEXT_PATTERN("[a-zA-Z0-9ąĄćĆśŚęĘóÓłŁńŃżŻźŹ ~!@#$%^&*()-_=+'?,.<>\\[\\]{}|]*");


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
