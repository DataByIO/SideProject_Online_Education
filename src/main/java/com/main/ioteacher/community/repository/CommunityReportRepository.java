package com.main.ioteacher.community.repository;

import com.main.ioteacher.community.entity.CommunityReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface CommunityReportRepository extends JpaRepository<CommunityReport, Long> {

    /** ✅ 중복 신고 방지 (User 관계 기반 + Enum 매핑) */
    boolean existsByReporter_UserIdAndTargetTypeAndTargetId(
            String reporterId,
            CommunityReport.TargetType targetType,
            Long targetId
    );

    /** ✅ 하루 신고 횟수 제한 */
    @Query("SELECT COUNT(r) FROM CommunityReport r " +
            "WHERE r.reporter.userId = :userId AND DATE(r.createdAt) = CURRENT_DATE")
    int countTodayReportsByUser(@Param("userId") String userId, LocalDate now);
}
