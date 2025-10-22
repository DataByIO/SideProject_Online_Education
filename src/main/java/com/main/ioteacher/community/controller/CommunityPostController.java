package com.main.ioteacher.community.controller;

import com.main.ioteacher.community.entity.*;
import com.main.ioteacher.community.service.CommunityPostService;
import com.main.ioteacher.config.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/community/posts")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService postService;
    private final JwtUtil jwtUtil;

    /** ✅ 게시글 목록 (페이지네이션 지원) */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPosts(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "5") int limit,
            @RequestParam(defaultValue = "all") String category,
            HttpServletRequest request
    ) {
        String userId = null;
        try { userId = jwtUtil.getUserIdFromRequest(request); } catch (Exception ignored) {}

        Page<CommunityPostResponse> page = postService.getPagedPosts(category, offset, limit, userId);

        Map<String, Object> result = new HashMap<>();
        result.put("posts", page.getContent());
        result.put("totalElements", page.getTotalElements());
        result.put("totalPages", page.getTotalPages());
        result.put("currentPage", page.getNumber());
        return ResponseEntity.ok(result);
    }

    /** ✅ 단일 게시글 조회 (조회수 증가) */
    @GetMapping("/{postId}")
    public ResponseEntity<CommunityPostResponse> getPostById(
            @PathVariable Long postId,
            HttpServletRequest request
    ) {
        String userId = null;
        try { userId = jwtUtil.getUserIdFromRequest(request); } catch (Exception ignored) {}
        return ResponseEntity.ok(postService.getPostAndIncreaseViews(postId, userId));
    }

    /** ✅ 게시글 작성 */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommunityPostResponse> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request
    ) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        validateUser(userId);

        CommunityPostRequest req = new CommunityPostRequest();
        req.setUserId(userId);
        req.setTitle(title);
        req.setContent(content);
        req.setCategory(CommunityPost.Category.valueOf(category));

        return ResponseEntity.ok(postService.createPostWithImage(req, image));
    }

    /** ✅ 게시글 수정 */
    @PutMapping(value = "/{postId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommunityPostResponse> updatePost(
            @PathVariable Long postId,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            @RequestPart(value = "image", required = false) MultipartFile image,
            HttpServletRequest request
    ) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        validateUser(userId);

        CommunityPostRequest req = new CommunityPostRequest();
        req.setUserId(userId);
        req.setTitle(title);
        req.setContent(content);
        req.setCategory(CommunityPost.Category.valueOf(category));

        return ResponseEntity.ok(postService.updatePostWithImage(postId, req, image, userId));
    }

    /** ✅ 좋아요 토글 */
    @PostMapping("/{postId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Long postId, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        validateUser(userId);
        return ResponseEntity.ok(postService.toggleLike(postId, userId));
    }

    /** ✅ 조회수 증가 */
    @PostMapping("/{postId}/view")
    public ResponseEntity<String> increaseView(@PathVariable Long postId) {
        postService.increaseViewCount(postId);
        return ResponseEntity.ok("조회수 증가 완료");
    }

    /** ✅ 게시글 삭제 */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        String userId = jwtUtil.getUserIdFromRequest(request);
        validateUser(userId);
        postService.deletePost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    private void validateUser(String userId) {
        if (userId == null || userId.isBlank())
            throw new IllegalArgumentException("로그인 후 이용 가능합니다.");
    }
}
