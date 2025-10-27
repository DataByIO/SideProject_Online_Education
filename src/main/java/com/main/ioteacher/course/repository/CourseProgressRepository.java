package com.main.ioteacher.course.repository;

import com.main.ioteacher.course.entity.CourseProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CourseProgressRepository extends JpaRepository<CourseProgress, Long> {

    Optional<CourseProgress> findByUser_UserIdAndCourse_CourseId(String userId, Long courseId);

    List<CourseProgress> findByUser_UserId(String userId);

}
