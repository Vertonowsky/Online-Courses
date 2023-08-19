package com.example.vertonowsky.user;

import com.example.vertonowsky.avatar.AvatarDto;
import com.example.vertonowsky.course.CourseDto;
import com.example.vertonowsky.payment.PaymentHistoryDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto {

    private AvatarDto avatar;
    private List<CourseDto> courses;
    private String email;
    private List<PaymentHistoryDto> paymentHistories;

    public AvatarDto getAvatar() {
        return avatar;
    }

    public void setAvatar(AvatarDto avatar) {
        this.avatar = avatar;
    }

    public List<CourseDto> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseDto> courses) {
        this.courses = courses;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<PaymentHistoryDto> getPaymentHistories() {
        return paymentHistories;
    }

    public void setPaymentHistories(List<PaymentHistoryDto> paymentHistories) {
        this.paymentHistories = paymentHistories;
    }
}
