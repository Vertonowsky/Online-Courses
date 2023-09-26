package com.example.vertonowsky;

public enum ErrorCode {

    LOGIN_REQUIRED("Dostęp jedynie dla zalogowanych użytkowników."),
    REGISTRATION_METHOD_INVALID("Funkcja nie jest dostępna dla użytkowników zarejestrowanych poprzez serwisy zewnętrzne."),
    TOKEN_EXPIRED("Okres ważności tokenu uległ wygaśnięciu."),
    TOKEN_INVALID("Nieprawidłowy token."),
    TOKEN_NOT_FOUND("Nie odnaleziono tokenu."),
    USER_NOT_FOUND("Nie odnaleziono użytkownika.");

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
