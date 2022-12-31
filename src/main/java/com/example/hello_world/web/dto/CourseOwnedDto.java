package com.example.hello_world.web.dto;

import com.example.hello_world.persistence.model.Course;

import java.util.Date;

public class CourseOwnedDto {

    private Course course;

    private Date expiryDate;

    private int finishedTopics;

    private int percentage;

    public CourseOwnedDto(Course course, Date expiryDate) {
        this.course = course;
        this.expiryDate = expiryDate;
    }

    public CourseOwnedDto(Course course, Date expiryDate, int finishedTopics, int percentage) {
        this.course = course;
        this.expiryDate = expiryDate;
        this.finishedTopics = finishedTopics;
        this.percentage = percentage;
    }


    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getFinishedTopics() {
        return finishedTopics;
    }

    public void setFinishedTopics(int finishedTopics) {
        this.finishedTopics = finishedTopics;
    }

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
}
