package com.example.hello_world.validation;

public class TopicNotFoundException extends Exception {


    public TopicNotFoundException() {
    }

    public TopicNotFoundException(String message) {
        super(message);
    }

    public TopicNotFoundException(Throwable cause) {
        super(cause);
    }

    public TopicNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }


}
