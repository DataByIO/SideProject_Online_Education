package com.main.ioteacher.mypage.service;

import com.main.ioteacher.course.entity.CourseEnrollment;
import com.main.ioteacher.course.repository.CourseEnrollmentRepository;
import com.main.ioteacher.mypage.entity.MyPageCourseResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageService {

    /** ✅ 수강신청 데이터 접근 */
    private final CourseEnrollmentRepository enrollmentRepository;

    /** ✅ 현재 로그인된 사용자 ID 가져오기 */
    private String getCurrentUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /** ✅ 내가 수강신청한 모든 강의 조회 */
    public List<MyPageCourseResponse> getMyEnrollments() {
        String userId = getCurrentUserId();

        // Enrollment 엔티티 기준으로 수정 (CourseEnrollment)
        List<CourseEnrollment> enrollments = enrollmentRepository.findByUserId(userId);

        // MyPageCourseResponse 변환
        return enrollments.stream().map(e -> MyPageCourseResponse.builder()
                .courseId(e.getCourseId())
                .title("강의 제목 로딩 예정") // Course title은 조인 대신 프론트 캐싱 or 별도 쿼리로 처리
                .type("online")              // 간단히 type 지정 (필요 시 Course 조인 추가)
                .status(e.getStatus())
                .enrolledAt(LocalDate.from(e.getRequestedAt()))
                .progress(0)                 // 진도율 API 연동 후 반영
                .imageUrl(null)              // 강의 이미지 URL도 Course 조인 후 반영
                .build()
        ).collect(Collectors.toList());
    }
}
