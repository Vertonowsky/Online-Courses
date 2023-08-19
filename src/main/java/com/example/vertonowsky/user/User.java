package com.example.vertonowsky.user;


import com.example.vertonowsky.avatar.Avatar;
import com.example.vertonowsky.course.model.CourseOwned;
import com.example.vertonowsky.discount.model.DiscountCodeUsed;
import com.example.vertonowsky.payment.PaymentHistory;
import com.example.vertonowsky.role.Role;
import com.example.vertonowsky.security.RegistrationMethod;
import com.example.vertonowsky.token.model.PasswordRecoveryToken;
import com.example.vertonowsky.token.model.VerificationToken;
import com.example.vertonowsky.topic.model.FinishedTopic;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;
import java.util.Set;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Integer id;

    @Column(nullable = false, length = 32, columnDefinition = "varchar(32) default 'DEFAULT'")
    @Enumerated(value = EnumType.STRING)
    private RegistrationMethod registrationMethod = RegistrationMethod.DEFAULT;

    @Column(nullable = false, length = 60)
    private String email;

    @Column(length = 64)
    private String password;


    @Column(nullable = false)
    private boolean verified;


    //@Column(nullable = false, length = 32)
    //Rank rank;

    @Column(name="registration_date", nullable = false)
    private OffsetDateTime registrationDate;


    @OneToMany(mappedBy = "user")
    private Set<DiscountCodeUsed> codes;

    @OneToMany(mappedBy = "user")
    private Set<CourseOwned> coursesOwned;

    @OneToMany(mappedBy = "user")
    @OrderBy("proceedDate DESC")
    private Set<PaymentHistory> paymentHistories;

    @OneToMany(mappedBy = "user")
    private Set<FinishedTopic> finishedTopics;

    @OneToMany(mappedBy = "user")
    private Set<VerificationToken> verificationTokens;

    @OneToMany(mappedBy = "user")
    private Set<PasswordRecoveryToken> passwordRecoveryTokens;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @ManyToOne
    @JoinColumn(name = "avatar_id")
    private Avatar avatar;



    public User() {

    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, RegistrationMethod registrationMethod) {
        this.email = email;
        this.password = null;
        this.registrationMethod = registrationMethod;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public RegistrationMethod getRegistrationMethod() {
        return registrationMethod;
    }

    public void setRegistrationMethod(RegistrationMethod registrationMethod) {
        this.registrationMethod = registrationMethod;
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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public OffsetDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(OffsetDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Set<DiscountCodeUsed> getCodes() {
        return codes;
    }

    public void setCodes(Set<DiscountCodeUsed> codes) {
        this.codes = codes;
    }

    public Set<CourseOwned> getCoursesOwned() {
        return coursesOwned;
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

    public Set<VerificationToken> getVerificationTokens() {
        return verificationTokens;
    }

    public void setVerificationTokens(Set<VerificationToken> verificationTokens) {
        this.verificationTokens = verificationTokens;
    }

    public Set<PasswordRecoveryToken> getPasswordRecoveryTokens() {
        return passwordRecoveryTokens;
    }

    public void setPasswordRecoveryTokens(Set<PasswordRecoveryToken> passwordRecoveryTokens) {
        this.passwordRecoveryTokens = passwordRecoveryTokens;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Avatar getAvatar() {
        return avatar;
    }

    public void setAvatar(Avatar avatar) {
        this.avatar = avatar;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        User comparing = (User) o;
        return (this.id.equals(comparing.id) &&
                this.email.equals(comparing.email) &&
                this.password.equals(comparing.password) &&
                this.verified == comparing.verified) &&
                this.registrationDate.equals(comparing.registrationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, verified, registrationDate);
    }


}
