package com.example.vertonowsky.course.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;


@Embeddable
public class CourseKey implements Serializable {

    @Column(name = "course_id")
    private Integer courseId;

    @Column(name = "user_id")
    private Integer userId;


    public CourseKey() {

    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass())
            return false;

        CourseKey comparing = (CourseKey) o;
        return (this.courseId.equals(comparing.courseId) && this.userId.equals(comparing.userId));
    }

    @Override
    public int hashCode() {
        return Objects.hash(courseId, userId);
    }


}
