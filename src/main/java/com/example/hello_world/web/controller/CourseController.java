package com.example.hello_world.web.controller;

import com.example.hello_world.Category;
import com.example.hello_world.HelloWorldApplication;
import com.example.hello_world.TopicStatus;
import com.example.hello_world.persistence.model.*;
import com.example.hello_world.persistence.repository.CourseRepository;
import com.example.hello_world.persistence.repository.FinishedTopicRepository;
import com.example.hello_world.persistence.repository.TopicRepository;
import com.example.hello_world.persistence.repository.UserRepository;
import com.example.hello_world.security.MyUserDetails;
import com.example.hello_world.web.dto.ChapterDto;
import com.example.hello_world.web.dto.TopicDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;

@RestController
public class CourseController {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private FinishedTopicRepository finishedTopicRepository;

    @Autowired
    private UserRepository userRepository;


    @GetMapping("/list2")
    public Iterable<Course> getCourses() {
        return courseRepository.findAll();
    }

    @PostMapping("/add2")
    public String addCourse() {
        Course course = new Course("Egzamin Ósmoklasisty",
                "Kurs Ósmoklasisty Matematyka",
                "Zawiera wiedzę potrzebną, żeby napisać egzamin nawet na 100%! Obejmuje nie tylko zagadnienia z klasy 8 ale z całej szkoły podstawowej – tłumaczy od podstaw działania na ułamkach, jednostki, i wiele innych rzeczy.",
                Category.MATEMATYKA,
                "Brak korzyści",
                249.0,
                120.0);


        Course course2 = new Course("Egzamin Maturalny Podstawa",
                "Kurs Maturalny - Matematyka PODSTAWA",
                "Matura podstawowa z wynikiem 100% to nie problem! Dokładnie zrozumiesz zagadnienia wymagane na maturze. Nawet najtrudniejsze tematy staną się dla Ciebie proste i zrozumiałe.",
                Category.MATEMATYKA,
                "<li>Ponad 70 godzin materiałów wideo</li>\n" +
                        "            <li>110 zadań do samodzielnego rozwiązania + odpowiedzi</li>\n" +
                        "            <li>3-miesięczny dostęp do kursu</li>\n" +
                        "            <li>starannie wyselekcjonowane zadania</li>\n" +
                        "            <li>swobodę uczenia się o dowolnej porze</li>\n" +
                        "            <li>tłumaczenie od doświadczonego korepetytora</li>",
                289.0,
                0.0);

        Course course3 = new Course("Egzamin Maturalny Rozszerzenie",
                "Kurs Maturalny - Matematyka ROZ",
                "Rozszerzona matematyka wcale nie jest taka ciężka! Przekonaj się o tym na własne oczy i zaskocz wszystkich niedowiarków.",
                Category.MATEMATYKA,
                "<li>Ponad 70 godzin materiałów wideo</li>\n" +
                        "            <li>110 zadań do samodzielnego rozwiązania + odpowiedzi</li>\n" +
                        "            <li>3-miesięczny dostęp do kursu</li>\n" +
                        "            <li>starannie wyselekcjonowane zadania</li>\n" +
                        "            <li>swobodę uczenia się o dowolnej porze</li>\n" +
                        "            <li>tłumaczenie od doświadczonego korepetytora</li>",
                329.0,
                0.0);

        courseRepository.save(course);
        courseRepository.save(course2);
        courseRepository.save(course3);
        return "Added 3 courses to repository.";
    }






    @GetMapping("/lista-kursow")
    public ModelAndView courseListView(Model model) {
        model.addAttribute("subjects", courseRepository.findAllTypes());
        model.addAttribute("categories", courseRepository.findAllCategories());
        return new ModelAndView("lista-kursow");
    }


    @GetMapping("/wyswietl/{id}")
    public ModelAndView courseSpectateView(@PathVariable("id") Integer id, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        MyUserDetails currentUserName = null;
        if (auth.getPrincipal() instanceof MyUserDetails) {
            currentUserName = (MyUserDetails) auth.getPrincipal();

            Optional<User> optionalUser = userRepository.findByEmail(currentUserName.getUsername());
            if (optionalUser.isEmpty()) return new ModelAndView("redirect:/");
        }
        model.addAttribute("loggedIn", (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))));



        if (id < 1) return new ModelAndView("redirect:/");
        Optional<Course> course = courseRepository.findById(id);
        if (course.isEmpty()) return new ModelAndView("redirect:/");


        model.addAttribute("course", course.get());
        model.addAttribute("topics", null);
        return new ModelAndView("wyswietl");
    }




    @GetMapping("/kurs/{courseId}")
    public ModelAndView openCourseVideo(@PathVariable("courseId") Integer courseId, @RequestParam(value = "topicId", required = false) Integer topicId,
                                        @RequestParam(value = "s", required = false) Integer scrollPosition, Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        if (auth.getPrincipal() instanceof MyUserDetails currentUserName) {
            Optional<User> optionalUser = userRepository.findByEmail(currentUserName.getUsername());
            //if (optionalUser.isEmpty()) return new ModelAndView("redirect:/");
            if (optionalUser.isPresent())
                user = optionalUser.get();
        }

        if (courseId < 1 || topicId != null && topicId < 1) return new ModelAndView("redirect:/");

        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isEmpty()) return new ModelAndView("redirect:/");

        Course course = optionalCourse.get();
        Topic selectedTopic = (topicId == null ? course.getFirstTopic() : course.getTopicById(topicId));
        if (selectedTopic == null) return new ModelAndView("redirect:/");


        List<ChapterDto> chapterDtoList = new ArrayList<>();
        int counter = 0;
        boolean selectedTopicFinished = false; //mark if active topic has been finished
        for (Chapter ch : course.getChapters()) {
            List<TopicDto> topicDtoList = new ArrayList<>();
            for (Topic t : ch.getTopics()) {

                boolean blocked = false;  //refers to current topic status (is it blocked?)
                if (user != null) {
                    if (isCourseStillValid(user, courseId)) {
                        TopicStatus status = TopicStatus.AVAILABLE;
                        if (user.isTopicFinished(t.getId()))
                            status = TopicStatus.FINISHED;

                        if (t.equals(selectedTopic)) {
                            topicDtoList.add(new TopicDto(t, status, true));  // true as last parameter means its active topic
                            if (status == TopicStatus.FINISHED)
                                selectedTopicFinished = true;
                        } else
                            topicDtoList.add(new TopicDto(t, status, false)); // false means its not currently active topic
                    }
                }

                if (user == null || user != null && !isCourseStillValid(user, courseId)) {

                    if (counter < 3) {
                        if (t.equals(selectedTopic))
                            topicDtoList.add(new TopicDto(t, TopicStatus.AVAILABLE, true));
                        else
                            topicDtoList.add(new TopicDto(t, TopicStatus.AVAILABLE, false));

                    } else {
                        topicDtoList.add(new TopicDto(t, TopicStatus.BLOCKED, false, true));
                        blocked = true;
                    }

                }
                counter++;

                //Check if user has permission to access specified topic
                if (t.equals(selectedTopic)) {
                    if (blocked) return new ModelAndView("redirect:/");
                }
            }
            chapterDtoList.add(new ChapterDto(topicDtoList, ch));
        }


        model.addAttribute("loggedIn", (auth != null && auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))));
        model.addAttribute("videosPath", HelloWorldApplication.videoesPath);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseOwned", (user != null && user.isCourseOwnedAndValid(course)));
        model.addAttribute("course", course);
        model.addAttribute("topic", selectedTopic);
        model.addAttribute("selectedTopicFinished", selectedTopicFinished);
        model.addAttribute("data", chapterDtoList);

        return new ModelAndView("kurs");
    }




    @PostMapping("/kurs/markAsFinished")
    public Map<String, Object> markAsFinished(@RequestParam(value = "topicId") Integer topicId) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono tematu o podanym identyfikatorze.");
        Optional<Topic> optionalTopic = topicRepository.findById(topicId);

        if (topicId < 1 || optionalTopic.isEmpty()) return map;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof MyUserDetails currentUserName) {
            Optional<User> optionalUser = userRepository.findByEmail(currentUserName.getUsername());
            if (optionalUser.isEmpty()) {
                map.replace("message", "Błąd: Musisz byc zalogowany aby korzystać z tej funkcji!");
                return map;
            }

            User user = optionalUser.get();
            Course c = optionalTopic.get().getChapter().getCourse();
            if (!user.isCourseOwnedAndValid(c)) {
                map.replace("message", "Błąd: Musisz zakupić kurs aby skorzystać z tej opcji!");
                return map;
            }

            Optional<FinishedTopic> foundTopic = finishedTopicRepository.findAllWithCondition(user, optionalTopic.get());
            if (foundTopic.isEmpty()) {
                FinishedTopic ft = new FinishedTopic(user, optionalTopic.get(), new Date(System.currentTimeMillis()));
                finishedTopicRepository.save(ft);
                map.replace("success", true);
                map.put("type", 1);
                map.replace("message", "Sukces: Oznaczono temat jako wykonany!");
                return map;
            }

            finishedTopicRepository.delete(foundTopic.get());
            map.replace("success", true);
            map.put("type", 0);
            map.replace("message", "Sukces: Odznaczono temat!");
            return map;

        }

        map.replace("message", "Błąd: Musisz byc zalogowany aby korzystać z tej funkcji!");
        return map;
    }









    public boolean isCourseStillValid(User user, Integer courseId) {
        for (CourseOwned item : user.getCoursesOwned()) {
            if (!item.getCourse().getId().equals(courseId)) continue;

            Date now = new Date(System.currentTimeMillis());
            if (now.compareTo(item.getExpiryDate()) < 0) return true;

        }
        return false;
    }
}
