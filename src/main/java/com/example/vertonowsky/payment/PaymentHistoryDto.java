package com.example.vertonowsky.payment;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.text.DecimalFormat;
import java.time.OffsetDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PaymentHistoryDto {

    private Double amount;
    private String currency;
    private Integer id;
    private String courseName;
    private OffsetDateTime proceedDate;
    private boolean success;

    public Double getAmount() {
        return amount;
    }

    public String getAmountAsString() {
        if (amount == null) return null;
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(amount);
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public OffsetDateTime getProceedDate() {
        return proceedDate;
    }

    public void setProceedDate(OffsetDateTime proceedDate) {
        this.proceedDate = proceedDate;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
