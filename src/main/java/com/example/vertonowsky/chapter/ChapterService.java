package com.example.vertonowsky.chapter;

import com.example.vertonowsky.Regex;
import com.example.vertonowsky.course.CourseQueryType;
import com.example.vertonowsky.course.CourseService;
import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.exception.ChapterNotFoundException;
import com.example.vertonowsky.exception.CourseNotFoundException;
import com.example.vertonowsky.exception.InvalidTextFormatException;
import com.example.vertonowsky.topic.repository.TopicRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final TopicRepository topicRepository;
    private final CourseService courseService;

    public ChapterService(ChapterRepository chapterRepository, TopicRepository topicRepository, CourseService courseService) {
        this.chapterRepository = chapterRepository;
        this.topicRepository = topicRepository;
        this.courseService = courseService;
    }

    public Chapter get(Integer id) {
        return chapterRepository.findById(id).orElse(null);
    }

    public Chapter getByTopicId(Integer topicId) {
        return chapterRepository.findByTopicId(topicId).orElse(null);
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
        Course course = courseService.get(courseId, CourseQueryType.BASE);
        if (course == null)
            throw new CourseNotFoundException("Błąd: Nie odnaleziono podanego kursu.");

        if (!Regex.POLISH_TEXT_PATTERN.matches(title))
            throw new InvalidTextFormatException("Błąd: Podany tutuł zawiera niedozwolone znaki.");


        Chapter chapter = new Chapter();
        chapter.setCourse(course);
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
        Chapter chapter = get(chapterId);
        if (chapter == null)
            throw new ChapterNotFoundException("Błąd: Nie odnaleziono podanego rozdziału.");

        topicRepository.deleteAll(chapter.getTopics());
        chapter.setTopics(new HashSet<>());
        chapterRepository.delete(chapter);
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
        Chapter chapter = get(chapterId);
        if (chapter == null)
            throw new ChapterNotFoundException("Błąd: Nie odnaleziono podanego rozdziału.");

        if (!Regex.POLISH_TEXT_PATTERN.matches(title))
            throw new InvalidTextFormatException("Błąd: Podany tutuł zawiera niedozwolone znaki.");


        chapter.setTitle(title);
        chapter.setIndex(index);
        chapterRepository.save(chapter);
    }

}
