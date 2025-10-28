package com.main.ioteacher.inquiry.controller;

import com.main.ioteacher.inquiry.entity.InquiryAnswer;
import com.main.ioteacher.inquiry.service.InquiryAnswerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ✅ 관리자 답변 API
 * - 답변 등록 / 조회 / 수정 / 삭제
 */
@RestController
@RequestMapping("/api/answers")
@RequiredArgsConstructor
public class InquiryAnswerController {

    private final InquiryAnswerService inquiryAnswerService;

    /** ✅ 답변 등록 */
    @PostMapping("/{inquiryId}/{adminId}")
    public ResponseEntity<InquiryAnswer> createAnswer(@PathVariable Long inquiryId,
                                                      @PathVariable String adminId,
                                                      @RequestBody String content) {
        return ResponseEntity.ok(inquiryAnswerService.createAnswer(inquiryId, adminId, content));
    }

    /** ✅ 특정 문의의 답변 조회 */
    @GetMapping("/{inquiryId}")
    public ResponseEntity<InquiryAnswer> getAnswerByInquiry(@PathVariable Long inquiryId) {
        return ResponseEntity.ok(inquiryAnswerService.getAnswerByInquiryId(inquiryId));
    }

    /** ✅ 답변 수정 */
    @PutMapping("/{answerId}")
    public ResponseEntity<InquiryAnswer> updateAnswer(@PathVariable Long answerId,
                                                      @RequestBody String content) {
        return ResponseEntity.ok(inquiryAnswerService.updateAnswer(answerId, content));
    }

    /** ✅ 답변 삭제 */
    @DeleteMapping("/{answerId}")
    public ResponseEntity<Void> deleteAnswer(@PathVariable Long answerId) {
        inquiryAnswerService.deleteAnswer(answerId);
        return ResponseEntity.noContent().build();
    }
}
