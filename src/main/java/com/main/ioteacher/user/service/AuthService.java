package com.main.ioteacher.user.service;

import com.main.ioteacher.config.JwtUtil;
import com.main.ioteacher.user.Role;
import com.main.ioteacher.user.UserStatus;
import com.main.ioteacher.user.dto.UserDtos.CreateReq;
import com.main.ioteacher.user.dto.UserDtos.LoginRequest;
import com.main.ioteacher.user.dto.UserDtos.Resp;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입
     */
    public Resp register(CreateReq req) {
        if (userRepository.findById(req.getUserId()).isPresent()) {
            return new Resp(false, "이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .userId(req.getUserId())
                .email(req.getEmail().toLowerCase())
                .password(encoder.encode(req.getPassword()))
                .name(req.getName())
                .role(Role.valueOf(req.getRole().toUpperCase()))
                .status(UserStatus.ACTIVE)
                .blacklisted(false)
                .build();

        userRepository.save(user);
        return new Resp(true, "회원가입이 완료되었습니다.");
    }

    /**
     * 로그인
     */
    public Map<String, String> login(LoginRequest req, HttpServletResponse response) {
        User user = userRepository.findById(req.getUserId().toLowerCase())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        if (user.getStatus() != UserStatus.ACTIVE || Boolean.TRUE.equals(user.getBlacklisted())) {
            throw new IllegalStateException("해당 계정은 로그인할 수 없습니다.");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getUserId(), user.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());

        // DB에 refresh token 저장
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        // Refresh Token 쿠키로 전달
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7); // 7일
        response.addCookie(cookie);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("tokenType", "Bearer");

        return tokens;
    }

    /**
     * Access Token 재발급
     */
    public String refresh(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new IllegalStateException("Refresh Token이 유효하지 않습니다.");
        }

        String userId = jwtUtil.getUserId(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new IllegalStateException("Refresh Token 불일치");
        }

        return jwtUtil.generateAccessToken(user.getUserId(), user.getRole().name());
    }

    /**
     * 로그아웃
     */
    public void logout(String userId, HttpServletResponse response) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Refresh Token 삭제
        user.setRefreshToken(null);
        userRepository.save(user);

        // 쿠키 삭제
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
