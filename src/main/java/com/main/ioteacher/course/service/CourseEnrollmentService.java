package com.main.ioteacher.course.service;

import com.main.ioteacher.course.entity.CourseEnrollment;
import com.main.ioteacher.course.entity.MyApprovedCourseResponse;
import com.main.ioteacher.course.repository.CourseEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CourseEnrollmentService {

    private final CourseEnrollmentRepository enrollmentRepo;

    /** ============================
     *  🔹 특정 강좌에 대한 신청 여부 조회
     * ============================ */
    public CourseEnrollment getEnrollment(String userId, Long courseId) {
        return enrollmentRepo.findByUserIdAndCourseId(userId, courseId).orElse(null);
    }

    /** ============================
     *  🔹 수강 신청
     * ============================ */
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

    /** ============================
     *  🔹 수강 취소
     * ============================ */
    public void cancelEnrollment(String userId, Long courseId) {
        CourseEnrollment enrollment = enrollmentRepo.findByUserIdAndCourseId(userId, courseId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "수강 신청 내역이 없습니다."));

        if (!"PENDING".equals(enrollment.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "승인된 신청은 취소할 수 없습니다.");
        }

        enrollmentRepo.delete(enrollment);
    }

    /** ============================
     *  🔹 관리자 승인
     * ============================ */
    public CourseEnrollment approveEnrollment(Long enrollmentId) {
        CourseEnrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 신청이 존재하지 않습니다."));

        enrollment.setStatus("APPROVED");
        enrollment.setApprovedAt(LocalDateTime.now());

        return enrollmentRepo.save(enrollment);
    }

    /** ============================
     *  🔹 관리자 거절
     * ============================ */
    public CourseEnrollment rejectEnrollment(Long enrollmentId) {
        CourseEnrollment enrollment = enrollmentRepo.findById(enrollmentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "해당 신청이 존재하지 않습니다."));

        enrollment.setStatus("REJECTED");
        enrollment.setApprovedAt(LocalDateTime.now());

        return enrollmentRepo.save(enrollment);
    }

    /** ============================
     *  🔹 리뷰 등록용 - 승인 확인
     * ============================ */
    public boolean isApprovedEnrolled(String userId, Long courseId) {
        return enrollmentRepo.isApprovedEnrolled(userId, courseId);
    }

    /* =======================================================
       🔥 안전 변환기(Boolean / String / Number → Integer/Double)
       ======================================================= */

    private Integer toInt(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.intValue();

        String s = v.toString().trim().toLowerCase();
        if (s.equals("true")) return 1;
        if (s.equals("false")) return 0;

        try { return Integer.parseInt(s); }
        catch (Exception e) { return null; }
    }

    private Double toDouble(Object v) {
        if (v == null) return null;
        if (v instanceof Number n) return n.doubleValue();

        String s = v.toString().trim().toLowerCase();
        if (s.equals("true")) return 1.0;
        if (s.equals("false")) return 0.0;

        try { return Double.parseDouble(s); }
        catch (Exception e) { return null; }
    }

    private String toStr(Object v) {
        if (v == null) return null;

        // JSON/Text가 byte[] 로 오는 문제 해결
        if (v instanceof byte[] bytes) {
            return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
        }

        return v.toString();
    }


    private LocalDateTime toLDT(Object v) {
        if (v instanceof java.sql.Timestamp t) return t.toLocalDateTime();
        return null;
    }

    /* =======================================================
       🔥 mapToResponse (Raw row → DTO)
       ======================================================= */
    private MyApprovedCourseResponse convert(Map<String, Object> row) {

        return MyApprovedCourseResponse.builder()
                .courseId(((Number) row.get("courseId")).longValue())
                .userId(toStr(row.get("userId")))
                .type(toStr(row.get("type")))
                .status(toStr(row.get("status")))

                .title(toStr(row.get("title")))
                .description(toStr(row.get("description")))
                .imageUrl(toStr(row.get("imageUrl")))

                .certificate(toInt(row.get("certificate")))
                .durationSeconds(toInt(row.get("durationSeconds")))
                .progressRate(toDouble(row.get("progressRate")))

                .lastWatchedAt(toLDT(row.get("lastWatchedAt")))
                .updatedAt(toLDT(row.get("updatedAt")))

                .startDate(toStr(row.get("startDate")))
                .endDate(toStr(row.get("endDate")))
                .build();
    }

    /* =======================================================
       🔥 마이페이지: 승인된 강의 + 진도율 통합 조회
       ======================================================= */
    public List<MyApprovedCourseResponse> getApprovedCoursesWithProgress(String userId) {

        List<Map<String, Object>> internal = enrollmentRepo.findInternalApprovedCourses(userId);
        List<Map<String, Object>> external = enrollmentRepo.findExternalApprovedCourses(userId);

        List<Map<String, Object>> raw = new ArrayList<>();
        raw.addAll(internal);
        raw.addAll(external);

        // 🔥 boolean / tinyint / bit → 문자열 → 에러 문제 해결됨
        return raw.stream()
                .map(this::convert)
                .toList();
    }
}
