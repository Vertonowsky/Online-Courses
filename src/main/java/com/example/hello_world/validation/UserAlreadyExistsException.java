package com.example.hello_world.validation;

public class UserAlreadyExistsException extends Throwable {


    public UserAlreadyExistsException(final String message) {
        super(message);
    }


}
