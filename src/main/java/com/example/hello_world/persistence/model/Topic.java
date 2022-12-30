package com.example.hello_world.persistence.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "topic_id")
    private Integer id;

    @Column(name = "topic_index")
    private Integer index;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "video_location", nullable = false, length = 200)
    private String location;

    @Column(name = "duration", nullable = false)
    private Integer duration;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "chapter_id_conn")
    @JsonIgnore
    private Chapter chapter;


    @OneToMany(mappedBy = "topic")
    @JsonIgnore
    private Set<FinishedTopic> finishedTopics;



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

    public Set<FinishedTopic> getFinishedTopics() {
        return finishedTopics;
    }

    public void setFinishedTopics(Set<FinishedTopic> finishedTopics) {
        this.finishedTopics = finishedTopics;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String formatDuration() {
        int hours   = duration/3600;
        int minutes = (duration % 3600) / 60;
        int seconds = (duration % 60);

        if (hours > 0)
            return String.format("%d:%02d:%02d", hours, minutes, seconds);

        return String.format("%02d:%02d", minutes, seconds);
    }


}
