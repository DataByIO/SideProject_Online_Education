package com.main.ioteacher.course.controller;

import com.main.ioteacher.course.entity.Course;
import com.main.ioteacher.course.repository.CourseEnrollmentRepository;
import com.main.ioteacher.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.nio.file.Files;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
public class VideoStreamController {

    private final CourseRepository courseRepository;
    private final CourseEnrollmentRepository enrollmentRepository;

    // ✅ 로컬 비디오 저장 경로 (설정 파일로 분리 가능)
    private static final String VIDEO_DIR = "/Users/kimnoah/IdeaProjects/ioteacher/uploads/videos/";


    @GetMapping("/stream/{courseId}")
    public ResponseEntity<?> stream(@PathVariable Long courseId) {
        // 1️⃣ 인증 사용자 확인
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"인증이 필요합니다.\"}");
        }

        String userId = auth.getName(); // JwtAuthenticationFilter 에서 설정된 username(userId)
        log.debug("[VideoStream] 요청 사용자 ID: {}", userId);

        // 2️⃣ 수강 승인 여부 확인
        boolean approved = enrollmentRepository.existsByUserIdAndCourseIdAndStatus(userId, courseId, "APPROVED");
        if (!approved) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"이 강의를 시청할 권한이 없습니다.\"}");
        }

        // 3️⃣ 강의 및 영상 파일 확인
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"강의를 찾을 수 없습니다.\"}");
        }

        String videoUrl = course.getVideoUrl();
        if (!StringUtils.hasText(videoUrl)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"강의 영상이 등록되어 있지 않습니다.\"}");
        }

        String filename = Paths.get(videoUrl).getFileName().toString();
        Path path = Paths.get(VIDEO_DIR, filename);
        File file = path.toFile();

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"영상 파일을 찾을 수 없습니다.\"}");
        }

        // 4️⃣ 영상 리소스 반환
        Resource resource = new FileSystemResource(file);
        MediaType mediaType = MediaTypeFactory.getMediaType(filename)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        log.info("[VideoStream] 사용자 '{}' 에게 영상 '{}' 스트리밍 시작", userId, file.getName());
        log.info("[VideoStream] 요청된 파일 경로: {}", course.getVideoUrl());
        log.info("[VideoStream] 파일 존재 여부: {}", Files.exists(Paths.get(course.getVideoUrl())));


        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getName() + "\"")
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .contentLength(file.length())
                .body(resource);
    }
}
