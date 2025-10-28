package com.main.ioteacher.inquiry.service;

import com.main.ioteacher.inquiry.entity.Inquiry;
import com.main.ioteacher.inquiry.entity.InquiryAnswer;
import com.main.ioteacher.inquiry.entity.InquiryStatus;
import com.main.ioteacher.inquiry.repository.InquiryAnswerRepository;
import com.main.ioteacher.inquiry.repository.InquiryRepository;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ✅ InquiryAnswerService
 * - 관리자 답변 등록 / 수정 / 삭제 / 조회
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InquiryAnswerService {

    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    /** ✅ 답변 등록 (관리자) */
    public InquiryAnswer createAnswer(Long inquiryId, String adminId, String content) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의가 존재하지 않습니다."));
        User admin = userRepository.findByUserId(adminId)
                .orElseThrow(() -> new IllegalArgumentException("관리자 계정을 찾을 수 없습니다."));

        InquiryAnswer answer = InquiryAnswer.builder()
                .inquiry(inquiry)
                .admin(admin)
                .content(content)
                .build();

        // 문의 상태를 ANSWERED 로 자동 변경
        inquiry.setStatus(InquiryStatus.ANSWERED);
        inquiryRepository.save(inquiry);

        return inquiryAnswerRepository.save(answer);
    }

    /** ✅ 문의 ID로 답변 조회 */
    @Transactional(readOnly = true)
    public InquiryAnswer getAnswerByInquiryId(Long inquiryId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("문의가 존재하지 않습니다."));
        return inquiryAnswerRepository.findByInquiry(inquiry)
                .orElse(null);
    }

    /** ✅ 답변 수정 */
    public InquiryAnswer updateAnswer(Long answerId, String newContent) {
        InquiryAnswer answer = inquiryAnswerRepository.findById(answerId)
                .orElseThrow(() -> new IllegalArgumentException("답변이 존재하지 않습니다."));
        answer.setContent(newContent);
        return inquiryAnswerRepository.save(answer);
    }

    /** ✅ 답변 삭제 */
    public void deleteAnswer(Long answerId) {
        inquiryAnswerRepository.deleteById(answerId);
    }
}
