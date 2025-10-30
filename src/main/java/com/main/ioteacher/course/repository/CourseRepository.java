package com.main.ioteacher.course.repository;

import com.main.ioteacher.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByType(String type); // type=online/offline
    /**
     * ✅ 단순 조회수 +1 (성능 최적화용)
     *
     * @return
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Course c SET c.viewsCount = c.viewsCount + 1 WHERE c.courseId = :courseId")
    void incrementViewCount(Long courseId); // 또는 int로 반환 가능
}
