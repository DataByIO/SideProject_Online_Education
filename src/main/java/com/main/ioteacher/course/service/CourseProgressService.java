package com.main.ioteacher.course.service;

import com.main.ioteacher.course.entity.CourseProgress;
import com.main.ioteacher.course.entity.CourseProgressResponse;
import com.main.ioteacher.course.repository.CourseProgressRepository;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import com.main.ioteacher.course.entity.Course;
import com.main.ioteacher.course.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseProgressService {

    private final CourseProgressRepository progressRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    /** ✅ 진도율 업데이트 */
    @Transactional
    public void updateProgress(String userId, Long courseId, int watchedSec, int durationSec, boolean completed) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        CourseProgress progress = progressRepository
                .findByUser_UserIdAndCourse_CourseId(userId, courseId)
                .orElse(CourseProgress.builder()
                        .user(user)
                        .course(course)
                        .watchedSeconds(0)
                        .durationSeconds(0)
                        .completed(false)
                        .build());

        progress.setWatchedSeconds(Math.max(watchedSec, progress.getWatchedSeconds()));
        progress.setDurationSeconds(durationSec);
        progress.setCompleted(completed);
        progress.setWatchedSeconds(Math.min(progress.getWatchedSeconds(), progress.getDurationSeconds()));
        progress.setUpdatedAt(LocalDateTime.now());

        progressRepository.save(progress);
    }

    /** ✅ 특정 강의 진도율 조회 */
    @Transactional(readOnly = true)
    public CourseProgress getProgress(String userId, Long courseId) {
        return progressRepository
                .findByUser_UserIdAndCourse_CourseId(userId, courseId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<CourseProgressResponse> getAllProgress(String userId) {
        List<CourseProgress> progresses = progressRepository.findByUser_UserId(userId);
        return progresses.stream()
                .map(progress -> new CourseProgressResponse(progress))
                .toList();
    }



}
