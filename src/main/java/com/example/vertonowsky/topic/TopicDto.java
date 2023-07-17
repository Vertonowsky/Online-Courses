package com.example.vertonowsky.topic;

import com.example.vertonowsky.topic.model.Topic;

public class TopicDto {

    private Topic topic;
    private TopicStatus status;

    private boolean active;

    private boolean blocked;




    public TopicDto(Topic topic, TopicStatus status, boolean active) {
        this.topic = topic;
        this.status = status;
        this.active = active;
    }

    public TopicDto(Topic topic, TopicStatus status, boolean active, boolean blocked) {
        this.topic = topic;
        this.status = status;
        this.active = active;
        this.blocked = blocked;
    }


    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public TopicStatus getStatus() {
        return status;
    }

    public void setStatus(TopicStatus status) {
        this.status = status;
    }

    public String getStatusAsString() {
        return status.toString().toLowerCase();
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isBlocked() {
        return blocked;
    }

    public void setBlocked(boolean blocked) {
        this.blocked = blocked;
    }
}
