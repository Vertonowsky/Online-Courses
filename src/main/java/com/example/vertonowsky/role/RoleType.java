package com.example.vertonowsky.role;

public enum RoleType {

    ROLE_USER("Użytkownik"),

    ROLE_ADMIN("Administrator");

    private String role;

    RoleType(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
