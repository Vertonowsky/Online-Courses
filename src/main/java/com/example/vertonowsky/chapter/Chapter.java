package com.example.vertonowsky.chapter;


import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.topic.model.Topic;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.Set;

@Entity
@Table(name = "chapters")
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_id")
    private Integer id;

    @Column(name = "chapter_index")
    @Min(0)
    private Integer index;

    @Column(length = 100)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    @JsonIgnore
    private Course course;

    @OneToMany(fetch=FetchType.LAZY, mappedBy = "chapter")
    @OrderBy("index ASC, title ASC")
    private Set<Topic> topics;


    public Chapter() {

    }

    public Chapter(Integer index, String title) {
        this.index = index;
        this.title = title;
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

    public Course getCourse() {
        return this.course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Set<Topic> getTopics() {
        return topics;
    }

    public void setTopics(Set<Topic> topics) {
        this.topics = topics;
    }


}
