package com.example.hello_world.validation;

public class InvalidUUIDFormatException extends Exception {


    public InvalidUUIDFormatException() {
    }

    public InvalidUUIDFormatException(String message) {
        super(message);
    }

    public InvalidUUIDFormatException(Throwable cause) {
        super(cause);
    }

    public InvalidUUIDFormatException(String message, Throwable cause) {
        super(message, cause);
    }


}
