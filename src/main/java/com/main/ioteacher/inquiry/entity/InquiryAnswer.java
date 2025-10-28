package com.main.ioteacher.inquiry.entity;

import com.main.ioteacher.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * ✅ 관리자 답변 테이블 (inquiry_answers)
 * - 관리자(admin)가 등록한 문의 답변
 * - 관리자가 삭제되면 admin_id는 NULL 처리
 */
@Entity
@Table(name = "inquiry_answers")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id", nullable = false, updatable = false)
    private Long answerId; // 답변 고유 ID

    /** ✅ 참조 문의 (FK → inquiries) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    /** ✅ 답변 작성자(관리자) (FK → users) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User admin; // 삭제 시 SET NULL 적용됨

    /** ✅ 답변 내용 */
    @Lob
    @Column(nullable = false)
    private String content;

    /** ✅ 작성일시 */
    @Column(columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
}
