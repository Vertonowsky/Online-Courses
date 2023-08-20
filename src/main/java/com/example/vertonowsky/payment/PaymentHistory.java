package com.example.vertonowsky.payment;


import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "payment_history")
public class PaymentHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "proceed_date", nullable = false)
    private OffsetDateTime proceedDate;

    @Column(name = "amount", nullable = false)
    private Double amount;

    @Column(name = "currency", nullable = false)
    private String currency;

    @Column(name = "success", nullable = false)
    private boolean success;


    public PaymentHistory() {}

    public PaymentHistory(OffsetDateTime proceedDate, Double amount, String currency, boolean success) {
        this.proceedDate = proceedDate;
        this.amount = amount;
        this.currency = currency;
        this.success = success;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public OffsetDateTime getProceedDate() {
        return proceedDate;
    }

    public void setProceedDate(OffsetDateTime proceedDate) {
        this.proceedDate = proceedDate;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
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

        PaymentHistory comparing = (PaymentHistory) o;
        return (this.id.equals(comparing.id) &&
                this.proceedDate.equals(comparing.proceedDate) &&
                this.amount.equals(comparing.amount) &&
                this.currency.equals(comparing.currency) &&
                this.success == comparing.success);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, proceedDate, amount, currency, success);
    }
}
