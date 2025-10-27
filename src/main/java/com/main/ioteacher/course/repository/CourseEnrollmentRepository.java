package com.main.ioteacher.course.repository;

import com.main.ioteacher.course.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    /** ✅ 특정 유저의 전체 수강내역 (마이페이지용) */
    List<CourseEnrollment> findByUserId(String userId);

    /** ✅ 특정 유저 + 강의 조합으로 수강신청 조회 */
    Optional<CourseEnrollment> findByUserIdAndCourseId(String userId, Long courseId);

    /** ✅ 해당 유저가 특정 강의에 이미 신청했는지 여부 */
    boolean existsByUserIdAndCourseId(String userId, Long courseId);

    /** ✅ 특정 강의에 승인 상태로 등록된지 여부 (리뷰 가능 여부 체크용) */
    @Query("""
        SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
        FROM CourseEnrollment e
        WHERE e.userId = :userId
          AND e.courseId = :courseId
          AND e.status = 'APPROVED'
    """)
    boolean isApprovedEnrolled(String userId, Long courseId);

    /** ✅ 특정 유저가 해당 강의를 ‘승인된 상태’로 수강 중인지 여부 */
    boolean existsByUserIdAndCourseIdAndStatus(String userId, Long courseId, String status);
}
