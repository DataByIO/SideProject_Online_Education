package com.main.ioteacher.user.service;

import com.main.ioteacher.user.UserStatus;
import com.main.ioteacher.user.entity.UserDtos.Resp;
import com.main.ioteacher.user.entity.UserDtos.UserResp;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final HttpServletResponse response;
    private final PasswordEncoder passwordEncoder;

    @Value("${file.upload-dir:/Users/kimnoah/IdeaProjects/ioteacher/uploads/userProfile/}")
    private String uploadDir;

    /** ✅ 사용자 정보 조회 */
    public UserResp getUser(String userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return UserResp.builder()
                .userId(u.getUserId())
                .email(u.getEmail())
                .name(u.getName())
                .profileImageUrl(u.getProfileImageUrl())
                .role(u.getRole().name())
                .status(u.getStatus())
                .phone(u.getPhone())
                .zipCode(u.getZipCode())
                .detailAddress(u.getDetailAddress())
                .roadAddress(u.getRoadAddress())
                .agreeMarketing(u.getAgreeMarketing())
                .createdAt(u.getCreatedAt())
                .lastLogin(u.getLastLogin())
                .build();
    }

    /** ✅ 아이디 / 이메일 중복 체크 */
    public Map<String, Object> checkDuplicate(String type, String value) {
        boolean exists;
        switch (type.toLowerCase()) {
            case "userid" -> exists = userRepository.findByUserId(value).isPresent();
            case "email" -> exists = userRepository.existsByEmail(value.toLowerCase());
            default -> throw new IllegalArgumentException("잘못된 type 값입니다. (허용: userId, email)");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("exists", exists);
        result.put("message", exists
                ? "이미 사용 중인 " + (type.equalsIgnoreCase("userId") ? "아이디" : "이메일") + "입니다."
                : "사용 가능한 " + (type.equalsIgnoreCase("userId") ? "아이디" : "이메일") + "입니다.");
        return result;
    }

    /** ✅ 사용자 상태 변경 (관리자용) */
    public Resp updateStatus(String userId, UserStatus status) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        u.setStatus(status);
        userRepository.save(u);
        return new Resp(true, "사용자 상태가 변경되었습니다.");
    }

    /* ---------------------------------------------------------
     * ✅ ProfileManagement 기능 확장 (분리 구조)
     * --------------------------------------------------------- */

    /** ✅ 1️⃣ JSON 기반 프로필 정보 수정 */
    public Resp updateProfile(String userId, Map<String, String> data, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (data != null) {
            user.setName(data.getOrDefault("name", user.getName()));
            user.setPhone(data.getOrDefault("phone", user.getPhone()));
            user.setZipCode(data.getOrDefault("zipCode", user.getZipCode()));
            user.setRoadAddress(data.getOrDefault("roadAddress", user.getRoadAddress()));
            user.setDetailAddress(data.getOrDefault("detailAddress", user.getDetailAddress()));
            user.setAgreeMarketing(Boolean.parseBoolean(
                    data.getOrDefault("agreeMarketing", String.valueOf(user.getAgreeMarketing()))
            ));
        }

        if (image != null && !image.isEmpty()) {
            String imageUrl = saveProfileImage(image);
            user.setProfileImageUrl(imageUrl);
        }

        // ✅ createdAt 자동 설정 (신규 계정 생성 시)
        if (user.getCreatedAt() == null) {
            user.setCreatedAt(LocalDateTime.now());
        }

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return new Resp(true, "프로필이 성공적으로 수정되었습니다.");
    }

    /** ✅ 2️⃣ 프로필 이미지 단독 업로드 */
    public String uploadProfileImage(String userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String imageUrl = saveProfileImage(file);
        user.setProfileImageUrl(imageUrl);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return imageUrl;
    }

    /** ✅ 실제 파일 저장 로직 */
    private String saveProfileImage(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("파일이 비어 있습니다.");

        try {
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
            File dest = new File(dir, fileName);
            file.transferTo(dest);
            return "/uploads/userProfile/" + fileName;
        } catch (IOException e) {
            log.error("프로필 이미지 저장 실패", e);
            throw new RuntimeException("이미지 업로드 실패");
        }
    }

    /** ✅ 비밀번호 확인 (현재 비밀번호 확인) */
    public boolean verifyCurrentPassword(String userId, String currentPassword) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        return passwordEncoder.matches(currentPassword, user.getPassword());
    }

    /** ✅ 비밀번호 변경 (현재 비밀번호 확인 + 자동 로그아웃) */
    public Resp changePasswordSecure(String userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (!encoder.matches(currentPassword, user.getPassword())) {
            return new Resp(false, "현재 비밀번호가 일치하지 않습니다.");
        }

        user.setPassword(encoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        // ✅ 로그아웃 쿠키 삭제 처리
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        user.setRefreshToken(null);
        userRepository.save(user);

        return new Resp(true, "비밀번호가 변경되어 자동 로그아웃되었습니다.");
    }

    /** ✅ 회원 탈퇴 (UserStatus.DELETED 로 변경) */
    public Resp markAsDeleted(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        user.setStatus(UserStatus.DELETED);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        return new Resp(true, "회원 탈퇴가 완료되었습니다.");
    }
}
