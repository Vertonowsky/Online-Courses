package com.example.vertonowsky.course.model;


import com.example.vertonowsky.advantage.Advantage;
import com.example.vertonowsky.chapter.Chapter;
import com.example.vertonowsky.course.CategoryType;
import com.example.vertonowsky.payment.PaymentHistory;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Course {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    private Integer id;

    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false, length = 400)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private CategoryType categoryType;

    @Column(name = "price", nullable = false, precision = 2)
    private Double price;

    @Column(name = "price_promotion", nullable = false, precision = 2)
    private Double pricePromotion;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @OrderBy("index ASC, title ASC")
    private Set<Chapter> chapters;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    private Set<CourseOwned> coursesOwned;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    private Set<PaymentHistory> paymentHistories;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "course")
    @OrderBy("index ASC")
    private Set<Advantage> advantages;



    public Course() {}

    public Course(String type, String name, String description, CategoryType categoryType, Double price, Double pricePromotion, Set<Chapter> chapters, Set<CourseOwned> coursesOwned, Set<PaymentHistory> paymentHistories, Set<Advantage> advantages) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.categoryType = categoryType;
        this.price = price;
        this.pricePromotion = pricePromotion;
        this.chapters = chapters;
        this.coursesOwned = coursesOwned;
        this.paymentHistories = paymentHistories;
        this.advantages = advantages;
    }



    public Integer getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CategoryType getCategoryType() {
        return categoryType;
    }

    public String getCategoryAsString() {
        String s = String.valueOf(categoryType).toLowerCase();
        return StringUtils.capitalize(s);
    }

    public void setCategory(CategoryType categoryType) {
        this.categoryType = categoryType;
    }

    public Set<PaymentHistory> getPaymentHistories() {
        return paymentHistories;
    }

    public void setPaymentHistories(Set<PaymentHistory> paymentHistories) {
        this.paymentHistories = paymentHistories;
    }

    public Double getPrice() {
        return price;
    }

//    public String getPriceAsString() {
//        return df.format(price);
//    }
//
//    public String getPricePromotionAsString() {
//        return df.format(pricePromotion);
//    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPricePromotion() {
        return pricePromotion;
    }

    public void setPricePromotion(Double pricePromotion) {
        this.pricePromotion = pricePromotion;
    }

    public Set<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(Set<Chapter> chapters) {
        this.chapters = chapters;
    }

    public Set<CourseOwned> getCoursesOwned() {
        return coursesOwned;
    }

    public void setCoursesOwned(Set<CourseOwned> coursesOwned) {
        this.coursesOwned = coursesOwned;
    }

    public Set<Advantage> getAdvantages() {
        return advantages;
    }

    public void setAdvantages(Set<Advantage> advantages) {
        this.advantages = advantages;
    }
}
