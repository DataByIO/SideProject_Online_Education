package com.main.ioteacher.community.controller;

import com.main.ioteacher.community.entity.CommentResponse;
import com.main.ioteacher.community.entity.CommentRequest;
import com.main.ioteacher.community.entity.ReportRequest;
import com.main.ioteacher.community.service.CommunityCommentService;
import com.main.ioteacher.config.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 커뮤니티 댓글 컨트롤러
 */
@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityCommentController {

    private final CommunityCommentService commentService;
    private final JwtUtil jwtUtil;

    /** ✅ 댓글 목록 조회 */
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        String userId = null;
        try {
            userId = jwtUtil.getUserIdFromRequest(request);
        } catch (Exception ignored) {}
        return ResponseEntity.ok(commentService.getComments(postId, userId));
    }

    /** ✅ 댓글 작성 */
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest request,
            HttpServletRequest requestObj
    ) {
        String userId = jwtUtil.getUserIdFromRequest(requestObj);
        request.setUserId(userId);
        return ResponseEntity.ok(commentService.addComment(postId, request));
    }

    /** ✅ 댓글 수정 */
    @PutMapping("/comments/{commentId}")
    public ResponseEntity<Void> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentRequest request,
            HttpServletRequest requestObj
    ) {
        String userId = jwtUtil.getUserIdFromRequest(requestObj);
        request.setUserId(userId);
        commentService.updateComment(commentId, request);
        return ResponseEntity.ok().build();
    }

    /** ✅ 댓글 삭제 */
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    /** ✅ 댓글 좋아요 */
    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable Long commentId,
            HttpServletRequest request
    ) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        return ResponseEntity.ok(commentService.toggleCommentLike(commentId, userId));
    }

    /** ✅ 댓글 신고 (중복 방지 + 하루 5회 제한) */
    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<String> reportComment(
            @PathVariable Long commentId,
            @RequestBody ReportRequest req,
            HttpServletRequest request
    ) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        req.setUserId(userId);
        String result = commentService.reportComment(commentId, req);
        return ResponseEntity.ok(result);
    }
}
