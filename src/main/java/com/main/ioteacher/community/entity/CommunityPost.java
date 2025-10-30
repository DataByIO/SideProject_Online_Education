package com.main.ioteacher.community.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.main.ioteacher.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "community_posts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    /** ✅ User 엔티티 연관관계 추가 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Category category;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String imageUrl;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer likesCount;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer viewsCount;

    @Column(nullable = false, columnDefinition = "INT UNSIGNED DEFAULT 0")
    private Integer reportCount;

    @Column(columnDefinition = "TEXT")
    private String reportReasons;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /** ✅ 좋아요 누른 사용자 목록 (userId 문자열만 저장) */
    @ElementCollection
    @CollectionTable(name = "community_post_liked_users", joinColumns = @JoinColumn(name = "community_post_post_id"))
    @Column(name = "user_id")
    private Set<String> likedUsers = new HashSet<>();

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (likesCount == null) likesCount = 0;
        if (viewsCount == null) viewsCount = 0;
        if (reportCount == null) reportCount = 0;
        if (reportReasons == null) reportReasons = "[]";
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public enum Category {
        notice, general, question, study, job
    }
}