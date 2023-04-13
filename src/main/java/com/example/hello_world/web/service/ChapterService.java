package com.example.hello_world.web.service;

import com.example.hello_world.Regex;
import com.example.hello_world.persistence.model.Chapter;
import com.example.hello_world.persistence.model.Course;
import com.example.hello_world.persistence.repository.ChapterRepository;
import com.example.hello_world.persistence.repository.CourseRepository;
import com.example.hello_world.validation.CourseNotFoundException;
import com.example.hello_world.validation.InvalidTextFormatException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChapterService {

    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;

    public ChapterService(CourseRepository courseRepository, ChapterRepository chapterRepository) {
        this.courseRepository = courseRepository;
        this.chapterRepository = chapterRepository;
    }


    /**
     * Add a new chapter to the specified course and save it into the database
     *
     * @param courseId id of the course
     * @param title title of the chapter
     * @param index index of the chapter
     * @throws CourseNotFoundException thrown when course with specified id doesn't exist
     * @throws InvalidTextFormatException thrown when user provides banned characters
     */
    public void createNewChapter(Integer courseId, String title, Integer index) throws CourseNotFoundException, InvalidTextFormatException {

        Optional<Course> course = courseRepository.findById(courseId);
        if (course.isEmpty())
            throw new CourseNotFoundException("Błąd: Nie odnaleziono podanego kursu.");

        if (!Regex.POLISH_TEXT_PATTERN.matches(title))
            throw new InvalidTextFormatException("Błąd: Podany tutuł zawiera niedozwolone znaki.");


        Chapter chapter = new Chapter();
        chapter.setCourse(course.get());
        chapter.setIndex(index);
        chapter.setTitle(title);
        chapterRepository.save(chapter);

    }
}
