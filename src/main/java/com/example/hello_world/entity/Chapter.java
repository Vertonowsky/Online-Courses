package com.example.hello_world.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "chapters")
public class Chapter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chapter_id")
    Integer id;

    @Column(name = "chapter_index")
    @Min(0)
    Integer chapterIndex;

    @Column(length = 100)
    String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id_conn")
    @JsonIgnore
    Course course;


    public Chapter() {

    }

    public Chapter(Integer chapterIndex, String title) {
        this.chapterIndex = chapterIndex;
        this.title = title;
    }


    public Integer getId() {
        return id;
    }

    public Integer getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(Integer chapterIndex) {
        this.chapterIndex = chapterIndex;
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
}
