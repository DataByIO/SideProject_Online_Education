package com.main.ioteacher.course.service;

import com.main.ioteacher.course.entity.CourseEnrollment;
import com.main.ioteacher.course.repository.CourseEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseEnrollmentService {

    private final CourseEnrollmentRepository enrollmentRepo;

    /**
     * ✅ 특정 강좌에 대한 사용자 신청 상태 조회
     */
    public CourseEnrollment getEnrollment(String userId, Long courseId) {
        return enrollmentRepo.findByUserIdAndCourseId(userId, courseId).orElse(null);
    }

    /**
     * ✅ 수강 신청
     * 이미 존재할 경우 예외 발생 (중복 방지)
     */
    public CourseEnrollment requestEnrollment(String userId, Long courseId) {
        if (enrollmentRepo.existsByUserIdAndCourseId(userId, courseId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 신청된 강의입니다.");
        }

        CourseEnrollment enrollment = CourseEnrollment.builder()
                .userId(userId)
                .courseId(courseId)
                .status("PENDING")
                .requestedAt(LocalDateTime.now())
                .build();

        return enrollmentRepo.save(enrollment);
    }

    /**
     * ✅ 수강 취소 (PENDING 상태만 가능)
     */
    public void cancelEnrollment(String userId, Long courseId) {
        Optional<CourseEnrollment> opt = enrollmentRepo.findByUserIdAndCourseId(userId, courseId);

        if (opt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "수강 신청 내역이 없습니다.");
        }

        CourseEnrollment enrollment = opt.get();

        if (!"PENDING".equals(enrollment.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "승인된 신청은 취소할 수 없습니다.");
        }

        enrollmentRepo.delete(enrollment);
    }

    /**
     * ✅ 관리자 승인 처리
     * status → APPROVED, approvedAt 갱신
     */
    public CourseEnrollment approveEnrollment(Long enrollmentId) {
        CourseEnrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 신청이 존재하지 않습니다."));

        enrollment.setStatus("APPROVED");
        enrollment.setApprovedAt(LocalDateTime.now());
        return enrollmentRepo.save(enrollment);
    }

    /**
     * ✅ 관리자 거절 처리
     * status → REJECTED, approvedAt 갱신
     */
    public CourseEnrollment rejectEnrollment(Long enrollmentId) {
        CourseEnrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 신청이 존재하지 않습니다."));

        enrollment.setStatus("REJECTED");
        enrollment.setApprovedAt(LocalDateTime.now());
        return enrollmentRepo.save(enrollment);
    }

    /**
     * ✅ 리뷰 등록 시 승인된 수강생인지 확인 (검증용)
     */
    public boolean isApprovedEnrolled(String userId, Long courseId) {
        return enrollmentRepo.isApprovedEnrolled(userId, courseId);
    }
}
