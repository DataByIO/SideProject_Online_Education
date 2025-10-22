package com.main.ioteacher.community.entity;

import com.main.ioteacher.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 게시글/댓글 신고 내역 엔티티
 * - 신고자, 대상, 사유 저장
 */
@Entity
@Table(name = "community_reports")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class CommunityReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TargetType targetType;  // POST or COMMENT

    @Column(nullable = false)
    private Long targetId;          // 신고된 게시글 or 댓글의 ID

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Reason reason;

    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public enum TargetType { POST, COMMENT }
    public enum Reason { SPAM, INAPPROPRIATE, HARASSMENT, OTHER }
}
