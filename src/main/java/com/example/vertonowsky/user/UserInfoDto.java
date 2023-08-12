package com.example.vertonowsky.user;

import com.example.vertonowsky.avatar.AvatarDto;
import com.example.vertonowsky.course.dto.CourseOwnedDto;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;


@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto {

    private List<CourseOwnedDto> courses;

    private AvatarDto avatar;

    public List<CourseOwnedDto> getCourses() {
        return courses;
    }

    public void setCourses(List<CourseOwnedDto> courses) {
        this.courses = courses;
    }

    public AvatarDto getAvatar() {
        return avatar;
    }

    public void setAvatar(AvatarDto avatar) {
        this.avatar = avatar;
    }
}
