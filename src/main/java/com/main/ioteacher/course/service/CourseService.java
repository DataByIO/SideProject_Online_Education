package com.main.ioteacher.course.service;

import com.main.ioteacher.course.entity.Course;
import com.main.ioteacher.course.repository.CourseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepo;

    @Transactional
    public void incrementViewCount(Long courseId) {
        courseRepo.incrementViewCount(courseId); // ✅ 반환값 없음, 단순 실행
    }

}
