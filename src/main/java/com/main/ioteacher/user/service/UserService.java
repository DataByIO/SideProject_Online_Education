package com.main.ioteacher.user.service;

import com.main.ioteacher.user.UserStatus;
import com.main.ioteacher.user.dto.UserDtos.Resp;
import com.main.ioteacher.user.dto.UserDtos.UserResp;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    /**
     * 사용자 조회
     */
    public UserResp getUser(String userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        return UserResp.builder()
                .userId(u.getUserId())
                .email(u.getEmail())
                .name(u.getName())
                .profileImageUrl(u.getProfileImageUrl())  // ✅ 추가
                .role(u.getRole().name())
                .status(u.getStatus())
                .build();
    }

    /**
     * 사용자 상태 변경
     */
    public Resp updateStatus(String userId, UserStatus status) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        u.setStatus(status);
        userRepository.save(u);

        return new Resp(true, "사용자 상태가 변경되었습니다.");
    }

    /**
     * 비밀번호 변경
     */
    public Resp changePassword(String userId, String newPassword) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        u.setPassword(encoder.encode(newPassword));
        userRepository.save(u);

        return new Resp(true, "비밀번호가 변경되었습니다.");
    }

    /**
     * 회원 탈퇴
     */
    public Resp deleteUser(String userId) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        userRepository.delete(u);

        return new Resp(true, "회원 탈퇴가 완료되었습니다.");
    }
}
