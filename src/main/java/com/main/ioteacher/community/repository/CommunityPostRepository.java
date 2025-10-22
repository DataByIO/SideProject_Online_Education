package com.main.ioteacher.community.repository;

import com.main.ioteacher.community.entity.CommunityPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityPostRepository extends JpaRepository<CommunityPost, Long> {

    /**
     * ✅ 카테고리별 게시글 조회 (Pageable 지원)
     */
    Page<CommunityPost> findByCategoryOrderByCreatedAtDesc(CommunityPost.Category category, Pageable pageable);

    /**
     * ✅ 전체 게시글 최신순 조회
     */
    Page<CommunityPost> findAllByOrderByCreatedAtDesc(Pageable pageable);

    /**
     * ✅ 특정 사용자의 게시글 조회
     */
    Page<CommunityPost> findByUser_UserIdOrderByCreatedAtDesc(String userId, Pageable pageable);
}
