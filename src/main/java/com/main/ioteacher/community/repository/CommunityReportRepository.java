package com.main.ioteacher.community.repository;

import com.main.ioteacher.community.entity.CommunityReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CommunityReportRepository extends JpaRepository<CommunityReport, Long> {

    /** ✅ 중복 신고 방지용 */
    boolean existsByReporterUserIdAndTargetTypeAndTargetId(String reporterId, String targetType, Long targetId);

    /** ✅ 오늘 신고한 횟수 계산 (LocalDate 사용) */
    @Query("SELECT COUNT(r) FROM CommunityReport r WHERE r.reporter.userId = :userId AND DATE(r.createdAt) = CURRENT_DATE")
    int countTodayReportsByUser(@Param("userId") String userId, LocalDate now);
}
