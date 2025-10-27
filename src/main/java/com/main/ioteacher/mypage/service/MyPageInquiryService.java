package com.main.ioteacher.mypage.service;

import com.main.ioteacher.mypage.entity.*;
import com.main.ioteacher.mypage.repository.InquiryRepository;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyPageInquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /** 문의 등록 */
    public InquiryResponse createInquiry(InquiryRequest req) {
        User user = userRepository.findByUserId(getUserId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));

        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .type(req.getType())
                .title(req.getTitle())
                .content(req.getContent())
                .status("WAITING")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Inquiry saved = inquiryRepository.save(inquiry);
        return toResponse(saved);
    }

    /** 내 문의내역 조회 */
    public List<InquiryResponse> getMyInquiries() {
        return inquiryRepository.findByUser_UserIdOrderByCreatedAtDesc(getUserId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** 문의 수정 */
    public InquiryResponse updateInquiry(Long id, InquiryRequest req) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의 없음"));
        inquiry.setTitle(req.getTitle());
        inquiry.setContent(req.getContent());
        inquiry.setUpdatedAt(LocalDateTime.now());
        return toResponse(inquiryRepository.save(inquiry));
    }

    /** 문의 삭제 */
    public void deleteInquiry(Long id) {
        inquiryRepository.deleteById(id);
    }

    private InquiryResponse toResponse(Inquiry i) {
        return InquiryResponse.builder()
                .id(i.getId())
                .title(i.getTitle())
                .type(i.getType())
                .content(i.getContent())
                .status(i.getStatus())
                .adminReply(i.getAdminReply())
                .createdAt(i.getCreatedAt())
                .updatedAt(i.getUpdatedAt())
                .build();
    }
}
