package com.example.hello_world.entity;


import com.example.hello_world.Category;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "course_id")
    Integer id;

    @Column(name = "type", nullable = false, length = 100)
    String type;

    @Column(name = "name", nullable = false, length = 100)
    String name;

    @Column(name = "description", nullable = false, length = 400)
    String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    Category category;

    @Column(name = "advantages", nullable = false, length = 600)
    String advantages;

    @Column(name = "price", nullable = false, precision = 2)
    Double price;

    @Column(name = "price_promotion", nullable = false, precision = 2)
    Double pricePromotion;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "course")
    Set<Chapter> chapters;


    public Course() {

    }

    public Course(String type, String name, String description, Category category, String advantages, Double price, Double pricePromotion) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.category = category;
        this.advantages = advantages;
        this.price = price;
        this.pricePromotion = pricePromotion;
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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getAdvantages() {
        return advantages;
    }

    public void setAdvantages(String advantages) {
        this.advantages = advantages;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPricePromotion() {
        return pricePromotion;
    }

    public void setPricePromotion(Double pricPromotion) {
        this.pricePromotion = pricePromotion;
    }

    public Set<Chapter> getChapters() {
        return chapters;
    }

    public void setChapters(Set<Chapter> chapters) {
        this.chapters = chapters;
    }
}
