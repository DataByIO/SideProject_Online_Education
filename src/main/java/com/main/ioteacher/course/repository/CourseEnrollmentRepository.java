package com.main.ioteacher.course.repository;

import com.main.ioteacher.course.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    /**
     * ✅ 수강 승인 여부 확인
     *
     */
    boolean existsByUserIdAndCourseIdAndStatus(String userId, Long courseId, String status);

    /**
     * ✅ 특정 유저 + 강의 조합으로 수강신청 조회
     * (없을 경우 Optional.empty() 반환)
     */
    Optional<CourseEnrollment> findByUserIdAndCourseId(String userId, Long courseId);

    /**
     * ✅ 특정 유저가 이미 해당 강의를 신청했는지 여부
     * (중복 방지용)
     */
    boolean existsByUserIdAndCourseId(String userId, Long courseId);

    /**
     * ✅ 리뷰 등록 시: 해당 유저가 승인된 상태로 수강 중인지 여부 확인
     * (APPROVED 상태만 true)
     */
    @Query("""
        SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
        FROM CourseEnrollment e
        WHERE e.userId = :userId
          AND e.courseId = :courseId
          AND e.status = 'APPROVED'
    """)
    boolean isApprovedEnrolled(String userId, Long courseId);
}
