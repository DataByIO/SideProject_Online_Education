package com.main.ioteacher.user.controller;

import com.main.ioteacher.user.dto.UserDtos.CreateReq;
import com.main.ioteacher.user.dto.UserDtos.LoginRequest;
import com.main.ioteacher.user.dto.UserDtos.Resp;
import com.main.ioteacher.user.dto.UserDtos.TokenResponse;
import com.main.ioteacher.user.service.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입
     */
    @PostMapping("/register")
    public Resp register(@Valid @RequestBody CreateReq req) {
        return authService.register(req);
    }

    /**
     * 로그인
     * - Access Token JSON 반환
     * - Refresh Token은 HttpOnly 쿠키로 내려감
     */
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req, HttpServletResponse response) {
        var tokens = authService.login(req, response);
        return new TokenResponse(tokens.get("accessToken"), tokens.get("tokenType"));
    }

    /**
     * Access Token 재발급
     * - Refresh Token은 쿠키에서 자동 전송됨
     */
    @PostMapping("/refresh")
    public TokenResponse refresh(@CookieValue("refreshToken") String refreshToken,
                                 HttpServletResponse response) {
        String newAccess = authService.refresh(refreshToken);
        return new TokenResponse(newAccess, "Bearer");
    }

    /**
     * 로그아웃
     * - Refresh Token 무효화 + 쿠키 삭제
     */
    @PostMapping("/logout")
    public Resp logout(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String userId = body.get("userId");
        authService.logout(userId, response);
        return new Resp(true, "로그아웃 완료");
    }
}
