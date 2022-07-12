package com.example.hello_world.persistence.model;


import javax.persistence.*;

@Entity
@Table(name = "users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Integer id;

    //@Column(name="salt", nullable = false, length = 32)
    //String salt;

    @Column(nullable = false, length = 60)
    private String email;


    @Column(nullable = false, length = 64)
    private String password;


    private boolean active;


    private String roles;

    //@Column(nullable = false, length = 32)
    //Rank rank;

    //@Column(name="registration_date", nullable = false)
    //Date registrationDate;



    public User() {

    }


    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}