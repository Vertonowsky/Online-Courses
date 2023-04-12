package com.example.hello_world.validation;

public class DiscountCodeExpiredException extends Exception {


    public DiscountCodeExpiredException() {
    }

    public DiscountCodeExpiredException(String message) {
        super(message);
    }

    public DiscountCodeExpiredException(Throwable cause) {
        super(cause);
    }

    public DiscountCodeExpiredException(String message, Throwable cause) {
        super(message, cause);
    }


}
