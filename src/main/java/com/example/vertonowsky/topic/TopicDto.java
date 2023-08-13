package com.example.vertonowsky.topic;

public class TopicDto {

    private Integer id;
    private Integer index;
    private String title;
    private String location;
    private Integer duration;
    private String status;
    private boolean active;
    private boolean blocked;


    public TopicDto() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String formatDuration() {
        int hours   = duration/3600;
        int minutes = (duration % 3600) / 60;
        int seconds = (duration % 60);

        if (hours > 0)
            return String.format("%d:%02d:%02d", hours, minutes, seconds);

        return String.format("%02d:%02d", minutes, seconds);
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
