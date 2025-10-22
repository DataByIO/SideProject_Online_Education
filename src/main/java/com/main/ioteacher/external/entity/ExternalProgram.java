package com.main.ioteacher.external.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "external_programs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExternalProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long programId;

    private String category;

    /** ✅ 다국어 필드 (예: { "ko": "제목", "en": "Title" }) */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, String> title;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, String> subtitle;

    private String recruitmentPeriod;
    private String recruitmentCapacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ProgramStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, String> recruitmentTarget;

    private LocalDate examDate;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, String> selectionCriteria;

    private String resultNotice;
    private String contact;
    private String educationPeriod;
    private String mainImageUrl;

    /** ✅ 교육장 정보 예: { "ko": "서울특별시 강남구", "en": "Gangnam, Seoul" } */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, String> educationLocation;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, String> completionCriteria;

    /** ✅ 복수 혜택 예: { "ko": ["식사제공", "수료증"], "en": ["Meals", "Certificate"] } */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, List<String>> benefits;

    /** ✅ 일정 정보 예: [{ "date": "2025-03-01", "topic": "Orientation" }, ...] */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Map<String, Object>> schedule;

    // ✅ 강사 정보
    private String instructorName;
    private String instructorProfileImage;

    /** ✅ 강사 소개 다국어 */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, String> instructorBio;

    /** ✅ 강사 추가 소개 다국어 (복수 문장 등) */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, List<String>> instructorBio2;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
