package com.main.ioteacher.mypage.entity;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class InquiryResponse {
    private Long id;
    private String type;
    private String title;
    private String content;
    private String status;
    private String adminReply;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
