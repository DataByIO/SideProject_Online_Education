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
        /** ✅ 아이디 찾기 요청 DTO */
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class FindIdRequest {
            @NotBlank
            private String name;

            @NotBlank
            private String phoneNumber; // 프론트 입력값(하이픈 포함 가능)
        }

        /** ✅ 아이디 찾기 응답 DTO */
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class FindIdResponse {
            // 프론트에서 이메일을 마스킹해도 되고,
            // 백엔드에서 마스킹된 값을 같이 주면 UI 구현이 쉬움
            private String email;        // 원본
            private String maskedEmail;  // 마스킹
        }
    }
