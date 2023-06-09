package com.example.hello_world.web.dto;

import java.util.UUID;

public class PasswordRecoverDto {

    private UUID token;
    private String password;

    private String passwordRepeat;


    public PasswordRecoverDto() {}

    public PasswordRecoverDto(UUID token, String password, String passwordRepeat) {
        this.token = token;
        this.password = password;
        this.passwordRepeat = passwordRepeat;
    }

    public boolean arePasswordsEqual() {
        return password.equals(passwordRepeat);
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }
}
