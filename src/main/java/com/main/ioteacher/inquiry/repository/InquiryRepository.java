package com.main.ioteacher.inquiry.repository;

import com.main.ioteacher.inquiry.entity.Inquiry;
import com.main.ioteacher.inquiry.entity.InquiryStatus;
import com.main.ioteacher.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ✅ InquiryRepository
 * - 사용자별, 상태별, 최신순 조회 지원
 */
@Repository("inquiryModuleRepository")
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {

    /** ✅ 특정 사용자(User 엔티티)의 문의 목록 조회 (최신순) + User 즉시 로딩 */
    @EntityGraph(attributePaths = {"user"})
    List<Inquiry> findByUserOrderByCreatedAtDesc(User user);

    /** ✅ 사용자 기준 전체 문의 + 답변 fetch join */
    @Query("""
    SELECT DISTINCT i
    FROM Inquiry i
    LEFT JOIN FETCH i.user u
    LEFT JOIN FETCH i.answers a
    LEFT JOIN FETCH a.admin
    WHERE i.user = :user
    ORDER BY i.createdAt DESC
""")
    List<Inquiry> findByUserWithAnswers(@Param("user") User user);

    /** ✅ userId로 직접 조회하는 버전 (문자열 기반) + User 즉시 로딩 */
    @EntityGraph(attributePaths = {"user"})
    List<Inquiry> findByUser_UserIdOrderByCreatedAtDesc(String userId);

    /** ✅ 상태별 문의 목록 조회 (관리자용 등) */
    @EntityGraph(attributePaths = {"user"})
    List<Inquiry> findByStatusOrderByCreatedAtDesc(InquiryStatus status);
}
