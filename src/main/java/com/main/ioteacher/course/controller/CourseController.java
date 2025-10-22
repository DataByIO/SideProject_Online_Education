package com.main.ioteacher.course.controller;

import com.main.ioteacher.course.entity.CourseResponse;
import com.main.ioteacher.course.entity.Course;
import com.main.ioteacher.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseRepository courseRepo;

    // ✅ 다국어 단일 강의 조회
    @GetMapping("/{id}")
    public CourseResponse getCourse(
            @PathVariable Long id,
            @RequestParam(defaultValue = "ko") String lang
    ) {
        Course course = courseRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Course not found"));
        return CourseResponse.from(course, lang);
    }

    // ✅ 다국어 목록 조회 (필터 지원)
    @GetMapping
    public List<CourseResponse> getCourses(
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "ko") String lang
    ) {
        List<Course> courses = (type != null)
                ? courseRepo.findByType(type)
                : courseRepo.findAll();
        return courses.stream()
                .map(c -> CourseResponse.from(c, lang))
                .toList();
    }
}
