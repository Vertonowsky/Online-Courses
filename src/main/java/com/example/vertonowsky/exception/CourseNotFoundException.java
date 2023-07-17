package com.example.vertonowsky.exception;

public class CourseNotFoundException extends Exception {


    public CourseNotFoundException() {
    }

    public CourseNotFoundException(String message) {
        super(message);
    }

    public CourseNotFoundException(Throwable cause) {
        super(cause);
    }

    public CourseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }


}
