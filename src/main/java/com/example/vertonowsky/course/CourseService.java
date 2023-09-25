package com.example.vertonowsky.course;

import com.example.vertonowsky.Collections;
import com.example.vertonowsky.advantage.AdvantageDto;
import com.example.vertonowsky.advantage.AdvantageType;
import com.example.vertonowsky.chapter.Chapter;
import com.example.vertonowsky.chapter.ChapterDto;
import com.example.vertonowsky.chapter.ChapterSerializer;
import com.example.vertonowsky.chapter.CourseInfoDto;
import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.course.model.CourseOwned;
import com.example.vertonowsky.course.repository.CourseOwnedRepository;
import com.example.vertonowsky.course.repository.CourseRepository;
import com.example.vertonowsky.topic.TopicDto;
import com.example.vertonowsky.topic.TopicSerializer;
import com.example.vertonowsky.topic.TopicService;
import com.example.vertonowsky.topic.TopicStatus;
import com.example.vertonowsky.topic.model.FinishedTopic;
import com.example.vertonowsky.topic.model.Topic;
import com.example.vertonowsky.user.User;
import com.example.vertonowsky.user.service.UserService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.vertonowsky.course.CourseSerializer.Task.BASE;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseOwnedRepository courseOwnedRepository;
    private final TopicService topicService;
    private final UserService userService;

    public CourseService(CourseRepository courseRepository, CourseOwnedRepository courseOwnedRepository, TopicService topicService, UserService userService) {
        this.courseRepository = courseRepository;
        this.courseOwnedRepository = courseOwnedRepository;
        this.topicService = topicService;
        this.userService = userService;
    }

    /**
     * Get List of Courses filtered by type and category
     *
     * @param typeFilters List of selected course types
     * @param categoryFilters List of selected categories
     * @param limit number of elements to return from the original list
     * @return List of courses sorted by given filters
     */
    public List<CourseDto> getCoursesWithCriteria(List<String> typeFilters, List<String> categoryFilters, int limit) {
        if (typeFilters.isEmpty() && categoryFilters.isEmpty()) {
            List<Course> courses = courseRepository.findAll(); //return all courses if no filters are applied
            if (limit > 0) courses = limitNumberOfElements(courses, limit);
            return courses.stream().map(course -> CourseSerializer.serialize(course, BASE)).toList();
        }

        //change category filters from List<String> to List<Category> [Enum type]
        List<CategoryType> catFilters = categoryFilters.stream().map(CategoryType::valueOf).toList();

        List<Course> courses = courseRepository.findAllWithCondition(typeFilters, catFilters);
        if (limit > 0) courses = limitNumberOfElements(courses, limit);

        return courses.stream().map(course -> CourseSerializer.serialize(course, BASE)).toList();
    }

    /**
     * Reduce size of given List
     *
     * @param list collection of elemenets
     * @param limit number of returned elements.
     * @return List with reduced size
     */
    private <T> List<T> limitNumberOfElements(List<T> list, int limit) {
        if (limit <= 0) return list;
        return list.stream().limit(limit).toList();
    }

    /**
     * Update heading of the courses list (counts courses and shows current category filter)
     *
     * @param size number of displayed courses
     * @param categoryFilters List of String storing selected category types
     * @return HashMap containing prefix and postfix for topPanel
     */
    public HashMap<String, String> generateCoursesListHeading(int size, List<String> categoryFilters) {
        String prefix = " kurs";
        if (size > 1 && size <= 4) prefix = " kursy";
        if (size >= 5) prefix = " kursów";

        StringBuilder category = new StringBuilder();
        if (categoryFilters == null || categoryFilters.isEmpty()) {
            category = new StringBuilder("Wszystko");
        } else {
            for (int i = 0; i < categoryFilters.size(); i++) {
                category.append(StringUtils.capitalize(categoryFilters.get(i).toLowerCase()));
                if (i < categoryFilters.size() - 1) category.append(", ");
            }
        }

        HashMap<String, String> response = new HashMap<>();
        response.put("topPanelPrefix", String.format("%d %s", size, prefix));
        response.put("topPanelCategory", category.toString());

        return response;
    }

    public List<String> convertCategoryParamList(ObjectMapper mapper, String categoryFilters) throws JsonProcessingException {
        List<String> categoryParamList = mapper.readValue(categoryFilters, new TypeReference<>(){}); //Convert string in JSON format to List<String>
        categoryParamList.replaceAll(String::toUpperCase); //make every string contain only big characters
        return categoryParamList;
    }



    public List<CourseDto> getCourseWithFilters(ObjectMapper mapper, String typeFilters, String categoryFilters, Integer limit) throws JsonProcessingException {
        List<String> typeParamList = mapper.readValue(typeFilters, new TypeReference<>(){}); //Convert string in JSON format to List<String>
        List<String> categoryParamList = convertCategoryParamList(mapper, categoryFilters);

        return getCoursesWithCriteria(typeParamList, categoryParamList, limit < 0 ? 0 : limit); // limit = 0, means  there is no limit for course count
    }


    public Course get(Integer id) {
        return courseRepository.findById(id).orElse(null);
    }

    public List<Course> listAll() {
        return courseRepository.findAll();
    }

    public List<String> listTypes() {
        return courseRepository.findAllTypes();
    }

    public List<String> listCategories() {
        return courseRepository.findAllCategories();
    }


    public Course get(Integer id, CourseQueryType courseQueryType) {
        if (courseQueryType.equals(CourseQueryType.BASE))
            return courseRepository.findById(id).orElse(null);

        if (courseQueryType.equals(CourseQueryType.ADVANTAGES))
            return courseRepository.findByIdWithAdvantages(id).orElse(null);

        if (courseQueryType.equals(CourseQueryType.CHAPTERS))
            return courseRepository.findByIdWithChapters(id).orElse(null);

        if (courseQueryType.equals(CourseQueryType.PAYMENTS))
            return courseRepository.findByIdWithPaymentHistories(id).orElse(null);

        return courseRepository.findByIdAllData(id).orElse(null);
    }

    public Topic getFirstTopic(Course course) {
        if (Collections.isNullOrEmpty(course.getChapters()))
            return null;

        return course.getChapters().stream().flatMap(chapter -> chapter.getTopics().stream()).findFirst().orElse(null);
    }

    public CourseInfoDto getDetailedCourseInfo(User user, Course course, Integer topicId) {
        Topic selectedTopic = (topicId == null ? getFirstTopic(course) : topicService.getTopicById(course, topicId));
        if (selectedTopic == null) return null;

        if (course.getChapters() == null) return null;

        Set<FinishedTopic> finishedTopics = Optional.ofNullable(user).map(User::getFinishedTopics).orElse(null);
        List<Chapter> chapters = course.getChapters().stream().toList();
        if (Collections.isNullOrEmpty(chapters)) return null;


        List<ChapterDto> chaptersDto = new LinkedList<>();
        int counter = 0;
        boolean selectedTopicFinished = false; //mark if active topic has been finished
        for (Chapter chapter : chapters) {
            List<TopicDto> topics = new LinkedList<>();
            for (Topic t : chapter.getTopics()) {

                boolean blocked = false;  //refers to current topic status (is it blocked?)
                if (user != null) {
                    if (userService.isCourseValid(user, course)) {
                        TopicStatus status = TopicStatus.AVAILABLE;
                        if (userService.isTopicFinished(finishedTopics, t.getId()))
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
            ChapterDto chapterDto = ChapterSerializer.serialize(chapter);
            chapterDto.setTopics(topics);
            chaptersDto.add(chapterDto);
        }

        return new CourseInfoDto(chaptersDto, TopicSerializer.serialize(selectedTopic), selectedTopicFinished);
    }


    public void calculateAdvantages(CourseDto courseDto) {
        List<AdvantageDto> advantages = courseDto.getAdvantages();
        if (advantages == null) return;
        for (AdvantageDto advantage : advantages) {
            if (advantage.getAdvantageType().equals(AdvantageType.TOTAL_DURATION)) {
                long totalDuration = getTotalDuration(courseDto);
                String suffix = " godzina";
                if (totalDuration > 1 && totalDuration < 5) suffix = " godziny";
                if (totalDuration >= 5) suffix = " godzin";

                String title = advantage.getTitle();
                title = title.replace("%ss%", String.format("%d %s", totalDuration, suffix));
                advantage.setTitle(title);
            }

            if (advantage.getAdvantageType().equals(AdvantageType.TASKS_COUNT)) {
                int tasksCount = 100;
                String suffix = " zadanie";
                if (tasksCount > 1 && tasksCount < 5) suffix = " zadania";
                if (tasksCount >= 5) suffix = " zadań";

                String title = advantage.getTitle();
                title = title.replace("%ss%", String.format("%d %s", tasksCount, suffix));
                advantage.setTitle(title);
            }
        }
    }


    @Transactional
    public void updateExistingCourseExpiration(User user, Course course, OffsetDateTime since, OffsetDateTime till) {
        courseOwnedRepository.updateExistingCourseExpiration(user, course, since, till);
    }


    public void newCourseOwned(User user, Course course, OffsetDateTime since, OffsetDateTime till) {
        CourseOwned courseOwned = new CourseOwned();
        courseOwned.setStatus(CourseStatus.NEW);
        courseOwned.setBuyDate(since);
        courseOwned.setExpiryDate(till);
        courseOwned.setCourse(course);
        courseOwned.setUser(user);

        courseOwnedRepository.save(courseOwned);
    }


    public long getTotalDuration(CourseDto courseDto) {
        Set<TopicDto> topics = courseDto.getChapters().stream().flatMap(chapter -> {
            if (chapter.getTopics() == null) return null;
            return chapter.getTopics().stream();
        }).collect(Collectors.toSet());

        if (Collections.isNullOrEmpty(topics)) return 0;
        long totalSeconds = topics.stream().mapToLong(topic -> topic.getDuration() == null ? 0 : topic.getDuration()).sum();
        return totalSeconds / 3600;
    }

}