package com.example.vertonowsky.profile;

import com.example.vertonowsky.course.dto.CourseOwnedDto;
import com.example.vertonowsky.course.model.CourseOwned;
import com.example.vertonowsky.topic.model.FinishedTopic;
import com.example.vertonowsky.user.User;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

@Service
public class ProfileService {


    /**
     * Calculate what is the user's progress on every of his courses
     *
     * @param user user object
     * @return ArrayList containing owned courses with their completion progress
     */
    public ArrayList<CourseOwnedDto> calculateCourseCompletion(User user) {
        ArrayList<CourseOwnedDto> courseOwnedDtos = new ArrayList<>();
        for (CourseOwned co : user.getCoursesOwned()) {
            CourseOwnedDto cod = new CourseOwnedDto(co.getCourse(), co.getExpiryDate());
            int mainId = cod.getCourse().getId();
            int topicCount = co.getCourse().getTopicCount();

            for (FinishedTopic ft : user.getFinishedTopics()) {
                if (ft.getTopic().getChapter().getCourse().getId().equals(mainId)) {
                    cod.setFinishedTopics(cod.getFinishedTopics() + 1);
                }
            }

            int percentage = (int)((cod.getFinishedTopics() / (double)topicCount) * 100);
            if (percentage > 100) percentage = 100;
            cod.setPercentage(percentage);


            courseOwnedDtos.add(cod);
        }
        return courseOwnedDtos;
    }





    /**
     * Calculate what is user's progress in recent days based on topics completion
     *
     * @param user user object
     * @return HashMap containing data about recent progress
     */
    public HashMap<String, Integer> calculateRecentProgress(User user) {
        HashMap<String, Integer> response = new HashMap<>();
        Date now = new Date(System.currentTimeMillis());
        LocalDate localDate1 = now.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int lastDay = 0, lastWeek = 0, lastMonth = 0;


        // Calculate how many goals have been reached recently
        for (FinishedTopic ft : user.getFinishedTopics()) {
            long difference = now.getTime() - ft.getDate().getTime();  // Difference in milliseconds between two dates (always positive)
            LocalDate localDate2 = ft.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

            if (localDate1.isEqual(localDate2))  // Check if it is the same day
                lastDay++;
            if (difference / (1000 * 60 * 60 * 24) <= 7)  // Difference in time converted to days
                lastWeek++;
            if (difference / (1000 * 60 * 60 * 24) <= 30)  // Difference in time converted to days
                lastMonth++;
        }

        // Make it be percentage
        int lastDayPercentage = Math.min(lastDay * 100, 100);
        int lastWeekPercentage = Math.min((int)((lastWeek / (double)7) * 100), 100);
        int lastMonthPercentage = Math.min((int)((lastMonth / (double)30) * 100), 100);

        response.put("lastDay", lastDay);
        response.put("lastWeek", lastWeek);
        response.put("lastMonth", lastMonth);
        response.put("lastDayPercentage", lastDayPercentage);
        response.put("lastWeekPercentage", lastWeekPercentage);
        response.put("lastMonthPercentage", lastMonthPercentage);
        return response;
    }

}
