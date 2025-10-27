package com.main.ioteacher.course.entity;

import com.main.ioteacher.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "course_progress",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id"}))
@Getter @Setter
@Builder @NoArgsConstructor @AllArgsConstructor
public class CourseProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id", nullable = false)
    private Long progressId;

    /** ✅ 사용자 연결 (users.user_id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    /** ✅ 강의 연결 (courses.course_id) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false)
    private Course course;

    /** ✅ 현재까지 시청 시간(초) */
    @Column(name = "watched_sec", nullable = false)
    private int watchedSec;

    /** ✅ 총 강의 길이(초) */
    @Column(name = "duration_sec", nullable = false)
    private int durationSec;

    /** ✅ 완강 여부 */
    @Column(name = "completed", nullable = false)
    private boolean completed;

    /** ✅ 최종 갱신 시각 */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
