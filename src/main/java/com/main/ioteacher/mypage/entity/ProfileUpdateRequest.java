package com.main.ioteacher.mypage.entity;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProfileUpdateRequest {
    private String name;
    private String phone;
    private String address;
}
