package com.main.ioteacher.community.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.ioteacher.community.entity.*;
import com.main.ioteacher.community.repository.*;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * ✅ 커뮤니티 신고 서비스
 * - 게시글 및 댓글 신고 로직 통합
 * - Enum 변환 + 기타(reasonDetail) 입력 처리
 * - 하루 5회 신고 제한 + 중복 신고 방지
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CommunityReportService {

    private final CommunityPostRepository postRepo;
    private final CommunityCommentRepository commentRepo;
    private final CommunityReportRepository reportRepo;
    private final UserRepository userRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final int MAX_REPORTS_PER_DAY = 5;

    // ------------------------------------------------------------------------
    // ✅ 게시글 신고
    // ------------------------------------------------------------------------
    public synchronized String reportPost(Long postId, String reasonInput, String reporterId, String reasonDetail) {
        User reporter = userRepo.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 신고자 ID입니다."));

        // ✅ 하루 신고 횟수 제한
        if (reportRepo.countTodayReportsByUser(reporterId, LocalDate.now()) >= MAX_REPORTS_PER_DAY)
            return "하루 신고 가능 횟수(5회)를 초과했습니다.";

        // ✅ 중복 신고 방지
        if (reportRepo.existsByReporter_UserIdAndTargetTypeAndTargetId(
                reporterId, CommunityReport.TargetType.POST, postId))
            return "이미 이 게시글을 신고하셨습니다.";

        // ✅ Enum 변환 및 기타 사유 처리
        CommunityReport.Reason reason = CommunityReport.Reason.fromString(reasonInput);
        String detail = reason == CommunityReport.Reason.OTHER
                ? Optional.ofNullable(reasonDetail).orElse("기타 사유")
                : reason.getDisplayName();

        // ✅ 신고 저장
        reportRepo.save(CommunityReport.builder()
                .reporter(reporter)
                .targetType(CommunityReport.TargetType.POST)
                .targetId(postId)
                .reason(reason)
                .reasonDetail(detail)
                .reason_ko(detail)
                .createdAt(LocalDateTime.now())
                .build());

        // ✅ 게시글 신고 카운트 및 사유 누적
        CommunityPost post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        post.setReportCount(post.getReportCount() + 1);

        List<Map<String, String>> reasons = parseReasons(post.getReportReasons());
        reasons.add(getMultilingualReason(reason, detail));

        post.setReportReasons(toJson(reasons));
        postRepo.save(post);

        return "게시글 신고가 접수되었습니다.";
    }

    // ------------------------------------------------------------------------
    // ✅ 댓글 신고
    // ------------------------------------------------------------------------
    public synchronized String reportComment(Long commentId, String reasonInput, String reporterId, String reasonDetail) {
        User reporter = userRepo.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 신고자 ID입니다."));

        // ✅ 하루 신고 횟수 제한
        if (reportRepo.countTodayReportsByUser(reporterId, LocalDate.now()) >= MAX_REPORTS_PER_DAY)
            return "하루 신고 가능 횟수(5회)를 초과했습니다.";

        // ✅ 중복 신고 방지
        if (reportRepo.existsByReporter_UserIdAndTargetTypeAndTargetId(
                reporterId, CommunityReport.TargetType.COMMENT, commentId))
            return "이미 이 댓글을 신고하셨습니다.";

        // ✅ Enum 변환 및 기타 사유 처리
        CommunityReport.Reason reason = CommunityReport.Reason.fromString(reasonInput);
        String detail = reason == CommunityReport.Reason.OTHER
                ? Optional.ofNullable(reasonDetail).orElse("기타 사유")
                : reason.getDisplayName();

        // ✅ 신고 저장
        reportRepo.save(CommunityReport.builder()
                .reporter(reporter)
                .targetType(CommunityReport.TargetType.COMMENT)
                .targetId(commentId)
                .reason(reason)
                .reasonDetail(detail)
                .reason_ko(detail)
                .createdAt(LocalDateTime.now())
                .build());

        // ✅ 댓글 신고 카운트 및 사유 누적
        CommunityComment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        comment.setReportCount(comment.getReportCount() + 1);

        List<Map<String, String>> reasons = Optional.ofNullable(comment.getReportReasons())
                .orElse(new ArrayList<>());
        reasons.add(getMultilingualReason(reason, detail));
        comment.setReportReasons(reasons);

        commentRepo.save(comment);
        return "댓글 신고가 접수되었습니다.";
    }

    // ------------------------------------------------------------------------
    // ✅ 공용 유틸
    // ------------------------------------------------------------------------

    /** ✅ 다국어 신고 사유 구성 */
    private Map<String, String> getMultilingualReason(CommunityReport.Reason reason, String detail) {
        return switch (reason) {
            case SPAM -> Map.of("reason_ko", "스팸/광고", "reason_en", "Spam/Advertisement");
            case INAPPROPRIATE -> Map.of("reason_ko", "부적절한 내용", "reason_en", "Inappropriate Content");
            case HARASSMENT -> Map.of("reason_ko", "괴롭힘/욕설", "reason_en", "Harassment/Abuse");
            case OTHER -> Map.of("reason_ko", detail, "reason_en", "Other: " + detail);
        };
    }

    /** ✅ JSON 문자열 → List<Map> 변환 */
    private List<Map<String, String>> parseReasons(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    /** ✅ List<Map> → JSON 문자열 직렬화 */
    private String toJson(List<Map<String, String>> reasons) {
        try {
            return objectMapper.writeValueAsString(reasons);
        } catch (Exception e) {
            return "[]";
        }
    }
}
