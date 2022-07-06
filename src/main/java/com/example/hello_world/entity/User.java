package com.example.hello_world.entity;


import org.springframework.data.annotation.CreatedDate;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

@Entity
@Table(name = "users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    Integer id;

    @Column(name="salt", nullable = false, length = 32)
    @Size(min=32, max=32, message = "Błędny salt.")
    @NotNull
    String salt;

    @Column(nullable = false, length = 60)
    @Email(message = "Nieprawidłowy format adresu email.")
    @NotNull
    String email;

    @Column(nullable = false, length = 64)
    @NotNull
    String password;

    @Column(nullable = false, length = 32)
    @NotNull
    Rank rank;

    @Column(name="registration_date", nullable = false)
    @CreatedDate
    @NotNull
    Date registrationDate;


    public User() {

    }


    public User(String salt, String email, String password, Rank rank) {
        this.salt = salt;
        this.email = email;
        this.password = password;
        this.rank = rank;
    }

    public Integer getId() {
        return id;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
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

    public Rank getRank() {
        return rank;
    }

    public void setRank(Rank rank) {
        this.rank = rank;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
}
