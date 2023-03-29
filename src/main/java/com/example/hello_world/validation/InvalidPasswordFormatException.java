package com.example.hello_world.validation;

public class InvalidPasswordFormatException extends Exception {


    public InvalidPasswordFormatException() {
    }

    public InvalidPasswordFormatException(String message) {
        super(message);
    }

    public InvalidPasswordFormatException(Throwable cause) {
        super(cause);
    }

    public InvalidPasswordFormatException(String message, Throwable cause) {
        super(message, cause);
    }


}
