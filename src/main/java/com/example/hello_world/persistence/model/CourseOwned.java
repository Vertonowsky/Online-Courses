package com.example.hello_world.persistence.model;


import com.example.hello_world.CourseStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "courses_owned")
public class CourseOwned {

    @EmbeddedId
    @Column(name = "id")
    private CourseKey id = new CourseKey();

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user2;

    @ManyToOne
    @MapsId("courseId")
    @JoinColumn(name = "course_id")
    private Course course2;

    @Column(name = "buy_date")
    private Date buyDate;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CourseStatus status;


    public CourseKey getId() {
        return id;
    }

    public User getUser() {
        return user2;
    }

    public void setUser(User user) {
        this.user2 = user;
    }

    public Course getCourse() {
        return course2;
    }

    public void setCourse(Course course) {
        this.course2 = course;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }


    public CourseOwned(Date buyDate, Date expiryDate, CourseStatus status) {
        this.buyDate = buyDate;
        this.expiryDate = expiryDate;
        this.status = status;
    }

    public CourseOwned() {

    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        CourseOwned comparing = (CourseOwned) o;
        return (this.id.equals(comparing.id) &&
                this.buyDate.equals(comparing.buyDate) &&
                this.expiryDate.equals(comparing.expiryDate) &&
                this.status.equals(comparing.status));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, buyDate, expiryDate, status);
    }

}
