package com.main.ioteacher.community.repository;

import com.main.ioteacher.community.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    /** ✅ 특정 게시글의 모든 댓글 조회 (User 즉시 로딩 포함, 최신순 정렬) */
    @Query("SELECT c FROM CommunityComment c JOIN FETCH c.user WHERE c.post.postId = :postId ORDER BY c.createdAt ASC")
    List<CommunityComment> findByPost_PostId(@Param("postId") Long postId);

    /** ✅ 게시글별 댓글 수 조회 */
    @Query("SELECT COUNT(c) FROM CommunityComment c WHERE c.post.postId = :postId")
    long countByPostId(@Param("postId") Long postId);
}
