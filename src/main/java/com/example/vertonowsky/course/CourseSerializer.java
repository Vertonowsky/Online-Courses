package com.example.vertonowsky.course;

import com.example.vertonowsky.advantage.AdvantageSerializer;
import com.example.vertonowsky.chapter.ChapterSerializer;
import com.example.vertonowsky.course.model.Course;
import com.example.vertonowsky.course.model.CourseOwned;
import com.example.vertonowsky.payment.PaymentHistorySerializer;
import org.hibernate.Hibernate;

import java.util.HashMap;
import java.util.Map;

public class CourseSerializer {

    private static final Map<Task, SerializationTask> serializationTasks = initializeSerializationTasks();

    public enum Task {
        BASE, ADVANTAGES, CHAPTERS, PAYMENTS, PRICE;
    }

    private interface SerializationTask {
        void serialize(CourseDto courseDto, Course course);
    }

    private static Map<Task, SerializationTask> initializeSerializationTasks() {
        Map<Task, SerializationTask> tasks = new HashMap<>();
        tasks.put(Task.BASE, CourseSerializer::base);
        tasks.put(Task.ADVANTAGES, CourseSerializer::advantages);
        tasks.put(Task.CHAPTERS, CourseSerializer::chapters);
        tasks.put(Task.PAYMENTS, CourseSerializer::payments);
        tasks.put(Task.PRICE, CourseSerializer::price);
        return tasks;
    }

    private static void base(CourseDto courseDto, Course course) {
        courseDto.setCourseId(course.getId());
        courseDto.setCategoryType(course.getCategoryType());
        courseDto.setName(course.getName());
        courseDto.setDescription(course.getDescription());
    }

    private static void expiryDate(CourseDto courseDto, CourseOwned courseOwned) {
        courseDto.setExpiryDate(courseOwned.getExpiryDate());
    }

    private static void advantages(CourseDto courseDto, Course course) {
        if (Hibernate.isInitialized(course.getAdvantages()) && course.getAdvantages() != null)
            courseDto.setAdvantages(course.getAdvantages().stream().map(AdvantageSerializer::serialize).toList());
    }

    private static void chapters(CourseDto courseDto, Course course) {
        if (Hibernate.isInitialized(course.getChapters()) && course.getChapters() != null)
            courseDto.setChapters(course.getChapters().stream().map(ChapterSerializer::serialize).toList());
    }

    private static void payments(CourseDto courseDto, Course course) {
        if (Hibernate.isInitialized(course.getPaymentHistories()) && course.getPaymentHistories() != null)
            courseDto.setPaymentHistories(course.getPaymentHistories().stream().map(PaymentHistorySerializer::serialize).toList());
    }

    private static void price(CourseDto courseDto, Course course) {
        courseDto.setPrice(course.getPrice());
        courseDto.setPricePromotion(course.getPricePromotion());
    }

    public static CourseDto serialize(Course course, Task... tasks) {
        CourseDto dto = new CourseDto();

        for (Task task : tasks) {
            SerializationTask serializationTask = serializationTasks.get(task);
            if (serializationTask != null) {
                serializationTask.serialize(dto, course);
            }
        }

        return dto;
    }



    public static CourseDto serialize(CourseOwned courseOwned, Task... tasks) {
        CourseDto dto = serialize(courseOwned.getCourse(), tasks);

        expiryDate(dto, courseOwned);

        return dto;
    }

}
