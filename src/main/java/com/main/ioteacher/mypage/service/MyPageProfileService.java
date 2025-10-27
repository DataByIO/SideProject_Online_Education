package com.main.ioteacher.mypage.service;

import com.main.ioteacher.mypage.entity.ProfileUpdateRequest;
import com.main.ioteacher.user.entity.User;
import com.main.ioteacher.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Service
@RequiredArgsConstructor
public class MyPageProfileService {

    private final UserRepository userRepository;
    private static final String PROFILE_DIR =
            "/Users/kimnoah/IdeaProjects/ioteacher/uploads/userProfile/";

    private String getUserId() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /** ✅ 프로필 수정 */
    public void updateProfile(ProfileUpdateRequest req) {
        User user = userRepository.findByUserId(getUserId())
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        user.setName(req.getName());
        user.setPhone(req.getPhone());
        user.setAddress(req.getAddress());
        userRepository.save(user);
    }

    /** ✅ 프로필 이미지 업로드 */
    public String uploadProfileImage(MultipartFile file) {
        try {
            Files.createDirectories(Paths.get(PROFILE_DIR));
            String filename = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(PROFILE_DIR + filename);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/userProfile/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("이미지 업로드 실패", e);
        }
    }
}
