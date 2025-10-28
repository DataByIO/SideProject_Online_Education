package com.main.ioteacher.inquiry.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * ✅ Inquiry + Answer 응답 DTO
 */
@Getter
@Builder
public class InquiryResponse {

    private Long inquiryId;
    private String category;
    private String title;
    private String content;
    private List<String> attachments;
    private String status;
    private String createdAt;
    private String updatedAt;
    private String userId;
    private String userName;
    private String userProfile;
    private AnswerResponse response; // ✅ 관리자 답변 추가

    /** ✅ 관리자 답변 DTO */
    @Getter
    @Builder
    public static class AnswerResponse {
        private Long answerId;
        private String adminId;
        private String content;
        private String createdAt;
    }

    /** ✅ 엔티티 → DTO 변환 */
    public static InquiryResponse from(Inquiry inquiry) {
        ObjectMapper mapper = new ObjectMapper();
        List<String> files = new ArrayList<>();
        try {
            if (inquiry.getAttachments() != null && !inquiry.getAttachments().isEmpty()) {
                files = mapper.readValue(inquiry.getAttachments(), new TypeReference<>() {});
            }
        } catch (Exception e) {
            files = new ArrayList<>();
        }

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        // ✅ 관리자 답변이 존재하는 경우 1건만 선택
        AnswerResponse answerDto = null;
        if (inquiry.getAnswers() != null && !inquiry.getAnswers().isEmpty()) {
            var answer = inquiry.getAnswers().get(0); // 1:1 관계로 가정
            answerDto = AnswerResponse.builder()
                    .answerId(answer.getAnswerId())
                    .adminId(answer.getAdmin() != null ? answer.getAdmin().getUserId() : null)
                    .content(answer.getContent())
                    .createdAt(answer.getCreatedAt() != null ? answer.getCreatedAt().format(fmt) : null)
                    .build();
        }

        return InquiryResponse.builder()
                .inquiryId(inquiry.getInquiryId())
                .category(inquiry.getCategory())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .attachments(files)
                .status(inquiry.getStatus().name())
                .createdAt(inquiry.getCreatedAt() != null ? inquiry.getCreatedAt().format(fmt) : null)
                .updatedAt(inquiry.getUpdatedAt() != null ? inquiry.getUpdatedAt().format(fmt) : null)
                .userId(inquiry.getUser() != null ? inquiry.getUser().getUserId() : null)
                .userName(inquiry.getUser() != null ? inquiry.getUser().getName() : null)
                .userProfile(inquiry.getUser() != null ? inquiry.getUser().getProfileImageUrl() : null)
                .response(answerDto)
                .build();
    }
}
