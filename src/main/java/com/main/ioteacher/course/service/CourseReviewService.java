package com.main.ioteacher.course.service;

import com.main.ioteacher.course.entity.Course;
import com.main.ioteacher.course.entity.CourseReview;
import com.main.ioteacher.course.repository.CourseEnrollmentRepository;
import com.main.ioteacher.course.repository.CourseRepository;
import com.main.ioteacher.course.repository.CourseReviewRepository;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseReviewService {

    private final CourseReviewRepository reviewRepo;
    private final CourseRepository courseRepo;
    private final CourseEnrollmentRepository enrollmentRepo;
    private final UserRepository userRepo;

    /** ✅ 강의별 리뷰 조회 */
    public List<CourseReview> getReviewsByCourseId(Long courseId) {
        return reviewRepo.findAllByCourseIdOrderByReviewedAtDesc(courseId);
    }

    /** ✅ 리뷰 등록 (모든 검증 포함) */
    @Transactional
    public CourseReview addReview(String userId, Long courseId, CourseReview payload) {
        // 1️⃣ 강의 존재 확인
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "강의를 찾을 수 없습니다."));

        // 2️⃣ 사용자 존재 확인
        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));

        // 3️⃣ 수강 승인 여부
        boolean enrolled = enrollmentRepo.isApprovedEnrolled(userId, courseId);
        if (!enrolled)
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "수강 승인된 사용자만 리뷰를 등록할 수 있습니다.");

        // 4️⃣ 중복 리뷰 방지
        if (reviewRepo.existsByCourse_CourseIdAndUser_UserId(courseId, userId))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 리뷰를 작성했습니다.");

        // 5️⃣ 리뷰 저장
        CourseReview review = CourseReview.builder()
                .course(course)
                .user(user)
                .rating(safeRating(payload.getRating()))
                .comment(payload.getComment())
                .reviewedAt(LocalDateTime.now())
                .build();

        CourseReview saved = reviewRepo.save(review);

        // 6️⃣ 평균 평점 갱신
        updateCourseRatingAvg(courseId);

        return saved;
    }

    /** ✅ 리뷰 수정 */
    @Transactional
    public CourseReview updateReview(String userId, Long reviewId, CourseReview payload) {
        CourseReview review = reviewRepo.findByIdWithUser(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getUserId().equals(userId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 리뷰만 수정할 수 있습니다.");

        review.setRating(safeRating(payload.getRating()));
        review.setComment(payload.getComment());
        review.setReviewedAt(LocalDateTime.now());

        CourseReview saved = reviewRepo.save(review);
        updateCourseRatingAvg(review.getCourse().getCourseId());
        return saved;
    }

    /** ✅ 리뷰 삭제 */
    @Transactional
    public void deleteReviewByUser(Long reviewId, String userId) {
        CourseReview review = reviewRepo.findByReviewIdAndUser_UserId(reviewId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "본인의 리뷰만 삭제할 수 있습니다."));
        reviewRepo.delete(review);
        updateCourseRatingAvg(review.getCourse().getCourseId());
    }

    /** ✅ 평균 평점 갱신 */
    private void updateCourseRatingAvg(Long courseId) {
        Double avg = reviewRepo.findAverageRating(courseId);
        double rounded = BigDecimal.valueOf(avg == null ? 0.0 : avg)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "강의를 찾을 수 없습니다."));
        course.setRatingAvg(rounded);
        courseRepo.save(course);
    }

    /** ✅ 평점 보정 */
    private int safeRating(Integer rating) {
        int r = (rating == null) ? 0 : rating;
        return Math.max(1, Math.min(5, r));
    }
}

