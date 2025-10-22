package com.main.ioteacher.course.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "course_enrollments",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_enrollment_user_course", columnNames = {"user_id", "course_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseEnrollment {

    /** PK: 수강신청 고유 ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /** FK → courses.course_id */
    @Column(name = "course_id", nullable = false)
    private Long courseId;

    /** FK → users.user_id */
    @Column(name = "user_id", nullable = false, length = 32)
    private String userId;

    /** 상태 (PENDING / APPROVED / REJECTED) */
    @Column(nullable = false, length = 20)
    private String status = "PENDING";

    /** 신청 시각 */
    @Column(name = "requested_at", nullable = false, updatable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime requestedAt = LocalDateTime.now();

    /** 승인 시각 (승인 시에만 값 존재) */
    @Column(name = "approved_at")
    private LocalDateTime approvedAt;
}
