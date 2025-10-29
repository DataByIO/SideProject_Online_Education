package com.main.ioteacher.user.controller;

import com.main.ioteacher.user.UserStatus;
import com.main.ioteacher.user.entity.UserDtos.Resp;
import com.main.ioteacher.user.entity.UserDtos.UserResp;
import com.main.ioteacher.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /** ✅ 현재 로그인한 사용자 정보 조회 */
    @GetMapping("/me")
    public UserResp getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        return userService.getUser(userId);
    }

    /** ✅ 특정 사용자 조회 */
    @GetMapping("/{userId}")
    public UserResp getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    /** ✅ 아이디 / 이메일 중복 체크 */
    @GetMapping("/check-duplicate")
    public Map<String, Object> checkDuplicate(@RequestParam("type") String type,
                                              @RequestParam("value") String value) {
        return userService.checkDuplicate(type, value);
    }

    /** ✅ 사용자 상태 변경 (관리자용) */
    @PatchMapping("/{userId}/status")
    public Resp updateStatus(@PathVariable String userId, @RequestParam UserStatus status) {
        return userService.updateStatus(userId, status);
    }

    /* ---------------------------------------------------------
     * ✅ ProfileManagement 기능 확장 (완전 분리 구조)
     * --------------------------------------------------------- */

    /** ✅ 1️⃣ 기본 프로필 수정 (JSON 전용)
     *  Content-Type: application/json
     */
    @PutMapping(value = "/me", consumes = "application/json")
    public Resp updateProfileJson(@AuthenticationPrincipal UserDetails userDetails,
                                  @RequestBody Map<String, String> data) {
        String userId = userDetails.getUsername();
        return userService.updateProfile(userId, data, null);
    }

    /** ✅ 2️⃣ 프로필 이미지 업로드 (파일 전용)
     *  Content-Type: multipart/form-data
     */
    @PostMapping(value = "/me/profile-image", consumes = "multipart/form-data")
    public Map<String, String> uploadProfileImage(@AuthenticationPrincipal UserDetails userDetails,
                                                  @RequestParam("image") MultipartFile file) {
        String imageUrl = userService.uploadProfileImage(userDetails.getUsername(), file);
        return Map.of("imageUrl", imageUrl);
    }

    /** ✅ 비밀번호 변경 (현재 비밀번호 확인 + 로그아웃 처리) */
    @PutMapping("/me/password")
    public Resp changePasswordSecure(@AuthenticationPrincipal UserDetails userDetails,
                                     @RequestBody Map<String, String> body) {
        String userId = userDetails.getUsername();
        String currentPassword = body.get("currentPassword");
        String newPassword = body.get("newPassword");
        return userService.changePasswordSecure(userId, currentPassword, newPassword);
    }

    /** ✅ 현재 비밀번호 검증 (프론트엔드 blur 이벤트용)
     *  POST /api/users/me/verify-password
     *  Body: { "currentPassword": "1234!" }
     */
    @PostMapping("/me/verify-password")
    public Resp verifyPassword(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestBody Map<String, String> body) {
        String userId = userDetails.getUsername();
        String currentPassword = body.get("currentPassword");
        boolean valid = userService.verifyCurrentPassword(userId, currentPassword);
        return new Resp(valid, valid ? "비밀번호 일치" : "비밀번호 불일치");
    }

    /** ✅ 회원 탈퇴 (실제 삭제 X → 상태 변경) */
    @DeleteMapping("/me")
    public Resp deleteMyAccount(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();
        return userService.markAsDeleted(userId);
    }
}
