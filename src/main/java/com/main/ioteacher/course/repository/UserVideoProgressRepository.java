package com.main.ioteacher.course.repository;

import com.main.ioteacher.course.entity.UserVideoProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserVideoProgressRepository extends JpaRepository<UserVideoProgress, Long> {
    Optional<UserVideoProgress> findByUserIdAndCourseId(String userId, Long courseId);
}
