package com.example.vertonowsky.topic;

import com.example.vertonowsky.Regex;
import com.example.vertonowsky.chapter.Chapter;
import com.example.vertonowsky.chapter.ChapterRepository;
import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.course.repository.CourseRepository;
import com.example.vertonowsky.exception.*;
import com.example.vertonowsky.topic.model.FinishedTopic;
import com.example.vertonowsky.topic.model.Topic;
import com.example.vertonowsky.topic.repository.FinishedTopicRepository;
import com.example.vertonowsky.topic.repository.TopicRepository;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserQueryType;
import com.example.vertonowsky.user.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class TopicService {

    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final ChapterRepository chapterRepository;
    private final FinishedTopicRepository finishedTopicRepository;
    private final UserService userService;


    public TopicService(CourseRepository courseRepository, TopicRepository topicRepository, ChapterRepository chapterRepository, FinishedTopicRepository finishedTopicRepository, UserService userService) {
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.chapterRepository = chapterRepository;
        this.finishedTopicRepository = finishedTopicRepository;
        this.userService = userService;
    }

    /**
     *
     * @param topicId id of the topic
     * @return Map of objects: success, type, message
     * @throws TopicNotFoundException thrown when topic with specified id doesn't exist
     * @throws UserNotLoggedInException thrown when user is not logged in
     * @throws CourseNotOwnedException thrown when course with specified id doesn't exist
     */
    public Map<String, Object> markAsFinished(int topicId) throws TopicNotFoundException, UserNotLoggedInException, CourseNotOwnedException {
        if (topicId < 1) throw new TopicNotFoundException("Błąd: Nie odnaleziono tematu.");

        Topic topic = topicRepository.findById(topicId).orElse(null);
        if (topic == null) throw new TopicNotFoundException("Błąd: Nie odnaleziono tematu.");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.get(auth, UserQueryType.FINISHED_TOPICS_AND_COURSES_OWNED);
        if (user == null) throw new UserNotLoggedInException("Błąd: Musisz byc zalogowany aby korzystać z tej funkcji!");

        Course course = courseRepository.findByTopicId(topic.getId()).orElse(null);
        if (course == null || !userService.isCourseValid(user, course)) throw new CourseNotOwnedException("Błąd: Musisz zakupić kurs aby skorzystać z tej opcji!");

        FinishedTopic foundTopic = finishedTopicRepository.findAllWithCondition(user, topic).orElse(null);
        if (foundTopic == null) {
            //TODO IMPORTANT
            FinishedTopic ft = new FinishedTopic(user, topic, OffsetDateTime.now());
            finishedTopicRepository.save(ft);
            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            map.put("type", 1);
            map.put("message", "Sukces: Oznaczono temat jako wykonany!");
            return map;
        }

        finishedTopicRepository.delete(foundTopic);
        Map<String, Object> map = new HashMap<>();
        map.put("success", true);
        map.put("type", 0);
        map.put("message", "Sukces: Odznaczono temat!");
        return map;
    }


    /**
     * Add a new topic to the specified chapter and save it into the database
     *
     * @param chapterId id of the chapter that the topic is inside of
     * @param topicTitle title of the topic
     * @param topicIndex index of the topic
     * @param topicVideo path to the video which will be linked to the topic
     * @param duration duration of the video
     * @throws InvalidTextFormatException thrown when user provides banned characters
     * @throws InvalidDataException thrown when data exceeds the range of allowed limitations
     * @throws ChapterNotFoundException thrown when chapter with specified id doesn't exist
     */
    public void createNewTopic(Integer chapterId, String topicTitle, Integer topicIndex, String topicVideo, Double duration) throws InvalidTextFormatException, InvalidDataException, ChapterNotFoundException {
        Optional<Chapter> chapter = chapterRepository.findById(chapterId);
        if (chapter.isEmpty())
            throw new ChapterNotFoundException("Błąd: Nie odnaleziono podanego rozdziału.");

        if (topicIndex < 0)
            throw new InvalidDataException("Błąd: Indeks nie może być mniejszy niż 0.");

        if (duration < 0.0 || duration > 10000000.0)
            throw new InvalidDataException("Błąd: Nieprawidłowa długość filmu.");

        if (!Regex.POLISH_TEXT_PATTERN.matches(topicTitle) || !Regex.POLISH_TEXT_PATTERN.matches(topicVideo))
            throw new InvalidTextFormatException("Błąd: Nazwa kursu bądź ścieżka video zawiera niedozwolone znaki.");


        Topic topic = new Topic();
        topic.setIndex(topicIndex);
        topic.setTitle(topicTitle);
        topic.setLocation(topicVideo);
        topic.setDuration(duration.intValue());
        topic.setChapter(chapter.get());
        topicRepository.save(topic);
    }


    /**
     * Delete topic with specified id
     *
     * @param topicId id of the topic
     * @throws TopicNotFoundException thrown when topic with specified id doesn't exist
     */
    public void deleteTopic(Integer topicId) throws TopicNotFoundException {
        Optional<Topic> topic = topicRepository.findById(topicId);
        if (topic.isEmpty())
            throw new TopicNotFoundException("Błąd: Nie odnaleziono podanego tematu.");

        topicRepository.delete(topic.get());
    }


    /**
     * Edit topic details
     *
     * @param chapterId id of the chaper that the topic is linked to
     * @param topicId id of the topic
     * @param index new index of the topic
     * @param title new topic's title
     * @param video new path to the video that is linked to the topic
     * @param duration duration of the video
     * @throws ChapterNotFoundException thrown when chapter with specified id doesn't exist
     * @throws TopicNotFoundException thrown when topic with specified id doesn't exist
     * @throws InvalidTextFormatException thrown when user provides banned characters
     * @throws InvalidDataException thrown when data exceeds the range of allowed limitations
     */
    public void editTopic(Integer chapterId, Integer topicId, Integer index, String title, String video, Double duration) throws InvalidTextFormatException, ChapterNotFoundException, InvalidDataException, TopicNotFoundException {
        Optional<Chapter> optionalChapter = chapterRepository.findById(chapterId);
        if (optionalChapter.isEmpty())
            throw new ChapterNotFoundException("Błąd: Nie odnaleziono podanego rozdziału.");

        Optional<Topic> optionalTopic = topicRepository.findById(topicId);
        if (optionalTopic.isEmpty())
            throw new TopicNotFoundException("Błąd: Nie odnaleziono podanego tematu.");

        if (index < 0)
            throw new InvalidDataException("Błąd: Indeks nie może być mniejszy niż 0.");

        if (duration < 0.0 || duration > 10000000.0)
            throw new InvalidDataException("Błąd: Nieprawidłowa długość filmu.");

        if (!Regex.POLISH_TEXT_PATTERN.matches(title) || !Regex.POLISH_TEXT_PATTERN.matches(video))
            throw new InvalidTextFormatException("Błąd: Nazwa tematu bądź ścieżka video zawiera niedozwolone znaki.");


        Topic topic = optionalTopic.get();
        topic.setIndex(index);
        topic.setTitle(title);
        topic.setLocation(video);
        topic.setDuration(duration.intValue());
        topic.setChapter(optionalChapter.get());
        topicRepository.save(topic);
    }


    /**
     *
     * @param videoLocation path to the video
     * @param duration duration of the video
     * @throws InvalidTextFormatException thrown when user provides banned characters
     * @throws InvalidDataException thrown when data exceeds the range of allowed limitations
     */
    public void updateAllTopicsDuration(String videoLocation, Double duration) throws InvalidDataException, InvalidTextFormatException {
        if (duration < 0.0 || duration > 10000000.0)
            throw new InvalidDataException("Błąd: Nieprawidłowa długość filmu.");

        if (!Regex.POLISH_TEXT_PATTERN.matches(videoLocation))
            throw new InvalidTextFormatException("Błąd: Ścieżka video zawiera niedozwolone znaki.");

        List<Topic> topics = topicRepository.findAllByVideoLocation(videoLocation);
        for (Topic topic : topics)
            topic.setDuration(duration.intValue());

        topicRepository.saveAll(topics);

    }


}
