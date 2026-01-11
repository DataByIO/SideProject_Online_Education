package com.main.ioteacher.course.repository;

import com.main.ioteacher.course.entity.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {

    /** ✅ 특정 유저의 전체 수강내역 (기존 유지) */
    List<CourseEnrollment> findByUserId(String userId);

    /** ✅ 특정 유저 + 강의 조합으로 수강신청 조회 */
    Optional<CourseEnrollment> findByUserIdAndCourseId(String userId, Long courseId);

    /** ✅ 이미 신청 여부 확인 */
    boolean existsByUserIdAndCourseId(String userId, Long courseId);

    /** ✅ 특정 강의에 승인 상태로 등록 여부 (리뷰 검증용) */
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
    @Query(value = """
        SELECT
            d.course_id AS courseId,
            c.user_id AS userId,
            d.type AS type,
            c.status AS status,
            e.last_watched_at AS lastWatchedAt,
            e.duration_seconds AS durationSeconds,
            e.progress_rate AS progressRate,
            d.certificate AS certificate,
            d.title AS title,
            d.description AS description,
            d.image_url AS imageUrl,
            c.requested_at AS updatedAt,
            d.start_date AS startDate,
            d.end_date AS endDate
        FROM course_enrollments c
        LEFT JOIN course_progress e
            ON e.course_id = c.course_id
            AND e.user_id = c.user_id
        LEFT JOIN courses d
            ON d.course_id = c.course_id
        WHERE c.user_id = :userId
""", nativeQuery = true)
    List<Map<String, Object>> findInternalApprovedCourses(String userId);


    @Query(value = """
SELECT
    ep.program_id AS courseId,
    er.user_id AS userId,
    'external' AS type,
    ep.status AS status,
    NULL AS lastWatchedAt,
    NULL AS durationSeconds,
    NULL AS progressRate,
    NULL AS certificate,
    ep.title AS title,
    ep.subtitle AS description,
    ep.image_url AS imageUrl,
    er.updated_at AS updatedAt,
    ep.start_date AS startDate,
    ep.end_date AS endDate
FROM external_program_applications er
LEFT JOIN external_programs ep
    ON er.program_id = ep.program_id
WHERE er.user_id = :userId
""", nativeQuery = true)
    List<Map<String, Object>> findExternalApprovedCourses(String userId);


}
