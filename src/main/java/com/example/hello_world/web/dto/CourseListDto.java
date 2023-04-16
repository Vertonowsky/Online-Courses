package com.example.hello_world.web.dto;

import com.example.hello_world.Category;
import org.springframework.util.StringUtils;

public class CourseListDto {

    private final Integer id;

    private final String name;

    private final Category category;

    private final String description;

    public CourseListDto(Integer id, String name, Category category, String description) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
    }


    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public String capitalizeCategory() {
        String s = String.valueOf(category).toLowerCase();
        return StringUtils.capitalize(s);
    }


}
