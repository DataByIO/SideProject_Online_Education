package com.main.ioteacher.course.controller;

import com.main.ioteacher.course.entity.CourseEnrollment;
import com.main.ioteacher.course.service.CourseEnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class CourseEnrollmentController {

    private final CourseEnrollmentService enrollmentService;

    // 1. 특정 강의 + 특정 유저 신청 상태 확인
    @GetMapping("/{courseId}/{userId}")
    public ResponseEntity<?> getEnrollment(@PathVariable Long courseId, @PathVariable String userId) {
        CourseEnrollment enrollment = enrollmentService.getEnrollment(userId, courseId);
        if (enrollment == null) {
            return ResponseEntity.ok(Map.of("status", "NONE"));
        }
        return ResponseEntity.ok(Map.of("status", enrollment.getStatus()));
    }

    // 2. 수강 신청
    @PostMapping("/{courseId}/{userId}")
    public CourseEnrollment requestEnrollment(@PathVariable Long courseId,
                                              @PathVariable String userId) {
        return enrollmentService.requestEnrollment(userId, courseId);
    }

    // 3. 수강 취소
    @DeleteMapping("/{courseId}/{userId}")
    public void cancelEnrollment(@PathVariable Long courseId, @PathVariable String userId) {
        enrollmentService.cancelEnrollment(userId, courseId);
    }

    // 3. 관리자 승인
    @PutMapping("/approve/{enrollmentId}")
    public CourseEnrollment approveEnrollment(@PathVariable Long enrollmentId) {
        return enrollmentService.approveEnrollment(enrollmentId);
    }
}

