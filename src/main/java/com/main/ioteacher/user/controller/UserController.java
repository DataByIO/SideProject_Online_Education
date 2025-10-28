package com.main.ioteacher.user.controller;

import com.main.ioteacher.user.UserStatus;
import com.main.ioteacher.user.entity.UserDtos.Resp;
import com.main.ioteacher.user.entity.UserDtos.UserResp;
import com.main.ioteacher.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")  // ✅ 사용자 관리 전용 prefix
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 현재 로그인한 사용자 조회
     * -> 프론트에서 /api/users/me 호출 시 JWT 인증된 사용자 정보 반환
     */
    @GetMapping("/me")
    public UserResp getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
        String userId = userDetails.getUsername();  // JwtAuthenticationFilter에서 세팅한 userId
        return userService.getUser(userId);
    }

    /**
     * 특정 사용자 조회
     */
    @GetMapping("/{userId}")
    public UserResp getUser(@PathVariable String userId) {
        return userService.getUser(userId);
    }

    /**
     * 사용자 상태 변경
     */
    @PatchMapping("/{userId}/status")
    public Resp updateStatus(@PathVariable String userId, @RequestParam UserStatus status) {
        return userService.updateStatus(userId, status);
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping("/{userId}/password")
    public Resp changePassword(@PathVariable String userId, @RequestParam String newPassword) {
        return userService.changePassword(userId, newPassword);
    }

    /**
     * 회원 탈퇴
     */
    @DeleteMapping("/{userId}")
    public Resp deleteUser(@PathVariable String userId) {
        return userService.deleteUser(userId);
    }
}
