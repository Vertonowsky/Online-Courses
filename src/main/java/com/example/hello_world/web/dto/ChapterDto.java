package com.example.hello_world.web.dto;

import com.example.hello_world.persistence.model.Chapter;

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
