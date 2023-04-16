package com.example.hello_world.web.service;

import com.example.hello_world.Regex;
import com.example.hello_world.persistence.model.Chapter;
import com.example.hello_world.persistence.model.Course;
import com.example.hello_world.persistence.repository.ChapterRepository;
import com.example.hello_world.persistence.repository.CourseRepository;
import com.example.hello_world.persistence.repository.TopicRepository;
import com.example.hello_world.validation.ChapterNotFoundException;
import com.example.hello_world.validation.CourseNotFoundException;
import com.example.hello_world.validation.InvalidTextFormatException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChapterService {

    private final TopicRepository topicRepository;
    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;


    public ChapterService(TopicRepository topicRepository, ChapterRepository chapterRepository, CourseRepository courseRepository) {
        this.topicRepository = topicRepository;
        this.chapterRepository = chapterRepository;
        this.courseRepository = courseRepository;
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


    /**
     * Delete chapter with specified id
     *
     * @param chapterId id of the topic
     * @throws ChapterNotFoundException thrown when chapter with specified id doesn't exist
     */
    public void deleteChapter(Integer chapterId) throws ChapterNotFoundException {
        Optional<Chapter> chapter = chapterRepository.findById(chapterId);
        if (chapter.isEmpty())
            throw new ChapterNotFoundException("Błąd: Nie odnaleziono podanego rozdziału.");

        topicRepository.deleteAll(chapter.get().getTopics());
        chapterRepository.delete(chapter.get());
    }


    /**
     * Edit chapter details
     *
     * @param chapterId id of the chapter
     * @param index index of the chapter
     * @param title new chapter's title
     * @throws InvalidTextFormatException thrown when user provides banned characters
     * @throws ChapterNotFoundException thrown when chapter with specified id doesn't exist
     */
    public void editChapter(Integer chapterId, Integer index, String title) throws InvalidTextFormatException, ChapterNotFoundException {
        Optional<Chapter> optionalChapter = chapterRepository.findById(chapterId);
        if (optionalChapter.isEmpty())
            throw new ChapterNotFoundException("Błąd: Nie odnaleziono podanego rozdziału.");

        if (!Regex.POLISH_TEXT_PATTERN.matches(title))
            throw new InvalidTextFormatException("Błąd: Podany tutuł zawiera niedozwolone znaki.");


        Chapter chapter = optionalChapter.get();
        chapter.setTitle(title);
        chapter.setIndex(index);
        chapterRepository.save(chapter);
    }




}
