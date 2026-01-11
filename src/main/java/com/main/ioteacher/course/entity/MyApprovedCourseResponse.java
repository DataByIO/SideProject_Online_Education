package com.main.ioteacher.course.entity;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class MyApprovedCourseResponse {

    private Long courseId;
    private String userId;
    private String type;
    private String status;

    private String title;
    private String description;
    private String imageUrl;

    private Integer certificate;  // 존재할 수 있으니 Integer
    private LocalDateTime lastWatchedAt;
    private Integer durationSeconds;
    private Double progressRate;

    private String startDate;
    private String endDate;
    private LocalDateTime updatedAt;
}
