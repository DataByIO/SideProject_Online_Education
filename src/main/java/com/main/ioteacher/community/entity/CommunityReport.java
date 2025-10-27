package com.main.ioteacher.community.entity;

import com.main.ioteacher.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 게시글/댓글 신고 내역 엔티티
 * - Enum 사유 + 기타 사유(reasonDetail) + 다국어 JSON 저장
 */
@Entity
@Table(name = "community_reports")
@Builder
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CommunityReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    /** ✅ 신고자 (User FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    /** ✅ 신고 대상 종류 (POST / COMMENT) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TargetType targetType;

    /** ✅ 신고 대상 ID (게시글 ID or 댓글 ID) */
    @Column(nullable = false)
    private Long targetId;

    /** ✅ 신고 사유 (Enum) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Reason reason;

    /** ✅ 사용자가 입력한 상세 사유 ("기타"일 경우 사용) */
    @Column(columnDefinition = "TEXT")
    private String reasonDetail;

    /** ✅ 다국어 사유 (JSON) */
    @Column(columnDefinition = "json")
    private String reason_ko;
    @Column(columnDefinition = "json")
    private String reason_en;
    @Column(columnDefinition = "json")
    private String reason_ja;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /** ✅ 신고 대상 구분 */
    public enum TargetType {
        POST, COMMENT
    }

    /** ✅ 신고 사유 Enum (기타는 별도 입력 저장) */
    public enum Reason {
        SPAM("스팸/홍보"),
        HARASSMENT("괴롭힘/비방"),
        INAPPROPRIATE("부적절한 내용"),
        OTHER("기타");

        private final String displayName;

        Reason(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }

        /** ✅ 한글명, 영문, Enum명 모두 인식 */
        public static Reason fromString(String input) {
            if (input == null) return OTHER;
            for (Reason r : values()) {
                if (r.name().equalsIgnoreCase(input) || r.displayName.equalsIgnoreCase(input)) {
                    return r;
                }
            }
            return OTHER;
        }
    }
}
