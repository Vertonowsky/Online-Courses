package com.example.vertonowsky.chapter;

import com.example.vertonowsky.topic.TopicDto;

import java.util.List;

public class ChapterDto {


    Chapter chapter;
    List<TopicDto> topicDtoList;

    public ChapterDto(List<TopicDto> topicDtoList, Chapter chapter) {
        this.topicDtoList = topicDtoList;
        this.chapter = chapter;
    }



    public Chapter getChapter() {
        return chapter;
    }

    public void setChapter(Chapter chapter) {
        this.chapter = chapter;
    }

    public List<TopicDto> getTopicDtoList() {
        return topicDtoList;
    }

    public void setTopicDtoList(List<TopicDto> topicDtoList) {
        this.topicDtoList = topicDtoList;
    }
}
