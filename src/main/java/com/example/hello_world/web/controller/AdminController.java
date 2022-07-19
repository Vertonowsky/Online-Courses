package com.example.hello_world.web.controller;

import com.example.hello_world.persistence.model.Chapter;
import com.example.hello_world.persistence.model.Course;
import com.example.hello_world.persistence.model.Topic;
import com.example.hello_world.persistence.repository.ChapterRepository;
import com.example.hello_world.persistence.repository.CourseRepository;
import com.example.hello_world.persistence.repository.TopicRepository;
import com.example.hello_world.web.dto.CourseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class AdminController {

    private final String STRING_PATTERN = "[a-zA-Z0-9ąĄćĆśŚęĘóÓłŁńŃżŻźŹ ~!@#$%^&*()-_=+'?,.<>\\[\\]{}|]*";

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    TopicRepository topicRepository;


    @RequestMapping(value = {"/admin", "/admin/{id}"})
    public ModelAndView openAdminView(@PathVariable(name = "id", required = false) Integer id, Model model) {
        ModelAndView mav = new ModelAndView("admin");
        model.addAttribute("courses", courseRepository.findAllCoursesNames());
        if (id == null) {
            return mav;
        }

        if (id < 1 || courseRepository.findById(id).isEmpty()) {
            mav.setViewName("redirect:/");
            return mav;
        }

        model.addAttribute("selectedCourse", courseRepository.findById(id).get());

        File folder = new File("D:\\Xampp\\htdocs\\edu\\videos");
        File[] listOfFiles = folder.listFiles();
        List<String> allVideos = new ArrayList<>();

        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    allVideos.add(file.getName());
                }
            }
        }

        model.addAttribute("videos", allVideos);
        return mav;
    }












    @PostMapping("/admin/addNewChapter")
    public Map<String, Object> addNewChapter(@RequestParam(value = "courseId") Integer courseId, @RequestParam(value = "chapterTitle") String title, @RequestParam(value = "chapterIndex") Integer index) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono podanego kursu.");

        if (courseId < 1 || courseRepository.findById(courseId).isEmpty()) return map;
        if (!isStringValid(title)) {
            map.replace("message", "Błąd: Podany tutuł zawiera niedozwolone znaki.");
            return map;
        }


        Optional<Course> course = courseRepository.findById(courseId);
        Chapter chapter = new Chapter();
        chapter.setCourse(course.get());
        chapter.setIndex(index);
        chapter.setTitle(title);
        chapterRepository.save(chapter);

        map.replace("success", true);
        map.replace("message", "Sukces: Dodano nowy rozdział do kursu!");
        return map;
    }









    @PostMapping("/admin/addNewTopic")
    public Map<String, Object> addNewTopic(@RequestParam(value = "courseId") Integer courseId, @RequestParam(value = "chapterId") Integer chapterId, @RequestParam(value = "topicTitle") String topicTitle,
                                           @RequestParam(value = "topicIndex") Integer topicIndex, @RequestParam(value = "topicVideo") String topicVideo) {

        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono podanego kursu.");

        if (courseId < 1 || courseRepository.findById(courseId).isEmpty()) return map;

        if (chapterId < 1 || chapterRepository.findById(chapterId).isEmpty()) {
            map.replace("message", "Błąd: Nie odnaleziono rozdziału o podanym identyfikatorze.");
            return map;
        }

        if (topicIndex < 0) {
            map.replace("message", "Błąd: Indeks nie może być mniejszy niż 0.");
            return map;
        }

        if (!isStringValid(topicTitle)) {
            map.replace("message", "Błąd: Nazwa kursu bądź ścieżka video zawiera niedozwolone znaki.");
            return map;
        }

        if (!isStringValid(topicVideo)) {
            map.replace("message", "Błąd: 22222222222222");
            return map;
        }


        Optional<Chapter> chapter = chapterRepository.findById(chapterId);
        Topic topic = new Topic();
        topic.setIndex(topicIndex);
        topic.setTitle(topicTitle);
        topic.setLocation(topicVideo);
        topic.setChapter(chapter.get());
        topicRepository.save(topic);

        map.replace("success", true);
        map.replace("message", "Sukces: Dodano nowy temat do kursu!");
        return map;
    }









    @PostMapping("/admin/deleteTopic")
    public Map<String, Object> deleteTopic(@RequestParam(value = "topicId") Integer topicId) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono podanego tematu.");

        if (topicId < 1 || topicRepository.findById(topicId).isEmpty()) return map;

        Topic topic = topicRepository.findById(topicId).get();
        topicRepository.delete(topic);

        map.replace("success", true);
        map.replace("message", "Sukces: Usunięto wskazany temat.");
        return map;
    }







    @PostMapping("/admin/deleteChapter")
    public Map<String, Object> deleteChapter(@RequestParam(value = "chapterId") Integer chapterId) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono podanego rozdziału.");

        if (chapterId < 1 || chapterRepository.findById(chapterId).isEmpty()) return map;

        Chapter chapter = chapterRepository.findById(chapterId).get();
        for (Topic topic : chapter.getTopics()) {
            topicRepository.delete(topic);
        }

        chapterRepository.delete(chapter);

        map.replace("success", true);
        map.replace("message", "Sukces: Usunięto wskazany rozdział wraz z tematami.");
        return map;
    }










    @GetMapping("/admin/getCourseInfo/{id}")
    public ModelAndView getCourseInfo(@PathVariable(name = "id", required = false) Integer courseId, Model model) {
        if (courseId == null) {
            return null;
        }

        if (courseId < 1 || courseRepository.findById(courseId).isEmpty()) {
            return null;
        }

        model.addAttribute("selectedCourse", courseRepository.findById(courseId).get());
        return new ModelAndView("admin :: course_preview");
    }



    @GetMapping("/admin/getChaptersInfo/{id}")
    public ModelAndView getChaptersInfo(@PathVariable(name = "id", required = false) Integer courseId, Model model) {
        if (courseId == null) {
            return null;
        }

        if (courseId < 1 || courseRepository.findById(courseId).isEmpty()) {
            return null;
        }

        model.addAttribute("selectedCourse", courseRepository.findById(courseId).get());
        return new ModelAndView("admin :: chapter_select");
    }




    private boolean isStringValid(String title) {
        Pattern pattern = Pattern.compile(STRING_PATTERN);
        Matcher matcher = pattern.matcher(title);
        return matcher.matches();
    }












    @GetMapping("/admin/lol")
    public Iterable<CourseDto> lol() {
        return courseRepository.findAllCoursesNames();
    }

}
