package com.main.ioteacher.course.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseId;

    @Column(nullable = false, length = 20)
    private String type; // online / offline

    // ✅ 다국어 지원 JSON 필드
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> title;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> category;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> description;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> fullDescription;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> instructorName;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> instructorBio;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> instructorBio2;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, Object> language;

    // ✅ ENUM 타입으로 변경 (JSON 제거)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CourseStatus status; // upcoming, ongoing, closed

    // ✅ JSON 배열 필드
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, List<String>> learningGoals;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, List<String>> targetAudience;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private List<String> descriptionImgs;

    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private Map<String, List<String>> curriculum;

    // ✅ 일반 필드
    private Integer priceKrw;
    private Double ratingAvg;
    private Integer durationMinutes;
    private String videoUrl;
    private String imageUrl;
    private String instructorProfileImage;
    private Integer totalStudents;
    private Boolean certificate;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime updatedAt;

    public Course(Long courseId) {
        this.courseId = courseId;
    }
}
