package com.main.ioteacher.user.dto;

import com.main.ioteacher.user.UserStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

//(DTOs) → API 요청/응답용, 클라이언트와 통신할 때 사용
public class UserDtos {

    /**
     * 회원가입 요청 DTO
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class CreateReq {
        @NotBlank
        private String userId;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        @Size(min = 6)
        private String password;

        @NotBlank
        private String name;

        private String role = "USER"; // 기본 USER
    }

    /**
     * 로그인 요청 DTO
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class LoginRequest {
        @NotBlank
        private String userId;

        @NotBlank
        private String password;
    }

    /**
     * 공통 응답 DTO
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class Resp {
        private boolean success;
        private String message;
    }

    /**
     * 사용자 정보 응답 DTO
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UserResp {
        private String userId;    // ✅ PK 기준으로 변경
        private String email;
        private String name;
        private String role;
        private UserStatus status;
        private String profileImageUrl;
    }

    /**
     * 토큰 응답 DTO
     * Refresh Token은 HttpOnly 쿠키에 담아 내려주므로 포함하지 않음
     */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class TokenResponse {
        private String accessToken;
        private String tokenType; // Bearer
    }
}
