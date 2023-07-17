package com.example.vertonowsky.exception;

public class InvalidTextFormatException extends Exception {


    public InvalidTextFormatException() {
    }

    public InvalidTextFormatException(String message) {
        super(message);
    }

    public InvalidTextFormatException(Throwable cause) {
        super(cause);
    }

    public InvalidTextFormatException(String message, Throwable cause) {
        super(message, cause);
    }


}
