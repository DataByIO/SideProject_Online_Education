package com.main.ioteacher.community.controller;

import com.main.ioteacher.community.entity.ReportRequest;
import com.main.ioteacher.community.service.CommunityReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/community")
@RequiredArgsConstructor
public class CommunityReportController {

    private final CommunityReportService reportService;

    /** ✅ 게시글 신고 */
    @PostMapping("/posts/{postId}")
    public ResponseEntity<String> reportPost(
            @PathVariable Long postId,
            @RequestBody ReportRequest req
    ) {
        if (req.getUserId() == null || req.getReason() == null)
            throw new IllegalArgumentException("신고 정보가 올바르지 않습니다.");

        String result = reportService.reportPost(postId, req.getReason(), req.getUserId());
        return ResponseEntity.ok(result);
    }

    /** ✅ 댓글 신고 */
    @PostMapping("/comments/{commentId}")
    public ResponseEntity<String> reportComment(
            @PathVariable Long commentId,
            @RequestBody ReportRequest req
    ) {
        if (req.getUserId() == null || req.getReason() == null)
            throw new IllegalArgumentException("신고 정보가 올바르지 않습니다.");

        String result = reportService.reportComment(commentId, req.getReason(), req.getUserId());
        return ResponseEntity.ok(result);
    }
}
