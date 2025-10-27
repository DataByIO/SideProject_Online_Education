package com.main.ioteacher.mypage.entity;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class InquiryRequest {
    private String type;
    private String title;
    private String content;
}
