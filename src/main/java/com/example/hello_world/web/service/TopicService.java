package com.example.hello_world.web.service;

import com.example.hello_world.Regex;
import com.example.hello_world.persistence.model.*;
import com.example.hello_world.persistence.repository.ChapterRepository;
import com.example.hello_world.persistence.repository.FinishedTopicRepository;
import com.example.hello_world.persistence.repository.TopicRepository;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.validation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TopicService {


    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final ChapterRepository chapterRepository;
    private final FinishedTopicRepository finishedTopicRepository;


    public TopicService(UserRepository userRepository, TopicRepository topicRepository, ChapterRepository chapterRepository, FinishedTopicRepository finishedTopicRepository) {
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.chapterRepository = chapterRepository;
        this.finishedTopicRepository = finishedTopicRepository;
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

        Optional<Topic> optionalTopic = topicRepository.findById(topicId);
        if (optionalTopic.isEmpty()) throw new TopicNotFoundException("Błąd: Nie odnaleziono tematu.");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (!User.isLoggedIn(auth) || userRepository.findByEmail(User.getEmail(auth)).isEmpty()) throw new UserNotLoggedInException("Błąd: Musisz byc zalogowany aby korzystać z tej funkcji!");

        User user = userRepository.findByEmail(User.getEmail(auth)).get();
        Course c = optionalTopic.get().getChapter().getCourse();
        if (!user.isCourseOwnedAndValid(c)) throw new CourseNotOwnedException("Błąd: Musisz zakupić kurs aby skorzystać z tej opcji!");

        Optional<FinishedTopic> foundTopic = finishedTopicRepository.findAllWithCondition(user, optionalTopic.get());
        if (foundTopic.isEmpty()) {
            FinishedTopic ft = new FinishedTopic(user, optionalTopic.get(), new Date(System.currentTimeMillis()));
            finishedTopicRepository.save(ft);
            Map<String, Object> map = new HashMap<>();
            map.put("success", true);
            map.put("type", 1);
            map.put("message", "Sukces: Oznaczono temat jako wykonany!");
            return map;
        }

        finishedTopicRepository.delete(foundTopic.get());
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
            throw new ChapterNotFoundException("Błąd: Nie odnaleziono podanego kursu.");

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




}
