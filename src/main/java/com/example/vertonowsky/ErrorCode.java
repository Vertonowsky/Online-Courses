package com.example.vertonowsky;

public enum ErrorCode {

    USER_NOT_FOUND_EXCEPTION("Nie odnaleziono użytkownika.");

    private String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
