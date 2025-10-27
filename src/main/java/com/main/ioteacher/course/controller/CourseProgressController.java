package com.main.ioteacher.course.controller;

import com.main.ioteacher.course.entity.CourseProgress;
import com.main.ioteacher.course.service.CourseProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseProgressController {

    private final CourseProgressService progressService;

    /** ✅ 인증 유저 ID 추출 */
    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /** ✅ 진도율 업데이트 */
    @PostMapping("/{courseId}/progress")
    public ResponseEntity<String> updateProgress(
            @PathVariable Long courseId,
            @RequestBody ProgressRequest request) {

        progressService.updateProgress(
                getUserId(),
                courseId,
                request.getWatchedSec(),
                request.getDurationSec(),
                request.isCompleted()
        );
        return ResponseEntity.ok("Progress updated successfully");
    }

    /** ✅ 특정 강의의 진도율 조회 */
    @GetMapping("/{courseId}/progress")
    public ResponseEntity<CourseProgress> getProgress(@PathVariable Long courseId) {
        CourseProgress progress = progressService.getProgress(getUserId(), courseId);
        return ResponseEntity.ok(progress);
    }

    /** ✅ 내부 요청 DTO */
    @lombok.Data
    public static class ProgressRequest {
        private int watchedSec;
        private int durationSec;
        private boolean completed;
    }
}
