package com.example.vertonowsky.exception;

public class InvalidDataException extends Exception {


    public InvalidDataException() {
    }

    public InvalidDataException(String message) {
        super(message);
    }

    public InvalidDataException(Throwable cause) {
        super(cause);
    }

    public InvalidDataException(String message, Throwable cause) {
        super(message, cause);
    }


}