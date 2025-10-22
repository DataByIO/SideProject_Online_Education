package com.main.ioteacher.course.repository;

import com.main.ioteacher.course.entity.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {

    /** ✅ 리뷰 목록 조회 (User 정보까지 한 번에 fetch) */
    @Query("""
        SELECT r FROM CourseReview r
        JOIN FETCH r.user u
        WHERE r.course.courseId = :courseId
        ORDER BY r.reviewedAt DESC
    """)
    List<CourseReview> findAllByCourseIdOrderByReviewedAtDesc(@Param("courseId") Long courseId);

    /** ✅ 특정 강의 + 특정 유저의 리뷰 존재 여부 확인 */
    boolean existsByCourse_CourseIdAndUser_UserId(Long courseId, String userId);

    /** ✅ 강의 평균 평점 계산 */
    @Query("SELECT AVG(r.rating) FROM CourseReview r WHERE r.course.courseId = :courseId")
    Double findAverageRating(@Param("courseId") Long courseId);

    /** ✅ 리뷰 단건 조회 (유저 정보 포함) */
    @Query("SELECT r FROM CourseReview r JOIN FETCH r.user WHERE r.reviewId = :reviewId")
    Optional<CourseReview> findByIdWithUser(@Param("reviewId") Long reviewId);

    /** ✅ 특정 유저의 리뷰인지 검증용 (권한 확인용) */
    Optional<CourseReview> findByReviewIdAndUser_UserId(Long reviewId, String userId);
}
