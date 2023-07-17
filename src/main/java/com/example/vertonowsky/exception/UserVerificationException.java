package com.example.vertonowsky.exception;

public class UserVerificationException extends Exception {



    public UserVerificationException() {
    }

    public UserVerificationException(String message) {
        super(message);
    }

    public UserVerificationException(Throwable cause) {
        super(cause);
    }

    public UserVerificationException(String message, Throwable cause) {
        super(message, cause);
    }


}
