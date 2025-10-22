package com.main.ioteacher.community.entity;

import com.main.ioteacher.community.entity.CommunityComment;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 댓글 응답 DTO (User 엔티티 조인 기반 + 좋아요 여부 포함)
 */
@Data
@Builder
public class CommentResponse {

    private Long commentId;
    private String userId;
    private String userName;
    private String userProfileImageUrl;
    private String content;
    private int likesCount;
    private int reportCount;
    private boolean isLiked; // ✅ 로그인 사용자가 좋아요 눌렀는지
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponse fromEntity(CommunityComment entity, String currentUserId) {
        boolean liked = false;
        if (currentUserId != null && entity.getLikedUsers() != null) {
            liked = entity.getLikedUsers().contains(currentUserId);
        }

        return CommentResponse.builder()
                .commentId(entity.getCommentId())
                .userId(entity.getUser().getUserId())
                .userName(entity.getUser().getName())
                .userProfileImageUrl(entity.getUser().getProfileImageUrl())
                .content(entity.getContent())
                .likesCount(entity.getLikesCount())
                .reportCount(entity.getReportCount())
                .isLiked(liked)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
