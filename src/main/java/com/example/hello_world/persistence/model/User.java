package com.example.hello_world.persistence.model;


import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
public class User {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Integer id;

    //@Column(name="salt", nullable = false, length = 32)
    //String salt;

    @Column(nullable = false, length = 60)
    private String email;

    @Column(nullable = false, length = 64)
    private String password;


    private boolean active;


    private String roles;

    //@Column(nullable = false, length = 32)
    //Rank rank;

    @Column(name="registration_date", nullable = false)
    private Date registrationDate;


    @OneToMany(mappedBy = "user")
    private Set<DiscountCodeUsed> codes;

    @OneToMany(mappedBy = "user")
    private Set<CourseOwned> coursesOwned;

    @OneToMany(mappedBy = "user")
    @OrderBy("proceedDate DESC")
    private Set<PaymentHistory> paymentHistories;

    @OneToMany(mappedBy = "user")
    private Set<FinishedTopic> finishedTopics;



    public User() {

    }


    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }


    public Set<DiscountCodeUsed> getCodes() {
        return codes;
    }

    public void setCodes(Set<DiscountCodeUsed> codes) {
        this.codes = codes;
    }

    public List<CourseOwned> getCoursesOwned() {
        List<CourseOwned> arr = new ArrayList<>(coursesOwned);

        //Sort owned courses by expiry date descending
        Collections.sort(arr, new Comparator<CourseOwned>() {
            @Override
            public int compare(CourseOwned co1, CourseOwned co2) {
                return co2.getExpiryDate().compareTo(co1.getExpiryDate());
            }
        });

        return arr;
    }

    public void setCoursesOwned(Set<CourseOwned> coursesOwned) {
        this.coursesOwned = coursesOwned;
    }

    public Set<PaymentHistory> getPaymentHistories() {
        return paymentHistories;
    }

    public void setPaymentHistories(Set<PaymentHistory> paymentHistories) {
        this.paymentHistories = paymentHistories;
    }


    public Set<FinishedTopic> getFinishedTopics() {
        return finishedTopics;
    }

    public void setFinishedTopics(Set<FinishedTopic> finishedTopics) {
        this.finishedTopics = finishedTopics;
    }


    public Course getCourseIfOwnedById(Integer id) {
        for (CourseOwned item : coursesOwned) {
            if (item.getCourse().getId().equals(id)) {
                return item.getCourse();
            }
        }
        return null;
    }


    public boolean isTopicFinished(Integer topicId) {
        for (FinishedTopic ft : finishedTopics) {
            if (ft.getTopic().getId().equals((topicId)))
                return true;
        }
        return false;
    }







    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        User comparing = (User) o;
        return (this.id.equals(comparing.id) &&
                this.email.equals(comparing.email) &&
                this.password.equals(comparing.password) &&
                this.active == comparing.active) &&
                this.roles.equals(comparing.roles) &&
                this.registrationDate.equals(comparing.registrationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, active, roles, registrationDate);
    }


}
