package com.main.ioteacher.user.controller;
import com.main.ioteacher.user.entity.UserDtos;
import com.main.ioteacher.user.entity.UserDtos.CreateReq;
import com.main.ioteacher.user.entity.UserDtos.LoginRequest;
import com.main.ioteacher.user.entity.UserDtos.Resp;
import com.main.ioteacher.user.entity.UserDtos.TokenResponse;
import com.main.ioteacher.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
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

    /** ✅ 회원가입 */
    @PostMapping("/register")
    public Resp register(@Valid @RequestBody CreateReq req) {
        return authService.register(req);
    }

    /** ✅ 회원가입용 이메일 인증 (새 이메일만 허용) */
    @PostMapping("/send-verification")
    public Resp sendVerification(HttpServletRequest request, @RequestBody Map<String, String> body) {
        String clientIp = request.getRemoteAddr();
        return authService.sendVerification(body.get("email"), clientIp);
    }

    /** ✅ 회원가입용 인증 코드 확인 */
    @PostMapping("/verify-code")
    public Resp verifyCode(@RequestBody Map<String, String> body) {
        return authService.verifyCode(body.get("email"), body.get("code"));
    }

    /** ✅ 비밀번호 재설정용 이메일 인증 (기존 이메일만 허용) */
    @PostMapping("/send-reset-verification")
    public Resp sendResetVerification(HttpServletRequest request, @RequestBody Map<String, String> body) {
        String clientIp = request.getRemoteAddr();
        return authService.sendResetVerification(body.get("email"), clientIp);
    }

    /** ✅ 비밀번호 재설정용 코드 검증 */
    @PostMapping("/verify-reset-code")
    public Resp verifyResetCode(@RequestBody Map<String, String> body) {
        return authService.verifyResetCode(body.get("email"), body.get("code"));
    }

    /** ✅ 비밀번호 재설정 처리 */
    @PostMapping("/reset-password")
    public Resp resetPassword(@RequestBody Map<String, String> body) {
        return authService.resetPassword(body.get("email"), body.get("newPassword"));
    }

    /** ✅ 로그인 */
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest req, HttpServletResponse response) {
        var tokens = authService.login(req, response);
        return new TokenResponse(tokens.get("accessToken"), tokens.get("tokenType"));
    }

    /** ✅ 토큰 재발급 */
    @PostMapping("/refresh")
    public TokenResponse refresh(@CookieValue("refreshToken") String refreshToken,
                                 HttpServletResponse response) {
        String newAccess = authService.refresh(refreshToken);
        return new TokenResponse(newAccess, "Bearer");
    }

    /** ✅ 로그아웃 */
    @PostMapping("/logout")
    public Resp logout(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String userId = body.get("userId");
        authService.logout(userId, response);
        return new Resp(true, "로그아웃 완료");
    }

    /** ✅ 아이디(이메일) 찾기: 이름 + 전화번호 */
    @PostMapping("/find-id")
    public UserDtos.FindIdResponse findId(@Valid @RequestBody UserDtos.FindIdRequest req) {
        return authService.findId(req);
    }
}
