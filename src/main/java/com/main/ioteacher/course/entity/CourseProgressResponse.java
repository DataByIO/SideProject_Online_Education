package com.main.ioteacher.course.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * ✅ CourseProgress 전용 DTO
 * - course_progress + course 정보 결합
 * - entity 폴더 내에서만 관리
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseProgressResponse {

    private Long courseId;         // 강의 ID
    private String title;          // 강의 제목 (ko 기준)
    private LocalDateTime lastWatchedAt; // ✅ 추가
    private String description;    // 강의 설명
    private String imageUrl;       // 대표 이미지 URL
    private Double progressRate;   // 진도율 (%)
    private boolean completed;     // 수료 여부

    /** ✅ Entity → DTO 변환 생성자 */
    public CourseProgressResponse(CourseProgress progress) {
        if (progress.getCourse() != null) {
            this.courseId = progress.getCourse().getCourseId();
            this.lastWatchedAt = progress.getLastWatchedAt();
            this.title = progress.getCourse().getTitle() != null
                    ? (String) progress.getCourse().getTitle().getOrDefault("ko", "제목 없음")
                    : "제목 없음";
            this.description = progress.getCourse().getDescription() != null
                    ? (String) progress.getCourse().getDescription().getOrDefault("ko", "")
                    : "";
            this.imageUrl = progress.getCourse().getImageUrl();
        } else {
            this.courseId = null;
            this.lastWatchedAt = null;
            this.title = "제목 없음";
            this.description = "";
            this.imageUrl = null;
        }

        this.progressRate = progress.getProgressRate() != null ? progress.getProgressRate() : 0.0;
        this.completed = progress.isCompleted();
    }
}
