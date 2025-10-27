package com.main.ioteacher.community.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * ✅ 신고 요청 DTO
 * - 게시글 및 댓글 신고 시 공용으로 사용
 * - reason: Enum 값 (SPAM, HARASSMENT, INAPPROPRIATE, OTHER)
 * - reasonDetail: 사용자가 직접 입력한 사유 (기타일 경우)
 */
@Getter
@Setter
@NoArgsConstructor
public class ReportRequest {

    /** ✅ 신고자 ID */
    private String userId;

    /** ✅ 신고 사유 (Enum 이름 or 한글명) */
    private String reason;

    /** ✅ 사용자가 직접 입력한 상세 사유 (기타일 경우 필수) */
    private String reasonDetail;

    /** ✅ 안전하게 상세 사유 반환 */
    public String getReasonDetail() {
        return reasonDetail != null ? reasonDetail.trim() : null;
    }

    /** ✅ Enum 값으로 변환 시도 (대문자, 한글 모두 대응) */
    public CommunityReport.Reason getParsedReason() {
        return CommunityReport.Reason.fromString(reason);
    }
}
