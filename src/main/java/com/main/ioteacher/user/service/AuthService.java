package com.main.ioteacher.user.service;

import com.main.ioteacher.common.MailService;
import com.main.ioteacher.config.JwtUtil;
import com.main.ioteacher.user.Role;
import com.main.ioteacher.user.UserStatus;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.entity.UserDtos.CreateReq;
import com.main.ioteacher.user.entity.UserDtos.LoginRequest;
import com.main.ioteacher.user.entity.UserDtos.Resp;
import com.main.ioteacher.user.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final MailService emailService;
    private final VerificationCodeCache verificationCache;
    private final VerificationRateLimiter rateLimiter;

    // ✅ 비밀번호 재설정용 코드 캐시 (임시 메모리 — 추후 Redis로 대체 가능)
    private final Map<String, String> resetCodes = new HashMap<>();

    /**
     * 회원가입
     */
    public Resp register(CreateReq req) {
        if (userRepository.findById(req.getUserId()).isPresent()) {
            return new Resp(false, "이미 존재하는 아이디입니다.");
        }

        User user = User.builder()
                .userId(req.getUserId())
                .email(req.getEmail().toLowerCase())
                .password(encoder.encode(req.getPassword()))
                .name(req.getName())
                .phone(req.getPhone())
                .zipCode(req.getZipCode())
                .detailAddress(req.getDetailAddress())
                .roadAddress(req.getRoadAddress())
                .profileImageUrl(req.getProfileImageUrl())
                .interests(req.getInterests())
                .emailVerified(req.isEmailVerified())
                .agreeMarketing(req.isAgreeMarketing())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .role(Role.valueOf(req.getRole().toUpperCase()))
                .status(UserStatus.ACTIVE)
                .blacklisted(false)
                .build();

        userRepository.save(user);
        return new Resp(true, "회원가입이 완료되었습니다.");
    }

    /**
     * ✅ 회원가입 이메일 인증 (새 이메일만 허용)
     */
    public Resp sendVerification(String email, String clientIp) {
        boolean isEmailAvailable = !userRepository.existsByEmail(email);

        if (!isEmailAvailable) {
            return new Resp(false, "이미 등록된 이메일입니다. 다른 이메일을 사용하세요.");
        }

        if (!rateLimiter.allowRequest(clientIp)) {
            return new Resp(false, "요청이 너무 잦습니다. 잠시 후 다시 시도하세요.");
        }

        String code = generateCode();
        emailService.sendVerificationCode(email, code, isEmailAvailable);
        verificationCache.store(email, code);

        return new Resp(true, "이메일로 인증코드가 발송되었습니다.");
    }

    /**
     * ✅ 회원가입용 인증 코드 검증
     */
    public Resp verifyCode(String email, String code) {
        boolean valid = verificationCache.verify(email, code);
        if (!valid) {
            return new Resp(false, "인증 코드가 올바르지 않거나 만료되었습니다.");
        }

        userRepository.findByEmail(email).ifPresentOrElse(
                user -> {
                    user.setEmailVerified(true);
                    userRepository.save(user);
                },
                () -> {} // 신규 이메일은 회원가입 시 emailVerified=true로 처리됨
        );

        return new Resp(true, "이메일 인증이 완료되었습니다.");
    }

    /**
     * ✅ 비밀번호 재설정용 인증 메일 발송 (이미 등록된 이메일만 허용)
     */
    public Resp sendResetVerification(String email, String clientIp) {
        if (!userRepository.existsByEmail(email)) {
            return new Resp(false, "등록되지 않은 이메일입니다.");
        }

        if (!rateLimiter.allowRequest(clientIp)) {
            return new Resp(false, "요청이 너무 잦습니다. 잠시 후 다시 시도하세요.");
        }

        String code = generateCode();
        resetCodes.put(email, code);
        emailService.sendResetPasswordMail(email, code);
        return new Resp(true, "비밀번호 재설정용 인증코드를 전송했습니다.");
    }

    /**
     * ✅ 비밀번호 재설정용 인증 코드 검증
     */
    public Resp verifyResetCode(String email, String code) {
        String stored = resetCodes.get(email);
        if (stored == null) {
            return new Resp(false, "인증 코드가 존재하지 않습니다.");
        }
        if (!stored.equals(code)) {
            return new Resp(false, "인증 코드가 올바르지 않습니다.");
        }

        resetCodes.remove(email);
        return new Resp(true, "이메일 인증이 완료되었습니다.");
    }

    /**
     * ✅ 비밀번호 재설정 처리
     */
    public Resp resetPassword(String email, String newPassword) {
        Optional<User> optUser = userRepository.findByEmail(email);
        if (optUser.isEmpty()) {
            return new Resp(false, "존재하지 않는 이메일입니다.");
        }

        User user = optUser.get();
        user.setPassword(encoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // ✅ 비밀번호 변경 완료 알림 메일 전송
        emailService.sendPasswordResetSuccessMail(email);

        return new Resp(true, "비밀번호가 성공적으로 재설정되었습니다.");
    }


    /**
     * ✅ 로그인
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

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 24 * 7);
        response.addCookie(cookie);

        Map<String, String> tokens = new HashMap<>();
        tokens.put("accessToken", accessToken);
        tokens.put("tokenType", "Bearer");

        return tokens;
    }

    /**
     * ✅ Access Token 재발급
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
     * ✅ 로그아웃
     */
    public void logout(String userId, HttpServletResponse response) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setRefreshToken(null);
        userRepository.save(user);

        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    /** ✅ 인증 코드 생성 (6자리 숫자) */
    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1000000));
    }
}
