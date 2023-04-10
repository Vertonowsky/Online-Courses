package com.example.hello_world.web.service;

import com.example.hello_world.Category;
import com.example.hello_world.persistence.repository.CourseRepository;
import com.example.hello_world.web.dto.CourseListDto;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class CourseService {


    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }





    /**
     * Get List of Courses filtered by type and category
     *
     * @param typeFilters List of selected course types
     * @param categoryFilters List of selected categories
     * @param limit number of elements to return from the original list
     * @return List of courses sorted by given filters
     */
    public List<CourseListDto> getCoursesWithCriteria(List<String> typeFilters, List<String> categoryFilters, int limit) {
        if (typeFilters.size() == 0 && categoryFilters.size() == 0) {
            List<CourseListDto> courses = courseRepository.findAllDtos(); //return all courses if no filters are applied
            if (limit > 0) return limitNumberOfElements(courses, limit);
            return courses;
        }


        //change category filters from List<String> to List<Category> [Enum type]
        List<Category> catFilters = new ArrayList<>();
        for (String s : categoryFilters) {
            try {
                Category cat = Category.valueOf(s);
                catFilters.add(cat);
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }

        List<CourseListDto> courses = courseRepository.findAllWithCondition(typeFilters, catFilters);
        if (limit > 0) return limitNumberOfElements(courses, limit);

        return courses;
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
        if (size > 0 && size <= 4) prefix = " kursy";
        if (size >= 5) prefix = " kursów";

        StringBuilder category = new StringBuilder();
        if (categoryFilters.size() == 0) category = new StringBuilder("Wszystko");
        if (categoryFilters.size() > 0) {
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

}
