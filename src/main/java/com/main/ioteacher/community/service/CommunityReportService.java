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
import java.util.*;

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

    /** ✅ 게시글 신고 */
    public synchronized String reportPost(Long postId, String reason, String reporterId) {
        User reporter = userRepo.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 신고자 ID입니다."));

        if (reportRepo.countTodayReportsByUser(reporterId, LocalDate.now()) >= MAX_REPORTS_PER_DAY)
            return "하루 신고 가능 횟수(5회)를 초과했습니다.";

        if (reportRepo.existsByReporterUserIdAndTargetTypeAndTargetId(
                reporterId, String.valueOf(CommunityReport.TargetType.POST), postId))
            return "이미 이 게시글을 신고하셨습니다.";

        reportRepo.save(CommunityReport.builder()
                .reporter(reporter)
                .targetType(CommunityReport.TargetType.POST)
                .targetId(postId)
                .reason(CommunityReport.Reason.valueOf(reason.toUpperCase()))
                .build());

        CommunityPost post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        post.setReportCount(post.getReportCount() + 1);

        List<Map<String, String>> reasons = parseReasons(post.getReportReasons());
        reasons.add(getMultilingualReason(reason));

        post.setReportReasons(toJson(reasons));
        postRepo.save(post);

        return "게시글 신고 완료";
    }

    /** ✅ 댓글 신고 */
    public synchronized String reportComment(Long commentId, String reason, String reporterId) {
        User reporter = userRepo.findById(reporterId)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 신고자 ID입니다."));

        if (reportRepo.countTodayReportsByUser(reporterId, LocalDate.now()) >= MAX_REPORTS_PER_DAY)
            return "하루 신고 가능 횟수(5회)를 초과했습니다.";

        if (reportRepo.existsByReporterUserIdAndTargetTypeAndTargetId(
                reporterId, String.valueOf(CommunityReport.TargetType.COMMENT), commentId))
            return "이미 이 댓글을 신고하셨습니다.";

        reportRepo.save(CommunityReport.builder()
                .reporter(reporter)
                .targetType(CommunityReport.TargetType.COMMENT)
                .targetId(commentId)
                .reason(CommunityReport.Reason.valueOf(reason.toUpperCase()))
                .build());

        CommunityComment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        comment.setReportCount(comment.getReportCount() + 1);

        List<Map<String, String>> reasons =
                Optional.ofNullable(comment.getReportReasons()).orElse(new ArrayList<>());

        reasons.add(getMultilingualReason(reason));
        comment.setReportReasons(reasons);

        commentRepo.save(comment);
        return "댓글 신고 완료";
    }

    // ------------------------------------------------------
    // ✅ 공통 유틸 메서드
    // ------------------------------------------------------

    private Map<String, String> getMultilingualReason(String code) {
        return switch (code.toLowerCase()) {
            case "spam" -> Map.of("reason_ko", "스팸/광고", "reason_en", "Spam/Advertisement");
            case "inappropriate" -> Map.of("reason_ko", "부적절한 내용", "reason_en", "Inappropriate Content");
            case "harassment" -> Map.of("reason_ko", "괴롭힘/욕설", "reason_en", "Harassment/Abuse");
            default -> Map.of("reason_ko", "기타", "reason_en", "Other");
        };
    }

    private List<Map<String, String>> parseReasons(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<>() {});
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private String toJson(List<Map<String, String>> reasons) {
        try {
            return objectMapper.writeValueAsString(reasons);
        } catch (Exception e) {
            return "[]";
        }
    }
}
