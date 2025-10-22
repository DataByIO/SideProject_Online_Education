package com.main.ioteacher.community.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.main.ioteacher.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "community_comments")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"post"})  // ✅ post 필드 직렬화 방지
public class CommunityComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    /** ✅ 게시글 연관 관계 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost post;

    /** ✅ 작성자 (User 테이블 FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    /** ✅ 내용 */
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer likesCount;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer reportCount;

    /** ✅ 신고 사유 */
    @ElementCollection
    @CollectionTable(name = "comment_report_reasons", joinColumns = @JoinColumn(name = "comment_id"))
    private List<Map<String, String>> reportReasons = new ArrayList<>();

    /** ✅ 좋아요한 유저 목록 */
    @ElementCollection
    @CollectionTable(name = "community_comment_liked_users", joinColumns = @JoinColumn(name = "community_comment_comment_id"))
    @Column(name = "user_id")
    private Set<String> likedUsers = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.likesCount == null) this.likesCount = 0;
        if (this.reportCount == null) this.reportCount = 0;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
