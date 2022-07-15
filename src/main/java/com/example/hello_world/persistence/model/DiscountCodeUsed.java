package com.example.hello_world.persistence.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "discount_codes_used")
public class DiscountCodeUsed {

    @EmbeddedId
    @Column(name = "id")
    DiscountCodeKey id = new DiscountCodeKey();


    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    User user;


    @ManyToOne
    @MapsId("codeId")
    @JoinColumn(name = "code_id")

    DiscountCode discountCode;


    @Column(name = "date")
    private Date date;


    public DiscountCodeKey getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public DiscountCode getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(DiscountCode discountCode) {
        this.discountCode = discountCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public DiscountCodeUsed(Date date) {
        this.date = date;
    }

    public DiscountCodeUsed() {

    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        DiscountCodeUsed comparing = (DiscountCodeUsed) o;
        return (this.id.equals(comparing.id) &&
                this.date.equals(comparing.date));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date);
    }
}
