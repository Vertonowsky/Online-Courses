package com.example.vertonowsky.topic.model;


import com.example.vertonowsky.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "finished_topic")
public class FinishedTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operation_id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "date", nullable = false)
    private OffsetDateTime date;





    public FinishedTopic() {}

    public FinishedTopic(User user, Topic topic, OffsetDateTime date) {
        this.user = user;
        this.topic = topic;
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public OffsetDateTime getDate() {
        return date;
    }

    public void setDate(OffsetDateTime date) {
        this.date = date;
    }



    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        FinishedTopic comparing = (FinishedTopic) o;
        return (this.id.equals(comparing.id) &&
                this.date.equals(comparing.date));
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, date);
    }
}
