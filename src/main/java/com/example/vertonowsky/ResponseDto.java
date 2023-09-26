package com.example.vertonowsky;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseDto {

    private Boolean success;
    private String message;
    private Long tokenCooldown;

    public ResponseDto(String message) {
        this.message = message;
    }

    public ResponseDto(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTokenCooldown() {
        return tokenCooldown;
    }

    public void setTokenCooldown(Long tokenCooldown) {
        this.tokenCooldown = tokenCooldown;
    }
}
