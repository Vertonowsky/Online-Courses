package com.example.hello_world.validation;

public class InvalidEmailFormatException extends Exception {


    public InvalidEmailFormatException() {
    }

    public InvalidEmailFormatException(String message) {
        super(message);
    }

    public InvalidEmailFormatException(Throwable cause) {
        super(cause);
    }

    public InvalidEmailFormatException(String message, Throwable cause) {
        super(message, cause);
    }


}
