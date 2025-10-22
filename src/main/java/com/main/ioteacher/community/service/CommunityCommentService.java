package com.main.ioteacher.community.service;

import com.main.ioteacher.community.entity.CommentResponse;
import com.main.ioteacher.community.entity.*;
import com.main.ioteacher.community.repository.CommunityCommentRepository;
import com.main.ioteacher.community.repository.CommunityPostRepository;
import com.main.ioteacher.community.repository.CommunityReportRepository;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityCommentService {

    private final CommunityCommentRepository commentRepository;
    private final CommunityPostRepository postRepository;
    private final UserRepository userRepository;
    private final CommunityReportRepository reportRepo;

    /** ✅ 게시글별 댓글 조회 (좋아요 상태 포함) */
    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long postId, String currentUserId) {
        return commentRepository.findByPost_PostId(postId).stream()
                .map(c -> CommentResponse.fromEntity(c, currentUserId))
                .collect(Collectors.toList());
    }

    /** ✅ 댓글 작성 */
    public CommentResponse addComment(Long postId, CommentRequest request) {
        CommunityPost post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        CommunityComment comment = CommunityComment.builder()
                .post(post)
                .user(user)
                .content(request.getContent())
                .likesCount(0)
                .reportCount(0)
                .likedUsers(new HashSet<>())
                .build();

        return CommentResponse.fromEntity(commentRepository.save(comment), request.getUserId());
    }

    /** ✅ 댓글 수정 */
    public void updateComment(Long commentId, CommentRequest request) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getUserId().equals(request.getUserId())) {
            throw new SecurityException("본인 댓글만 수정할 수 있습니다.");
        }

        comment.setContent(request.getContent());
    }

    /** ✅ 댓글 삭제 */
    public void deleteComment(Long commentId, String userId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (!comment.getUser().getUserId().equals(userId)) {
            throw new SecurityException("본인 댓글만 삭제할 수 있습니다.");
        }

        commentRepository.delete(comment);
    }

    /** ✅ 댓글 좋아요 토글 (한 번만 가능, 재클릭 시 취소) */
    public Map<String, Object> toggleCommentLike(Long commentId, String userId) {
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        if (comment.getLikedUsers() == null)
            comment.setLikedUsers(new HashSet<>());

        Set<String> likedUsers = comment.getLikedUsers();
        boolean isLiked;

        if (likedUsers.contains(userId)) {
            likedUsers.remove(userId);
            comment.setLikesCount(Math.max(0, comment.getLikesCount() - 1));
            isLiked = false;
        } else {
            likedUsers.add(userId);
            comment.setLikesCount(comment.getLikesCount() + 1);
            isLiked = true;
        }

        commentRepository.save(comment);

        Map<String, Object> result = new HashMap<>();
        result.put("liked", isLiked);
        result.put("likesCount", comment.getLikesCount());
        return result;
    }

    // -----------------------------------------------------------------
    // ✅ 댓글 신고 (중복 방지 + 하루 5회 제한)
    // -----------------------------------------------------------------
    public String reportComment(Long commentId, ReportRequest req) {
        User reporter = userRepository.findByUserId(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        CommunityComment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // ✅ 하루 신고 제한 (최대 5회)
        int todayCount = reportRepo.countTodayReportsByUser(req.getUserId(), LocalDate.now());
        if (todayCount >= 5) {
            throw new IllegalStateException("하루 신고 가능 횟수(5회)를 초과했습니다.");
        }

        // ✅ 중복 신고 방지
        boolean alreadyReported = reportRepo.existsByReporterUserIdAndTargetTypeAndTargetId(
                req.getUserId(), "COMMENT", commentId
        );
        if (alreadyReported) {
            throw new IllegalStateException("이미 신고한 댓글입니다.");
        }

        // ✅ 신고 등록
        CommunityReport report = CommunityReport.builder()
                .reporter(reporter)
                .targetType(CommunityReport.TargetType.COMMENT)
                .targetId(commentId)
                .reason(CommunityReport.Reason.valueOf(req.getReason().toUpperCase()))
                .createdAt(LocalDateTime.now())
                .build();
        reportRepo.save(report);


        // ✅ 댓글 신고 카운트 + 사유 누적
        comment.setReportCount(comment.getReportCount() + 1);
        String oldReasons = comment.getReportReasons().toString();
        if (oldReasons == null || oldReasons.isBlank() || oldReasons.equals("[]")) {
            comment.setReportReasons(List.of(Map.of("reason", req.getReason())));
        } else {
            List<Map<String, String>> updated = new ArrayList<>(comment.getReportReasons());
            updated.add(Map.of("reason", req.getReason()));
            comment.setReportReasons(updated);
        }

        commentRepository.save(comment);
        return "댓글 신고가 접수되었습니다.";
    }
}
