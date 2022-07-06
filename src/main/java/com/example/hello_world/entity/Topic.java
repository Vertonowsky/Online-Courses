package com.example.hello_world.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    Integer id;

    @Column(name = "topic_index")
    @Min(0)
    Integer index;

    @Column(name = "title", nullable = false, length = 100)
    String title;

    @Column(name = "video_location", nullable = false,length = 200)
    String location;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "chapter_id_conn")
    @JsonIgnore
    Chapter chapter;



    public Topic() {

    }


    public Topic(Integer index, String title, String location) {
        this.index = index;
        this.title = title;
        this.location = location;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }
}
