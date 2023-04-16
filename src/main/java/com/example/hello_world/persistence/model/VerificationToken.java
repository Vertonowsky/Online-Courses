package com.example.hello_world.persistence.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "verification_tokens")
public class VerificationToken {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="token_id")
    private Integer id;


    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;


    @Type(type="uuid-char")
    @Column(nullable = false, length = 64)
    private UUID token;


    @Column(name = "creation_date", nullable = false)
    private Date creationDate;

    @Column(name = "expiry_date", nullable = false)
    private Date expiryDate;


    @Column(nullable = false)
    private boolean valid = true;




    public VerificationToken(Date creationDate) {
        this.creationDate = creationDate;
        this.token        = UUID.randomUUID();

        //Calculate expiryDate = creationDate + 24 hours
        Date now = new Date(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(now);
        calendar.add(Calendar.HOUR_OF_DAY, 24);

        this.expiryDate = calendar.getTime();

    }

    public VerificationToken() {

    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public UUID getToken() {
        return token;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
