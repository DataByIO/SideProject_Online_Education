package com.main.ioteacher.community.service;

import com.main.ioteacher.community.entity.*;
import com.main.ioteacher.community.repository.CommunityCommentRepository;
import com.main.ioteacher.community.repository.CommunityPostRepository;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CommunityPostService {

    private final CommunityPostRepository postRepo;
    private final CommunityCommentRepository commentRepo; // ✅ 추가
    private final UserRepository userRepo;

    /** ✅ 게시글 생성 */
    public CommunityPostResponse createPostWithImage(CommunityPostRequest req, MultipartFile image) {
        User user = userRepo.findByUserId(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String imageUrl = saveImageFile(image);

        CommunityPost post = CommunityPost.builder()
                .user(user)
                .category(req.getCategory())
                .title(req.getTitle())
                .content(req.getContent())
                .imageUrl(imageUrl)
                .likesCount(0)
                .viewsCount(0)
                .reportCount(0)
                .build();

        return CommunityPostResponse.fromEntity(postRepo.save(post));
    }

    /** ✅ 게시글 수정 */
    public CommunityPostResponse updatePostWithImage(Long postId, CommunityPostRequest req, MultipartFile image, String userId) {
        CommunityPost post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (!post.getUser().getUserId().equals(userId))
            throw new SecurityException("수정 권한이 없습니다.");

        if (image != null && !image.isEmpty()) {
            deleteOldImageFile(post.getImageUrl());
            post.setImageUrl(saveImageFile(image));
        }

        post.setTitle(req.getTitle());
        post.setContent(req.getContent());
        post.setCategory(req.getCategory());
        return CommunityPostResponse.fromEntity(postRepo.save(post));
    }

    /** ✅ 이미지 저장 */
    private String saveImageFile(MultipartFile image) {
        if (image == null || image.isEmpty()) return null;
        try {
            String uploadDir = "/Users/kimnoah/IdeaProjects/ioteacher/uploads/feed/";
            Files.createDirectories(Paths.get(uploadDir));
            String fileName = UUID.randomUUID() + "_" + image.getOriginalFilename();
            Path path = Paths.get(uploadDir + fileName);
            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/feed/" + fileName;
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage(), e);
        }
    }

    /** ✅ 기존 이미지 삭제 */
    private void deleteOldImageFile(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) return;
        try {
            String baseDir = "/Users/kimnoah/IdeaProjects/ioteacher/uploads";
            Path path = Paths.get(baseDir + imageUrl.replace("/uploads", ""));
            if (Files.exists(path)) Files.delete(path);
        } catch (Exception e) {
            System.err.println("⚠️ 이미지 삭제 실패: " + e.getMessage());
        }
    }

    /** ✅ 페이지네이션 게시글 조회 (댓글 수 포함) */
    public Page<CommunityPostResponse> getPagedPosts(String category, int offset, int limit, String currentUserId) {
        Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<CommunityPost> page =
                (category == null || category.isBlank() || category.equals("all"))
                        ? postRepo.findAllByOrderByCreatedAtDesc(pageable)
                        : postRepo.findByCategoryOrderByCreatedAtDesc(CommunityPost.Category.valueOf(category), pageable);

        return page.map(post -> {
            boolean isLiked = currentUserId != null &&
                    post.getLikedUsers() != null &&
                    post.getLikedUsers().contains(currentUserId);

            // ✅ 게시글별 댓글 수 추가
            long commentCount = commentRepo.countByPostId(post.getPostId());

            CommunityPostResponse res = CommunityPostResponse.fromEntity(post);
            res.setIsLiked(isLiked);
            res.setCommentsCount((int) commentCount);
            return res;
        });
    }

    /** ✅ 단일 게시글 조회 */
    public CommunityPostResponse getPostAndIncreaseViews(Long postId, String userId) {
        CommunityPost post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        post.setViewsCount(post.getViewsCount() + 1);
        postRepo.save(post);

        // ✅ 댓글 수 포함
        long commentCount = commentRepo.countByPostId(postId);

        CommunityPostResponse response = CommunityPostResponse.fromEntity(post, userId);
        response.setCommentsCount((int) commentCount);
        return response;
    }

    /** ✅ 좋아요 토글 */
    public Map<String, Object> toggleLike(Long postId, String userId) {
        CommunityPost post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        if (post.getLikedUsers() == null) post.setLikedUsers(new HashSet<>());

        boolean isLiked;
        if (post.getLikedUsers().contains(userId)) {
            post.getLikedUsers().remove(userId);
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            isLiked = false;
        } else {
            post.getLikedUsers().add(userId);
            post.setLikesCount(post.getLikesCount() + 1);
            isLiked = true;
        }
        postRepo.save(post);

        Map<String, Object> result = new HashMap<>();
        result.put("isLiked", isLiked);
        result.put("likesCount", post.getLikesCount());
        return result;
    }

    /** ✅ 조회수 증가 */
    public void increaseViewCount(Long postId) {
        CommunityPost post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        post.setViewsCount(post.getViewsCount() + 1);
        postRepo.save(post);
    }

    /** ✅ 게시글 삭제 */
    public void deletePost(Long postId, String userId) {
        CommunityPost post = postRepo.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글이 존재하지 않습니다."));
        if (!post.getUser().getUserId().equals(userId))
            throw new SecurityException("삭제 권한이 없습니다.");
        deleteOldImageFile(post.getImageUrl());
        postRepo.delete(post);
    }
}
