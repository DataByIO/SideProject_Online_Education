package com.main.ioteacher.community.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * ✅ 신고 요청 DTO
 * - 게시글 및 댓글 신고 시 공용으로 사용
 */
@Getter
@Setter
public class ReportRequest {
    private String userId; // 신고자 ID
    private String reason; // 신고 사유
}
