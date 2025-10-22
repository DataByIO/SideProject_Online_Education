package com.main.ioteacher.course.repository;

import com.main.ioteacher.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByType(String type); // type=online/offline
}
