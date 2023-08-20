package com.example.vertonowsky.token.model;

import com.example.vertonowsky.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * This class doesn't have an @Entity annotation, as it won't be persisted in the database by itself.
 *
 *
 * IMPORTANT!!
 * If we're using this strategy, ancestors cannot contain associations with other entities.
 */
@MappedSuperclass
public abstract class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    protected Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    protected User user;

    @Type(type = "uuid-char")
    @Column(nullable = false, length = 64)
    protected UUID token;

    @Column(name = "creation_date", nullable = false)
    protected OffsetDateTime creationDate;

    @Column(name = "expiry_date", nullable = false)
    protected OffsetDateTime expiryDate;

    @Column(nullable = false)
    protected boolean valid = true;


    public Token() {
        OffsetDateTime now = OffsetDateTime.now();
        this.token = UUID.randomUUID();
        this.creationDate = now;
        this.expiryDate = now.plusHours(24);
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UUID getToken() {
        return token;
    }

    public void setToken(UUID token) {
        this.token = token;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(OffsetDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public OffsetDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(OffsetDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Token token1)) return false;
        return valid == token1.valid && id.equals(token1.id) && user.equals(token1.user) && token.equals(token1.token) && creationDate.equals(token1.creationDate) && expiryDate.equals(token1.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, token, creationDate, expiryDate, valid);
    }
}