package com.example.hello_world.controller;

import com.example.hello_world.entity.Chapter;
import com.example.hello_world.entity.Course;
import com.example.hello_world.repository.ChapterRepository;
import com.example.hello_world.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChapterController {


    @Autowired
    private ChapterRepository chapterRepository;

    @Autowired
    private CourseRepository courseRepository;


    @PostMapping("/add3")
    public String addChapter() {
        Chapter chapter = new Chapter(0, "Rozdział 01 - Liczby i działania");

        Course course = courseRepository.findById(2).get();
        chapter.setCourse(course);

        chapterRepository.save(chapter);
        return "Added new chapter to repository!";
    }


    @GetMapping("/list3")
    public Iterable<Chapter> getChapters() {
        return chapterRepository.findAll();
    }

}
