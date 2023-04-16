package com.example.hello_world.web.controller;

import com.example.hello_world.persistence.model.Chapter;
import com.example.hello_world.persistence.model.Topic;
import com.example.hello_world.persistence.repository.ChapterRepository;
import com.example.hello_world.persistence.repository.CourseRepository;
import com.example.hello_world.persistence.repository.TopicRepository;
import com.example.hello_world.validation.*;
import com.example.hello_world.web.service.ChapterService;
import com.example.hello_world.web.service.EmailService;
import com.example.hello_world.web.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AdminController {

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

    private final TopicService topicService;
    private final ChapterService chapterService;

    public AdminController(TopicService topicService, ChapterService chapterService) {
        this.topicService = topicService;
        this.chapterService = chapterService;
    }

    @RequestMapping(value = {"/admin", "/admin/{id}"})
    public ModelAndView openAdminView(@PathVariable(name = "id", required = false) Integer id, Model model) {
        model.addAttribute("courses", courseRepository.findAllCoursesNames());
        model.addAttribute("videos", getVideoList());
        model.addAttribute("videosPath", courseVideoesPath);

        if (id == null)
            return new ModelAndView("admin");

        if (id < 1 || courseRepository.findById(id).isEmpty())
            return new ModelAndView("redirect:/");

        model.addAttribute("selectedCourse", courseRepository.findById(id).get());
        return new ModelAndView("admin");
    }





    /*@GetMapping("/admin/sendMail")
    public ModelAndView sendMail(Model model) {
        emailService.sendEmail("nieznane656@gmail.com", "Test email", "Welcome to Online-Courses! It is a test email. You don't need to respond.");
        try {
            HashMap<String, Object> variables = new HashMap<>();
            variables.put("to", "nieznane656@gmail.com");
            variables.put("url", "http://localhost:8080/rejestracja/weryfikacja&token=iuahdiuoshd123123azsdasd");

            emailService.sendVerificationEmail("nieznane656@gmail.com", "Potwierdź swoją rejestrację - Kursowo.pl", variables, "templates/test-email.html");

        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        model.addAttribute("to", "nieznane656@gmail.com");
        model.addAttribute("url", "http://localhost:8080/rejestracja/weryfikacja&token=iuahdiuoshd123123azsdasd");

        return new ModelAndView("test-email");
    }*/


    /**
     * Add a new chapter to the specified course
     *
     * @param courseId id of the course
     * @param title title of the chapter
     * @param index index of the chapter
     * @return Map containing "success" and "message" values
     */
    @PostMapping("/admin/addNewChapter")
    public Map<String, Object> addNewChapter(@RequestParam(value = "courseId") Integer courseId, @RequestParam(value = "chapterTitle") String title, @RequestParam(value = "chapterIndex") Integer index) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono podanego kursu.");

        if (courseId < 1) return map;

        try {

            chapterService.createNewChapter(courseId, title, index);

            map.replace("success", true);
            map.replace("message", "Sukces: Dodano nowy rozdział do kursu.");
            return map;

        } catch (CourseNotFoundException | InvalidTextFormatException e) {
            map.replace("success", false);
            map.replace("message", e.getMessage());
            return map;
        }
    }


    /**
     * Add a new topic to the specified chapter
     *
     * @param chapterId id of the chapter that the topic is inside of
     * @param topicTitle title of the topic
     * @param topicIndex index of the topic
     * @param topicVideo path to the video which will be linked to the topic
     * @param duration duration of the video
     * @return Map containing "success" and "message" values
     */
    @PostMapping("/admin/addNewTopic")
    public Map<String, Object> createNewTopic(@RequestParam(value = "chapterId") Integer chapterId, @RequestParam(value = "topicTitle") String topicTitle,
                                           @RequestParam(value = "topicIndex") Integer topicIndex, @RequestParam(value = "topicVideo") String topicVideo,
                                           @RequestParam(value = "duration") Double duration) {

        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono rozdziału o danym id.");

        if (chapterId < 1) return map;

        try {

            topicService.createNewTopic(chapterId, topicTitle, topicIndex, topicVideo, duration);

            map.replace("success", true);
            map.replace("message", "Sukces: Dodano nowy temat do kursu!");
            return map;

        } catch (ChapterNotFoundException | InvalidTextFormatException | InvalidDataException e) {
            map.replace("success", false);
            map.replace("message", e.getMessage());
            return map;
        }

    }


    /**
     * Delete chapter with specified id
     *
     * @param chapterId id of the chapter
     * @return Map containing "success" and "message" values
     */
    @PostMapping("/admin/deleteChapter")
    public Map<String, Object> deleteChapter(@RequestParam(value = "chapterId") Integer chapterId) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono podanego rozdziału.");

        if (chapterId < 1) return map;

        try {

            chapterService.deleteChapter(chapterId);

            map.replace("success", true);
            map.replace("message", "Sukces: Usunięto wskazany rozdział wraz z tematami.");
            return map;

        } catch (ChapterNotFoundException e) {
            map.replace("success", false);
            map.replace("message", e.getMessage());
            return map;
        }

    }


    /**
     * Delete topic with specified id
     *
     * @param topicId id of the topic
     * @return Map containing "success" and "message" values
     */
    @PostMapping("/admin/deleteTopic")
    public Map<String, Object> deleteTopic(@RequestParam(value = "topicId") Integer topicId) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono podanego tematu.");

        if (topicId < 1) return map;

        try {

            topicService.deleteTopic(topicId);

            map.replace("success", true);
            map.replace("message", "Sukces: Usunięto wskazany temat.");
            return map;

        } catch (TopicNotFoundException e) {
            map.replace("success", false);
            map.replace("message", e.getMessage());
            return map;
        }
    }


    /**
     * Edit chapter details
     *
     * @param chapterId id of the chapter
     * @param title new chapter's title
     * @param index index of the chapter
     * @return Map containing "success" and "message" values
     */
    @PostMapping("/admin/editChapter")
    public Map<String, Object> editChapter(@RequestParam(value = "chapterId") Integer chapterId, @RequestParam(value = "chapterTitle") String title, @RequestParam(value = "chapterIndex") Integer index) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono podanego rozdziału.");

        if (chapterId < 1) return map;

        try {

            chapterService.editChapter(chapterId, index, title);

            map.replace("success", true);
            map.replace("message", "Sukces: Edytowano rozdział!");
            return map;

        } catch (InvalidTextFormatException | ChapterNotFoundException e) {
            map.replace("success", false);
            map.replace("message", e.getMessage());
            return map;
        }
    }


    /**
     * Edit topic details
     *
     * @param chapterId id of the chaper that the topic is linked to
     * @param topicId id of the topic
     * @param topicTitle new topic's title
     * @param topicIndex new index of the topic
     * @param topicVideo new path to the video that is linked to the topic
     * @param duration duration of the video
     * @return Map containing "success" and "message" values
     */
    @PostMapping("/admin/editTopic")
    public Map<String, Object> editTopic(@RequestParam(value = "chapterId") Integer chapterId, @RequestParam(value = "topicId") Integer topicId, @RequestParam(value = "topicTitle") String topicTitle,
                                         @RequestParam(value = "topicIndex") Integer topicIndex, @RequestParam(value = "topicVideo") String topicVideo,
                                         @RequestParam(value = "duration") Double duration) {

        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("message", "Błąd: Nie odnaleziono tematu o danym id.");

        if (topicId < 1) return map;

        try {

            topicService.editTopic(chapterId, topicId, topicIndex, topicTitle, topicVideo, duration);

            map.replace("success", true);
            map.replace("message", "Sukces: Edytowano temat!");
            return map;

        } catch (InvalidTextFormatException | ChapterNotFoundException | InvalidDataException | TopicNotFoundException e) {
            map.replace("success", false);
            map.replace("message", e.getMessage());
            return map;
        }

    }


    /**
     * Update duration of all videos in the database
     *
     * @param videoLocation path to the video
     * @param duration duration of the video
     * @return Map containing "success" and "message" values
     */
    @PostMapping("/admin/updateDuration")
    public Map<String, Object> updateDurations(@RequestParam(value = "videoLocation") String videoLocation, @RequestParam(value = "duration") Double duration) {
        Map<String, Object> map = new HashMap<>();

        try {

            topicService.updateAllTopicsDuration(videoLocation, duration);

            map.put("success", true);
            map.put("message", "Sukces: Edytowano temat!");
            return map;

        } catch (InvalidTextFormatException | InvalidDataException e) {
            map.put("success", false);
            map.put("message", e.getMessage());
            return map;
        }

    }


    /**
     * Generate html fragment with chapter details
     *
     * @param chapterId id of the chapter
     * @param model instance of the Model class. Used to pass attributes to the end user
     * @return HTML fragment which includes view of the chapter and it's topics
     */
    @GetMapping("/admin/getChapterEditDetails/{id}")
    public ModelAndView getChapterEditDetails(@PathVariable(name = "id") Integer chapterId, Model model) {
        if (chapterId == null || chapterId < 1 || chapterRepository.findById(chapterId).isEmpty()) {
            model.addAttribute("editChapter", null);
            return new ModelAndView("admin :: editChapter");
        }

        model.addAttribute("editChapter", chapterRepository.findById(chapterId).get());
        return new ModelAndView("admin :: editChapter");
    }


    /**
     * Generate html fragment with topic details
     *
     * @param topicId id of the topic
     * @param courseId id of the course
     * @param model instance of the Model class. Used to pass attributes to the end user
     * @return HTML fragment which allows user to edit a topic
     */
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


    /**
     * Get fragment containing whole course preview
     *
     * @param courseId id of the course
     * @param model instance of the Model class. Used to pass attributes to the end user
     * @return HTML fragment which contains a list of all courses
     */
    @GetMapping("/admin/getCourseInfo/{id}")
    public ModelAndView getCourseInfo(@PathVariable(name = "id", required = false) Integer courseId, Model model) {
        if (courseId == null || courseId < 1 || courseRepository.findById(courseId).isEmpty()) {
            model.addAttribute("selectedCourse", null);
            return new ModelAndView("admin :: course_preview");
        }

        model.addAttribute("selectedCourse", courseRepository.findById(courseId).get());
        return new ModelAndView("admin :: course_preview");
    }


    /**
     * Generade HTML fragment containing all chapters from the course
     *
     * @param courseId id of the course
     * @param model instance of the Model class. Used to pass attributes to the end user
     * @return HTML fragment which contains a list of all chapters inside a course
     */
    @GetMapping("/admin/getChaptersInfo/{id}")
    public ModelAndView getChaptersInfo(@PathVariable(name = "id", required = false) Integer courseId, Model model) {
        if (courseId == null || courseId < 1 || courseRepository.findById(courseId).isEmpty()) {
            model.addAttribute("selectedCourse", null);
            return new ModelAndView("admin :: chapter_select");
        }

        model.addAttribute("selectedCourse", courseRepository.findById(courseId).get());
        return new ModelAndView("admin :: chapter_select");
    }


    /**
     * Get all paths of videos that can be used as topic media
     *
     * @return List containing all video paths
     */
    private List<String> getVideoList() {
        File folder = new File(localVideoesPath);
        if (!folder.exists()) return new ArrayList<>();

        File[] listOfFiles = folder.listFiles();
        List<String> allVideos = new ArrayList<>();


        if (listOfFiles != null && listOfFiles.length > 0) {
            for (File file : listOfFiles) {
                if (file.isFile()) {
                    allVideos.add(file.getName());
                }
            }
        }

        return allVideos;
    }
}
