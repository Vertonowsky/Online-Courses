package com.example.vertonowsky.exception;

public class LowDelayException extends Exception {


    public LowDelayException() {
    }

    public LowDelayException(String message) {
        super(message);
    }

    public LowDelayException(Throwable cause) {
        super(cause);
    }

    public LowDelayException(String message, Throwable cause) {
        super(message, cause);
    }


}
