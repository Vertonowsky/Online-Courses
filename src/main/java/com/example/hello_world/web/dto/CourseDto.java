package com.example.hello_world.web.dto;

import com.example.hello_world.Category;

public class CourseDto {

    private Integer id;

    private String name;

    private Category category;

    public CourseDto(Integer id, String name, Category category) {
        this.id = id;
        this.name = name;
        this.category = category;
    }




    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}