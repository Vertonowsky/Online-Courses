package com.example.hello_world;

public enum Regex {

    DISCOUNT_PATTERN("[a-zA-Z0-9]*");


    private final String pattern;

    Regex(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return pattern;
    }
}
