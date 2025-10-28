package com.main.ioteacher.inquiry.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.main.ioteacher.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * ✅ 사용자 문의 테이블 (inquiries)
 * - 사용자가 등록한 문의 내역 저장
 * - 첨부파일은 JSON 문자열로 관리 (프론트에서 직렬화/역직렬화)
 */
@Entity
@Table(name = "inquiries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id", nullable = false, updatable = false)
    private Long inquiryId; // 문의 고유 ID

    /** ✅ 작성자 (User FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // ✅ 추가
    private User user;

    /** ✅ 문의 유형 (예: 수강문의, 결제문의 등) */
    @Column(length = 50, nullable = false)
    private String category;

    /** ✅ 문의 제목 */
    @Column(length = 255, nullable = false)
    private String title;

    /** ✅ 문의 내용 */
    @Lob
    @Column(nullable = false)
    private String content;

    /** ✅ 상태 ENUM */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InquiryStatus status = InquiryStatus.PENDING;

    /** ✅ 첨부파일(JSON 문자열) */
    @Lob
    private String attachments;

    /** ✅ 작성일시 */
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;

    /** ✅ 수정일시 */
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt;

    /** ✅ 1:N — 문의에 대한 답변 목록 */
    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InquiryAnswer> answers;
}
