package com.example.vertonowsky.advantage;

import com.example.vertonowsky.course.model.Course;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Advantage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advantage_id")
    private Integer id;

    @Column(name = "advantage_index")
    private Integer index;

    @Column(length = 150)
    private String title;

    private boolean premium;

    @Column(name = "type", columnDefinition = "varchar(30) default 'DEFAULT'")
    @Enumerated(EnumType.STRING)
    private AdvantageType advantageType;

    @ManyToOne
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;


    public Integer getId() {
        return id;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isPremium() {
        return premium;
    }

    public void setPremium(boolean premium) {
        this.premium = premium;
    }

    public AdvantageType getAdvantageType() {
        return advantageType;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Advantage advantage = (Advantage) o;
        return premium == advantage.premium && Objects.equals(index, advantage.index) && Objects.equals(title, advantage.title) && advantageType == advantage.advantageType && Objects.equals(course, advantage.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, title, premium, advantageType, course);
    }
}
