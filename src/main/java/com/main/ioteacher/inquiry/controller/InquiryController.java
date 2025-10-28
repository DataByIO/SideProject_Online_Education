package com.main.ioteacher.inquiry.controller;

import com.main.ioteacher.inquiry.entity.InquiryResponse;
import com.main.ioteacher.inquiry.entity.InquiryStatus;
import com.main.ioteacher.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * ✅ 사용자 문의 API 컨트롤러
 */
@Slf4j
@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;

    /** ✅ (1) 로그인 사용자의 문의 목록 조회 */
    @GetMapping("/my")
    public ResponseEntity<List<InquiryResponse>> getMyInquiries(
            @AuthenticationPrincipal User user
    ) {
        String userId = user.getUsername();
        log.debug("[INQUIRY] 사용자 문의 조회 요청 userId={}", userId);
        return ResponseEntity.ok(inquiryService.getUserInquiries(userId));
    }

    /** ✅ (2) 문의 등록 */
    @PostMapping
    public ResponseEntity<InquiryResponse> createInquiry(
            @AuthenticationPrincipal User user,
            @RequestPart("category") String category,
            @RequestPart("title") String title,
            @RequestPart("content") String content,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {
        String userId = user.getUsername();
        log.debug("[INQUIRY] 문의 등록 요청 userId={}, category={}, title={}", userId, category, title);
        InquiryResponse saved = inquiryService.createInquiry(userId, category, title, content, files);
        return ResponseEntity.ok(saved);
    }

    /** ✅ (3) 문의 완료 처리 */
    @PutMapping("/{inquiryId}/close")
    public ResponseEntity<InquiryResponse> closeInquiry(
            @PathVariable Long inquiryId,
            @AuthenticationPrincipal User user
    ) {
        String userId = user.getUsername();
        InquiryResponse updated = inquiryService.updateStatus(inquiryId, InquiryStatus.CLOSED, userId);
        return ResponseEntity.ok(updated);
    }

    /** ✅ (4) 문의 삭제 */
    @DeleteMapping("/{inquiryId}")
    public ResponseEntity<Void> deleteInquiry(
            @PathVariable Long inquiryId,
            @AuthenticationPrincipal User user
    ) {
        String userId = user.getUsername();
        inquiryService.deleteInquiry(inquiryId, userId);
        return ResponseEntity.noContent().build();
    }
}
