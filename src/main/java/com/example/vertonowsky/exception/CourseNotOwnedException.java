package com.example.vertonowsky.exception;

public class CourseNotOwnedException extends Exception {


    public CourseNotOwnedException() {
    }

    public CourseNotOwnedException(String message) {
        super(message);
    }

    public CourseNotOwnedException(Throwable cause) {
        super(cause);
    }

    public CourseNotOwnedException(String message, Throwable cause) {
        super(message, cause);
    }


}
