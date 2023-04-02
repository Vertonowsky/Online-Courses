package com.example.hello_world.web.controller;

import com.example.hello_world.persistence.model.Chapter;
import com.example.hello_world.persistence.model.Course;
import com.example.hello_world.persistence.model.Topic;
import com.example.hello_world.persistence.repository.ChapterRepository;
import com.example.hello_world.persistence.repository.CourseRepository;
import com.example.hello_world.persistence.repository.TopicRepository;
import com.example.hello_world.web.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${course.videos.path}")
    private String courseVideoesPath;

    @Value("${local.videos.path}")
    private String localVideoesPath;

    @Autowired
    CourseRepository courseRepository;

    @Autowired
    ChapterRepository chapterRepository;

    @Autowired
    TopicRepository topicRepository;

    @Autowired
    EmailService emailService;


    /*
    @Autowired
    public void setChapterDependency(ChapterRepository chapterRepository) {
        this.chapterRepository = chapterRepository;
    }
    */

    @RequestMapping(value = {"/admin", "/admin/{id}"})
    public ModelAndView openAdminView(@PathVariable(name = "id", required = false) Integer id, Model model) {
        ModelAndView mav = new ModelAndView("admin");
        model.addAttribute("courses", courseRepository.findAllCoursesNames());
        model.addAttribute("videos", getVideoList());
        model.addAttribute("videosPath", courseVideoesPath);

        if (id == null) {
            return mav;
        }

        if (id < 1 || courseRepository.findById(id).isEmpty()) {
            mav.setViewName("redirect:/");
            return mav;
        }

        model.addAttribute("selectedCourse", courseRepository.findById(id).get());
        return mav;
    }





    @GetMapping("/admin/sendMail")
    public ModelAndView sendMail(Model model) {
        //emailService.sendEmail("nieznane656@gmail.com", "Test email", "Welcome to Online-Courses! It is a test email. You don't need to respond.");
//        try {
//            HashMap<String, Object> variables = new HashMap<>();
//            variables.put("to", "nieznane656@gmail.com");
//            variables.put("url", "http://localhost:8080/rejestracja/weryfikacja&token=iuahdiuoshd123123azsdasd");
//
//            emailService.sendVerificationEmail("nieznane656@gmail.com", "Potwierdź swoją rejestrację - Kursowo.pl", variables, "templates/test-email.html");
//
//        } catch(Exception e) {
//            System.out.println(e.getMessage());
//        }
        model.addAttribute("to", "nieznane656@gmail.com");
        model.addAttribute("url", "http://localhost:8080/rejestracja/weryfikacja&token=iuahdiuoshd123123azsdasd");

        return new ModelAndView("test-email");
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
    public Map<String, Object> addNewTopic(@RequestParam(value = "chapterId") Integer chapterId, @RequestParam(value = "topicTitle") String topicTitle,
                                           @RequestParam(value = "topicIndex") Integer topicIndex, @RequestParam(value = "topicVideo") String topicVideo,
                                           @RequestParam(value = "duration") Double duration) {

        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono rozdziału o podanym identyfikatorze.");

        if (chapterId < 1 || chapterRepository.findById(chapterId).isEmpty()) return map;

        if (topicIndex < 0) {
            map.replace("message", "Błąd: Indeks nie może być mniejszy niż 0.");
            return map;
        }

        if (duration < 0.0 || duration > 10000000.0) {
            map.replace("message", "Błąd: Nieprawidłowa długość filmu.");
            return map;
        }

        if (!isStringValid(topicTitle) || !isStringValid(topicVideo)) {
            map.replace("message", "Błąd: Nazwa kursu bądź ścieżka video zawiera niedozwolone znaki.");
            return map;
        }

        Optional<Chapter> chapter = chapterRepository.findById(chapterId);
        Topic topic = new Topic();
        topic.setIndex(topicIndex);
        topic.setTitle(topicTitle);
        topic.setLocation(topicVideo);
        topic.setDuration(duration.intValue());
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












    @PostMapping("/admin/editChapter")
    public Map<String, Object> editChapter(@RequestParam(value = "chapterId") Integer chapterId, @RequestParam(value = "chapterTitle") String title, @RequestParam(value = "chapterIndex") Integer index) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono podanego rozdziału.");

        if (chapterId < 1 || chapterRepository.findById(chapterId).isEmpty()) return map;
        if (!isStringValid(title)) {
            map.replace("message", "Błąd: Podany tutuł zawiera niedozwolone znaki.");
            return map;
        }


        Chapter chapter = chapterRepository.findById(chapterId).get();
        chapter.setTitle(title);
        chapter.setIndex(index);
        chapterRepository.save(chapter);

        map.replace("success", true);
        map.replace("message", "Sukces: Edytowano rozdział!");
        return map;
    }




    @PostMapping("/admin/editTopic")
    public Map<String, Object> editTopic(@RequestParam(value = "chapterId") Integer chapterId, @RequestParam(value = "topicId") Integer topicId, @RequestParam(value = "topicTitle") String topicTitle,
                                         @RequestParam(value = "topicIndex") Integer topicIndex, @RequestParam(value = "topicVideo") String topicVideo,
                                         @RequestParam(value = "duration") Double duration) {

        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono tematu o podanym identyfikatorze.");

        if (topicId < 1 || topicRepository.findById(topicId).isEmpty()) return map;

        if (topicIndex < 0) {
            map.replace("message", "Błąd: Indeks nie może być mniejszy niż 0.");
            return map;
        }

        if (duration < 0.0 || duration > 10000000.0) {
            map.replace("message", "Błąd: Nieprawidłowa długość filmu.");
            return map;
        }

        if (!isStringValid(topicTitle) || !isStringValid(topicVideo)) {
            map.replace("message", "Błąd: Nazwa tematu bądź ścieżka video zawiera niedozwolone znaki.");
            return map;
        }


        Topic topic = topicRepository.findById(topicId).get();
        topic.setIndex(topicIndex);
        topic.setTitle(topicTitle);
        topic.setLocation(topicVideo);
        topic.setDuration(duration.intValue());

        if (chapterRepository.findById(chapterId).isPresent())
            topic.setChapter(chapterRepository.findById(chapterId).get());

        topicRepository.save(topic);

        map.replace("success", true);
        map.replace("message", "Sukces: Edytowano temat!");
        return map;
    }







    @PostMapping("/admin/updateDuration")
    public Map<String, Object> updateDurations(@RequestParam(value = "videoLocation") String videoLocation, @RequestParam(value = "duration") Double duration) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Wystąpił nieoczekiwany problem.");

        if (duration < 0.0 || duration > 10000000.0) {
            map.put("message", "Błąd: Nieprawidłowa długość filmu.");
            return map;
        }

        if (videoLocation.isEmpty() || !isStringValid(videoLocation)) {
            map.put("message", "Błąd: Nazwa tematu bądź ścieżka video zawiera niedozwolone znaki.");
            return map;
        }

        List<Topic> topics = topicRepository.findAllByVideoLocation(videoLocation);

        for (Topic topic : topics) {
            topic.setDuration(duration.intValue());
            topicRepository.save(topic);
        }

        map.replace("success", true);
        map.replace("message", "Sukces: Edytowano temat!");
        return map;
    }








    @GetMapping("/admin/getChapterEditDetails/{id}")
    public ModelAndView getChapterEditDetails(@PathVariable(name = "id") Integer chapterId, Model model) {
        if (chapterId == null || chapterId < 1 || chapterRepository.findById(chapterId).isEmpty()) {
            model.addAttribute("editChapter", null);
            return new ModelAndView("admin :: editChapter");
        }

        model.addAttribute("editChapter", chapterRepository.findById(chapterId).get());
        return new ModelAndView("admin :: editChapter");
    }



    @GetMapping("/admin/getTopicEditDetails/{id}")
    public ModelAndView getTopicEditDetails(@PathVariable(name = "id") Integer topicId, @RequestParam(value = "courseId") Integer courseId, Model model) {
        if (courseId == null || topicId == null) {
            model.addAttribute("editTopic", null);
            return new ModelAndView("admin :: editTopic");
        }

        if (courseId < 1 || courseRepository.findById(courseId).isEmpty()) {
            model.addAttribute("editTopic", null);
            return new ModelAndView("admin :: editTopic");
        }

        if (topicId < 1 || topicRepository.findById(topicId).isEmpty()) {
            model.addAttribute("editTopic", null);
            return new ModelAndView("admin :: editTopic");
        }

        Topic finalTopic = topicRepository.findById(topicId).get();
        Chapter finalChapter = null;

        for (Chapter chapter : chapterRepository.findAll()) {
            if (chapter.getTopics().contains(finalTopic)) {
                finalChapter = chapter;
                break;
            }
        }

        model.addAttribute("selectedCourse", courseRepository.findById(courseId).get());
        model.addAttribute("videos", getVideoList());
        model.addAttribute("finalChapter", finalChapter);
        model.addAttribute("editTopic", finalTopic);
        return new ModelAndView("admin :: editTopic");
    }









    @GetMapping("/admin/getCourseInfo/{id}")
    public ModelAndView getCourseInfo(@PathVariable(name = "id", required = false) Integer courseId, Model model) {
        if (courseId == null || courseId < 1 || courseRepository.findById(courseId).isEmpty()) {
            model.addAttribute("selectedCourse", null);
            return new ModelAndView("admin :: course_preview");
        }

        model.addAttribute("selectedCourse", courseRepository.findById(courseId).get());
        return new ModelAndView("admin :: course_preview");
    }



    @GetMapping("/admin/getChaptersInfo/{id}")
    public ModelAndView getChaptersInfo(@PathVariable(name = "id", required = false) Integer courseId, Model model) {
        if (courseId == null || courseId < 1 || courseRepository.findById(courseId).isEmpty()) {
            model.addAttribute("selectedCourse", null);
            return new ModelAndView("admin :: chapter_select");
        }

        model.addAttribute("selectedCourse", courseRepository.findById(courseId).get());
        return new ModelAndView("admin :: chapter_select");
    }




    private boolean isStringValid(String title) {
        Pattern pattern = Pattern.compile(STRING_PATTERN);
        Matcher matcher = pattern.matcher(title);
        return matcher.matches();
    }



    private List<String> getVideoList() {
        File folder = new File(localVideoesPath);
        if (!folder.exists()) return new ArrayList<>();

        File[] listOfFiles = folder.listFiles();
        List<String> allVideos = new ArrayList<>();


        if (listOfFiles.length > 0) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    allVideos.add(file.getName());
                }
            }
        }

        return allVideos;
    }


}
