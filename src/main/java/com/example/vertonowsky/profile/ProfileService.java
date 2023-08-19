package com.example.vertonowsky.profile;

import com.example.vertonowsky.chapter.Chapter;
import com.example.vertonowsky.course.dto.CourseDto;
import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.course.model.CourseOwned;
import com.example.vertonowsky.topic.model.FinishedTopic;
import com.example.vertonowsky.topic.model.Topic;
import com.example.vertonowsky.user.User;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ProfileService {


    /**
     * Calculate what is the user's progress on every of his courses
     *
     * @param user user object
     * @return ArrayList containing owned courses with their completion progress
     */
    public List<CourseOwned> calculateCourseCompletion(User user) {
        List<CourseOwned> courseOwnedDtos = new LinkedList<>();

        List<Integer> finishedTopicsIds = user.getFinishedTopics().stream().map((finishedTopic -> finishedTopic.getTopic().getId())).toList();

        for (CourseOwned courseOwned : user.getCoursesOwned()) {


//            Set<Chapter> chapters = courseOwned.getCourse().getChapters();
//            if (chapters == null) continue;

//            chapters.stream().filter((chapter -> chapter.getTopics().in)).


//            CourseDto courseDto = CourseSerializer.serialize(courseOwned.getCourse());;
//            CourseOwnedDto courseOwnedDto = CourseSerializer.serialize(courseDto);
//            courseOwnedDto.setExpiryDate(co.getExpiryDate());
//            int mainId = courseOwnedDto.getId();
//            int topicCount = co.getCourse().getTopicCount();


            if (user.getFinishedTopics() == null) continue;

//            for (FinishedTopic ft : user.getFinishedTopics()) {
//                if (ft.getTopic().getChapter().getCourse().getId().equals(mainId)) {
//                    courseOwnedDto.setFinishedTopics(courseOwnedDto.getFinishedTopics() + 1);
//                }
//            }
//
//            int percentage = (int)((courseOwnedDto.getFinishedTopics() / (double)topicCount) * 100);
//            if (percentage > 100) percentage = 100;
//            courseOwnedDto.setPercentage(percentage);
//
//
//            courseOwnedDtos.add(courseOwnedDto);
        }
        return courseOwnedDtos;
    }





    public void calculateCourseCompletion(List<CourseDto> courseDtos, User user) {
        List<CourseOwned> courseOwnedList = user.getCoursesOwned();
        Set<Integer> finishedTopicsIds = user.getFinishedTopics().stream().map((finishedTopic -> finishedTopic.getTopic().getId())).collect(Collectors.toSet());

        for (CourseOwned courseOwned : courseOwnedList) {
            Course course = courseOwned.getCourse();
            Set<Chapter> chapters = course.getChapters();
            if (chapters == null) continue;

            Set<Topic> topics = chapters.stream().flatMap((chapter -> chapter.getTopics().stream())).collect(Collectors.toSet());

            int finishedCount = (int) topics.stream().map(Topic::getId).filter(finishedTopicsIds::contains).count();
            int topicCount = topics.size();

            CourseDto courseDto = courseDtos.stream().filter(dto -> dto.getCourseId().equals(course.getId())).findFirst().orElse(null);
            if (courseDto != null) {
                int percentage = (int) ((finishedCount / (double) topicCount) * 100);
                if (percentage > 100) percentage = 100;

                courseDto.setFinishedTopics(finishedCount);
                courseDto.setPercentage(percentage);
            }
        }
    }





    /**
     * Calculate what is user's progress in recent days based on topics completion
     *
     * @param user userF object
     * @return HashMap containing data about recent progress
     */
    public List<RecentProgressDto> calculateRecentProgress(User user) {
        List<RecentProgressDto> recentProgressDtoList = new LinkedList<>();
        Set<FinishedTopic> finishedTopics = user.getFinishedTopics();
        OffsetDateTime now = OffsetDateTime.now();

        recentProgressDtoList.add(getFinishedTopicsCount(finishedTopics, now, 1));
        recentProgressDtoList.add(getFinishedTopicsCount(finishedTopics, now, 7));
        recentProgressDtoList.add(getFinishedTopicsCount(finishedTopics, now, 30));

        return recentProgressDtoList;
    }


    private RecentProgressDto getFinishedTopicsCount(Set<FinishedTopic> finishedTopics, OffsetDateTime now, Integer days) {
        RecentProgressDto dto = new RecentProgressDto();
        int count = (int) finishedTopics.stream().filter(finishedTopic -> finishedTopic.getDate().isAfter(now.minusDays(days))).count();
        int percentage = Math.min((int)((count / (double)days) * 100), 100);

        dto.setCount(count);
        dto.setPercentage(percentage);

        if (days.equals(1)) dto.setRecentProgressType(RecentProgressType.LAST_DAY);
        if (days.equals(7)) dto.setRecentProgressType(RecentProgressType.LAST_WEEK);
        if (days.equals(30)) dto.setRecentProgressType(RecentProgressType.LAST_MONTH);

        return dto;
    }

}
