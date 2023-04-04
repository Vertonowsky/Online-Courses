package com.example.hello_world.validation;

public class TokenExpiredException extends Exception {


    public TokenExpiredException() {
    }

    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException(Throwable cause) {
        super(cause);
    }

    public TokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }


}
