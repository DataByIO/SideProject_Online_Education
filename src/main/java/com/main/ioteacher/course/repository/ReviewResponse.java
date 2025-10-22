package com.main.ioteacher.course.repository;

import com.main.ioteacher.course.entity.CourseReview;
import java.time.LocalDateTime;

public record ReviewResponse(
        Long reviewId,
        String userId,
        String profileImageUrl,
        String comment,
        int rating,
        LocalDateTime reviewedAt
) {
    public static ReviewResponse from(CourseReview r) {
        return new ReviewResponse(
                r.getReviewId(),
                r.getUser().getUserId(),
                r.getUser().getProfileImageUrl(),
                r.getComment(),
                r.getRating(),
                r.getReviewedAt()
        );
    }
}
