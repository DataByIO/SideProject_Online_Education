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

    @PostMapping("/posts/{postId}/report")
    public ResponseEntity<String> reportPost(
            @PathVariable Long postId,
            @RequestBody ReportRequest req
    ) {
        if (req.getUserId() == null || req.getReason() == null)
            throw new IllegalArgumentException("신고 정보가 올바르지 않습니다.");

        // ✅ reasonDetail 함께 전달
        String result = reportService.reportPost(
                postId,
                req.getReason(),
                req.getUserId(),
                req.getReasonDetail()
        );

        return ResponseEntity.ok(result);
    }
}
