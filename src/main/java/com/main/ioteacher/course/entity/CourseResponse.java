package com.main.ioteacher.course.entity;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class CourseResponse {

    private Long courseId;
    private String type;
    private String title;
    private String category;
    private String description;
    private String fullDescription;
    private String instructorProfileImage;
    private String instructorName;
    private String instructorBio;
    private String instructorBio2;
    private String language;
    private String status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime updatedAt;
    private Integer priceKrw;
    private Double ratingAvg;
    private Integer durationMinutes;
    private String videoUrl; // ✅ 단일 URL로 반환
    private String imageUrl;
    private Integer totalStudents;
    private Boolean certificate;
    private Integer viewsCount;
    private List<String> learningGoals;
    private List<String> targetAudience;
    private List<String> curriculum;
    private List<String> descriptionImgs;

    public static CourseResponse from(Course c, String lang) {
        return CourseResponse.builder()
                .courseId(c.getCourseId())
                .type(c.getType())
                .startDate(c.getStartDate())
                .endDate(c.getEndDate())
                .viewsCount(c.getViewsCount())
                .updatedAt(c.getUpdatedAt())
                .title(extract(c.getTitle(), lang))
                .category(extract(c.getCategory(), lang))
                .description(extract(c.getDescription(), lang))
                .fullDescription(extract(c.getFullDescription(), lang))
                .instructorProfileImage(c.getInstructorProfileImage())
                .instructorName(extract(c.getInstructorName(), lang))
                .instructorBio(extract(c.getInstructorBio(), lang))
                .instructorBio2(extract(c.getInstructorBio2(), lang))
                .language(extract(c.getLanguage(), lang))
                .status(c.getStatus() != null ? c.getStatus().name().toLowerCase() : "upcoming")
                .priceKrw(c.getPriceKrw())
                .ratingAvg(c.getRatingAvg())
                .durationMinutes(c.getDurationMinutes())
                .videoUrl(extractVideo(c, lang)) // ✅ 다국어 영상 선택
                .imageUrl(c.getImageUrl())
                .totalStudents(c.getTotalStudents())
                .certificate(c.getCertificate())
                .learningGoals(extractList(c.getLearningGoals(), lang))
                .targetAudience(extractList(c.getTargetAudience(), lang))
                .curriculum(extractList(c.getCurriculum(), lang))
                .descriptionImgs(c.getDescriptionImgs())
                .build();
    }

    private static String extract(Map<String, Object> json, String lang) {
        if (json == null) return null;
        Object value = json.getOrDefault(lang, json.get("ko"));
        return value != null ? value.toString() : null;
    }

    private static List<String> extractList(Map<String, List<String>> json, String lang) {
        if (json == null) return null;
        List<String> value = json.get(lang);
        if (value == null) value = json.get("ko");
        return value;
    }

    /** ✅ 온라인 강의 비디오 URL 다국어 처리 */
    private static String extractVideo(Course c, String lang) {
        if (!"online".equals(c.getType())) return c.getVideoUrl() != null ? c.getVideoUrl().get("ko") : null;
        if (c.getVideoUrl() == null) return null;
        return c.getVideoUrl().getOrDefault(lang, c.getVideoUrl().get("ko"));
    }
}
