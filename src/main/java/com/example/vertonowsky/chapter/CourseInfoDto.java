package com.example.vertonowsky.chapter;


import com.example.vertonowsky.topic.TopicDto;

import java.util.List;

public class CourseInfoDto {


    private List<ChapterDto> chapters;
    private TopicDto selectedTopic;
    private boolean selectedTopicFinished;

    public CourseInfoDto(List<ChapterDto> chapters, TopicDto selectedTopic, boolean selectedTopicFinished) {
        this.chapters = chapters;
        this.selectedTopic = selectedTopic;
        this.selectedTopicFinished = selectedTopicFinished;
    }

    public List<ChapterDto> getChapters() {
        return chapters;
    }

    public void setChapters(List<ChapterDto> chapters) {
        this.chapters = chapters;
    }

    public TopicDto getSelectedTopic() {
        return selectedTopic;
    }

    public void setSelectedTopic(TopicDto selectedTopic) {
        this.selectedTopic = selectedTopic;
    }

    public boolean isSelectedTopicFinished() {
        return selectedTopicFinished;
    }

    public void setSelectedTopicFinished(boolean selectedTopicFinished) {
        this.selectedTopicFinished = selectedTopicFinished;
    }
}
