package com.example.hello_world.web.controller;

import com.example.hello_world.persistence.model.Chapter;
import com.example.hello_world.persistence.model.Course;
import com.example.hello_world.persistence.model.Topic;
import com.example.hello_world.persistence.repository.CourseRepository;
import com.example.hello_world.persistence.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TopicController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TopicRepository topicRepository;


    @PostMapping("/add4")
    public String addTopic() {
        Topic topic = new Topic(0, "01 - System rzymski", "/edu/videos/big.mp4");

        Course course = courseRepository.findById(2).get();
        Chapter chapter = null;
        for (Chapter ch : course.getChapters()) {
            if (ch.getId() == 1) {
                chapter = ch;
                break;
            }
        }

        if (chapter != null) {
            topic.setChapter(chapter);
        }

        topicRepository.save(topic);
        return "Added new topic to repository!";
    }


    @GetMapping("/list4")
    public Iterable<Topic> getTopics() {
        return topicRepository.findAll();
    }

}
