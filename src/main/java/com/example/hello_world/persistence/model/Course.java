package com.example.hello_world.persistence.model;


import com.example.hello_world.Category;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.text.DecimalFormat;
import java.util.Set;

@Entity
@Table(name = "courses")
public class Course {

    private static final DecimalFormat df = new DecimalFormat("0.00");

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
    private Category category;

    @Column(name = "advantages", nullable = false, length = 600)
    private String advantages;

    @Column(name = "price", nullable = false, precision = 2)
    private Double price;

    @Column(name = "price_promotion", nullable = false, precision = 2)
    private Double pricePromotion;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "course")
    @OrderBy("index ASC, title ASC")
    private Set<Chapter> chapters;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private Set<CourseOwned> coursesOwned;

    @OneToMany(mappedBy = "course")
    @JsonIgnore
    private Set<PaymentHistory> paymentHistories;





    public Course() {}

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

    public String getCategoryAsString() {
        String s = String.valueOf(category).toLowerCase();
        return StringUtils.capitalize(s);
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

    public String getPriceAsString() {
        return df.format(price);
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getPricePromotion() {
        return pricePromotion;
    }

    public String getPricePromotionAsString() {
        return df.format(pricePromotion);
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

    public Set<CourseOwned> getCoursesOwned() {
        return coursesOwned;
    }

    public void setCoursesOwned(Set<CourseOwned> coursesOwned) {
        this.coursesOwned = coursesOwned;
    }

    public Chapter getChapterById(Integer id) {
        for(Chapter chapter : chapters) {
                if (chapter.getId().equals(id)) return chapter;
        }
        return null;
    }

    public Topic getTopicById(Integer id) {
        for(Chapter chapter : chapters) {
            for(Topic topic : chapter.getTopics())
                if (topic.getId().equals(id)) return topic;
        }
        return null;
    }

    public Topic getFirstTopic() {
        for (Chapter chapter : chapters) {
            for(Topic topic : chapter.getTopics())
                return topic;
        }
        return null;
    }
}
