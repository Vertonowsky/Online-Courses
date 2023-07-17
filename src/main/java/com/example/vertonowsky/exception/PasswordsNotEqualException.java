package com.example.vertonowsky.exception;

public class PasswordsNotEqualException extends Exception {


    public PasswordsNotEqualException() {
    }

    public PasswordsNotEqualException(String message) {
        super(message);
    }

    public PasswordsNotEqualException(Throwable cause) {
        super(cause);
    }

    public PasswordsNotEqualException(String message, Throwable cause) {
        super(message, cause);
    }


}
