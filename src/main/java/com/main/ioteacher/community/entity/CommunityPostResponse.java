package com.main.ioteacher.community.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommunityPostResponse {

    private Long postId;
    private String userId;
    private String title;
    private String content;
    private String imageUrl;
    private String category;
    private String profileImageUrl;
    private int likesCount;
    private int viewsCount;
    private int reportCount;
    private int commentsCount;   // ✅ 댓글 수 추가
    private boolean isLiked;     // ✅ 현재 로그인 사용자가 좋아요했는지 여부
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /** ✅ 로그인 사용자 포함 버전 */
    public static CommunityPostResponse fromEntity(CommunityPost post, String currentUserId) {
        boolean liked = false;
        if (currentUserId != null && post.getLikedUsers() != null) {
            liked = post.getLikedUsers().contains(currentUserId);
        }

        return CommunityPostResponse.builder()
                .postId(post.getPostId())
                .userId(post.getUser().getUserId())
                .title(post.getTitle())
                .profileImageUrl(
                        post.getUser() != null ? post.getUser().getProfileImageUrl() : null
                )
                .content(post.getContent())
                .imageUrl(post.getImageUrl())
                .category(post.getCategory().name())
                .likesCount(post.getLikesCount() == null ? 0 : post.getLikesCount())
                .viewsCount(post.getViewsCount() == null ? 0 : post.getViewsCount())
                .reportCount(post.getReportCount() == null ? 0 : post.getReportCount())
                .isLiked(liked)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    /** ✅ 비로그인(기본) 버전 */
    public static CommunityPostResponse fromEntity(CommunityPost post) {
        return fromEntity(post, null);
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }
}
