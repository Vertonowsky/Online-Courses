package com.example.hello_world.validation;

public class UserNotLoggedInException extends Exception {


    public UserNotLoggedInException() {
    }

    public UserNotLoggedInException(String message) {
        super(message);
    }

    public UserNotLoggedInException(Throwable cause) {
        super(cause);
    }

    public UserNotLoggedInException(String message, Throwable cause) {
        super(message, cause);
    }


}
