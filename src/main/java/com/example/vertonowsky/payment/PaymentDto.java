package com.example.vertonowsky.payment;

import com.example.vertonowsky.course.CourseDto;
import com.example.vertonowsky.discount.DiscountCodeDto;
import com.example.vertonowsky.user.UserInfoDto;

public class PaymentDto {

    private UserInfoDto user;
    private CourseDto course;
    private DiscountCodeDto discountCode;
    private Double discount;
    private Double newPrice;


    public UserInfoDto getUser() {
        return user;
    }

    public void setUser(UserInfoDto user) {
        this.user = user;
    }

    public CourseDto getCourse() {
        return course;
    }

    public void setCourse(CourseDto course) {
        this.course = course;
    }

    public DiscountCodeDto getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(DiscountCodeDto discountCode) {
        this.discountCode = discountCode;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Double getNewPrice() {
        return newPrice;
    }

    public void setNewPrice(Double newPrice) {
        this.newPrice = newPrice;
    }
}
