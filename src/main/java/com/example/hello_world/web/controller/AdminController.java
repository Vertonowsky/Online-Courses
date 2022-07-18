package com.example.hello_world.web.controller;

import com.example.hello_world.persistence.model.Chapter;
import com.example.hello_world.persistence.model.Course;
import com.example.hello_world.persistence.repository.ChapterRepository;
import com.example.hello_world.persistence.repository.CourseRepository;
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

    private final String STRING_PATTERN = "[a-zA-Z0-9~!@#$%^&*()-_=+\\[]\\{}|'?,.<>]+";

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ChapterRepository chapterRepository;


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
        }


        Optional<Course> course = courseRepository.findById(courseId);
        Chapter chapter = new Chapter();
        chapter.setCourse(course.get());
        chapter.setIndex(index);
        chapter.setTitle(title);
        chapterRepository.save(chapter);

        map.replace("success", true);
        map.replace("message", "Sukces: Dodano nowy rozdział do kursu!");
        map.put("view", new ModelAndView("admin :: course_preview"));
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
