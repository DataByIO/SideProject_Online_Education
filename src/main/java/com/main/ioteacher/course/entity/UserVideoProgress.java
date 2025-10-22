package com.main.ioteacher.course.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_video_progress")
@Getter
@Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserVideoProgress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private Long courseId;
    private Integer watchedSec;
    private Boolean completed;
    private LocalDateTime lastWatched;
}

