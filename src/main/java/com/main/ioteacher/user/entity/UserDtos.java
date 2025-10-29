package com.main.ioteacher.user.entity;

import com.main.ioteacher.user.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class UserDtos {

    /** 회원가입 요청 DTO */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateReq {
        @NotBlank
        private String userId;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
        private String password;

        @NotBlank
        private String name;

        private String phone;

        /** ✅ 주소 구조 세분화 */
        private String roadAddress;
        private String detailAddress;
        private String zipCode;

        private String profileImageUrl;
        private List<String> interests;
        private boolean agreeMarketing;
        private boolean emailVerified;

        private String role = "USER";
    }

    /** 로그인 요청 DTO */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank
        private String userId;

        @NotBlank
        private String password;
    }

    /** 공통 응답 DTO */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Resp {
        private boolean success;
        private String message;
    }

    /** 사용자 정보 응답 DTO */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserResp {
        private String userId;
        private String email;
        private String name;
        private String role;
        private UserStatus status;
        private String profileImageUrl;

        private String phone;

        /** ✅ 주소 구조 세분화 */
        private String roadAddress;
        private String detailAddress;
        private String zipCode;

        private Boolean agreeMarketing;
        private LocalDateTime createdAt;
        private LocalDateTime lastLogin;
    }

    /** 토큰 응답 DTO */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String tokenType;
    }
}
