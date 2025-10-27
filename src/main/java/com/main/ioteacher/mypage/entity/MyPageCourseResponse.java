package com.main.ioteacher.mypage.entity;

import lombok.*;
import java.time.LocalDate;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class MyPageCourseResponse {
    private Long courseId;
    private String title;
    private String type;       // online / offline / external
    private String status;     // ongoing / completed / pending
    private LocalDate enrolledAt;
    private int progress;      // 온라인만 표시
    private String imageUrl;
}
