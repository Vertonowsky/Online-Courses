package com.example.vertonowsky.topic;

import com.example.vertonowsky.topic.model.Topic;

public class TopicSerializer {

    public static TopicDto serialize(Topic topic) {
        TopicDto dto = new TopicDto();
        dto.setId(topic.getId());
        dto.setDuration(topic.getDuration());
        dto.setLocation(topic.getLocation());
        dto.setIndex(topic.getIndex());
        dto.setTitle(topic.getTitle());
        return dto;
    }

    public static TopicDto serialize(Topic topic, TopicStatus status, boolean active) {
        TopicDto dto = serialize(topic);
        dto.setStatus(status.toString().toLowerCase());
        dto.setActive(active);
        return dto;
    }

    public static TopicDto serialize(Topic topic, TopicStatus status, boolean active, boolean blocked) {
        TopicDto dto = serialize(topic, status, active);
        dto.setBlocked(blocked);
        return dto;
    }

}
