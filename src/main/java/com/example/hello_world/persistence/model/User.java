package com.example.hello_world.persistence.model;


import com.example.hello_world.RegistrationMethod;
import com.example.hello_world.security.CustomOidcUser;
import com.example.hello_world.security.CustomUserDetails;
import org.springframework.security.core.Authentication;

import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "users")
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

    @OneToMany(mappedBy = "user")
    private Set<VerificationToken> verificationTokens;


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

    /*

    Added authentication methods. Implemented functions: isLoggedIn(Authentication authentication), getEmail(Authentication authentication),hasAuthority(Authentication authentication, String name)

     */

    public static String getEmail(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails) return ((CustomUserDetails) authentication.getPrincipal()).getEmail();
        if (authentication.getPrincipal() instanceof CustomOidcUser)    return ((CustomOidcUser)    authentication.getPrincipal()).getEmail();

        return null;
    }

    public static boolean isLoggedIn(Authentication authentication) {
        if (authentication == null) return false;
        if (authentication.getPrincipal() instanceof CustomOidcUser) return true;
        if (authentication.getPrincipal() instanceof CustomUserDetails) return true;

        return false;
    }

    public static boolean hasAuthority(Authentication authentication, String name) {
        return (authentication.getAuthorities().stream().anyMatch(usr -> usr.getAuthority().equals(name)));
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

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Date getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
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


    public Set<DiscountCodeUsed> getCodes() {
        return codes;
    }

    public void setCodes(Set<DiscountCodeUsed> codes) {
        this.codes = codes;
    }

    public Set<VerificationToken> getVerificationTokens() {
        return verificationTokens;
    }

    public void setVerificationTokens(Set<VerificationToken> verificationTokens) {
        this.verificationTokens = verificationTokens;
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


    public RegistrationMethod getRegistrationMethod() {
        return registrationMethod;
    }

    public void setRegistrationMethod(RegistrationMethod registrationMethod) {
        this.registrationMethod = registrationMethod;
    }

    public boolean isCourseOwnedAndValid(Course course) {
        for (CourseOwned courseOwned : coursesOwned) {
            if (!courseOwned.getCourse().equals(course)) continue;
            if (courseOwned.getExpiryDate().compareTo(new Date(System.currentTimeMillis())) < 0) continue;
            return true;
        }
        return false;
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
                this.verified == comparing.verified) &&
                this.roles.equals(comparing.roles) &&
                this.registrationDate.equals(comparing.registrationDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, password, verified, roles, registrationDate);
    }


}
