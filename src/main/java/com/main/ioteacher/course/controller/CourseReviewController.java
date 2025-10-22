package com.main.ioteacher.course.controller;

import com.main.ioteacher.course.entity.Course;
import com.main.ioteacher.course.entity.CourseReview;
import com.main.ioteacher.course.service.CourseReviewService;
import com.main.ioteacher.user.entity.User;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class CourseReviewController {

    private final CourseReviewService reviewService;

    /** ✅ 리뷰 목록 조회 */
    @GetMapping("/{courseId}")
    public ResponseEntity<List<ReviewResponse>> getReviews(@PathVariable Long courseId) {
        var reviews = reviewService.getReviewsByCourseId(courseId)
                .stream()
                .map(ReviewResponse::from)
                .toList();
        return ResponseEntity.ok(reviews);
    }

    /** ✅ 리뷰 등록 */
    @PostMapping("/{courseId}")
    public ResponseEntity<ReviewResponse> addReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long courseId,
            @RequestBody CourseReview payload
    ) {
        if (userDetails == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");

        String authUserId = userDetails.getUsername();
        log.info("[addReview] user={}, course={}", authUserId, courseId);

        CourseReview saved = reviewService.addReview(authUserId, courseId, payload);
        return ResponseEntity.ok(ReviewResponse.from(saved));
    }

    /** ✅ 리뷰 수정 */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long reviewId,
            @RequestBody CourseReview payload
    ) {
        if (userDetails == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");

        String authUserId = userDetails.getUsername();
        var updated = reviewService.updateReview(authUserId, reviewId, payload);
        return ResponseEntity.ok(ReviewResponse.from(updated));
    }

    /** ✅ 리뷰 삭제 */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<?> deleteReview(
            @PathVariable Long reviewId,
            Authentication authentication
    ) {
        if (authentication == null || authentication.getName() == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");

        reviewService.deleteReviewByUser(reviewId, authentication.getName());
        return ResponseEntity.ok("리뷰 삭제 완료");
    }

    // ✅ 응답 DTO
    @Data @Builder
    public static class ReviewResponse {
        private Long id;
        private int rating;
        private String comment;
        private String reviewedAt;
        private String userId;
        private String name;
        private String profileImageUrl;

        public static ReviewResponse from(CourseReview review) {
            User user = review.getUser();
            var fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            return ReviewResponse.builder()
                    .id(review.getReviewId())
                    .rating(review.getRating())
                    .comment(review.getComment())
                    .reviewedAt(review.getReviewedAt() != null ? review.getReviewedAt().format(fmt) : null)
                    .userId(user != null ? user.getUserId() : null)
                    .name(user != null ? user.getName() : "익명")
                    .profileImageUrl(user != null ? user.getProfileImageUrl() : null)
                    .build();
        }
    }
}
