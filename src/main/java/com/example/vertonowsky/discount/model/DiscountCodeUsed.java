package com.example.vertonowsky.discount.model;


import com.example.vertonowsky.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "discount_code_used")
public class DiscountCodeUsed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "code_id")
    private DiscountCode discountCode;

    @Column(name = "date")
    private OffsetDateTime date;




    public DiscountCodeUsed(OffsetDateTime date) {
        this.date = date;
    }

    public DiscountCodeUsed() {

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

    public DiscountCode getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(DiscountCode discountCode) {
        this.discountCode = discountCode;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
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
