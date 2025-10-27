package com.main.ioteacher.mypage.service;

import com.main.ioteacher.course.entity.Course;
import com.main.ioteacher.enrollment.entity.Enrollment;
import com.main.ioteacher.enrollment.repository.EnrollmentRepository;
import com.main.ioteacher.mypage.entity.MyPageCourseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    private final EnrollmentRepository enrollmentRepository;

    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /** ✅ 내가 수강신청한 모든 강의 */
    public List<MyPageCourseResponse> getMyEnrollments() {
        String userId = getCurrentUserId();
        List<Enrollment> enrollments = enrollmentRepository.findByUser_UserId(userId);

        return enrollments.stream().map(e -> {
            Course c = e.getCourse();
            return MyPageCourseResponse.builder()
                    .courseId(c.getCourseId())
                    .title(c.getTitle())
                    .type(c.getType().name().toLowerCase())
                    .status(e.getStatus().name().toLowerCase())
                    .enrolledAt(e.getEnrolledAt())
                    .progress(c.getType().name().equals("ONLINE") ? e.getProgress() : 0)
                    .imageUrl(c.getImageUrl())
                    .build();
        }).collect(Collectors.toList());
    }
}
