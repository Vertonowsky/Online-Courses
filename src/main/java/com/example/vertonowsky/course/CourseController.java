package com.example.vertonowsky.course;

import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.topic.TopicService;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.UserQueryType;
import com.example.vertonowsky.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.example.vertonowsky.course.CourseSerializer.Task.*;

@RestController
public class CourseController {

    @Value("${course.videos.path}")
    private String courseVideoesPath;
    private final CourseService courseService;
    private final TopicService topicService;
    private final UserService userService;

    public CourseController(CourseService courseService, TopicService topicService, UserService userService) {
        this.courseService = courseService;
        this.topicService = topicService;
        this.userService = userService;
    }

    /*
    @PostMapping("/add2")
    public String addCourse() {
        Course course = new Course("Egzamin Ósmoklasisty",
                "Kurs Ósmoklasisty Matematyka",
                "Zawiera wiedzę potrzebną, żeby napisać egzamin nawet na 100%! Obejmuje nie tylko zagadnienia z klasy 8, ale z całej szkoły podstawowej – tłumaczy od podstaw działania na ułamkach, jednostki, i wiele innych rzeczy.",
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


    @PostMapping("/add3")
    public String addChapter() {
        Chapter chapter = new Chapter(0, "Rozdział 01 - Liczby i działania");

        Course course = courseRepository.findById(2).get();
        chapter.setCourse(course);

        chapterRepository.save(chapter);
        return "Added new chapter to repository!";
    }

    */


    /**
     * Opens page which contains all the available courses
     *
     * @param model instance of the Model class. Used to pass attributes to the end user
     * @return HTML page
     */
    @GetMapping("/lista-kursow")
    public ModelAndView courseListView(Model model) {
        List<CourseDto> courses = courseService.getCoursesWithCriteria(new LinkedList<>(), new LinkedList<>(), 0); // limit = 0, means  there is no limit for course count
        HashMap<String, String> topPanel = courseService.generateCoursesListHeading(courses.size(), null);

        model.addAttribute("subjects", courseService.listTypes());
        model.addAttribute("categories", courseService.listCategories());
        model.addAttribute("topPanel", true);
        model.addAttribute("topPanelPrefix", topPanel.get("topPanelPrefix"));
        model.addAttribute("topPanelCategory", topPanel.get("topPanelCategory"));
        model.addAttribute("courses", courses);
        return new ModelAndView("lista-kursow");
    }


    /**
     * Load courses from database including the search filtering
     *
     * @param typeFilters JSON String storing selected course types
     * @param categoryFilters JSON String storing selected category types
     * @param limit number of returned courses. 0 <=> unlimited
     * @param model instance of the Model class. Used to pass attributes to the end user
     * @return List of courses which match the filters
     * @throws JsonProcessingException exception thrown when there was a problem with parsing JSON filters
     */
    @GetMapping("/api/courses/list")
    public ModelAndView getCourseInfo(@RequestParam String typeFilters, @RequestParam String categoryFilters, @RequestParam int limit, Model model) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<CourseDto> courses = courseService.getCourseWithFilters(mapper, typeFilters, categoryFilters, limit); // limit = 0, means  there is no limit for course count

        model.addAttribute("courses", courses);
        return new ModelAndView("index :: courses_data");
    }



    @GetMapping("/api/courses/top-panel")
    public ModelAndView getCourseTopPanel(@RequestParam String typeFilters, @RequestParam String categoryFilters, @RequestParam int limit, Model model) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<CourseDto> courses = courseService.getCourseWithFilters(mapper, typeFilters, categoryFilters, limit); // limit = 0, means  there is no limit for course count
        HashMap<String, String> topPanel = courseService.generateCoursesListHeading(courses.size(), courseService.convertCategoryParamList(mapper, categoryFilters));

        model.addAttribute("topPanel", true);
        model.addAttribute("topPanelPrefix", topPanel.get("topPanelPrefix"));
        model.addAttribute("topPanelCategory", topPanel.get("topPanelCategory"));
        return new ModelAndView("index :: top_panel");
    }



    @PostMapping("/api/discountCode/details")
    public ModelAndView getDiscountCodeDetails(@RequestParam String title, @RequestParam Double discount, Model model) {
        model.addAttribute("discountTitle", title);
        model.addAttribute("discountValue", discount * -1);

        return new ModelAndView("wyswietl :: discount_row");
    }


    /**
     * Opens page which contain given course
     *
     * @param id course id
     * @param model instance of the Model class. Used to pass attributes to the end user
     * @return HTML page
     */
    @GetMapping("/wyswietl/{id}")
    public ModelAndView courseSpectateView(@PathVariable("id") Integer id, Model model) {
        // INVALID DATA, redirect to index page.
        if (id < 1) return new ModelAndView("redirect:/");
        Course course = courseService.get(id, CourseQueryType.ALL);
        if (course == null) return new ModelAndView("redirect:/");

        // Check if user is logged in
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.get(auth, UserQueryType.ALL);

        CourseDto courseDto = CourseSerializer.serialize(course, BASE, ADVANTAGES, CHAPTERS, PRICE);
        courseService.calculateAdvantages(courseDto);

        model.addAttribute("loggedIn", userService.isLoggedIn(user));
        model.addAttribute("course", courseDto);
        return new ModelAndView("wyswietl");
    }


    /**
     * Opens course page. Here you can watch a video.
     *
     * @param courseId id of the coursae
     * @param topicId id of the topic
     * @param scrollPosition current scroll position
     * @param model instance of the Model class. Used to pass attributes to the end user
     * @return HTML page
     */
    @GetMapping("/kurs/{courseId}")
    public ModelAndView openCourseVideo(@PathVariable("courseId") Integer courseId, @RequestParam(value = "topicId", required = false) Integer topicId,
                                        @RequestParam(value = "s", required = false) Integer scrollPosition, Model model) {
        // Check if data is valid
        if (courseId < 1 || topicId != null && topicId < 1) return new ModelAndView("redirect:/");

        Course course = courseService.get(courseId, CourseQueryType.CHAPTERS);
        if (course == null) return new ModelAndView("redirect:/");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.get(auth, UserQueryType.ALL);

        model.addAttribute("loggedIn", userService.isLoggedIn(user));
        model.addAttribute("videosPath", courseVideoesPath);
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseOwned", (user != null && userService.isCourseValid(user, course)));
        model.addAttribute("data", courseService.getDetailedCourseInfo(user, course, topicId));

        return new ModelAndView("kurs");
    }



    @GetMapping("/api/course/details/{courseId}")
    public Course getDetails(@PathVariable("courseId") Integer courseId) {
        // Check if data is valid
        if (courseId < 1) return null;

        Course course = courseService.get(courseId);
        if (course == null) return null;

        return course;
    }

    /**
     * Mark video as already watched / finished
     *
     * @param topicId identifier of the topic
     */
    @PostMapping("/api/course/markAsFinished")
    public Map<String, Object> markAsFinished(@RequestParam(value = "topicId") Integer topicId) {
        try {

            return topicService.markAsFinished(topicId);

        } catch (Exception e) {
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);
            map.put("message", e.getMessage());
            return map;
        }
    }

}
