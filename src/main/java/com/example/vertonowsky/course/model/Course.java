package com.example.vertonowsky.course.model;


import com.example.vertonowsky.advantage.Advantage;
import com.example.vertonowsky.advantage.AdvantageType;
import com.example.vertonowsky.chapter.Chapter;
import com.example.vertonowsky.course.Category;
import com.example.vertonowsky.payment.PaymentHistory;
import com.example.vertonowsky.topic.model.Topic;
import com.example.vertonowsky.user.User;
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

    @OneToMany(mappedBy = "course")
    @OrderBy("index ASC")
    private Set<Advantage> advantages;





    public Course() {}

    public Course(String type, String name, String description, Category category, Double price, Double pricePromotion, Set<Chapter> chapters, Set<CourseOwned> coursesOwned, Set<PaymentHistory> paymentHistories, Set<Advantage> advantages) {
        this.type = type;
        this.name = name;
        this.description = description;
        this.category = category;
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

    public Set<PaymentHistory> getPaymentHistories() {
        return paymentHistories;
    }

    public void setPaymentHistories(Set<PaymentHistory> paymentHistories) {
        this.paymentHistories = paymentHistories;
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

    public int getTopicCount() {
        int suma = 0;
        for (Chapter chapter : chapters)
            suma += chapter.getTopics().size();
        return suma;
    }

    // Returns true if user has ever bought this course. Otherwise it returns false
    public boolean isOwnedByUser(User user) {
        for(CourseOwned co : user.getCoursesOwned()) {
            if (co.getId().equals(this.id))
                return true;
        }
        return false;
    }




    public Set<Advantage> getAdvantages() {

        for (Advantage advantage : advantages) {
            if (advantage.getAdvantageType().equals(AdvantageType.TOTAL_DURATION)) {

                int totalDuration = getTotalDuration();
                String suffix = " godzina";
                if (totalDuration > 1 && totalDuration < 5) suffix = " godziny";
                if (totalDuration >= 5) suffix = " godzin";

                String title = advantage.getTitle();
                title = title.replace("%ss%", String.format("%d %s", totalDuration, suffix));
                advantage.setTitle(title);

            }

            // TODO
            if (advantage.getAdvantageType().equals(AdvantageType.TASKS_COUNT)) {

                int tasksCount = 100;
                String suffix = " zadanie";
                if (tasksCount > 1 && tasksCount < 5) suffix = " zadania";
                if (tasksCount >= 5) suffix = " zadań";

                String title = advantage.getTitle();
                title = title.replace("%ss%", String.format("%d %s", tasksCount, suffix));
                advantage.setTitle(title);

            }

        }

        return advantages;
    }


    public int getTotalDuration() {
        long totalDuration = 0;
        for (Chapter chapter : this.chapters) {
            for (Topic topic : chapter.getTopics()) {

                totalDuration += topic.getDuration() == null ? 0 : topic.getDuration();

            }
        }

        return (int)(totalDuration/3600);
    }

}
