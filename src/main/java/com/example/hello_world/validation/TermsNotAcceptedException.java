package com.example.hello_world.validation;

public class TermsNotAcceptedException extends Exception {


    public TermsNotAcceptedException() {
    }

    public TermsNotAcceptedException(String message) {
        super(message);
    }

    public TermsNotAcceptedException(Throwable cause) {
        super(cause);
    }

    public TermsNotAcceptedException(String message, Throwable cause) {
        super(message, cause);
    }


}
