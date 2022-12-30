package com.example.hello_world.web.controller;

import com.example.hello_world.persistence.model.Topic;
import com.example.hello_world.persistence.repository.CourseRepository;
import com.example.hello_world.persistence.repository.TopicRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TopicController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TopicRepository topicRepository;


    @GetMapping("/list4")
    public Iterable<Topic> getTopics() {
        return topicRepository.findAll();
    }

}
