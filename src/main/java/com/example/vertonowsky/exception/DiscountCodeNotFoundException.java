package com.example.vertonowsky.exception;

public class DiscountCodeNotFoundException extends Exception {


    public DiscountCodeNotFoundException() {
    }

    public DiscountCodeNotFoundException(String message) {
        super(message);
    }

    public DiscountCodeNotFoundException(Throwable cause) {
        super(cause);
    }

    public DiscountCodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }


}
