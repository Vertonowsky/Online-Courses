package com.example.hello_world.validation;

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
