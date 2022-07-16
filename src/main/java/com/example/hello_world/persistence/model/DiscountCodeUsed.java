package com.example.hello_world.persistence.model;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "discount_codes_used")
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
    private Date date;

    @Column(name = "success")
    private boolean success;




    public DiscountCodeUsed(Date date, boolean success) {
        this.date = date;
        this.success = success;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
