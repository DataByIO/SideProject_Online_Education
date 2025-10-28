package com.main.ioteacher.inquiry.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.main.ioteacher.inquiry.entity.InquiryResponse;
import com.main.ioteacher.inquiry.entity.Inquiry;
import com.main.ioteacher.inquiry.entity.InquiryStatus;
import com.main.ioteacher.inquiry.repository.InquiryRepository;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * ✅ InquiryService
 * - 사용자 문의 등록 / 조회 / 상태 변경 / 삭제 관리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    /** ✅ 파일 저장 경로 */
    private static final String UPLOAD_DIR = "/Users/kimnoah/IdeaProjects/ioteacher/uploads/inquiry/";

    /**
     * ✅ (1) 사용자별 문의 목록 조회 (LazyInitializationException 방지)
     */
    public List<InquiryResponse> getUserInquiries(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        // ✅ fetch join으로 답변까지 한 번에 조회
        List<Inquiry> inquiries = inquiryRepository.findByUserWithAnswers(user);

        return inquiries.stream()
                .map(InquiryResponse::from)
                .collect(Collectors.toList());
    }


    /**
     * ✅ (2) 문의 등록
     */
    @Transactional
    public InquiryResponse createInquiry(String userId,
                                         String category,
                                         String title,
                                         String content,
                                         List<MultipartFile> files) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자 ID입니다."));

        // ✅ 파일 업로드 처리
        List<String> uploadedFiles = new ArrayList<>();
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file.isEmpty()) continue;
                try {
                    String ext = Optional.ofNullable(file.getOriginalFilename())
                            .filter(f -> f.contains("."))
                            .map(f -> f.substring(f.lastIndexOf(".")))
                            .orElse("");
                    String savedName = UUID.randomUUID() + ext;
                    File dest = new File(UPLOAD_DIR + savedName);
                    dest.getParentFile().mkdirs();
                    file.transferTo(dest);
                    uploadedFiles.add("/uploads/inquiry/" + savedName);
                } catch (IOException e) {
                    log.error("❌ 파일 업로드 실패: {}", e.getMessage());
                }
            }
        }

        // ✅ JSON 직렬화
        String attachmentsJson;
        try {
            attachmentsJson = new ObjectMapper().writeValueAsString(uploadedFiles);
        } catch (Exception e) {
            attachmentsJson = "[]";
        }

        // ✅ 엔티티 생성 및 저장
        Inquiry inquiry = Inquiry.builder()
                .user(user)
                .category(category)
                .title(title)
                .content(content)
                .status(InquiryStatus.PENDING)
                .attachments(attachmentsJson)
                .createdAt(LocalDateTime.now())
                .build();

        Inquiry saved = inquiryRepository.save(inquiry);
        log.info("✅ 문의 등록 완료 - userId={}, title={}", userId, title);

        return InquiryResponse.from(saved);
    }

    /**
     * ✅ (3) 문의 상태 변경
     */
    @Transactional
    public InquiryResponse updateStatus(Long inquiryId, InquiryStatus status, String userId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문의입니다."));

        if (!inquiry.getUser().getUserId().equals(userId)) {
            throw new SecurityException("본인 문의만 상태를 변경할 수 있습니다.");
        }

        inquiry.setStatus(status);
        log.info("✅ 문의 상태 변경 - inquiryId={}, status={}", inquiryId, status);
        return InquiryResponse.from(inquiry);
    }

    /**
     * ✅ (4) 문의 삭제
     */
    @Transactional
    public void deleteInquiry(Long inquiryId, String userId) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 문의입니다."));

        if (!inquiry.getUser().getUserId().equals(userId)) {
            throw new SecurityException("본인 문의만 삭제할 수 있습니다.");
        }

        inquiryRepository.delete(inquiry);
        log.info("🗑️ 문의 삭제 완료 - inquiryId={}, userId={}", inquiryId, userId);
    }
}
