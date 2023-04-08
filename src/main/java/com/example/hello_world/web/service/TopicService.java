package com.example.hello_world.web.service;

import com.example.hello_world.persistence.model.Course;
import com.example.hello_world.persistence.model.FinishedTopic;
import com.example.hello_world.persistence.model.Topic;
import com.example.hello_world.persistence.model.User;
import com.example.hello_world.persistence.repository.FinishedTopicRepository;
import com.example.hello_world.persistence.repository.TopicRepository;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.validation.CourseNotOwnedException;
import com.example.hello_world.validation.TopicNotFoundException;
import com.example.hello_world.validation.UserNotLoggedInException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class TopicService {


    private UserRepository userRepository;
    private TopicRepository topicRepository;
    private FinishedTopicRepository finishedTopicRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setTopicRepository(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    @Autowired
    public void setFinishedTopicRepository(FinishedTopicRepository finishedTopicRepository) {
        this.finishedTopicRepository = finishedTopicRepository;
    }



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

}
