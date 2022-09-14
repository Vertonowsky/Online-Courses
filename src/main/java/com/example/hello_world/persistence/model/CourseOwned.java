package com.example.hello_world.persistence.model;


import com.example.hello_world.CourseStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "courses_owned")
public class CourseOwned {

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

    @Column(name = "buy_date")
    private Date buyDate;

    @Column(name = "expiry_date")
    private Date expiryDate;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private CourseStatus status;


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
