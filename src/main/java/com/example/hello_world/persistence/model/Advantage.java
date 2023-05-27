package com.example.hello_world.persistence.model;

import com.example.hello_world.AdvantageType;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "advantages")
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




    public Advantage() {

    }

    public Advantage(Integer index, String title, boolean premium, AdvantageType advantageType, Course course) {
        this.index = index;
        this.title = title;
        this.premium = premium;
        this.advantageType = advantageType;
        this.course = course;
    }

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
}
