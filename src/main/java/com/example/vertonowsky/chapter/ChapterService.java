package com.example.vertonowsky.chapter;

import com.example.vertonowsky.Regex;
import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.course.repository.CourseRepository;
import com.example.vertonowsky.exception.ChapterNotFoundException;
import com.example.vertonowsky.exception.CourseNotFoundException;
import com.example.vertonowsky.exception.InvalidTextFormatException;
import com.example.vertonowsky.exception.TopicNotFoundException;
import com.example.vertonowsky.topic.TopicDto;
import com.example.vertonowsky.topic.TopicSerializer;
import com.example.vertonowsky.topic.TopicStatus;
import com.example.vertonowsky.topic.model.Topic;
import com.example.vertonowsky.topic.repository.TopicRepository;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
public class ChapterService {

    private final TopicRepository topicRepository;
    private final ChapterRepository chapterRepository;
    private final CourseRepository courseRepository;
    private final UserService userService;


    public ChapterService(TopicRepository topicRepository, ChapterRepository chapterRepository, CourseRepository courseRepository, UserService userService) {
        this.topicRepository = topicRepository;
        this.chapterRepository = chapterRepository;
        this.courseRepository = courseRepository;
        this.userService = userService;
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






    public CourseInfoDto listAllChapters(User user, Course course, Integer topicId) throws TopicNotFoundException {
        Topic selectedTopic = (topicId == null ? course.getFirstTopic() : course.getTopicById(topicId));
        if (selectedTopic == null) return null;


        List<ChapterDto> chapters = new LinkedList<>();
        int counter = 0;
        boolean selectedTopicFinished = false; //mark if active topic has been finished
        for (Chapter ch : course.getChapters()) {
            List<TopicDto> topics = new LinkedList<>();
            for (Topic t : ch.getTopics()) {

                boolean blocked = false;  //refers to current topic status (is it blocked?)
                if (user != null) {
                    if (userService.isCourseValid(user, course)) {
                        TopicStatus status = TopicStatus.AVAILABLE;
                        if (user.isTopicFinished(t.getId()))
                            status = TopicStatus.FINISHED;

                        if (t.equals(selectedTopic)) {
                            topics.add(TopicSerializer.serialize(t, status, true));  // true as last parameter means its active topic
                            if (status == TopicStatus.FINISHED)
                                selectedTopicFinished = true;
                        } else
                            topics.add(TopicSerializer.serialize(t, status, false)); // false means it's not currently active topic
                    }
                }

                if (user == null || (!userService.isCourseValid(user, course))) {

                    if (counter < 3) {
                        if (t.equals(selectedTopic))
                            topics.add(TopicSerializer.serialize(t, TopicStatus.AVAILABLE, true));
                        else
                            topics.add(TopicSerializer.serialize(t, TopicStatus.AVAILABLE, false));

                    } else {
                        topics.add(TopicSerializer.serialize(t, TopicStatus.BLOCKED, false, true));
                        blocked = true;
                    }

                }
                counter++;

                //Check if user has permission to access specified topic
                if (t.equals(selectedTopic)) {
                    if (blocked) return null;
                }
            }
            ChapterDto chapterDto = ChapterSerializer.serialize(ch);
            chapterDto.setTopics(topics);
            chapters.add(chapterDto);
        }


        return new CourseInfoDto(chapters, TopicSerializer.serialize(selectedTopic), selectedTopicFinished);
    }


}
