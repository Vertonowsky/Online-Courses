package com.example.vertonowsky.exception;

public class ChapterNotFoundException extends Exception {


    public ChapterNotFoundException() {
    }

    public ChapterNotFoundException(String message) {
        super(message);
    }

    public ChapterNotFoundException(Throwable cause) {
        super(cause);
    }

    public ChapterNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }


}
